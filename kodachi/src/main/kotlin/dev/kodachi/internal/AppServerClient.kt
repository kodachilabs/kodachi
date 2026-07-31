package dev.kodachi.internal

import dev.kodachi.CodexConfig
import dev.kodachi.mapRpcError
import dev.kodachi.TransportClosedException
import dev.kodachi.ServerRequestOutcome
import dev.kodachi.TurnStreamOverflowException
import dev.kodachi.protocol.CodexNotification
import dev.kodachi.protocol.TurnCompletedNotification
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Owns the app-server process, the JSON-RPC framing, and the fan-out of inbound
 * messages to whoever is waiting for them.
 *
 * The transport is one ordered stream, so exactly one reader coroutine consumes it and
 * hands each message to a request waiter, a turn stream, or the global event tap.
 */
internal class AppServerClient(
    private val config: CodexConfig,
    private val transport: Transport = ProcessTransport(config),
) : AutoCloseable {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineName("codex-appserver"),
    )
    private val writeMutex = Mutex()
    private val pending = ConcurrentHashMap<String, CompletableDeferred<JsonElement>>()

    // The routing fast path (a live turn's channel) is lock-free; the lock guards only the
    // pre-registration buffer, which is touched for a handful of events per turn.
    private val turnLock = Any()
    private val turnChannels = ConcurrentHashMap<String, Channel<CodexNotification>>()
    private val bufferedTurns = mutableMapOf<String, BufferedTurn>()

    /** One logical goal per thread; it claims that thread's turn events while it runs. */
    private val goalRoutes = ConcurrentHashMap<String, GoalRoute>()

    private val _events = MutableSharedFlow<CodexNotification>(
        extraBufferCapacity = EVENT_TAP_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** Lossy tap of every inbound notification. Turn streams are the lossless path. */
    val events: SharedFlow<CodexNotification> = _events

    @Volatile
    private var closed = false

    /** Events that arrived before their turn stream was registered. */
    private class BufferedTurn {
        val events = ArrayDeque<CodexNotification>()
        var completed = false
    }

    init {
        scope.launch { readLoop() }
    }

    // -----------------------------------------------------------------------
    // Outbound
    // -----------------------------------------------------------------------

    suspend fun request(method: String, params: JsonObject? = null): JsonElement {
        if (closed) throw TransportClosedException("Codex client is closed", transport.stderrTail())

        val id = UUID.randomUUID().toString()
        val waiter = CompletableDeferred<JsonElement>()
        pending[id] = waiter

        try {
            write(
                buildJsonObject {
                    put("id", id)
                    put("method", method)
                    if (params != null) put("params", params)
                },
            )
        } catch (t: Throwable) {
            pending.remove(id)
            throw t
        }

        val timeout = config.requestTimeoutMillis
        return try {
            if (timeout == null) waiter.await() else withTimeout(timeout) { waiter.await() }
        } catch (e: TimeoutCancellationException) {
            pending.remove(id)
            throw TransportClosedException(
                "Timed out after ${timeout}ms waiting for '$method'",
                transport.stderrTail(),
                e,
            )
        }
    }

    suspend fun notify(method: String, params: JsonObject? = null) {
        write(
            buildJsonObject {
                put("method", method)
                if (params != null) put("params", params)
            },
        )
    }

    private suspend fun write(message: JsonObject) {
        writeMutex.withLock {
            try {
                transport.writeLine(CodexJson.encodeToString(JsonObject.serializer(), message))
            } catch (t: Throwable) {
                throw TransportClosedException(
                    "Failed writing to app-server",
                    transport.stderrTail(),
                    t,
                )
            }
        }
    }

    // -----------------------------------------------------------------------
    // Turn streams
    // -----------------------------------------------------------------------

    /**
     * Claim the event stream for a turn.
     *
     * Called right after `turn/start` returns. Events for the turn can already have
     * arrived by then, so any buffered ones are replayed into the new channel — and if
     * the turn already finished, the channel is closed so the stream terminates cleanly
     * instead of waiting for a completion that will never come again.
     */
    fun registerTurn(turnId: String): Channel<CodexNotification> {
        synchronized(turnLock) {
            turnChannels[turnId]?.let { return it }

            // Bounded rather than UNLIMITED: a send that cannot land is a symptom worth
            // surfacing, not a reason to keep allocating.
            val channel = Channel<CodexNotification>(config.turnEventBufferSize)
            bufferedTurns.remove(turnId)?.let { buffered ->
                // Replay before publishing, so buffered events keep their place in order.
                buffered.events.forEach { channel.trySend(it) }
                if (buffered.completed) channel.close()
            }
            turnChannels[turnId] = channel
            return channel
        }
    }

    // -----------------------------------------------------------------------
    // Goal streams
    // -----------------------------------------------------------------------

    /**
     * Claim a thread's turn events for one logical goal.
     *
     * Registered *before* `thread/goal/set` is sent, so the goal's first turn cannot start
     * before there is somewhere to route it.
     *
     * @throws IllegalStateException if the thread already has a goal stream open
     */
    fun registerGoal(threadId: String): GoalRoute {
        val route = GoalRoute(threadId, config.turnEventBufferSize)
        val existing = goalRoutes.putIfAbsent(threadId, route)
        check(existing == null) {
            "thread $threadId already has an open goal stream; close it before starting another"
        }
        return route
    }

    fun unregisterGoal(route: GoalRoute) {
        // Remove only if it is still the current route, so a replacement is not clobbered.
        goalRoutes.remove(route.threadId, route)
        route.close()
    }

    fun unregisterTurn(turnId: String) {
        synchronized(turnLock) {
            turnChannels.remove(turnId)?.close()
            bufferedTurns.remove(turnId)
        }
    }

    /** Number of turn streams currently claimed. Exposed for tests. */
    internal val activeTurnCount: Int get() = turnChannels.size

    // -----------------------------------------------------------------------
    // Inbound
    // -----------------------------------------------------------------------

    private suspend fun readLoop() {
        try {
            while (true) {
                val line = transport.readLine() ?: break
                if (line.isBlank()) continue
                handleLine(line)
            }
            failAll(
                TransportClosedException(
                    "app-server closed its output stream",
                    transport.stderrTail(),
                ),
            )
        } catch (t: Throwable) {
            failAll(
                TransportClosedException(
                    "app-server transport failed: ${t.message}",
                    transport.stderrTail(),
                    t,
                ),
            )
        }
    }

    private suspend fun handleLine(line: String) {
        // try/catch rather than runCatching: this runs once per inbound line and a Result
        // allocation per line is pure waste.
        val message = try {
            CodexJson.parseToJsonElement(line).jsonObject
        } catch (_: Exception) {
            return // A non-JSON line is the child logging to stdout; ignore rather than die.
        }

        val method = message["method"]?.jsonPrimitive?.contentOrNull
        val id = message["id"]

        when {
            // Both method and id: a request from the server that we must answer.
            method != null && id != null && id != JsonNull -> handleServerRequest(method, id, message)

            // Method only: a notification.
            method != null -> route(
                NotificationCodec.decode(
                    method,
                    message["params"]?.let { it as? JsonObject } ?: JsonObject(emptyMap()),
                ),
            )

            // Neither: a response to one of our requests.
            else -> completeRequest(message)
        }
    }

    /**
     * Answer a server request on its own coroutine so a slow handler cannot stall the
     * reader — every other event on the stream is queued behind it.
     */
    private fun handleServerRequest(method: String, id: JsonElement, message: JsonObject) {
        val params = message["params"]?.let { it as? JsonObject } ?: JsonObject(emptyMap())
        scope.launch {
            val outcome = runCatching { ServerRequestDispatcher.dispatch(config.serverRequestHandler, method, params) }
                .getOrElse { failure ->
                    ServerRequestOutcome.Failure(
                        code = -32603,
                        message = "handler failed: ${failure.message ?: failure::class.simpleName}",
                    )
                }

            runCatching {
                write(
                    buildJsonObject {
                        // Echo the id verbatim: the server numbers its requests with
                        // integers, while ours are UUID strings.
                        put("id", id)
                        when (outcome) {
                            is ServerRequestOutcome.Success -> put("result", outcome.value)
                            is ServerRequestOutcome.Failure -> put(
                                "error",
                                buildJsonObject {
                                    put("code", outcome.code)
                                    put("message", outcome.message)
                                },
                            )
                        }
                    },
                )
            }
        }
    }

    private fun completeRequest(message: JsonObject) {
        val key = message["id"]?.jsonPrimitive?.contentOrNull ?: return
        val waiter = pending.remove(key) ?: return

        val error = message["error"] as? JsonObject
        if (error != null) {
            waiter.completeExceptionally(
                mapRpcError(
                    code = error["code"]?.jsonPrimitive?.contentOrNull?.toIntOrNull() ?: -32000,
                    message = error["message"]?.jsonPrimitive?.contentOrNull ?: "unknown error",
                    data = error["data"],
                ),
            )
            return
        }

        waiter.complete(message["result"] ?: JsonObject(emptyMap()))
    }

    private fun route(notification: CodexNotification) {
        // The tap is documented as lossy, so skip it entirely when nobody is listening —
        // which is the common case, and this runs once per streamed token.
        if (_events.subscriptionCount.value > 0) _events.tryEmit(notification)

        // A goal owns its thread's turns, so it is offered the event before turn routing.
        // The SDK never registers per-turn streams for a goal-driven thread, so this cannot
        // steal events from a caller's own stream.
        val threadId = notification.threadId
        if (threadId != null) {
            val goal = goalRoutes[threadId]
            if (goal != null && goal.claims(notification) && goal.observe(notification)) {
                if (goal.isFinished()) goalRoutes.remove(threadId, goal)
                return
            }
        }

        val turnId = notification.turnId ?: return

        // Fast path: a live turn stream, no lock. A failed send means the consumer has
        // fallen far enough behind to be a bug; end the stream with a diagnosis.
        turnChannels[turnId]?.let { channel ->
            if (channel.trySend(notification).isFailure) {
                channel.close(TurnStreamOverflowException(turnId, config.turnEventBufferSize))
            }
            return
        }

        synchronized(turnLock) {
            // registerTurn may have published the channel between our read and this lock.
            turnChannels[turnId]?.let {
                it.trySend(notification)
                return
            }

            // The turn/start response has not been handed back yet; hold these so the
            // stream sees them once it registers.
            val buffered = bufferedTurns.getOrPut(turnId) { BufferedTurn() }
            buffered.events.addLast(notification)
            if (buffered.events.size > MAX_BUFFERED_EVENTS_PER_TURN) buffered.events.removeFirst()
            if (notification is TurnCompletedNotification) buffered.completed = true
        }
    }

    private fun failAll(cause: Throwable) {
        pending.keys.toList().forEach { key ->
            pending.remove(key)?.completeExceptionally(cause)
        }
        synchronized(turnLock) {
            turnChannels.values.forEach { it.close(cause) }
            turnChannels.clear()
            bufferedTurns.clear()
        }
        goalRoutes.values.forEach { it.fail(cause) }
        goalRoutes.clear()
    }

    override fun close() {
        if (closed) return
        closed = true
        runCatching { transport.close() }
        failAll(TransportClosedException("Codex client was closed"))
        scope.cancel()
    }

    private companion object {
        const val EVENT_TAP_CAPACITY = 512
        const val MAX_BUFFERED_EVENTS_PER_TURN = 4096
    }
}

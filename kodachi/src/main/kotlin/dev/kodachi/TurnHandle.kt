package dev.kodachi

import dev.kodachi.protocol.AgentMessageThreadItem
import dev.kodachi.protocol.CodexNotification
import dev.kodachi.protocol.ErrorNotification
import dev.kodachi.protocol.ItemCompletedNotification
import dev.kodachi.protocol.ThreadItem
import dev.kodachi.protocol.ThreadTokenUsage
import dev.kodachi.protocol.ThreadTokenUsageUpdatedNotification
import dev.kodachi.protocol.Turn
import dev.kodachi.protocol.TurnCompletedNotification
import dev.kodachi.protocol.TurnDiffUpdatedNotification
import dev.kodachi.protocol.TurnError
import dev.kodachi.protocol.TurnInterruptParams
import dev.kodachi.protocol.TurnInterruptResponse
import dev.kodachi.protocol.TurnStatus
import dev.kodachi.protocol.TurnSteerParams
import dev.kodachi.protocol.TurnSteerResponse
import dev.kodachi.protocol.UserInput
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A running turn: its event stream, plus the controls to steer or stop it.
 *
 * Obtained from [CodexThread.turn]. The event stream is buffered from the moment the
 * handle is created, so nothing is lost between `turn/start` returning and your first
 * [stream] collection.
 */
public class TurnHandle internal constructor(
    private val codex: Codex,
    /** Thread this turn belongs to. */
    public val threadId: String,
    /** The turn as it was when it started; [TurnResult.status] carries the final state. */
    public val turn: Turn,
    private val channel: Channel<CodexNotification>,
) {

    /** Server-assigned turn id. */
    public val id: String get() = turn.id

    /**
     * Events for this turn, in order, ending after [TurnCompletedNotification].
     *
     * Collect at most once — the underlying channel is consumed, not replayed. Cancelling
     * the collection releases the stream but does *not* stop the turn; call [interrupt]
     * for that.
     *
     * Throws [TransportClosedException] if the app-server dies while the turn is open.
     */
    public fun stream(): Flow<CodexNotification> = flow {
        try {
            for (event in channel) {
                emit(event)
                if (event is TurnCompletedNotification) break
            }
        } finally {
            codex.client.unregisterTurn(id)
        }
    }

    /**
     * Inject additional input into this turn while it is running.
     *
     * The model incorporates it mid-flight rather than being interrupted and restarted.
     * Rejected once the turn has finished.
     */
    public suspend fun steer(input: List<UserInput>): TurnSteerResponse {
        require(input.isNotEmpty()) { "steer input must not be empty" }
        return codex.turns.steer(
            TurnSteerParams(threadId = threadId, expectedTurnId = id, input = input),
        )
    }

    /** Convenience overload for a single text steer. */
    public suspend fun steer(text: String): TurnSteerResponse = steer(promptInput(text))

    /**
     * Stop the turn.
     *
     * The stream still terminates with a [TurnCompletedNotification] carrying
     * [TurnStatus.INTERRUPTED]. Safe to call on an already-finished turn.
     */
    public suspend fun interrupt(): TurnInterruptResponse =
        codex.turns.interrupt(TurnInterruptParams(threadId = threadId, turnId = id))

    /**
     * Consume the whole stream and summarize it.
     *
     * This is what [CodexThread.run] uses. Calling it consumes [stream], so do one or
     * the other — not both.
     */
    public suspend fun collect(): TurnResult {
        val items = mutableListOf<ThreadItem>()
        val messages = mutableListOf<String>()
        var usage: ThreadTokenUsage? = null
        var status = TurnStatus.IN_PROGRESS
        var error: TurnError? = null
        var diff: String? = null

        stream().collect { event ->
            when (event) {
                is ItemCompletedNotification -> {
                    items += event.item
                    (event.item as? AgentMessageThreadItem)?.let { messages += it.text }
                }

                is ThreadTokenUsageUpdatedNotification -> usage = event.tokenUsage

                is TurnDiffUpdatedNotification -> diff = event.diff

                // A retryable error is superseded by whatever happens next; only a
                // terminal one describes the outcome.
                is ErrorNotification -> if (!event.willRetry) error = event.error

                is TurnCompletedNotification -> {
                    status = event.turn.status
                    event.turn.error?.let { error = it }
                }

                else -> Unit
            }
        }

        return TurnResult(
            threadId = threadId,
            turnId = id,
            status = status,
            finalResponse = messages.lastOrNull().orEmpty(),
            messages = messages.toList(),
            items = items.toList(),
            usage = usage,
            error = error,
            diff = diff,
        )
    }

    override fun toString(): String = "TurnHandle(threadId=$threadId, turnId=$id)"
}

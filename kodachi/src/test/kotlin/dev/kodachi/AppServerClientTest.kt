package dev.kodachi

import dev.kodachi.internal.AppServerClient
import dev.kodachi.protocol.AgentMessageDeltaNotification
import dev.kodachi.protocol.CodexNotification
import dev.kodachi.protocol.CommandExecutionRequestApprovalParams
import dev.kodachi.protocol.CommandExecutionRequestApprovalResponse
import dev.kodachi.protocol.FileChangeRequestApprovalParams
import dev.kodachi.protocol.FileChangeRequestApprovalResponse
import dev.kodachi.protocol.TurnCompletedNotification
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Transport-level behavior of the client, exercised without a real Codex process. */
class AppServerClientTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun client(
        transport: FakeTransport,
        serverRequestHandler: ServerRequestHandler = ServerRequestHandler.ACCEPT_ALL,
    ) = AppServerClient(
        config = CodexConfig(serverRequestHandler = serverRequestHandler, requestTimeoutMillis = 5_000),
        transport = transport,
    )

    /** Read the id the client generated for its request. */
    private fun FakeTransport.awaitRequestId(): String {
        val line = assertNotNull(nextWritten(), "client wrote no request")
        return json.parseToJsonElement(line).jsonObject["id"]!!.jsonPrimitive.content
    }

    @Test
    fun `a request resolves with its matching response`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                val call = async { client.request("thread/start", buildJsonObject { put("cwd", "/tmp") }) }

                val id = transport.awaitRequestId()
                transport.push("""{"id":"$id","result":{"thread":{"id":"thread-1"}}}""")

                val result = call.await().jsonObject
                assertEquals("thread-1", result["thread"]!!.jsonObject["id"]!!.jsonPrimitive.content)
            }
        }
    }

    @Test
    fun `responses are matched by id even when they arrive out of order`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                val first = async { client.request("a") }
                val firstId = transport.awaitRequestId()
                val second = async { client.request("b") }
                val secondId = transport.awaitRequestId()

                // Answer the second request first.
                transport.push("""{"id":"$secondId","result":{"which":"second"}}""")
                transport.push("""{"id":"$firstId","result":{"which":"first"}}""")

                assertEquals("first", first.await().jsonObject["which"]!!.jsonPrimitive.content)
                assertEquals("second", second.await().jsonObject["which"]!!.jsonPrimitive.content)
            }
        }
    }

    @Test
    fun `a JSON-RPC error becomes a CodexRpcException carrying code and data`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                // The whole exchange sits inside assertFailsWith: a failing `async` also
                // cancels its parent scope, so catching only around await() would race.
                val error = assertFailsWith<CodexRpcException> {
                    coroutineScope {
                        val call = async { client.request("turn/start") }
                        val id = transport.awaitRequestId()
                        transport.push(
                            """{"id":"$id","error":{"code":-32600,"message":"thread has an active turn","data":{"threadId":"t1"}}}""",
                        )
                        call.await()
                    }
                }

                assertEquals(-32600, error.code)
                assertTrue(error.message!!.contains("active turn"))
                assertNotNull(error.data)
            }
        }
    }

    @Test
    fun `turn events reach the registered stream in order and stop at completion`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                val channel = client.registerTurn("turn-1")

                transport.push(delta("turn-1", "Hel"))
                transport.push(delta("turn-1", "lo"))
                transport.push(turnCompleted("turn-1"))

                val received = mutableListOf<CodexNotification>()
                for (event in channel) {
                    received += event
                    if (event is TurnCompletedNotification) break
                }

                assertEquals(3, received.size)
                assertEquals("Hel", assertIs<AgentMessageDeltaNotification>(received[0]).delta)
                assertEquals("lo", assertIs<AgentMessageDeltaNotification>(received[1]).delta)
                assertIs<TurnCompletedNotification>(received[2])
            }
        }
    }

    @Test
    fun `events that arrive before the stream is registered are replayed, not lost`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                // The real server can emit events before `turn/start` has even returned.
                client.drainAfter(transport) {
                    transport.push(delta("turn-2", "early"))
                    transport.push(delta("turn-2", "-bird"))
                }

                val channel = client.registerTurn("turn-2")
                transport.push(turnCompleted("turn-2"))

                val deltas = mutableListOf<String>()
                for (event in channel) {
                    if (event is AgentMessageDeltaNotification) deltas += event.delta
                    if (event is TurnCompletedNotification) break
                }

                assertEquals(listOf("early", "-bird"), deltas)
            }
        }
    }

    @Test
    fun `a turn that completes before registration still terminates its stream`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                client.drainAfter(transport) {
                    transport.push(delta("turn-3", "done already"))
                    transport.push(turnCompleted("turn-3"))
                }

                // Registering after the fact must replay and then close, rather than
                // hanging forever waiting for a completion that already passed.
                val channel = client.registerTurn("turn-3")
                val received = mutableListOf<CodexNotification>()
                for (event in channel) received += event

                assertEquals(2, received.size)
                assertIs<TurnCompletedNotification>(received.last())
            }
        }
    }

    /**
     * Push lines, then block until the reader has definitely processed them.
     *
     * The reader consumes stdout strictly in order, so a response to a request issued
     * *before* those lines can only arrive after they have been handled. That gives a
     * deterministic barrier without reaching into the client's internals.
     */
    private suspend fun AppServerClient.drainAfter(
        transport: FakeTransport,
        push: () -> Unit,
    ) = coroutineScope {
        val barrier = async { request("barrier/ping") }
        val id = transport.awaitRequestId()
        push()
        transport.push("""{"id":"$id","result":{}}""")
        barrier.await()
    }

    @Test
    fun `an approval request is answered with the decision and the id echoed verbatim`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        var seen: CommandExecutionRequestApprovalParams? = null
        val handler = object : ServerRequestHandler(ApprovalDecision.ACCEPT_FOR_SESSION) {
            override suspend fun onCommandApproval(
                params: CommandExecutionRequestApprovalParams,
            ): CommandExecutionRequestApprovalResponse {
                seen = params
                return super.onCommandApproval(params)
            }
        }

        client(transport, handler).use {
            withTimeout(5_000) {
                // The server numbers its own requests with integers, unlike our UUIDs.
                transport.push(
                    """{"id":7,"method":"item/commandExecution/requestApproval","params":{"threadId":"t1","turnId":"turn-1","itemId":"call_1","startedAtMs":0,"command":"rm -rf build","cwd":"/repo","reason":"why not"}}""",
                )

                val reply = json.parseToJsonElement(
                    assertNotNull(transport.nextWritten(), "no approval reply written"),
                ).jsonObject

                assertEquals(7, reply["id"]!!.jsonPrimitive.int)
                assertEquals(
                    "acceptForSession",
                    reply["result"]!!.jsonObject["decision"]!!.jsonPrimitive.content,
                )

                val command = assertNotNull(seen, "handler never saw the request")
                assertEquals("rm -rf build", command.command)
                assertEquals("/repo", command.cwd)
            }
        }
    }

    @Test
    fun `an unmodelled server request is still answered so the turn cannot stall`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use {
            withTimeout(5_000) {
                // A method no ServerRequests entry covers, as a newer app-server would send.
                transport.push("""{"id":11,"method":"someFuture/requestApproval","params":{"foo":"bar"}}""")

                val reply = json.parseToJsonElement(
                    assertNotNull(transport.nextWritten(), "no reply written"),
                ).jsonObject
                assertEquals(11, reply["id"]!!.jsonPrimitive.int)

                // A JSON-RPC error, not an empty result: the client cannot answer, but the
                // server still gets an answer and the turn moves on.
                val error = assertNotNull(reply["error"], "reply carried no error").jsonObject
                assertEquals(-32601, error["code"]!!.jsonPrimitive.int)
                assertTrue(error["message"]!!.jsonPrimitive.content.contains("someFuture/requestApproval"))
            }
        }
    }

    @Test
    fun `a handler that throws answers with an error rather than stalling the turn`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        val handler = object : ServerRequestHandler() {
            override suspend fun onFileChangeApproval(
                params: FileChangeRequestApprovalParams,
            ): FileChangeRequestApprovalResponse = error("handler blew up")
        }

        client(transport, handler).use {
            withTimeout(5_000) {
                transport.push(
                    """{"id":3,"method":"item/fileChange/requestApproval","params":{"threadId":"t1","turnId":"turn-1","itemId":"i1","startedAtMs":0}}""",
                )

                val reply = json.parseToJsonElement(
                    assertNotNull(transport.nextWritten(), "no reply written"),
                ).jsonObject
                val error = assertNotNull(reply["error"], "reply carried no error").jsonObject
                assertEquals(-32603, error["code"]!!.jsonPrimitive.int)
                assertTrue(error["message"]!!.jsonPrimitive.content.contains("handler blew up"))
            }
        }
    }

    @Test
    fun `losing the transport fails in-flight requests with stderr context`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                val error = assertFailsWith<TransportClosedException> {
                    coroutineScope {
                        val call = async { client.request("thread/start") }
                        transport.awaitRequestId()
                        transport.pushEof()
                        call.await()
                    }
                }

                assertEquals("fake-stderr", error.stderrTail)
                assertTrue(error.message!!.contains("closed its output stream"))
            }
        }
    }

    @Test
    fun `losing the transport ends open turn streams with an exception`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                val channel = client.registerTurn("turn-4")
                transport.push(delta("turn-4", "partial"))
                transport.pushEof()

                assertFailsWith<TransportClosedException> {
                    for (unused in channel) {
                        // Drain until the channel is closed with the transport failure.
                    }
                }
            }
        }
    }

    @Test
    fun `non-JSON output on stdout is ignored instead of killing the reader`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        client(transport).use { client ->
            withTimeout(5_000) {
                transport.push("warning: something logged to stdout")

                val call = async { client.request("model/list") }
                val id = transport.awaitRequestId()
                transport.push("""{"id":"$id","result":{"models":[]}}""")

                assertNotNull(call.await())
            }
        }
    }

    private fun delta(turnId: String, text: String) = buildJsonObject {
        put("method", "item/agentMessage/delta")
        put(
            "params",
            buildJsonObject {
                put("threadId", "t1")
                put("turnId", turnId)
                put("itemId", "item-1")
                put("delta", text)
            },
        )
    }.toString()

    private fun turnCompleted(turnId: String) = buildJsonObject {
        put("method", "turn/completed")
        put(
            "params",
            buildJsonObject {
                put("threadId", "t1")
                put(
                    "turn",
                    buildJsonObject {
                        put("id", turnId)
                        put("status", "completed")
                        // `items` has no default in the generated Turn, so an omitted one
                        // would decode as UnknownNotification and never route as completion.
                        putJsonArray("items") { }
                    },
                )
            },
        )
    }.toString()
}

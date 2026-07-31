package dev.kodachi

import dev.kodachi.internal.AppServerClient
import dev.kodachi.protocol.AgentMessageDeltaNotification
import dev.kodachi.protocol.CodexNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * A turn's event stream is lossless, which means buffering whatever the consumer has not read
 * yet. An unbounded buffer turns a stalled collector into heap exhaustion, so the buffer is
 * capped and overflow fails with a diagnosis instead.
 */
class BackpressureTest {

    private fun delta(turnId: String, text: String): String =
        """{"method":"item/agentMessage/delta","params":{"threadId":"t1","turnId":"$turnId","itemId":"i1","delta":"$text"}}"""

    @Test
    fun `a stalled consumer fails the stream instead of growing the heap`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        val config = CodexConfig(turnEventBufferSize = 8, requestTimeoutMillis = 5_000)

        AppServerClient(config, transport).use { client ->
            withTimeout(10_000) {
                val channel = client.registerTurn("turn-1")

                // Push past the cap, then wait for the reader to have handled every line
                // before draining — otherwise the collector keeps up and never overflows.
                // A response can only arrive after the lines queued ahead of it were read.
                val barrier = async { client.request("barrier/ping") }
                val requestLine = requireNotNull(transport.nextWritten()) { "no barrier request" }
                val id = Json.parseToJsonElement(requestLine).jsonObject["id"]!!.jsonPrimitive.content
                repeat(40) { transport.push(delta("turn-1", "chunk$it")) }
                transport.push("""{"id":"$id","result":{}}""")
                barrier.await()

                val overflow = assertFailsWith<TurnStreamOverflowException> {
                    // Drain: the buffered events arrive first, then the close cause surfaces.
                    for (unused in channel) {
                        // Keep receiving until the channel is closed with its cause.
                    }
                }
                assertEquals("turn-1", overflow.turnId)
                assertEquals(8, overflow.bufferSize)
                assertTrue(overflow.message!!.contains("turnEventBufferSize"))
            }
        }
    }

    @Test
    fun `a consumer that keeps up never overflows`() = runBlocking(Dispatchers.Default) {
        val transport = FakeTransport()
        // A cap far below the message count: only a keeping-up consumer can survive it.
        val config = CodexConfig(turnEventBufferSize = 16, requestTimeoutMillis = 5_000)

        AppServerClient(config, transport).use { client ->
            withTimeout(20_000) {
                val channel = client.registerTurn("turn-2")
                val total = 500

                coroutineScope {
                    val consumer = async {
                        val seen = mutableListOf<CodexNotification>()
                        for (event in channel) {
                            seen += event
                            if (seen.size == total) break
                        }
                        seen
                    }

                    repeat(total) { transport.push(delta("turn-2", "c$it")) }

                    val seen = consumer.await()
                    assertEquals(total, seen.size)
                    // Order is preserved: one ordered stream in, one ordered stream out.
                    assertEquals("c0", assertIs<AgentMessageDeltaNotification>(seen.first()).delta)
                    assertEquals("c${total - 1}", assertIs<AgentMessageDeltaNotification>(seen.last()).delta)
                }
            }
        }
    }

    @Test
    fun `the default cap is high enough that real streaming never trips it`() {
        // A model emits hundreds of events per second; the default buffers thousands.
        assertTrue(
            CodexConfig().turnEventBufferSize >= 4096,
            "default turnEventBufferSize is too small to absorb a normal burst",
        )
    }
}

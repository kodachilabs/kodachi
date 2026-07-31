package dev.kodachi

import dev.kodachi.protocol.AgentMessageDeltaNotification
import dev.kodachi.protocol.ItemCompletedNotification
import dev.kodachi.protocol.TurnCompletedNotification
import dev.kodachi.protocol.TurnStatus
import dev.kodachi.protocol.TurnSteerResponse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Drives a real `codex app-server`, which means a real model call and real quota.
 *
 * Skipped unless enabled explicitly:
 * ```
 * ./gradlew :kodachi:test -DcodexIntegration=true
 * ```
 * Requires a `codex` on PATH (or `$CODEX_BIN`) that is already authenticated.
 */
class CodexIntegrationTest {

    private val enabled: Boolean = System.getProperty("codexIntegration") == "true"

    @Test
    fun `handshake reports server metadata`() {
        if (!enabled) return
        runBlocking {
            Codex.connect().use { codex ->
                val metadata = assertNotNull(codex.metadata)
                assertTrue(metadata.userAgent.contains("kodachi"))
                assertNotNull(metadata.codexHome)
            }
        }
    }

    @Test
    fun `a turn streams deltas and completes with the final message`() {
        if (!enabled) return
        runBlocking {
            Codex.connect(CodexConfig(cwd = System.getProperty("java.io.tmpdir"))).use { codex ->
                val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)

                val handle = thread.turn("Reply with exactly: hello from kotlin")
                val deltas = StringBuilder()
                var completed: TurnCompletedNotification? = null
                val items = mutableListOf<String>()

                withTimeout(180_000) {
                    handle.stream().collect { event ->
                        when (event) {
                            is AgentMessageDeltaNotification -> deltas.append(event.delta)
                            is ItemCompletedNotification -> items += event.item.type
                            is TurnCompletedNotification -> completed = event
                            else -> Unit
                        }
                    }
                }

                assertEquals(TurnStatus.COMPLETED, assertNotNull(completed).turn.status)
                assertTrue(deltas.isNotEmpty(), "expected streamed assistant text")
                assertTrue(items.contains("agentMessage"), "expected an agentMessage item")
            }
        }
    }

    @Test
    fun `run collects the whole turn including usage`() {
        if (!enabled) return
        runBlocking {
            Codex.connect(CodexConfig(cwd = System.getProperty("java.io.tmpdir"))).use { codex ->
                val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
                val result = withTimeout(180_000) { thread.run("Say the single word: ok") }

                assertTrue(result.isSuccess, "turn failed: ${result.error}")
                assertTrue(result.finalResponse.isNotBlank())
                assertNotNull(result.usage).let { usage ->
                    assertTrue(usage.total.totalTokens > 0)
                }
            }
        }
    }

    @Test
    fun `steering a running turn is accepted and the turn still completes`() {
        if (!enabled) return
        runBlocking {
            Codex.connect(CodexConfig(cwd = System.getProperty("java.io.tmpdir"))).use { codex ->
                val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
                // Long enough that the turn is still open once the first deltas land.
                val handle = thread.turn("Count from 1 to 40, one number per line.")

                var steer: TurnSteerResponse? = null
                var streamed = 0
                var completed: TurnCompletedNotification? = null

                withTimeout(180_000) {
                    handle.stream().collect { event ->
                        when (event) {
                            is AgentMessageDeltaNotification -> {
                                streamed += event.delta.length
                                // Steering from inside the collector is safe: the turn's
                                // event channel buffers while this coroutine waits on the
                                // request, so no event is lost.
                                if (steer == null && streamed >= 8) {
                                    steer = handle.steer("Stop counting and reply with exactly DONE.")
                                }
                            }

                            is TurnCompletedNotification -> completed = event
                            else -> Unit
                        }
                    }
                }

                assertEquals(
                    handle.id,
                    assertNotNull(steer, "expected streamed text to trigger a steer").turnId,
                    "steer should apply to the running turn, not start a new one",
                )
                assertEquals(TurnStatus.COMPLETED, assertNotNull(completed).turn.status)
            }
        }
    }

    @Test
    fun `interrupting a running turn ends the stream as interrupted`() {
        if (!enabled) return
        runBlocking {
            Codex.connect(CodexConfig(cwd = System.getProperty("java.io.tmpdir"))).use { codex ->
                val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
                val handle = thread.turn("Count from 1 to 40, one number per line.")

                var interrupted = false
                var completed: TurnCompletedNotification? = null

                // Interrupt on the very first delta: the model still has ~39 lines to go,
                // so the turn is unambiguously mid-flight. Reaching the end of collect at
                // all is the assertion that the stream terminates after an interrupt.
                withTimeout(180_000) {
                    handle.stream().collect { event ->
                        when (event) {
                            is AgentMessageDeltaNotification -> if (!interrupted) {
                                interrupted = true
                                handle.interrupt()
                            }

                            is TurnCompletedNotification -> completed = event
                            else -> Unit
                        }
                    }
                }

                assertTrue(interrupted, "expected streamed text before the interrupt")
                assertEquals(TurnStatus.INTERRUPTED, assertNotNull(completed).turn.status)
            }
        }
    }

    @Test
    fun `thread name and archive state round-trip`() {
        if (!enabled) return
        runBlocking {
            // No turn is started here: every call below is a cheap thread operation, so
            // this test costs no model quota.
            Codex.connect(CodexConfig(cwd = System.getProperty("java.io.tmpdir"))).use { codex ->
                val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
                val name = "kodachi integration ${System.currentTimeMillis()}"

                withTimeout(180_000) {
                    thread.setName(name)
                    assertEquals(name, thread.read().thread.name, "name should survive a read")

                    thread.archive()
                    val unarchived = thread.unarchive()
                    assertEquals(thread.id, unarchived.thread.id)
                    assertEquals(name, unarchived.thread.name)
                }
            }
        }
    }
}

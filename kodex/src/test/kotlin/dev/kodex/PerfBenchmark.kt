package dev.kodex

import dev.kodex.internal.AppServerClient
import dev.kodex.internal.CodexJson
import dev.kodex.internal.NotificationCodec
import dev.kodex.protocol.CodexNotification
import dev.kodex.protocol.TurnCompletedNotification
import dev.kodex.protocol.TurnStartParams
import dev.kodex.protocol.encodeParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.jsonObject
import kotlin.system.measureNanoTime
import kotlin.test.Test

/**
 * Throughput harness for the transport hot path.
 *
 * Not JMH — a warmed in-JVM loop, which is enough to compare a change against its own baseline
 * on the same machine. Treat the absolute numbers as indicative and the deltas as the result.
 *
 * ```
 * ./gradlew :kodex:test -DcodexBench=true --tests 'dev.kodex.PerfBenchmark' -i
 * ```
 */
class PerfBenchmark {

    private val enabled: Boolean = System.getProperty("codexBench") == "true"

    // The highest-volume event by far: one per streamed token.
    private val deltaLine = """
        {"method":"item/agentMessage/delta","params":{"threadId":"019fb6c4-abb3-77e1-9a0d-2b5ef3721a61","turnId":"019fb6c4-ac68-7f90-b990-7cbc808e5c93","itemId":"msg_0999006b7640c4ec016a6c3a90dfc48191a1720edc1a8f0eb0","delta":"hello"}}
    """.trimIndent()

    // A heavier payload: a completed command item, with enum fields and nested objects.
    private val itemLine = """
        {"method":"item/completed","params":{"item":{"type":"commandExecution","id":"call_1","command":"/bin/zsh -lc 'cargo build --release'","cwd":"/repo","status":"completed","exitCode":0,"aggregatedOutput":"Compiling codex v0.1.0\nFinished release [optimized] target(s) in 41.20s","durationMs":41200,"commandActions":[{"type":"unknown","command":"cargo build --release"}]},"threadId":"t1","turnId":"turn1","completedAtMs":1785477774693}}
    """.trimIndent()

    // Usage updates carry a nested breakdown and arrive once per turn segment.
    private val usageLine = """
        {"method":"thread/tokenUsage/updated","params":{"threadId":"t1","turnId":"turn1","tokenUsage":{"total":{"totalTokens":14591,"inputTokens":14583,"cachedInputTokens":4480,"outputTokens":8,"reasoningOutputTokens":0},"last":{"totalTokens":14591,"inputTokens":14583,"cachedInputTokens":4480,"outputTokens":8,"reasoningOutputTokens":0},"modelContextWindow":258400}}}
    """.trimIndent()

    @Test
    fun benchmark() {
        if (!enabled) return

        println("\n=== kodex transport benchmark ===")
        println("(warmed in-JVM loops; compare deltas, not absolutes)\n")
        println(String.format("%-42s %14s %12s", "path", "ops/sec", "ns/op"))
        println("-".repeat(70))

        bench("decode: agentMessage/delta", 200_000) { decodeOnce(deltaLine) }
        bench("decode: item/completed (command)", 100_000) { decodeOnce(itemLine) }
        bench("decode: tokenUsage/updated", 100_000) { decodeOnce(usageLine) }
        bench("parse only: agentMessage/delta", 200_000) {
            CodexJson.parseToJsonElement(deltaLine).jsonObject
        }
        bench("encode: turn/start params", 100_000) {
            encodeParams(
                TurnStartParams.serializer(),
                TurnStartParams(threadId = "t1", input = promptInput("hello")),
            )
        }

        benchTransport("transport: route to a live turn stream", 50_000)
        benchTransport("transport: route with no consumer", 50_000, registerTurn = false)

        // Throughput is nowhere near the bottleneck, so measure what a caller actually
        // waits on: process spawn and the handshake. Needs a real binary.
        if (System.getProperty("codexIntegration") == "true") {
            println()
            println("--- latency against the real binary (ms) ---")
            benchLatency()
        } else {
            println()
            println("(add -DcodexIntegration=true for real-binary startup latency)")
        }

        println()
    }

    /** Spawn + handshake + thread creation, which is the latency a caller sees at startup. */
    private fun benchLatency() {
        val tmp = System.getProperty("java.io.tmpdir")
        repeat(3) { attempt ->
            var spawnMs = 0.0
            var handshakeMs = 0.0
            var threadMs = 0.0
            runBlocking {
                val start = System.nanoTime()
                val codex = Codex(CodexConfig(cwd = tmp))
                spawnMs = (System.nanoTime() - start) / 1_000_000.0
                codex.use {
                    handshakeMs = millisSuspend { it.initialize() }
                    threadMs = millisSuspend { it.startThread(sandbox = Sandbox.READ_ONLY) }
                }
            }
            println(
                String.format(
                    "  run %d   spawn %.1f   handshake %.1f   thread/start %.1f   total %.1f",
                    attempt + 1, spawnMs, handshakeMs, threadMs, spawnMs + handshakeMs + threadMs,
                ),
            )
        }
    }

    private suspend fun millisSuspend(block: suspend () -> Unit): Double {
        val start = System.nanoTime()
        block()
        return (System.nanoTime() - start) / 1_000_000.0
    }

    private fun decodeOnce(line: String): CodexNotification {
        val message = CodexJson.parseToJsonElement(line).jsonObject
        val method = message["method"]!!.let { (it as kotlinx.serialization.json.JsonPrimitive).content }
        return NotificationCodec.decode(method, message["params"]!!.jsonObject)
    }

    private inline fun bench(name: String, iterations: Int, block: () -> Any?) {
        repeat(iterations / 4) { block() } // warm up JIT and let escape analysis settle
        val nanos = measureNanoTime { repeat(iterations) { block() } }
        report(name, iterations, nanos)
    }

    /**
     * Drives whole lines through the real reader loop, so this includes JSON parsing, typed
     * decode, the event tap and turn routing — everything a streamed token pays for.
     */
    private fun benchTransport(name: String, iterations: Int, registerTurn: Boolean = true) {
        val transport = BenchTransport(deltaLine, iterations)
        runBlocking(Dispatchers.Default) {
            AppServerClient(CodexConfig(requestTimeoutMillis = 5_000), transport).use { client ->
                val channel = if (registerTurn) {
                    client.registerTurn("019fb6c4-ac68-7f90-b990-7cbc808e5c93")
                } else {
                    null
                }

                val nanos = measureNanoTime {
                    withTimeout(120_000) {
                        if (channel != null) {
                            var seen = 0
                            for (event in channel) {
                                seen++
                                if (event is TurnCompletedNotification) break
                            }
                            check(seen >= iterations) { "consumed $seen of $iterations" }
                        } else {
                            // No turn registered and no tap subscriber: the reader still has to
                            // parse, decode and classify every line.
                            transport.awaitDrained()
                        }
                    }
                }
                report(name, iterations, nanos)
            }
        }
    }

    private fun report(name: String, iterations: Int, nanos: Long) {
        val perOp = nanos.toDouble() / iterations
        val opsPerSec = 1_000_000_000.0 / perOp
        println(String.format("%-42s %14s %12s", name, "%,.0f".format(opsPerSec), "%,.0f".format(perOp)))
    }
}

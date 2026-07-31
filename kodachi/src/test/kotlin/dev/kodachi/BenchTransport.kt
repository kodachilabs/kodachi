package dev.kodachi

import dev.kodachi.internal.Transport
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Replays a fixed number of identical lines, then one turn-completion, then EOF.
 *
 * Feeds the real reader loop from memory so a benchmark measures parsing, decoding and routing
 * without a child process in the way.
 */
internal class BenchTransport(
    private val line: String,
    private val count: Int,
) : Transport {

    private var emitted = 0
    private val drained = CountDownLatch(1)

    private val completion = """
        {"method":"turn/completed","params":{"threadId":"t1","turn":{"id":"019fb6c4-ac68-7f90-b990-7cbc808e5c93","items":[],"status":"completed"}}}
    """.trimIndent()

    override fun writeLine(line: String) {
        // Benchmarks never send anything.
    }

    override fun readLine(): String? = when {
        emitted < count -> {
            emitted++
            line
        }

        emitted == count -> {
            emitted++
            completion
        }

        else -> {
            drained.countDown()
            null
        }
    }

    /** Block until every line has been read and the stream closed. */
    fun awaitDrained() {
        check(drained.await(120, TimeUnit.SECONDS)) { "transport never drained" }
    }

    override fun stderrTail(): String = ""

    override fun close() {
        drained.countDown()
    }
}

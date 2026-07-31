package dev.kodex

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Retry exists for one narrow case: the server says it is overloaded. Anything else —
 * especially a dead transport, where the turn's state is unknown — must fail immediately.
 *
 * `runTest` skips the backoff delays, so these assert the policy rather than wall-clock time.
 */
class RetryTest {

    /** Deterministic jitter: always the midpoint of the range. */
    private val fixedRandom = object : Random() {
        override fun nextBits(bitCount: Int): Int = 0
        override fun nextDouble(): Double = 0.5
    }

    private fun overloaded(): CodexRpcException = CodexRpcException(
        code = -32000,
        message = "server overloaded",
        data = buildJsonObject { put("codexErrorInfo", "serverOverloaded") },
    )

    @Test
    fun `a transient overload is retried up to the attempt limit`() = runTest {
        var attempts = 0
        val error = assertFailsWith<CodexRpcException> {
            withRetry(RetryPolicy(maxAttempts = 3), random = fixedRandom) {
                attempts++
                throw overloaded()
            }
        }
        assertEquals(3, attempts, "expected exactly maxAttempts calls")
        assertTrue(error.message!!.contains("overloaded"))
    }

    @Test
    fun `a call that recovers returns without further attempts`() = runTest {
        var attempts = 0
        val result = withRetry(RetryPolicy(maxAttempts = 4), random = fixedRandom) {
            attempts++
            if (attempts < 3) throw overloaded()
            "ok"
        }
        assertEquals("ok", result)
        assertEquals(3, attempts)
    }

    @Test
    fun `a lost transport is never retried`() = runTest {
        // The process is gone and the turn's state is unknown; retrying could duplicate work.
        var attempts = 0
        assertFailsWith<TransportClosedException> {
            withRetry(RetryPolicy(maxAttempts = 5), random = fixedRandom) {
                attempts++
                throw TransportClosedException("app-server died")
            }
        }
        assertEquals(1, attempts)
    }

    @Test
    fun `a non-transient rpc error is never retried`() = runTest {
        var attempts = 0
        assertFailsWith<CodexRpcException> {
            withRetry(RetryPolicy(maxAttempts = 5), random = fixedRandom) {
                attempts++
                throw CodexRpcException(-32600, "thread has an active turn")
            }
        }
        assertEquals(1, attempts)
    }

    @Test
    fun `cancellation propagates instead of being retried`() = runTest {
        var attempts = 0
        assertFailsWith<CancellationException> {
            withRetry(RetryPolicy(maxAttempts = 5), random = fixedRandom) {
                attempts++
                throw CancellationException("caller went away")
            }
        }
        assertEquals(1, attempts, "cancellation must never be swallowed or retried")
    }

    @Test
    fun `backoff doubles and clamps to the ceiling`() = runTest {
        val policy = RetryPolicy(initialDelayMillis = 250, maxDelayMillis = 2_000, jitterRatio = 0.0)
        val schedule = (1..6).map { policy.delayMillisAfterAttempt(it, fixedRandom) }
        assertEquals(listOf(250L, 500L, 1_000L, 2_000L, 2_000L, 2_000L), schedule)
    }

    @Test
    fun `disabling retry means a single attempt`() = runTest {
        var attempts = 0
        assertFailsWith<CodexRpcException> {
            withRetry(RetryPolicy.NONE, random = fixedRandom) {
                attempts++
                throw overloaded()
            }
        }
        assertEquals(1, attempts)
    }

    @Test
    fun `overload classification accepts both wire spellings`() {
        // The schema's wire value is camelCase; the Python SDK matches snake_case. Accept both
        // rather than silently never retrying.
        assertTrue(isTransientOverload(overloaded()))
        assertTrue(
            isTransientOverload(
                CodexRpcException(-32000, "busy", buildJsonObject { put("codexErrorInfo", "server_overloaded") }),
            ),
        )
        assertFalse(isTransientOverload(CodexRpcException(-32600, "bad request")))
        assertFalse(isTransientOverload(TransportClosedException("gone")))
    }
}

/**
 * The JSON-RPC error taxonomy. Python exposes seven distinct classes so callers can branch on
 * why a call failed; these pin the code-to-type mapping and the two conditions that are only
 * discoverable from `data` or the message text.
 */
class RpcErrorMappingTest {

    private fun map(code: Int, message: String = "boom", data: kotlinx.serialization.json.JsonElement? = null) =
        mapRpcError(code, message, data)

    @Test
    fun `standard codes map to their own exception types`() {
        assertIs<ParseErrorException>(map(-32700))
        assertIs<InvalidRequestException>(map(-32600))
        assertIs<MethodNotFoundException>(map(-32601))
        assertIs<InvalidParamsException>(map(-32602))
        assertIs<InternalRpcException>(map(-32603))
    }

    @Test
    fun `an unknown code stays the generic rpc exception`() {
        val error = map(-1)
        assertEquals(CodexRpcException::class, error::class)
        assertEquals(-1, error.code)
        assertEquals("boom", error.serverMessage)
    }

    @Test
    fun `server-range overload becomes a busy error`() {
        // In the server-defined range the condition lives in `data`, not the code.
        val busy = map(
            -32000,
            "unavailable",
            buildJsonObject { put("codexErrorInfo", "serverOverloaded") },
        )
        assertIs<ServerBusyException>(busy)
        assertTrue(isTransientOverload(busy))

        // Same range, no overload marker: not retryable.
        val other = map(-32000, "something else")
        assertEquals(CodexRpcException::class, other::class)
        assertFalse(isTransientOverload(other))
    }

    @Test
    fun `overload is found however deeply it is nested`() {
        val nested = map(
            -32050,
            "unavailable",
            buildJsonObject {
                put("detail", buildJsonObject { put("errorInfo", "server_overloaded") })
            },
        )
        assertIs<ServerBusyException>(nested)
    }

    @Test
    fun `a retry-limit message maps to the more specific busy subclass`() {
        val exhausted = map(-32000, "Retry limit exceeded for this request")
        assertIs<RetryLimitExceededException>(exhausted)
        // Still transient family, so it remains a ServerBusyException.
        assertIs<ServerBusyException>(exhausted)

        assertIs<RetryLimitExceededException>(map(-32000, "too many failed attempts"))
    }

    @Test
    fun `a transport failure is never classed as transient`() {
        assertFalse(isTransientOverload(TransportClosedException("gone")))
    }
}

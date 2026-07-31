package dev.kodex

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.random.Random

/**
 * Backoff schedule for [withRetry].
 *
 * The defaults mirror the reference Python SDK's `retry_on_overload`: three attempts,
 * 250ms initial delay, doubling up to 2s, with plus-or-minus 20% jitter. Jitter matters
 * when several clients hit an overloaded server at once — without it they all come back
 * in lockstep and overload it again.
 *
 * @property maxAttempts total attempts including the first one; 1 disables retrying
 * @property initialDelayMillis wait after the first failure, before any jitter
 * @property maxDelayMillis ceiling the doubling stops at, before any jitter
 * @property jitterRatio fraction of the delay used as a symmetric random spread; 0 disables jitter
 */
public data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelayMillis: Long = 250,
    val maxDelayMillis: Long = 2_000,
    val jitterRatio: Double = 0.2,
) {
    init {
        require(maxAttempts >= 1) { "maxAttempts must be >= 1, was $maxAttempts" }
        require(initialDelayMillis >= 0) { "initialDelayMillis must be >= 0, was $initialDelayMillis" }
        require(maxDelayMillis >= 0) { "maxDelayMillis must be >= 0, was $maxDelayMillis" }
        require(jitterRatio >= 0.0) { "jitterRatio must be >= 0, was $jitterRatio" }
    }

    /**
     * How long to wait after [attempt] has failed, jitter included.
     *
     * Exposed so the schedule can be asserted on directly with a seeded [random], instead
     * of tests having to observe wall-clock delays.
     *
     * @param attempt 1-based number of the attempt that just failed
     */
    public fun delayMillisAfterAttempt(attempt: Int, random: Random = Random.Default): Long {
        require(attempt >= 1) { "attempt must be >= 1, was $attempt" }

        val ceiling = maxDelayMillis.toDouble()
        var base = min(ceiling, initialDelayMillis.toDouble())
        repeat(attempt - 1) { base = min(ceiling, base * 2) }

        val spread = base * jitterRatio
        val jittered = if (spread > 0.0) base + random.nextDouble(-spread, spread) else base
        return jittered.toLong().coerceAtLeast(0L)
    }

    public companion object {
        /** Retrying disabled: run once and let the first failure through. */
        public val NONE: RetryPolicy = RetryPolicy(maxAttempts = 1)
    }
}

/**
 * Run [block], retrying transient server overload under [policy].
 *
 * Only failures [retryIf] accepts are retried; everything else propagates on the first
 * attempt, as does the last failure once attempts run out. Cancellation is never retried
 * or swallowed.
 *
 * ```kotlin
 * val models = withRetry { codex.models() }
 * ```
 *
 * Wrap whole units of work, not resumptions of one: [block] runs again from the top, so a
 * turn that already emitted events and touched the workspace must not be retried this way
 * — the second run starts a *new* turn. Requests are the natural fit.
 *
 * @param random source of jitter; inject a seeded instance to make the schedule deterministic
 * @param retryIf classifies a failure as transient; defaults to [isTransientOverload]
 */
public suspend fun <T> withRetry(
    policy: RetryPolicy = RetryPolicy(),
    random: Random = Random.Default,
    retryIf: (Throwable) -> Boolean = ::isTransientOverload,
    block: suspend () -> T,
): T {
    var attempt = 0
    while (true) {
        attempt++
        try {
            return block()
        } catch (cancellation: CancellationException) {
            // Must come first: CancellationException is a Throwable, and treating a
            // cancelled scope as a retryable failure would resurrect the work.
            throw cancellation
        } catch (failure: Throwable) {
            if (attempt >= policy.maxAttempts || !retryIf(failure)) throw failure
            val wait = policy.delayMillisAfterAttempt(attempt, random)
            if (wait > 0) delay(wait)
        }
    }
}

/**
 * True when [failure] is a server-side overload worth waiting out.
 *
 * Deliberately narrow. Only a [CodexRpcException] qualifies, because only it proves the
 * server received the request and answered it, so a retry starts from a known state.
 * A [TransportClosedException] — the process died, the stream closed, or a request timed
 * out — never qualifies: the work may have half-happened and nobody can tell.
 *
 * Two shapes count, matching the reference Python SDK:
 *  - `serverOverloaded` (also seen as `server_overloaded`) anywhere in the error's `data`,
 *    which is where the app-server puts `codexErrorInfo`;
 *  - a server-range error code whose message or `data` says the server already exhausted
 *    its own retry budget for the operation.
 */
public fun isTransientOverload(failure: Throwable): Boolean = when (failure) {
    // The transport is gone: the turn's state is unknown, so retrying could duplicate work.
    is TransportClosedException -> false
    // The error taxonomy already decided this — see mapRpcError.
    is ServerBusyException -> true
    // A server-range error whose payload names overload anywhere inside it.
    is CodexRpcException -> failure.data.mentionsServerOverloaded()
    else -> false
}

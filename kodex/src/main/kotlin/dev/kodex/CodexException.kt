package dev.kodex

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** Base type for every failure raised by this SDK. */
public open class CodexException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

/**
 * The app-server returned a JSON-RPC error.
 *
 * Subclasses map the standard codes to distinct types, so a caller can react to *why* a call
 * failed instead of comparing integers:
 *
 * ```kotlin
 * try { codex.request("thread/read", params) }
 * catch (e: ServerBusyException) { … }      // transient, worth retrying
 * catch (e: InvalidParamsException) { … }   // our bug, retrying cannot help
 * ```
 *
 * [code] and [data] are passed through untouched, so the server's own taxonomy is still
 * reachable when the class hierarchy is not specific enough.
 */
public open class CodexRpcException(
    public val code: Int,
    message: String,
    public val data: JsonElement? = null,
) : CodexException("app-server error $code: $message") {

    /** The server's message on its own, without the code prefix. */
    public val serverMessage: String = message
}

/** `-32700` — the server could not parse what we sent. */
public class ParseErrorException(code: Int, message: String, data: JsonElement? = null) :
    CodexRpcException(code, message, data)

/** `-32600` — the request shape was rejected, e.g. a thread that already has an active turn. */
public class InvalidRequestException(code: Int, message: String, data: JsonElement? = null) :
    CodexRpcException(code, message, data)

/** `-32601` — this app-server version does not implement the method. */
public class MethodNotFoundException(code: Int, message: String, data: JsonElement? = null) :
    CodexRpcException(code, message, data)

/** `-32602` — the params were wrong; the same call will never succeed. */
public class InvalidParamsException(code: Int, message: String, data: JsonElement? = null) :
    CodexRpcException(code, message, data)

/** `-32603` — the server hit an internal failure. */
public class InternalRpcException(code: Int, message: String, data: JsonElement? = null) :
    CodexRpcException(code, message, data)

/**
 * The server is overloaded or unavailable.
 *
 * Transient, and the one condition worth retrying — see [withRetry].
 */
public open class ServerBusyException(code: Int, message: String, data: JsonElement? = null) :
    CodexRpcException(code, message, data)

/**
 * The server exhausted its own internal retry budget.
 *
 * Still a [ServerBusyException] — same transient family — but the condition has already been
 * retried upstream, so retrying from here is usually futile.
 */
public class RetryLimitExceededException(code: Int, message: String, data: JsonElement? = null) :
    ServerBusyException(code, message, data)

/**
 * The transport is gone: the process exited, stdout closed, or the client was closed
 * while a request was in flight. [stderrTail] carries the child's last stderr lines,
 * which is usually where the real cause is.
 */
public class TransportClosedException(
    message: String,
    public val stderrTail: String = "",
    cause: Throwable? = null,
) : CodexException(if (stderrTail.isBlank()) message else "$message\nstderr tail:\n$stderrTail", cause)

/**
 * A turn's event stream outgrew its buffer because nothing was consuming it fast enough.
 *
 * Normal streaming never approaches the limit — a model emits hundreds of events per second
 * against a buffer measured in thousands. Hitting this means the collector is blocked or was
 * never started. Raise [CodexConfig.turnEventBufferSize] only if you know the burst is real.
 */
public class TurnStreamOverflowException(
    public val turnId: String,
    public val bufferSize: Int,
) : CodexException(
    "Turn $turnId buffered $bufferSize events without being consumed. The stream was closed " +
        "to avoid exhausting the heap — collect TurnHandle.stream() promptly, or raise " +
        "CodexConfig.turnEventBufferSize.",
)

/** The `codex` executable could not be located. */
public class CodexBinaryNotFoundException(message: String) : CodexException(message)

/**
 * Map a raw JSON-RPC error onto the most specific exception type available.
 *
 * Mirrors the reference Python SDK's `map_jsonrpc_error`, including its awkward parts: the
 * server-defined range `-32099..-32000` reports overload inside `data` rather than in the code,
 * and a retry-limit failure is only identifiable from the message text.
 */
internal fun mapRpcError(code: Int, message: String, data: JsonElement?): CodexRpcException = when {
    code == -32700 -> ParseErrorException(code, message, data)
    code == -32600 -> InvalidRequestException(code, message, data)
    code == -32601 -> MethodNotFoundException(code, message, data)
    code == -32602 -> InvalidParamsException(code, message, data)
    code == -32603 -> InternalRpcException(code, message, data)

    code in -32099..-32000 -> when {
        message.mentionsRetryLimit() -> RetryLimitExceededException(code, message, data)
        data.mentionsServerOverloaded() -> ServerBusyException(code, message, data)
        else -> CodexRpcException(code, message, data)
    }

    else -> CodexRpcException(code, message, data)
}

private fun String.mentionsRetryLimit(): Boolean = lowercase().let {
    "retry limit" in it || "too many failed attempts" in it
}

/**
 * Whether an error payload reports server overload anywhere inside it.
 *
 * Searched recursively, and matched with underscores stripped so both the schema's camelCase
 * `serverOverloaded` and the snake_case `server_overloaded` the Python SDK looks for are
 * recognised — committing to one spelling would mean silently never detecting the condition.
 */
internal fun JsonElement?.mentionsServerOverloaded(): Boolean = when (this) {
    null -> false
    is JsonPrimitive -> contentOrNull?.lowercase()?.replace("_", "") == "serveroverloaded"
    is JsonObject -> values.any { it.mentionsServerOverloaded() }
    is JsonArray -> any { it.mentionsServerOverloaded() }
    else -> false
}

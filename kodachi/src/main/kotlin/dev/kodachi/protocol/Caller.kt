package dev.kodachi.protocol

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Issues a typed JSON-RPC request. Implemented by [dev.kodachi.Codex]; the generated API
 * classes depend on this rather than on the client, which keeps them testable in isolation.
 */
internal interface ProtocolCaller {
    suspend fun <R> call(
        method: String,
        params: JsonElement?,
        resultSerializer: DeserializationStrategy<R>,
    ): R
}

/**
 * Params serialization for generated API calls.
 *
 * Nulls and defaults are dropped: the app-server distinguishes an explicitly null
 * override from an absent one, so sending `"model": null` is not the same as omitting it.
 */
private val ParamsJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = false
}

internal fun <P> encodeParams(serializer: SerializationStrategy<P>, value: P): JsonObject =
    ParamsJson.encodeToJsonElement(serializer, value) as JsonObject

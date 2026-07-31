package dev.kodex.internal

import dev.kodex.protocol.CodexNotification
import dev.kodex.protocol.NotificationSerializers
import dev.kodex.protocol.UnknownNotification
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal object NotificationCodec {

    /**
     * Decode a notification payload into its typed form.
     *
     * Falls back to [UnknownNotification] both for unmapped methods and for payloads
     * that fail to decode. A schema change upstream must degrade to "untyped event",
     * never to a dropped event or a crashed reader loop.
     */
    fun decode(method: String, params: JsonObject): CodexNotification {
        NotificationSerializers[method]?.let { serializer ->
            runCatching { CodexJson.decodeFromJsonElement(serializer, params) }
                .getOrNull()
                ?.let { return it }
        }
        return UnknownNotification(
            method = method,
            params = params,
            threadId = params.stringOrNull("threadId"),
            turnId = params.turnIdOrNull(),
        )
    }

    private fun JsonObject.stringOrNull(key: String): String? =
        runCatching { this[key]?.jsonPrimitive?.contentOrNull }.getOrNull()

    /** Turn ids appear either flat as `turnId` or nested as `turn.id`, depending on the event. */
    private fun JsonObject.turnIdOrNull(): String? {
        stringOrNull("turnId")?.let { return it }
        return runCatching { this["turn"]?.jsonObject?.get("id")?.jsonPrimitive?.contentOrNull }.getOrNull()
    }
}

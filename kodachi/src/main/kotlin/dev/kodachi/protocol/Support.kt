package dev.kodachi.protocol

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull
import kotlin.reflect.KClass

/**
 * A server-initiated event.
 *
 * Every notification payload generated from the protocol schema implements this, so the
 * transport can route an event to the right turn without knowing its concrete type.
 * Methods this SDK version does not model arrive as [UnknownNotification].
 *
 * Consume these from [dev.kodachi.TurnHandle.stream] or [dev.kodachi.Codex.events].
 */
public interface CodexNotification {
    /** The JSON-RPC method that delivered this event, e.g. `item/completed`. */
    public val method: String

    /** Thread this event belongs to, when the event is thread-scoped. */
    public val threadId: String?

    /** Turn this event belongs to, when the event is turn-scoped. */
    public val turnId: String?
}

/**
 * A notification whose method this SDK version does not model, or one whose payload
 * failed to decode. [params] holds the raw wire payload.
 *
 * Routing still works: [threadId] and [turnId] are read from the payload, so even an
 * unmodelled turn-scoped event reaches the right turn's stream.
 */
public data class UnknownNotification(
    override val method: String,
    public val params: JsonObject,
    override val threadId: String? = null,
    override val turnId: String? = null,
) : CodexNotification

// ---------------------------------------------------------------------------
// Forward-compatible enums
// ---------------------------------------------------------------------------

/**
 * An enum with a stable wire representation.
 *
 * Generated enums carry an `UNKNOWN` entry and decode unrecognized values into it rather
 * than throwing, so a newer app-server adding a variant cannot break an older client.
 * Check for `UNKNOWN` if your logic must branch exhaustively.
 */
public interface WireEnum {
    /** The exact string this entry serializes to. */
    public val wire: String
}

internal open class WireEnumSerializer<T : Enum<T>>(
    name: String,
    private val fromWire: (String) -> T,
) : KSerializer<T> where T : WireEnum {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T = fromWire(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.wire)
    }
}

// ---------------------------------------------------------------------------
// Raw payload carriers
// ---------------------------------------------------------------------------

/** An object-shaped payload this SDK version does not model, kept verbatim. */
public interface RawPayload {
    public val raw: JsonObject
}

/** A value whose wire shape is a union of scalars and objects, kept verbatim. */
public interface RawValue {
    public val raw: JsonElement
}

internal open class RawPayloadSerializer<T : RawPayload>(
    private val create: (JsonObject) -> T,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): T =
        create((decoder as JsonDecoder).decodeJsonElement().jsonObject)

    override fun serialize(encoder: Encoder, value: T) {
        (encoder as JsonEncoder).encodeJsonElement(value.raw)
    }
}

internal open class RawValueSerializer<T : RawValue>(
    private val create: (JsonElement) -> T,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): T =
        create((decoder as JsonDecoder).decodeJsonElement())

    override fun serialize(encoder: Encoder, value: T) {
        (encoder as JsonEncoder).encodeJsonElement(value.raw)
    }
}

/**
 * Moves an externally tagged union between Kotlin and JSON, where the wire form is either a
 * bare string preset or a single-key object wrapping that variant's payload.
 *
 * The shape matters as much as the content, because these unions are sent as well as
 * received — an approval policy, an approval decision — and a server handed `{"never": {}}`
 * where it expects `"never"` rejects the request. So the generated union decides the exact
 * element for each variant and this only carries it through the format.
 *
 * Anything unrecognized (a newer preset, a newer object key, a shape that is neither) goes to
 * the union's `Unknown` carrier, which holds the element verbatim and encodes it back
 * unchanged rather than failing the decode or silently dropping it.
 */
internal open class MixedUnionSerializer<T : Any>(
    private val fromPreset: (String) -> T,
    private val fromKeyed: (Json, String, JsonElement) -> T?,
    private val fromUnknown: (JsonElement) -> T,
    private val toElement: (Json, T) -> JsonElement,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): T {
        val json = (decoder as JsonDecoder).json
        val element = decoder.decodeJsonElement()
        if (element is JsonPrimitive) {
            return if (element.isString) fromPreset(element.content) else fromUnknown(element)
        }
        val single = (element as? JsonObject)?.entries?.singleOrNull() ?: return fromUnknown(element)
        return fromKeyed(json, single.key, single.value) ?: fromUnknown(element)
    }

    override fun serialize(encoder: Encoder, value: T) {
        (encoder as JsonEncoder).encodeJsonElement(toElement(encoder.json, value))
    }
}

/**
 * Selects a union variant from its discriminator, falling back to the union's `Unknown…`
 * carrier so an unrecognized variant degrades instead of failing.
 *
 * The discriminator is usually `type`, but the schema also uses `kind` and `mode`, so the key
 * is passed in rather than assumed.
 */
internal open class TaggedUnionSerializer<T : Any>(
    baseClass: KClass<T>,
    private val tagKey: String,
    private val byTag: Map<String, KSerializer<out T>>,
    private val fallback: KSerializer<out T>,
) : JsonContentPolymorphicSerializer<T>(baseClass) {

    @Suppress("UNCHECKED_CAST")
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<T> {
        val tag = (element as? JsonObject)?.get(tagKey)?.let { (it as? JsonPrimitive)?.contentOrNull }
        return (byTag[tag] ?: fallback) as DeserializationStrategy<T>
    }
}

// ---------------------------------------------------------------------------
// Raw-object accessors used by generated Unknown… carriers
// ---------------------------------------------------------------------------

internal fun JsonObject.stringOrEmpty(key: String): String =
    (this[key] as? JsonPrimitive)?.contentOrNull ?: ""

internal fun JsonObject.numberOrZero(key: String): Long =
    (this[key] as? JsonPrimitive)?.longOrNull ?: 0L

internal fun JsonObject.booleanOrFalse(key: String): Boolean =
    (this[key] as? JsonPrimitive)?.booleanOrNull ?: false

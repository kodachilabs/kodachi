// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Generated-layer provenance.

@file:Suppress("unused", "RedundantVisibilityModifier", "LongParameterList", "MaxLineLength")
@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package dev.kodex.protocol

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Provenance of the generated protocol layer.
 *
 * Regenerate with `python3 scripts/generate_protocol.py`, and check for drift
 * against an installed binary with `--check` (see the README upgrade runbook).
 */
public object ProtocolInfo {
    /** Version of the `codex` binary whose schema produced these types. */
    public const val CODEX_VERSION: String = "0.146.0"

    /** Hash of the schema universe; changes whenever upstream changes shape. */
    public const val SCHEMA_FINGERPRINT: String = "sha256:3fa5bde67e8404a22b457fe9f43f13d2"

    /** Total schema definitions covered. */
    public const val DEFINITION_COUNT: Int = 608

    /** Server notifications this layer can route. */
    public const val NOTIFICATION_COUNT: Int = 70

    /** Client request methods this layer can call. */
    public const val CLIENT_REQUEST_COUNT: Int = 90

    /** Requests the server may send to the client. */
    public const val SERVER_REQUEST_COUNT: Int = 10
}

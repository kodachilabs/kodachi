// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Scalar and container aliases.

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
 * Backward-compatible API shape for ChatGPT workspace login restrictions.
 */
public typealias ForcedChatgptWorkspaceIds = JsonElement

public typealias McpElicitationEnumSchema = JsonElement

public typealias McpElicitationMultiSelectEnumSchema = JsonElement

public typealias McpElicitationPrimitiveSchema = JsonElement

public typealias McpElicitationSingleSelectEnumSchema = JsonElement

public typealias PathUri = String

/**
 * Contents returned when reading a resource from an MCP server.
 */
public typealias ResourceContent = JsonElement

public typealias ThreadId = String

public typealias v2 = JsonElement

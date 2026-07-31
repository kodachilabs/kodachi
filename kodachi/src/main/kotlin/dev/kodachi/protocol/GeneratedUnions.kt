// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Discriminated unions.

@file:Suppress("unused", "RedundantVisibilityModifier", "LongParameterList", "MaxLineLength")
@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package dev.kodachi.protocol

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

@Serializable(with = AccountSerializer::class)
public sealed interface Account {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class ApiKeyAccount(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "apiKey",
) : Account

@Serializable
public data class ChatgptAccount(
    val email: String?,
    val planType: PlanType,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgpt",
) : Account

@Serializable
public data class AmazonBedrockAccount(
    val usesCodexManagedCredentials: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "amazonBedrock",
) : Account

/**
 * A [Account] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownAccountSerializer::class)
public data class UnknownAccount(override val raw: JsonObject) : Account, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownAccountSerializer : RawPayloadSerializer<UnknownAccount>({ UnknownAccount(it) })

internal object AccountSerializer : TaggedUnionSerializer<Account>(Account::class, "type", mapOf(
    "apiKey" to ApiKeyAccount.serializer(),
    "chatgpt" to ChatgptAccount.serializer(),
    "amazonBedrock" to AmazonBedrockAccount.serializer(),
), UnknownAccountSerializer)

@Serializable(with = AgentMessageInputContentSerializer::class)
public sealed interface AgentMessageInputContent {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class InputTextAgentMessageInputContent(
    val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_text",
) : AgentMessageInputContent

@Serializable
public data class EncryptedContentAgentMessageInputContent(
    @SerialName("encrypted_content") val encryptedContent: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "encrypted_content",
) : AgentMessageInputContent

/**
 * A [AgentMessageInputContent] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownAgentMessageInputContentSerializer::class)
public data class UnknownAgentMessageInputContent(override val raw: JsonObject) : AgentMessageInputContent, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownAgentMessageInputContentSerializer : RawPayloadSerializer<UnknownAgentMessageInputContent>({ UnknownAgentMessageInputContent(it) })

internal object AgentMessageInputContentSerializer : TaggedUnionSerializer<AgentMessageInputContent>(AgentMessageInputContent::class, "type", mapOf(
    "input_text" to InputTextAgentMessageInputContent.serializer(),
    "encrypted_content" to EncryptedContentAgentMessageInputContent.serializer(),
), UnknownAgentMessageInputContentSerializer)

/**
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = AskForApprovalSerializer::class)
public sealed interface AskForApproval {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: AskForApprovalPreset) : AskForApproval

    /**
     * Wire form: `{"granular": …}`.
     */
    @Serializable
    public data class Granular(public val granular: AskForApprovalGranular) : AskForApproval

    /**
     * A [AskForApproval] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownAskForApprovalSerializer::class)
    public data class Unknown(override val raw: JsonElement) : AskForApproval, RawValue

    public companion object {
        /** The `untrusted` preset. */
        public val UNTRUSTED: AskForApproval = Preset(AskForApprovalPreset.UNTRUSTED)
        /** The `on-request` preset. */
        public val ON_REQUEST: AskForApproval = Preset(AskForApprovalPreset.ON_REQUEST)
        /** The `never` preset. */
        public val NEVER: AskForApproval = Preset(AskForApprovalPreset.NEVER)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [AskForApprovalPreset.UNKNOWN] and encoding as empty.
         */
        public fun of(preset: String): AskForApproval {
            val known = AskForApprovalPreset.fromWire(preset)
            return if (known == AskForApprovalPreset.UNKNOWN) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [AskForApproval].
 *
 * Split out so [AskForApproval.Preset] can be matched exhaustively while the union's object
 * variants keep their payloads.
 */
@Serializable(with = AskForApprovalPresetSerializer::class)
public enum class AskForApprovalPreset(override val wire: String) : WireEnum {
    UNTRUSTED("untrusted"),
    ON_REQUEST("on-request"),
    NEVER("never"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AskForApprovalPreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AskForApprovalPreset = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AskForApprovalPresetSerializer : WireEnumSerializer<AskForApprovalPreset>("AskForApprovalPreset", AskForApprovalPreset::fromWire)

internal object UnknownAskForApprovalSerializer : RawValueSerializer<AskForApproval.Unknown>({ AskForApproval.Unknown(it) })

internal object AskForApprovalSerializer : MixedUnionSerializer<AskForApproval>(
    fromPreset = { AskForApproval.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "granular" -> AskForApproval.Granular(
                json.decodeFromJsonElement<AskForApprovalGranular>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { AskForApproval.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is AskForApproval.Preset -> JsonPrimitive(value.value.wire)
            is AskForApproval.Granular -> buildJsonObject {
                put("granular", json.encodeToJsonElement(value.granular))
            }
            is AskForApproval.Unknown -> value.raw
        }
    },
)

/**
 * Location used to resolve a selected capability root.
 */
@Serializable(with = CapabilityRootLocationSerializer::class)
public sealed interface CapabilityRootLocation {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val environmentId: String
    public val path: String
}

/**
 * A path owned by an execution environment.
 */
@Serializable
public data class EnvironmentCapabilityRootLocation(
    override val environmentId: String,
    override val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "environment",
) : CapabilityRootLocation

/**
 * A [CapabilityRootLocation] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownCapabilityRootLocationSerializer::class)
public data class UnknownCapabilityRootLocation(override val raw: JsonObject) : CapabilityRootLocation, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val environmentId: String get() = raw.stringOrEmpty("environmentId")
    override val path: String get() = raw.stringOrEmpty("path")
}

internal object UnknownCapabilityRootLocationSerializer : RawPayloadSerializer<UnknownCapabilityRootLocation>({ UnknownCapabilityRootLocation(it) })

internal object CapabilityRootLocationSerializer : TaggedUnionSerializer<CapabilityRootLocation>(CapabilityRootLocation::class, "type", mapOf(
    "environment" to EnvironmentCapabilityRootLocation.serializer(),
), UnknownCapabilityRootLocationSerializer)

/**
 * This translation layer make sure that we expose codex error code in camel case.
 *
 * When an upstream HTTP status is available (for example, from the Responses API or a
 * provider), it is forwarded in `httpStatusCode` on the relevant `codexErrorInfo` variant.
 *
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = CodexErrorInfoSerializer::class)
public sealed interface CodexErrorInfo {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: CodexErrorInfoPreset) : CodexErrorInfo

    /**
     * Wire form: `{"httpConnectionFailed": …}`.
     */
    @Serializable
    public data class HttpConnectionFailed(public val httpConnectionFailed: CodexErrorInfoHttpConnectionFailed) : CodexErrorInfo

    /**
     * Failed to connect to the response SSE stream.
     *
     * Wire form: `{"responseStreamConnectionFailed": …}`.
     */
    @Serializable
    public data class ResponseStreamConnectionFailed(public val responseStreamConnectionFailed: CodexErrorInfoResponseStreamConnectionFailed) : CodexErrorInfo

    /**
     * The response SSE stream disconnected in the middle of a turn before completion.
     *
     * Wire form: `{"responseStreamDisconnected": …}`.
     */
    @Serializable
    public data class ResponseStreamDisconnected(public val responseStreamDisconnected: CodexErrorInfoResponseStreamDisconnected) : CodexErrorInfo

    /**
     * Reached the retry limit for responses.
     *
     * Wire form: `{"responseTooManyFailedAttempts": …}`.
     */
    @Serializable
    public data class ResponseTooManyFailedAttempts(public val responseTooManyFailedAttempts: CodexErrorInfoResponseTooManyFailedAttempts) : CodexErrorInfo

    /**
     * Returned when `turn/start` or `turn/steer` is submitted while the current active turn cannot
     * accept same-turn steering, for example `/review` or manual `/compact`.
     *
     * Wire form: `{"activeTurnNotSteerable": …}`.
     */
    @Serializable
    public data class ActiveTurnNotSteerable(public val activeTurnNotSteerable: CodexErrorInfoActiveTurnNotSteerable) : CodexErrorInfo

    /**
     * A [CodexErrorInfo] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownCodexErrorInfoSerializer::class)
    public data class Unknown(override val raw: JsonElement) : CodexErrorInfo, RawValue

    public companion object {
        /** The `contextWindowExceeded` preset. */
        public val CONTEXT_WINDOW_EXCEEDED: CodexErrorInfo = Preset(CodexErrorInfoPreset.CONTEXT_WINDOW_EXCEEDED)
        /** The `sessionBudgetExceeded` preset. */
        public val SESSION_BUDGET_EXCEEDED: CodexErrorInfo = Preset(CodexErrorInfoPreset.SESSION_BUDGET_EXCEEDED)
        /** The `usageLimitExceeded` preset. */
        public val USAGE_LIMIT_EXCEEDED: CodexErrorInfo = Preset(CodexErrorInfoPreset.USAGE_LIMIT_EXCEEDED)
        /** The `serverOverloaded` preset. */
        public val SERVER_OVERLOADED: CodexErrorInfo = Preset(CodexErrorInfoPreset.SERVER_OVERLOADED)
        /** The `cyberPolicy` preset. */
        public val CYBER_POLICY: CodexErrorInfo = Preset(CodexErrorInfoPreset.CYBER_POLICY)
        /** The `internalServerError` preset. */
        public val INTERNAL_SERVER_ERROR: CodexErrorInfo = Preset(CodexErrorInfoPreset.INTERNAL_SERVER_ERROR)
        /** The `unauthorized` preset. */
        public val UNAUTHORIZED: CodexErrorInfo = Preset(CodexErrorInfoPreset.UNAUTHORIZED)
        /** The `badRequest` preset. */
        public val BAD_REQUEST: CodexErrorInfo = Preset(CodexErrorInfoPreset.BAD_REQUEST)
        /** The `threadRollbackFailed` preset. */
        public val THREAD_ROLLBACK_FAILED: CodexErrorInfo = Preset(CodexErrorInfoPreset.THREAD_ROLLBACK_FAILED)
        /** The `sandboxError` preset. */
        public val SANDBOX_ERROR: CodexErrorInfo = Preset(CodexErrorInfoPreset.SANDBOX_ERROR)
        /** The `other` preset. */
        public val OTHER: CodexErrorInfo = Preset(CodexErrorInfoPreset.OTHER)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [CodexErrorInfoPreset.UNKNOWN] and encoding as empty.
         */
        public fun of(preset: String): CodexErrorInfo {
            val known = CodexErrorInfoPreset.fromWire(preset)
            return if (known == CodexErrorInfoPreset.UNKNOWN) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [CodexErrorInfo].
 *
 * Split out so [CodexErrorInfo.Preset] can be matched exhaustively while the union's object
 * variants keep their payloads.
 */
@Serializable(with = CodexErrorInfoPresetSerializer::class)
public enum class CodexErrorInfoPreset(override val wire: String) : WireEnum {
    CONTEXT_WINDOW_EXCEEDED("contextWindowExceeded"),
    SESSION_BUDGET_EXCEEDED("sessionBudgetExceeded"),
    USAGE_LIMIT_EXCEEDED("usageLimitExceeded"),
    SERVER_OVERLOADED("serverOverloaded"),
    CYBER_POLICY("cyberPolicy"),
    INTERNAL_SERVER_ERROR("internalServerError"),
    UNAUTHORIZED("unauthorized"),
    BAD_REQUEST("badRequest"),
    THREAD_ROLLBACK_FAILED("threadRollbackFailed"),
    SANDBOX_ERROR("sandboxError"),
    OTHER("other"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CodexErrorInfoPreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CodexErrorInfoPreset = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CodexErrorInfoPresetSerializer : WireEnumSerializer<CodexErrorInfoPreset>("CodexErrorInfoPreset", CodexErrorInfoPreset::fromWire)

internal object UnknownCodexErrorInfoSerializer : RawValueSerializer<CodexErrorInfo.Unknown>({ CodexErrorInfo.Unknown(it) })

internal object CodexErrorInfoSerializer : MixedUnionSerializer<CodexErrorInfo>(
    fromPreset = { CodexErrorInfo.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "httpConnectionFailed" -> CodexErrorInfo.HttpConnectionFailed(
                json.decodeFromJsonElement<CodexErrorInfoHttpConnectionFailed>(payload),
            )
            "responseStreamConnectionFailed" -> CodexErrorInfo.ResponseStreamConnectionFailed(
                json.decodeFromJsonElement<CodexErrorInfoResponseStreamConnectionFailed>(payload),
            )
            "responseStreamDisconnected" -> CodexErrorInfo.ResponseStreamDisconnected(
                json.decodeFromJsonElement<CodexErrorInfoResponseStreamDisconnected>(payload),
            )
            "responseTooManyFailedAttempts" -> CodexErrorInfo.ResponseTooManyFailedAttempts(
                json.decodeFromJsonElement<CodexErrorInfoResponseTooManyFailedAttempts>(payload),
            )
            "activeTurnNotSteerable" -> CodexErrorInfo.ActiveTurnNotSteerable(
                json.decodeFromJsonElement<CodexErrorInfoActiveTurnNotSteerable>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { CodexErrorInfo.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is CodexErrorInfo.Preset -> JsonPrimitive(value.value.wire)
            is CodexErrorInfo.HttpConnectionFailed -> buildJsonObject {
                put("httpConnectionFailed", json.encodeToJsonElement(value.httpConnectionFailed))
            }
            is CodexErrorInfo.ResponseStreamConnectionFailed -> buildJsonObject {
                put("responseStreamConnectionFailed", json.encodeToJsonElement(value.responseStreamConnectionFailed))
            }
            is CodexErrorInfo.ResponseStreamDisconnected -> buildJsonObject {
                put("responseStreamDisconnected", json.encodeToJsonElement(value.responseStreamDisconnected))
            }
            is CodexErrorInfo.ResponseTooManyFailedAttempts -> buildJsonObject {
                put("responseTooManyFailedAttempts", json.encodeToJsonElement(value.responseTooManyFailedAttempts))
            }
            is CodexErrorInfo.ActiveTurnNotSteerable -> buildJsonObject {
                put("activeTurnNotSteerable", json.encodeToJsonElement(value.activeTurnNotSteerable))
            }
            is CodexErrorInfo.Unknown -> value.raw
        }
    },
)

@Serializable(with = CommandActionSerializer::class)
public sealed interface CommandAction {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val command: String
}

@Serializable
public data class ReadCommandAction(
    override val command: String,
    val name: String,
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "read",
) : CommandAction

@Serializable
public data class ListFilesCommandAction(
    override val command: String,
    val path: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "listFiles",
) : CommandAction

@Serializable
public data class SearchCommandAction(
    override val command: String,
    val path: String? = null,
    val query: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "search",
) : CommandAction

@Serializable
public data class UnknownCommandAction(
    override val command: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "unknown",
) : CommandAction

/**
 * A [CommandAction] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnrecognizedCommandActionSerializer::class)
public data class UnrecognizedCommandAction(override val raw: JsonObject) : CommandAction, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val command: String get() = raw.stringOrEmpty("command")
}

internal object UnrecognizedCommandActionSerializer : RawPayloadSerializer<UnrecognizedCommandAction>({ UnrecognizedCommandAction(it) })

internal object CommandActionSerializer : TaggedUnionSerializer<CommandAction>(CommandAction::class, "type", mapOf(
    "read" to ReadCommandAction.serializer(),
    "listFiles" to ListFilesCommandAction.serializer(),
    "search" to SearchCommandAction.serializer(),
    "unknown" to UnknownCommandAction.serializer(),
), UnrecognizedCommandActionSerializer)

/**
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = CommandExecutionApprovalDecisionSerializer::class)
public sealed interface CommandExecutionApprovalDecision {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: CommandExecutionApprovalDecisionPreset) : CommandExecutionApprovalDecision

    /**
     * User approved the command, and wants to apply the proposed execpolicy amendment so future
     * matching commands can run without prompting.
     *
     * Wire form: `{"acceptWithExecpolicyAmendment": …}`.
     */
    @Serializable
    public data class AcceptWithExecpolicyAmendment(public val acceptWithExecpolicyAmendment: CommandExecutionApprovalDecisionAcceptWithExecpolicyAmendment) : CommandExecutionApprovalDecision

    /**
     * User chose a persistent network policy rule (allow/deny) for this host.
     *
     * Wire form: `{"applyNetworkPolicyAmendment": …}`.
     */
    @Serializable
    public data class ApplyNetworkPolicyAmendment(public val applyNetworkPolicyAmendment: CommandExecutionApprovalDecisionApplyNetworkPolicyAmendment) : CommandExecutionApprovalDecision

    /**
     * A [CommandExecutionApprovalDecision] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownCommandExecutionApprovalDecisionSerializer::class)
    public data class Unknown(override val raw: JsonElement) : CommandExecutionApprovalDecision, RawValue

    public companion object {
        /** The `accept` preset. */
        public val ACCEPT: CommandExecutionApprovalDecision = Preset(CommandExecutionApprovalDecisionPreset.ACCEPT)
        /** The `acceptForSession` preset. */
        public val ACCEPT_FOR_SESSION: CommandExecutionApprovalDecision = Preset(CommandExecutionApprovalDecisionPreset.ACCEPT_FOR_SESSION)
        /** The `decline` preset. */
        public val DECLINE: CommandExecutionApprovalDecision = Preset(CommandExecutionApprovalDecisionPreset.DECLINE)
        /** The `cancel` preset. */
        public val CANCEL: CommandExecutionApprovalDecision = Preset(CommandExecutionApprovalDecisionPreset.CANCEL)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [CommandExecutionApprovalDecisionPreset.UNKNOWN] and encoding as empty.
         */
        public fun of(preset: String): CommandExecutionApprovalDecision {
            val known = CommandExecutionApprovalDecisionPreset.fromWire(preset)
            return if (known == CommandExecutionApprovalDecisionPreset.UNKNOWN) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [CommandExecutionApprovalDecision].
 *
 * Split out so [CommandExecutionApprovalDecision.Preset] can be matched exhaustively while the
 * union's object variants keep their payloads.
 */
@Serializable(with = CommandExecutionApprovalDecisionPresetSerializer::class)
public enum class CommandExecutionApprovalDecisionPreset(override val wire: String) : WireEnum {
    ACCEPT("accept"),
    ACCEPT_FOR_SESSION("acceptForSession"),
    DECLINE("decline"),
    CANCEL("cancel"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CommandExecutionApprovalDecisionPreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CommandExecutionApprovalDecisionPreset = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CommandExecutionApprovalDecisionPresetSerializer : WireEnumSerializer<CommandExecutionApprovalDecisionPreset>("CommandExecutionApprovalDecisionPreset", CommandExecutionApprovalDecisionPreset::fromWire)

internal object UnknownCommandExecutionApprovalDecisionSerializer : RawValueSerializer<CommandExecutionApprovalDecision.Unknown>({ CommandExecutionApprovalDecision.Unknown(it) })

internal object CommandExecutionApprovalDecisionSerializer : MixedUnionSerializer<CommandExecutionApprovalDecision>(
    fromPreset = { CommandExecutionApprovalDecision.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "acceptWithExecpolicyAmendment" -> CommandExecutionApprovalDecision.AcceptWithExecpolicyAmendment(
                json.decodeFromJsonElement<CommandExecutionApprovalDecisionAcceptWithExecpolicyAmendment>(payload),
            )
            "applyNetworkPolicyAmendment" -> CommandExecutionApprovalDecision.ApplyNetworkPolicyAmendment(
                json.decodeFromJsonElement<CommandExecutionApprovalDecisionApplyNetworkPolicyAmendment>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { CommandExecutionApprovalDecision.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is CommandExecutionApprovalDecision.Preset -> JsonPrimitive(value.value.wire)
            is CommandExecutionApprovalDecision.AcceptWithExecpolicyAmendment -> buildJsonObject {
                put("acceptWithExecpolicyAmendment", json.encodeToJsonElement(value.acceptWithExecpolicyAmendment))
            }
            is CommandExecutionApprovalDecision.ApplyNetworkPolicyAmendment -> buildJsonObject {
                put("applyNetworkPolicyAmendment", json.encodeToJsonElement(value.applyNetworkPolicyAmendment))
            }
            is CommandExecutionApprovalDecision.Unknown -> value.raw
        }
    },
)

@Serializable(with = ConfigLayerSourceSerializer::class)
public sealed interface ConfigLayerSource {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

/**
 * Managed preferences layer delivered by MDM (macOS only).
 */
@Serializable
public data class MdmConfigLayerSource(
    val domain: String,
    val key: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "mdm",
) : ConfigLayerSource

/**
 * Managed config layer from a file (usually `managed_config.toml`).
 */
@Serializable
public data class SystemConfigLayerSource(
    val file: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "system",
) : ConfigLayerSource

/**
 * Enterprise-managed config layer delivered by the cloud config bundle.
 */
@Serializable
public data class EnterpriseManagedConfigLayerSource(
    val id: String,
    val name: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "enterpriseManaged",
) : ConfigLayerSource

/**
 * User config layer from $CODEX_HOME/config.toml. This layer is special in that it is expected
 * to be: - writable by the user - generally outside the workspace directory
 */
@Serializable
public data class UserConfigLayerSource(
    val file: String,
    val profile: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "user",
) : ConfigLayerSource

/**
 * Path to a .codex/ folder within a project. There could be multiple of these between `cwd`
 * and the project/repo root.
 */
@Serializable
public data class ProjectConfigLayerSource(
    val dotCodexFolder: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "project",
) : ConfigLayerSource

/**
 * Session-layer overrides supplied via `-c`/`--config`.
 */
@Serializable
public data class SessionFlagsConfigLayerSource(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "sessionFlags",
) : ConfigLayerSource

/**
 * `managed_config.toml` was designed to be a config that was loaded as the last layer on top
 * of everything else. This scheme did not quite work out as intended, but we keep this variant
 * as a "best effort" while we phase out `managed_config.toml` in favor of `requirements.toml`.
 */
@Serializable
public data class LegacyManagedConfigTomlFromFileConfigLayerSource(
    val file: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "legacyManagedConfigTomlFromFile",
) : ConfigLayerSource

@Serializable
public data class LegacyManagedConfigTomlFromMdmConfigLayerSource(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "legacyManagedConfigTomlFromMdm",
) : ConfigLayerSource

/**
 * A [ConfigLayerSource] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownConfigLayerSourceSerializer::class)
public data class UnknownConfigLayerSource(override val raw: JsonObject) : ConfigLayerSource, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownConfigLayerSourceSerializer : RawPayloadSerializer<UnknownConfigLayerSource>({ UnknownConfigLayerSource(it) })

internal object ConfigLayerSourceSerializer : TaggedUnionSerializer<ConfigLayerSource>(ConfigLayerSource::class, "type", mapOf(
    "mdm" to MdmConfigLayerSource.serializer(),
    "system" to SystemConfigLayerSource.serializer(),
    "enterpriseManaged" to EnterpriseManagedConfigLayerSource.serializer(),
    "user" to UserConfigLayerSource.serializer(),
    "project" to ProjectConfigLayerSource.serializer(),
    "sessionFlags" to SessionFlagsConfigLayerSource.serializer(),
    "legacyManagedConfigTomlFromFile" to LegacyManagedConfigTomlFromFileConfigLayerSource.serializer(),
    "legacyManagedConfigTomlFromMdm" to LegacyManagedConfigTomlFromMdmConfigLayerSource.serializer(),
), UnknownConfigLayerSourceSerializer)

@Serializable(with = ConfiguredHookHandlerSerializer::class)
public sealed interface ConfiguredHookHandler {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class CommandConfiguredHookHandler(
    val `async`: Boolean,
    val command: String,
    val additionalContextLimit: Long? = null,
    val commandWindows: String? = null,
    val statusMessage: String? = null,
    val timeoutSec: Long? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "command",
) : ConfiguredHookHandler

@Serializable
public data class PromptConfiguredHookHandler(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "prompt",
) : ConfiguredHookHandler

@Serializable
public data class AgentConfiguredHookHandler(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "agent",
) : ConfiguredHookHandler

/**
 * A [ConfiguredHookHandler] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownConfiguredHookHandlerSerializer::class)
public data class UnknownConfiguredHookHandler(override val raw: JsonObject) : ConfiguredHookHandler, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownConfiguredHookHandlerSerializer : RawPayloadSerializer<UnknownConfiguredHookHandler>({ UnknownConfiguredHookHandler(it) })

internal object ConfiguredHookHandlerSerializer : TaggedUnionSerializer<ConfiguredHookHandler>(ConfiguredHookHandler::class, "type", mapOf(
    "command" to CommandConfiguredHookHandler.serializer(),
    "prompt" to PromptConfiguredHookHandler.serializer(),
    "agent" to AgentConfiguredHookHandler.serializer(),
), UnknownConfiguredHookHandlerSerializer)

@Serializable(with = ContentItemSerializer::class)
public sealed interface ContentItem {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class InputTextContentItem(
    val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_text",
) : ContentItem

@Serializable
public data class InputImageContentItem(
    @SerialName("image_url") val imageUrl: String,
    val detail: ImageDetail? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_image",
) : ContentItem

@Serializable
public data class InputAudioContentItem(
    @SerialName("audio_url") val audioUrl: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_audio",
) : ContentItem

@Serializable
public data class OutputTextContentItem(
    val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "output_text",
) : ContentItem

/**
 * A [ContentItem] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownContentItemSerializer::class)
public data class UnknownContentItem(override val raw: JsonObject) : ContentItem, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownContentItemSerializer : RawPayloadSerializer<UnknownContentItem>({ UnknownContentItem(it) })

internal object ContentItemSerializer : TaggedUnionSerializer<ContentItem>(ContentItem::class, "type", mapOf(
    "input_text" to InputTextContentItem.serializer(),
    "input_image" to InputImageContentItem.serializer(),
    "input_audio" to InputAudioContentItem.serializer(),
    "output_text" to OutputTextContentItem.serializer(),
), UnknownContentItemSerializer)

@Serializable(with = DynamicToolCallOutputContentItemSerializer::class)
public sealed interface DynamicToolCallOutputContentItem {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class InputTextDynamicToolCallOutputContentItem(
    val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "inputText",
) : DynamicToolCallOutputContentItem

@Serializable
public data class InputImageDynamicToolCallOutputContentItem(
    val imageUrl: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "inputImage",
) : DynamicToolCallOutputContentItem

@Serializable
public data class InputAudioDynamicToolCallOutputContentItem(
    val audioUrl: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "inputAudio",
) : DynamicToolCallOutputContentItem

/**
 * A [DynamicToolCallOutputContentItem] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownDynamicToolCallOutputContentItemSerializer::class)
public data class UnknownDynamicToolCallOutputContentItem(override val raw: JsonObject) : DynamicToolCallOutputContentItem, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownDynamicToolCallOutputContentItemSerializer : RawPayloadSerializer<UnknownDynamicToolCallOutputContentItem>({ UnknownDynamicToolCallOutputContentItem(it) })

internal object DynamicToolCallOutputContentItemSerializer : TaggedUnionSerializer<DynamicToolCallOutputContentItem>(DynamicToolCallOutputContentItem::class, "type", mapOf(
    "inputText" to InputTextDynamicToolCallOutputContentItem.serializer(),
    "inputImage" to InputImageDynamicToolCallOutputContentItem.serializer(),
    "inputAudio" to InputAudioDynamicToolCallOutputContentItem.serializer(),
), UnknownDynamicToolCallOutputContentItemSerializer)

@Serializable(with = DynamicToolNamespaceToolSerializer::class)
public sealed interface DynamicToolNamespaceTool {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val description: String
    public val name: String
}

@Serializable
public data class FunctionDynamicToolNamespaceTool(
    override val description: String,
    val inputSchema: JsonElement,
    override val name: String,
    val deferLoading: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "function",
) : DynamicToolNamespaceTool

/**
 * A [DynamicToolNamespaceTool] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownDynamicToolNamespaceToolSerializer::class)
public data class UnknownDynamicToolNamespaceTool(override val raw: JsonObject) : DynamicToolNamespaceTool, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val description: String get() = raw.stringOrEmpty("description")
    override val name: String get() = raw.stringOrEmpty("name")
}

internal object UnknownDynamicToolNamespaceToolSerializer : RawPayloadSerializer<UnknownDynamicToolNamespaceTool>({ UnknownDynamicToolNamespaceTool(it) })

internal object DynamicToolNamespaceToolSerializer : TaggedUnionSerializer<DynamicToolNamespaceTool>(DynamicToolNamespaceTool::class, "type", mapOf(
    "function" to FunctionDynamicToolNamespaceTool.serializer(),
), UnknownDynamicToolNamespaceToolSerializer)

@Serializable(with = DynamicToolSpecSerializer::class)
public sealed interface DynamicToolSpec {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val description: String
    public val name: String
}

@Serializable
public data class FunctionDynamicToolSpec(
    override val description: String,
    val inputSchema: JsonElement,
    override val name: String,
    val deferLoading: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "function",
) : DynamicToolSpec

@Serializable
public data class NamespaceDynamicToolSpec(
    override val description: String,
    override val name: String,
    val tools: List<DynamicToolNamespaceTool>,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "namespace",
) : DynamicToolSpec

/**
 * A [DynamicToolSpec] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownDynamicToolSpecSerializer::class)
public data class UnknownDynamicToolSpec(override val raw: JsonObject) : DynamicToolSpec, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val description: String get() = raw.stringOrEmpty("description")
    override val name: String get() = raw.stringOrEmpty("name")
}

internal object UnknownDynamicToolSpecSerializer : RawPayloadSerializer<UnknownDynamicToolSpec>({ UnknownDynamicToolSpec(it) })

internal object DynamicToolSpecSerializer : TaggedUnionSerializer<DynamicToolSpec>(DynamicToolSpec::class, "type", mapOf(
    "function" to FunctionDynamicToolSpec.serializer(),
    "namespace" to NamespaceDynamicToolSpec.serializer(),
), UnknownDynamicToolSpecSerializer)

@Serializable(with = FileChangeSerializer::class)
public sealed interface FileChange {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class AddFileChange(
    val content: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "add",
) : FileChange

@Serializable
public data class DeleteFileChange(
    val content: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "delete",
) : FileChange

@Serializable
public data class UpdateFileChange(
    @SerialName("unified_diff") val unifiedDiff: String,
    @SerialName("move_path") val movePath: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "update",
) : FileChange

/**
 * A [FileChange] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownFileChangeSerializer::class)
public data class UnknownFileChange(override val raw: JsonObject) : FileChange, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownFileChangeSerializer : RawPayloadSerializer<UnknownFileChange>({ UnknownFileChange(it) })

internal object FileChangeSerializer : TaggedUnionSerializer<FileChange>(FileChange::class, "type", mapOf(
    "add" to AddFileChange.serializer(),
    "delete" to DeleteFileChange.serializer(),
    "update" to UpdateFileChange.serializer(),
), UnknownFileChangeSerializer)

@Serializable(with = FileSystemPathSerializer::class)
public sealed interface FileSystemPath {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class PathFileSystemPath(
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "path",
) : FileSystemPath

@Serializable
public data class GlobPatternFileSystemPath(
    val pattern: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "glob_pattern",
) : FileSystemPath

@Serializable
public data class SpecialFileSystemPath(
    val value: FileSystemSpecialPath,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "special",
) : FileSystemPath

/**
 * A [FileSystemPath] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownFileSystemPathSerializer::class)
public data class UnknownFileSystemPath(override val raw: JsonObject) : FileSystemPath, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownFileSystemPathSerializer : RawPayloadSerializer<UnknownFileSystemPath>({ UnknownFileSystemPath(it) })

internal object FileSystemPathSerializer : TaggedUnionSerializer<FileSystemPath>(FileSystemPath::class, "type", mapOf(
    "path" to PathFileSystemPath.serializer(),
    "glob_pattern" to GlobPatternFileSystemPath.serializer(),
    "special" to SpecialFileSystemPath.serializer(),
), UnknownFileSystemPathSerializer)

@Serializable(with = FileSystemSpecialPathSerializer::class)
public sealed interface FileSystemSpecialPath {
    /** Wire discriminator for this variant (`kind`). */
    public val kind: String
}

@Serializable
public data class RootFileSystemSpecialPath(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val kind: String = "root",
) : FileSystemSpecialPath

@Serializable
public data class MinimalFileSystemSpecialPath(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val kind: String = "minimal",
) : FileSystemSpecialPath

@Serializable
public data class KindFileSystemSpecialPath(
    val subpath: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val kind: String = "project_roots",
) : FileSystemSpecialPath

@Serializable
public data class TmpdirFileSystemSpecialPath(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val kind: String = "tmpdir",
) : FileSystemSpecialPath

@Serializable
public data class SlashTmpFileSystemSpecialPath(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val kind: String = "slash_tmp",
) : FileSystemSpecialPath

@Serializable
public data class FileSystemSpecialPathUnknown(
    val path: String,
    val subpath: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val kind: String = "unknown",
) : FileSystemSpecialPath

/**
 * A [FileSystemSpecialPath] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownFileSystemSpecialPathSerializer::class)
public data class UnknownFileSystemSpecialPath(override val raw: JsonObject) : FileSystemSpecialPath, RawPayload {
    override val kind: String get() = raw.stringOrEmpty("kind")
}

internal object UnknownFileSystemSpecialPathSerializer : RawPayloadSerializer<UnknownFileSystemSpecialPath>({ UnknownFileSystemSpecialPath(it) })

internal object FileSystemSpecialPathSerializer : TaggedUnionSerializer<FileSystemSpecialPath>(FileSystemSpecialPath::class, "kind", mapOf(
    "root" to RootFileSystemSpecialPath.serializer(),
    "minimal" to MinimalFileSystemSpecialPath.serializer(),
    "project_roots" to KindFileSystemSpecialPath.serializer(),
    "tmpdir" to TmpdirFileSystemSpecialPath.serializer(),
    "slash_tmp" to SlashTmpFileSystemSpecialPath.serializer(),
    "unknown" to FileSystemSpecialPathUnknown.serializer(),
), UnknownFileSystemSpecialPathSerializer)

/**
 * Responses API compatible content items that can be returned by a tool call. This is a subset
 * of ContentItem with the types we support as function call outputs.
 */
@Serializable(with = FunctionCallOutputContentItemSerializer::class)
public sealed interface FunctionCallOutputContentItem {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class InputTextFunctionCallOutputContentItem(
    val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_text",
) : FunctionCallOutputContentItem

@Serializable
public data class InputImageFunctionCallOutputContentItem(
    @SerialName("image_url") val imageUrl: String,
    val detail: ImageDetail? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_image",
) : FunctionCallOutputContentItem

@Serializable
public data class InputAudioFunctionCallOutputContentItem(
    @SerialName("audio_url") val audioUrl: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "input_audio",
) : FunctionCallOutputContentItem

@Serializable
public data class EncryptedContentFunctionCallOutputContentItem(
    @SerialName("encrypted_content") val encryptedContent: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "encrypted_content",
) : FunctionCallOutputContentItem

/**
 * A [FunctionCallOutputContentItem] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownFunctionCallOutputContentItemSerializer::class)
public data class UnknownFunctionCallOutputContentItem(override val raw: JsonObject) : FunctionCallOutputContentItem, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownFunctionCallOutputContentItemSerializer : RawPayloadSerializer<UnknownFunctionCallOutputContentItem>({ UnknownFunctionCallOutputContentItem(it) })

internal object FunctionCallOutputContentItemSerializer : TaggedUnionSerializer<FunctionCallOutputContentItem>(FunctionCallOutputContentItem::class, "type", mapOf(
    "input_text" to InputTextFunctionCallOutputContentItem.serializer(),
    "input_image" to InputImageFunctionCallOutputContentItem.serializer(),
    "input_audio" to InputAudioFunctionCallOutputContentItem.serializer(),
    "encrypted_content" to EncryptedContentFunctionCallOutputContentItem.serializer(),
), UnknownFunctionCallOutputContentItemSerializer)

@Serializable(with = GuardianApprovalReviewActionSerializer::class)
public sealed interface GuardianApprovalReviewAction {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class CommandGuardianApprovalReviewAction(
    val command: String,
    val cwd: String,
    val source: GuardianCommandSource,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "command",
) : GuardianApprovalReviewAction

@Serializable
public data class ExecveGuardianApprovalReviewAction(
    val argv: List<String>,
    val cwd: String,
    val program: String,
    val source: GuardianCommandSource,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "execve",
) : GuardianApprovalReviewAction

@Serializable
public data class ApplyPatchGuardianApprovalReviewAction(
    val cwd: String,
    val files: List<String>,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "applyPatch",
) : GuardianApprovalReviewAction

@Serializable
public data class NetworkAccessGuardianApprovalReviewAction(
    val host: String,
    val port: Int,
    val protocol: NetworkApprovalProtocol,
    val target: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "networkAccess",
) : GuardianApprovalReviewAction

@Serializable
public data class McpToolCallGuardianApprovalReviewAction(
    val server: String,
    val toolName: String,
    val connectorId: String? = null,
    val connectorName: String? = null,
    val toolTitle: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "mcpToolCall",
) : GuardianApprovalReviewAction

@Serializable
public data class RequestPermissionsGuardianApprovalReviewAction(
    val permissions: RequestPermissionProfile,
    val reason: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "requestPermissions",
) : GuardianApprovalReviewAction

/**
 * A [GuardianApprovalReviewAction] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownGuardianApprovalReviewActionSerializer::class)
public data class UnknownGuardianApprovalReviewAction(override val raw: JsonObject) : GuardianApprovalReviewAction, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownGuardianApprovalReviewActionSerializer : RawPayloadSerializer<UnknownGuardianApprovalReviewAction>({ UnknownGuardianApprovalReviewAction(it) })

internal object GuardianApprovalReviewActionSerializer : TaggedUnionSerializer<GuardianApprovalReviewAction>(GuardianApprovalReviewAction::class, "type", mapOf(
    "command" to CommandGuardianApprovalReviewAction.serializer(),
    "execve" to ExecveGuardianApprovalReviewAction.serializer(),
    "applyPatch" to ApplyPatchGuardianApprovalReviewAction.serializer(),
    "networkAccess" to NetworkAccessGuardianApprovalReviewAction.serializer(),
    "mcpToolCall" to McpToolCallGuardianApprovalReviewAction.serializer(),
    "requestPermissions" to RequestPermissionsGuardianApprovalReviewAction.serializer(),
), UnknownGuardianApprovalReviewActionSerializer)

@Serializable(with = LocalShellActionSerializer::class)
public sealed interface LocalShellAction {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class ExecLocalShellAction(
    val command: List<String>,
    val env: Map<String, String>? = null,
    @SerialName("timeout_ms") val timeoutMs: Long? = null,
    val user: String? = null,
    @SerialName("working_directory") val workingDirectory: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "exec",
) : LocalShellAction

/**
 * A [LocalShellAction] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownLocalShellActionSerializer::class)
public data class UnknownLocalShellAction(override val raw: JsonObject) : LocalShellAction, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownLocalShellActionSerializer : RawPayloadSerializer<UnknownLocalShellAction>({ UnknownLocalShellAction(it) })

internal object LocalShellActionSerializer : TaggedUnionSerializer<LocalShellAction>(LocalShellAction::class, "type", mapOf(
    "exec" to ExecLocalShellAction.serializer(),
), UnknownLocalShellActionSerializer)

@Serializable(with = LoginAccountParamsSerializer::class)
public sealed interface LoginAccountParams {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class ApiKeyv2LoginAccountParams(
    val apiKey: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "apiKey",
) : LoginAccountParams

@Serializable
public data class Chatgptv2LoginAccountParams(
    val appBrand: LoginAppBrand? = null,
    val codexStreamlinedLogin: Boolean? = null,
    val useHostedLoginSuccessPage: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgpt",
) : LoginAccountParams

@Serializable
public data class ChatgptDeviceCodev2LoginAccountParams(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgptDeviceCode",
) : LoginAccountParams

/**
 * [UNSTABLE] FOR OPENAI INTERNAL USE ONLY - DO NOT USE. The access token must contain the same
 * scopes that Codex-managed ChatGPT auth tokens have.
 */
@Serializable
public data class ChatgptAuthTokensv2LoginAccountParams(
    val accessToken: String,
    val chatgptAccountId: String,
    val chatgptPlanType: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgptAuthTokens",
) : LoginAccountParams

/**
 * [UNSTABLE] Managed Amazon Bedrock login is experimental.
 */
@Serializable
public data class AmazonBedrockv2LoginAccountParams(
    val apiKey: String,
    val region: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "amazonBedrock",
) : LoginAccountParams

/**
 * A [LoginAccountParams] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownLoginAccountParamsSerializer::class)
public data class UnknownLoginAccountParams(override val raw: JsonObject) : LoginAccountParams, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownLoginAccountParamsSerializer : RawPayloadSerializer<UnknownLoginAccountParams>({ UnknownLoginAccountParams(it) })

internal object LoginAccountParamsSerializer : TaggedUnionSerializer<LoginAccountParams>(LoginAccountParams::class, "type", mapOf(
    "apiKey" to ApiKeyv2LoginAccountParams.serializer(),
    "chatgpt" to Chatgptv2LoginAccountParams.serializer(),
    "chatgptDeviceCode" to ChatgptDeviceCodev2LoginAccountParams.serializer(),
    "chatgptAuthTokens" to ChatgptAuthTokensv2LoginAccountParams.serializer(),
    "amazonBedrock" to AmazonBedrockv2LoginAccountParams.serializer(),
), UnknownLoginAccountParamsSerializer)

@Serializable(with = LoginAccountResponseSerializer::class)
public sealed interface LoginAccountResponse {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class ApiKeyv2LoginAccountResponse(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "apiKey",
) : LoginAccountResponse

@Serializable
public data class Chatgptv2LoginAccountResponse(
    val authUrl: String,
    val loginId: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgpt",
) : LoginAccountResponse

@Serializable
public data class ChatgptDeviceCodev2LoginAccountResponse(
    val loginId: String,
    val userCode: String,
    val verificationUrl: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgptDeviceCode",
) : LoginAccountResponse

@Serializable
public data class ChatgptAuthTokensv2LoginAccountResponse(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "chatgptAuthTokens",
) : LoginAccountResponse

@Serializable
public data class AmazonBedrockv2LoginAccountResponse(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "amazonBedrock",
) : LoginAccountResponse

/**
 * A [LoginAccountResponse] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownLoginAccountResponseSerializer::class)
public data class UnknownLoginAccountResponse(override val raw: JsonObject) : LoginAccountResponse, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownLoginAccountResponseSerializer : RawPayloadSerializer<UnknownLoginAccountResponse>({ UnknownLoginAccountResponse(it) })

internal object LoginAccountResponseSerializer : TaggedUnionSerializer<LoginAccountResponse>(LoginAccountResponse::class, "type", mapOf(
    "apiKey" to ApiKeyv2LoginAccountResponse.serializer(),
    "chatgpt" to Chatgptv2LoginAccountResponse.serializer(),
    "chatgptDeviceCode" to ChatgptDeviceCodev2LoginAccountResponse.serializer(),
    "chatgptAuthTokens" to ChatgptAuthTokensv2LoginAccountResponse.serializer(),
    "amazonBedrock" to AmazonBedrockv2LoginAccountResponse.serializer(),
), UnknownLoginAccountResponseSerializer)

/**
 * Controls the effective multi-agent delegation instructions for a turn. `custom` means the
 * configured mode hint defines the policy instead of a built-in policy.
 *
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = MultiAgentModeSerializer::class)
public sealed interface MultiAgentMode {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: MultiAgentModePreset) : MultiAgentMode

    /**
     * Wire form: `{"custom": …}`.
     */
    @Serializable
    public data class Custom(public val custom: String) : MultiAgentMode

    /**
     * A [MultiAgentMode] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownMultiAgentModeSerializer::class)
    public data class Unknown(override val raw: JsonElement) : MultiAgentMode, RawValue

    public companion object {
        /** The `explicitRequestOnly` preset. */
        public val EXPLICIT_REQUEST_ONLY: MultiAgentMode = Preset(MultiAgentModePreset.EXPLICIT_REQUEST_ONLY)
        /** The `proactive` preset. */
        public val PROACTIVE: MultiAgentMode = Preset(MultiAgentModePreset.PROACTIVE)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [MultiAgentModePreset.UNKNOWN] and encoding as empty.
         */
        public fun of(preset: String): MultiAgentMode {
            val known = MultiAgentModePreset.fromWire(preset)
            return if (known == MultiAgentModePreset.UNKNOWN) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [MultiAgentMode].
 *
 * Split out so [MultiAgentMode.Preset] can be matched exhaustively while the union's object
 * variants keep their payloads.
 */
@Serializable(with = MultiAgentModePresetSerializer::class)
public enum class MultiAgentModePreset(override val wire: String) : WireEnum {
    EXPLICIT_REQUEST_ONLY("explicitRequestOnly"),
    PROACTIVE("proactive"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, MultiAgentModePreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): MultiAgentModePreset = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object MultiAgentModePresetSerializer : WireEnumSerializer<MultiAgentModePreset>("MultiAgentModePreset", MultiAgentModePreset::fromWire)

internal object UnknownMultiAgentModeSerializer : RawValueSerializer<MultiAgentMode.Unknown>({ MultiAgentMode.Unknown(it) })

internal object MultiAgentModeSerializer : MixedUnionSerializer<MultiAgentMode>(
    fromPreset = { MultiAgentMode.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "custom" -> MultiAgentMode.Custom(
                json.decodeFromJsonElement<String>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { MultiAgentMode.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is MultiAgentMode.Preset -> JsonPrimitive(value.value.wire)
            is MultiAgentMode.Custom -> buildJsonObject {
                put("custom", json.encodeToJsonElement(value.custom))
            }
            is MultiAgentMode.Unknown -> value.raw
        }
    },
)

@Serializable(with = ParsedCommandSerializer::class)
public sealed interface ParsedCommand {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val cmd: String
}

@Serializable
public data class ReadParsedCommand(
    override val cmd: String,
    val name: String,
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "read",
) : ParsedCommand

@Serializable
public data class ListFilesParsedCommand(
    override val cmd: String,
    val path: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "list_files",
) : ParsedCommand

@Serializable
public data class SearchParsedCommand(
    override val cmd: String,
    val path: String? = null,
    val query: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "search",
) : ParsedCommand

@Serializable
public data class UnknownParsedCommand(
    override val cmd: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "unknown",
) : ParsedCommand

/**
 * A [ParsedCommand] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnrecognizedParsedCommandSerializer::class)
public data class UnrecognizedParsedCommand(override val raw: JsonObject) : ParsedCommand, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val cmd: String get() = raw.stringOrEmpty("cmd")
}

internal object UnrecognizedParsedCommandSerializer : RawPayloadSerializer<UnrecognizedParsedCommand>({ UnrecognizedParsedCommand(it) })

internal object ParsedCommandSerializer : TaggedUnionSerializer<ParsedCommand>(ParsedCommand::class, "type", mapOf(
    "read" to ReadParsedCommand.serializer(),
    "list_files" to ListFilesParsedCommand.serializer(),
    "search" to SearchParsedCommand.serializer(),
    "unknown" to UnknownParsedCommand.serializer(),
), UnrecognizedParsedCommandSerializer)

@Serializable(with = PatchChangeKindSerializer::class)
public sealed interface PatchChangeKind {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class AddPatchChangeKind(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "add",
) : PatchChangeKind

@Serializable
public data class DeletePatchChangeKind(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "delete",
) : PatchChangeKind

@Serializable
public data class UpdatePatchChangeKind(
    @SerialName("move_path") val movePath: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "update",
) : PatchChangeKind

/**
 * A [PatchChangeKind] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownPatchChangeKindSerializer::class)
public data class UnknownPatchChangeKind(override val raw: JsonObject) : PatchChangeKind, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownPatchChangeKindSerializer : RawPayloadSerializer<UnknownPatchChangeKind>({ UnknownPatchChangeKind(it) })

internal object PatchChangeKindSerializer : TaggedUnionSerializer<PatchChangeKind>(PatchChangeKind::class, "type", mapOf(
    "add" to AddPatchChangeKind.serializer(),
    "delete" to DeletePatchChangeKind.serializer(),
    "update" to UpdatePatchChangeKind.serializer(),
), UnknownPatchChangeKindSerializer)

@Serializable(with = PluginSourceSerializer::class)
public sealed interface PluginSource {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class LocalPluginSource(
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "local",
) : PluginSource

@Serializable
public data class GitPluginSource(
    val url: String,
    val path: String? = null,
    val refName: String? = null,
    val sha: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "git",
) : PluginSource

@Serializable
public data class NpmPluginSource(
    val `package`: String,
    val registry: String? = null,
    val version: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "npm",
) : PluginSource

/**
 * The plugin is available in the remote catalog. Download metadata is kept server-side and is
 * not exposed through the app-server API.
 */
@Serializable
public data class RemotePluginSource(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "remote",
) : PluginSource

/**
 * A [PluginSource] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownPluginSourceSerializer::class)
public data class UnknownPluginSource(override val raw: JsonObject) : PluginSource, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownPluginSourceSerializer : RawPayloadSerializer<UnknownPluginSource>({ UnknownPluginSource(it) })

internal object PluginSourceSerializer : TaggedUnionSerializer<PluginSource>(PluginSource::class, "type", mapOf(
    "local" to LocalPluginSource.serializer(),
    "git" to GitPluginSource.serializer(),
    "npm" to NpmPluginSource.serializer(),
    "remote" to RemotePluginSource.serializer(),
), UnknownPluginSourceSerializer)

@Serializable(with = ReasoningItemContentSerializer::class)
public sealed interface ReasoningItemContent {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val text: String
}

@Serializable
public data class ReasoningTextReasoningItemContent(
    override val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "reasoning_text",
) : ReasoningItemContent

@Serializable
public data class TextReasoningItemContent(
    override val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "text",
) : ReasoningItemContent

/**
 * A [ReasoningItemContent] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownReasoningItemContentSerializer::class)
public data class UnknownReasoningItemContent(override val raw: JsonObject) : ReasoningItemContent, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val text: String get() = raw.stringOrEmpty("text")
}

internal object UnknownReasoningItemContentSerializer : RawPayloadSerializer<UnknownReasoningItemContent>({ UnknownReasoningItemContent(it) })

internal object ReasoningItemContentSerializer : TaggedUnionSerializer<ReasoningItemContent>(ReasoningItemContent::class, "type", mapOf(
    "reasoning_text" to ReasoningTextReasoningItemContent.serializer(),
    "text" to TextReasoningItemContent.serializer(),
), UnknownReasoningItemContentSerializer)

@Serializable(with = ReasoningItemReasoningSummarySerializer::class)
public sealed interface ReasoningItemReasoningSummary {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val text: String
}

@Serializable
public data class SummaryTextReasoningItemReasoningSummary(
    override val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "summary_text",
) : ReasoningItemReasoningSummary

/**
 * A [ReasoningItemReasoningSummary] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownReasoningItemReasoningSummarySerializer::class)
public data class UnknownReasoningItemReasoningSummary(override val raw: JsonObject) : ReasoningItemReasoningSummary, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val text: String get() = raw.stringOrEmpty("text")
}

internal object UnknownReasoningItemReasoningSummarySerializer : RawPayloadSerializer<UnknownReasoningItemReasoningSummary>({ UnknownReasoningItemReasoningSummary(it) })

internal object ReasoningItemReasoningSummarySerializer : TaggedUnionSerializer<ReasoningItemReasoningSummary>(ReasoningItemReasoningSummary::class, "type", mapOf(
    "summary_text" to SummaryTextReasoningItemReasoningSummary.serializer(),
), UnknownReasoningItemReasoningSummarySerializer)

@Serializable(with = ResponseItemSerializer::class)
public sealed interface ResponseItem {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class MessageResponseItem(
    val content: List<ContentItem>,
    val role: String,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    val phase: MessagePhase? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "message",
) : ResponseItem

@Serializable
public data class AgentMessageResponseItem(
    val author: String,
    val content: List<AgentMessageInputContent>,
    val recipient: String,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "agent_message",
) : ResponseItem

@Serializable
public data class ReasoningResponseItem(
    val summary: List<ReasoningItemReasoningSummary>,
    val content: List<ReasoningItemContent>? = null,
    @SerialName("encrypted_content") val encryptedContent: String? = null,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "reasoning",
) : ResponseItem

@Serializable
public data class LocalShellCallResponseItem(
    val action: LocalShellAction,
    val status: LocalShellStatus,
    @SerialName("call_id") val callId: String? = null,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "local_shell_call",
) : ResponseItem

@Serializable
public data class FunctionCallResponseItem(
    val arguments: String,
    @SerialName("call_id") val callId: String,
    val name: String,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    val namespace: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "function_call",
) : ResponseItem

@Serializable
public data class ToolSearchCallResponseItem(
    val arguments: JsonElement,
    val execution: String,
    @SerialName("call_id") val callId: String? = null,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    val status: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "tool_search_call",
) : ResponseItem

@Serializable
public data class FunctionCallOutputResponseItem(
    @SerialName("call_id") val callId: String,
    val output: JsonElement,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "function_call_output",
) : ResponseItem

@Serializable
public data class CustomToolCallResponseItem(
    @SerialName("call_id") val callId: String,
    val input: String,
    val name: String,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    val namespace: String? = null,
    val status: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "custom_tool_call",
) : ResponseItem

@Serializable
public data class CustomToolCallOutputResponseItem(
    @SerialName("call_id") val callId: String,
    val output: JsonElement,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    val name: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "custom_tool_call_output",
) : ResponseItem

@Serializable
public data class ToolSearchOutputResponseItem(
    val execution: String,
    val status: String,
    val tools: List<JsonElement>,
    @SerialName("call_id") val callId: String? = null,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "tool_search_output",
) : ResponseItem

@Serializable
public data class WebSearchCallResponseItem(
    val action: ResponsesApiWebSearchAction? = null,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    val status: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "web_search_call",
) : ResponseItem

@Serializable
public data class ImageGenerationCallResponseItem(
    val result: String,
    val status: String,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @SerialName("revised_prompt") val revisedPrompt: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "image_generation_call",
) : ResponseItem

@Serializable
public data class CompactionResponseItem(
    @SerialName("encrypted_content") val encryptedContent: String,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "compaction",
) : ResponseItem

@Serializable
public data class CompactionTriggerResponseItem(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "compaction_trigger",
) : ResponseItem

@Serializable
public data class ContextCompactionResponseItem(
    @SerialName("encrypted_content") val encryptedContent: String? = null,
    val id: String? = null,
    @SerialName("internal_chat_message_metadata_passthrough") val internalChatMessageMetadataPassthrough: InternalChatMessageMetadataPassthrough? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "context_compaction",
) : ResponseItem

@Serializable
public data class OtherResponseItem(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "other",
) : ResponseItem

/**
 * A [ResponseItem] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownResponseItemSerializer::class)
public data class UnknownResponseItem(override val raw: JsonObject) : ResponseItem, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownResponseItemSerializer : RawPayloadSerializer<UnknownResponseItem>({ UnknownResponseItem(it) })

internal object ResponseItemSerializer : TaggedUnionSerializer<ResponseItem>(ResponseItem::class, "type", mapOf(
    "message" to MessageResponseItem.serializer(),
    "agent_message" to AgentMessageResponseItem.serializer(),
    "reasoning" to ReasoningResponseItem.serializer(),
    "local_shell_call" to LocalShellCallResponseItem.serializer(),
    "function_call" to FunctionCallResponseItem.serializer(),
    "tool_search_call" to ToolSearchCallResponseItem.serializer(),
    "function_call_output" to FunctionCallOutputResponseItem.serializer(),
    "custom_tool_call" to CustomToolCallResponseItem.serializer(),
    "custom_tool_call_output" to CustomToolCallOutputResponseItem.serializer(),
    "tool_search_output" to ToolSearchOutputResponseItem.serializer(),
    "web_search_call" to WebSearchCallResponseItem.serializer(),
    "image_generation_call" to ImageGenerationCallResponseItem.serializer(),
    "compaction" to CompactionResponseItem.serializer(),
    "compaction_trigger" to CompactionTriggerResponseItem.serializer(),
    "context_compaction" to ContextCompactionResponseItem.serializer(),
    "other" to OtherResponseItem.serializer(),
), UnknownResponseItemSerializer)

@Serializable(with = ResponsesApiWebSearchActionSerializer::class)
public sealed interface ResponsesApiWebSearchAction {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class SearchResponsesApiWebSearchAction(
    val queries: List<String>? = null,
    val query: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "search",
) : ResponsesApiWebSearchAction

@Serializable
public data class OpenPageResponsesApiWebSearchAction(
    val url: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "open_page",
) : ResponsesApiWebSearchAction

@Serializable
public data class FindInPageResponsesApiWebSearchAction(
    val pattern: String? = null,
    val url: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "find_in_page",
) : ResponsesApiWebSearchAction

@Serializable
public data class OtherResponsesApiWebSearchAction(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "other",
) : ResponsesApiWebSearchAction

/**
 * A [ResponsesApiWebSearchAction] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownResponsesApiWebSearchActionSerializer::class)
public data class UnknownResponsesApiWebSearchAction(override val raw: JsonObject) : ResponsesApiWebSearchAction, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownResponsesApiWebSearchActionSerializer : RawPayloadSerializer<UnknownResponsesApiWebSearchAction>({ UnknownResponsesApiWebSearchAction(it) })

internal object ResponsesApiWebSearchActionSerializer : TaggedUnionSerializer<ResponsesApiWebSearchAction>(ResponsesApiWebSearchAction::class, "type", mapOf(
    "search" to SearchResponsesApiWebSearchAction.serializer(),
    "open_page" to OpenPageResponsesApiWebSearchAction.serializer(),
    "find_in_page" to FindInPageResponsesApiWebSearchAction.serializer(),
    "other" to OtherResponsesApiWebSearchAction.serializer(),
), UnknownResponsesApiWebSearchActionSerializer)

/**
 * User's decision in response to an ExecApprovalRequest.
 *
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = ReviewDecisionSerializer::class)
public sealed interface ReviewDecision {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: ReviewDecisionPreset) : ReviewDecision

    /**
     * User has approved this command and wants to apply the proposed execpolicy amendment so
     * future matching commands are permitted.
     *
     * Wire form: `{"approved_execpolicy_amendment": …}`.
     */
    @Serializable
    public data class ApprovedExecpolicyAmendment(@SerialName("approved_execpolicy_amendment") public val approvedExecpolicyAmendment: ReviewDecisionApprovedExecpolicyAmendment) : ReviewDecision

    /**
     * User chose to persist a network policy rule (allow/deny) for future requests to the same
     * host.
     *
     * Wire form: `{"network_policy_amendment": …}`.
     */
    @Serializable
    public data class NetworkPolicyAmendment(@SerialName("network_policy_amendment") public val networkPolicyAmendment: ReviewDecisionNetworkPolicyAmendment) : ReviewDecision

    /**
     * User has denied this command and the agent should not execute it, but it should continue the
     * session and try something else.
     *
     * Wire form: `{"denied": …}`.
     */
    @Serializable
    public data class Denied(public val denied: ReviewDecisionDenied) : ReviewDecision

    /**
     * A [ReviewDecision] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownReviewDecisionSerializer::class)
    public data class Unknown(override val raw: JsonElement) : ReviewDecision, RawValue

    public companion object {
        /** The `approved` preset. */
        public val APPROVED: ReviewDecision = Preset(ReviewDecisionPreset.APPROVED)
        /** The `approved_for_session` preset. */
        public val APPROVED_FOR_SESSION: ReviewDecision = Preset(ReviewDecisionPreset.APPROVED_FOR_SESSION)
        /** The `timed_out` preset. */
        public val TIMED_OUT: ReviewDecision = Preset(ReviewDecisionPreset.TIMED_OUT)
        /** The `abort` preset. */
        public val ABORT: ReviewDecision = Preset(ReviewDecisionPreset.ABORT)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [ReviewDecisionPreset.UNKNOWN] and encoding as empty.
         */
        public fun of(preset: String): ReviewDecision {
            val known = ReviewDecisionPreset.fromWire(preset)
            return if (known == ReviewDecisionPreset.UNKNOWN) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [ReviewDecision].
 *
 * Split out so [ReviewDecision.Preset] can be matched exhaustively while the union's object
 * variants keep their payloads.
 */
@Serializable(with = ReviewDecisionPresetSerializer::class)
public enum class ReviewDecisionPreset(override val wire: String) : WireEnum {
    APPROVED("approved"),
    APPROVED_FOR_SESSION("approved_for_session"),
    TIMED_OUT("timed_out"),
    ABORT("abort"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ReviewDecisionPreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ReviewDecisionPreset = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ReviewDecisionPresetSerializer : WireEnumSerializer<ReviewDecisionPreset>("ReviewDecisionPreset", ReviewDecisionPreset::fromWire)

internal object UnknownReviewDecisionSerializer : RawValueSerializer<ReviewDecision.Unknown>({ ReviewDecision.Unknown(it) })

internal object ReviewDecisionSerializer : MixedUnionSerializer<ReviewDecision>(
    fromPreset = { ReviewDecision.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "approved_execpolicy_amendment" -> ReviewDecision.ApprovedExecpolicyAmendment(
                json.decodeFromJsonElement<ReviewDecisionApprovedExecpolicyAmendment>(payload),
            )
            "network_policy_amendment" -> ReviewDecision.NetworkPolicyAmendment(
                json.decodeFromJsonElement<ReviewDecisionNetworkPolicyAmendment>(payload),
            )
            "denied" -> ReviewDecision.Denied(
                json.decodeFromJsonElement<ReviewDecisionDenied>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { ReviewDecision.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is ReviewDecision.Preset -> JsonPrimitive(value.value.wire)
            is ReviewDecision.ApprovedExecpolicyAmendment -> buildJsonObject {
                put("approved_execpolicy_amendment", json.encodeToJsonElement(value.approvedExecpolicyAmendment))
            }
            is ReviewDecision.NetworkPolicyAmendment -> buildJsonObject {
                put("network_policy_amendment", json.encodeToJsonElement(value.networkPolicyAmendment))
            }
            is ReviewDecision.Denied -> buildJsonObject {
                put("denied", json.encodeToJsonElement(value.denied))
            }
            is ReviewDecision.Unknown -> value.raw
        }
    },
)

@Serializable(with = ReviewTargetSerializer::class)
public sealed interface ReviewTarget {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

/**
 * Review the working tree: staged, unstaged, and untracked files.
 */
@Serializable
public data class UncommittedChangesReviewTarget(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "uncommittedChanges",
) : ReviewTarget

/**
 * Review changes between the current branch and the given base branch.
 */
@Serializable
public data class BaseBranchReviewTarget(
    val branch: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "baseBranch",
) : ReviewTarget

/**
 * Review the changes introduced by a specific commit.
 */
@Serializable
public data class CommitReviewTarget(
    val sha: String,
    val title: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "commit",
) : ReviewTarget

/**
 * Arbitrary instructions, equivalent to the old free-form prompt.
 */
@Serializable
public data class CustomReviewTarget(
    val instructions: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "custom",
) : ReviewTarget

/**
 * A [ReviewTarget] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownReviewTargetSerializer::class)
public data class UnknownReviewTarget(override val raw: JsonObject) : ReviewTarget, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownReviewTargetSerializer : RawPayloadSerializer<UnknownReviewTarget>({ UnknownReviewTarget(it) })

internal object ReviewTargetSerializer : TaggedUnionSerializer<ReviewTarget>(ReviewTarget::class, "type", mapOf(
    "uncommittedChanges" to UncommittedChangesReviewTarget.serializer(),
    "baseBranch" to BaseBranchReviewTarget.serializer(),
    "commit" to CommitReviewTarget.serializer(),
    "custom" to CustomReviewTarget.serializer(),
), UnknownReviewTargetSerializer)

@Serializable(with = SandboxPolicySerializer::class)
public sealed interface SandboxPolicy {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class DangerFullAccessSandboxPolicy(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "dangerFullAccess",
) : SandboxPolicy

@Serializable
public data class ReadOnlySandboxPolicy(
    val networkAccess: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "readOnly",
) : SandboxPolicy

@Serializable
public data class ExternalSandboxSandboxPolicy(
    val networkAccess: NetworkAccess? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "externalSandbox",
) : SandboxPolicy

@Serializable
public data class WorkspaceWriteSandboxPolicy(
    val excludeSlashTmp: Boolean? = null,
    val excludeTmpdirEnvVar: Boolean? = null,
    val networkAccess: Boolean? = null,
    val writableRoots: List<String> = emptyList(),
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "workspaceWrite",
) : SandboxPolicy

/**
 * A [SandboxPolicy] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownSandboxPolicySerializer::class)
public data class UnknownSandboxPolicy(override val raw: JsonObject) : SandboxPolicy, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownSandboxPolicySerializer : RawPayloadSerializer<UnknownSandboxPolicy>({ UnknownSandboxPolicy(it) })

internal object SandboxPolicySerializer : TaggedUnionSerializer<SandboxPolicy>(SandboxPolicy::class, "type", mapOf(
    "dangerFullAccess" to DangerFullAccessSandboxPolicy.serializer(),
    "readOnly" to ReadOnlySandboxPolicy.serializer(),
    "externalSandbox" to ExternalSandboxSandboxPolicy.serializer(),
    "workspaceWrite" to WorkspaceWriteSandboxPolicy.serializer(),
), UnknownSandboxPolicySerializer)

@Serializable(with = ScheduledTaskScheduleSerializer::class)
public sealed interface ScheduledTaskSchedule {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class HourlyScheduledTaskSchedule(
    val intervalHours: Int,
    val days: List<ScheduledTaskWeekday>? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "hourly",
) : ScheduledTaskSchedule

@Serializable
public data class DailyScheduledTaskSchedule(
    val time: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "daily",
) : ScheduledTaskSchedule

@Serializable
public data class WeekdaysScheduledTaskSchedule(
    val time: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "weekdays",
) : ScheduledTaskSchedule

@Serializable
public data class WeeklyScheduledTaskSchedule(
    val days: List<ScheduledTaskWeekday>,
    val time: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "weekly",
) : ScheduledTaskSchedule

/**
 * A [ScheduledTaskSchedule] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownScheduledTaskScheduleSerializer::class)
public data class UnknownScheduledTaskSchedule(override val raw: JsonObject) : ScheduledTaskSchedule, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownScheduledTaskScheduleSerializer : RawPayloadSerializer<UnknownScheduledTaskSchedule>({ UnknownScheduledTaskSchedule(it) })

internal object ScheduledTaskScheduleSerializer : TaggedUnionSerializer<ScheduledTaskSchedule>(ScheduledTaskSchedule::class, "type", mapOf(
    "hourly" to HourlyScheduledTaskSchedule.serializer(),
    "daily" to DailyScheduledTaskSchedule.serializer(),
    "weekdays" to WeekdaysScheduledTaskSchedule.serializer(),
    "weekly" to WeeklyScheduledTaskSchedule.serializer(),
), UnknownScheduledTaskScheduleSerializer)

/**
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = SessionSourceSerializer::class)
public sealed interface SessionSource {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: SessionSourcePreset) : SessionSource

    /**
     * Wire form: `{"custom": …}`.
     */
    @Serializable
    public data class Custom(public val custom: String) : SessionSource

    /**
     * Wire form: `{"subAgent": …}`.
     */
    @Serializable
    public data class SubAgent(public val subAgent: SubAgentSource) : SessionSource

    /**
     * A [SessionSource] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownSessionSourceSerializer::class)
    public data class Unknown(override val raw: JsonElement) : SessionSource, RawValue

    public companion object {
        /** The `cli` preset. */
        public val CLI: SessionSource = Preset(SessionSourcePreset.CLI)
        /** The `vscode` preset. */
        public val VSCODE: SessionSource = Preset(SessionSourcePreset.VSCODE)
        /** The `exec` preset. */
        public val EXEC: SessionSource = Preset(SessionSourcePreset.EXEC)
        /** The `appServer` preset. */
        public val APP_SERVER: SessionSource = Preset(SessionSourcePreset.APP_SERVER)
        /** The `unknown` preset. */
        public val UNKNOWN: SessionSource = Preset(SessionSourcePreset.UNKNOWN)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [SessionSourcePreset.UNKNOWN_] and encoding as empty.
         */
        public fun of(preset: String): SessionSource {
            val known = SessionSourcePreset.fromWire(preset)
            return if (known == SessionSourcePreset.UNKNOWN_) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [SessionSource].
 *
 * Split out so [SessionSource.Preset] can be matched exhaustively while the union's object
 * variants keep their payloads.
 */
@Serializable(with = SessionSourcePresetSerializer::class)
public enum class SessionSourcePreset(override val wire: String) : WireEnum {
    CLI("cli"),
    VSCODE("vscode"),
    EXEC("exec"),
    APP_SERVER("appServer"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, SessionSourcePreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): SessionSourcePreset = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object SessionSourcePresetSerializer : WireEnumSerializer<SessionSourcePreset>("SessionSourcePreset", SessionSourcePreset::fromWire)

internal object UnknownSessionSourceSerializer : RawValueSerializer<SessionSource.Unknown>({ SessionSource.Unknown(it) })

internal object SessionSourceSerializer : MixedUnionSerializer<SessionSource>(
    fromPreset = { SessionSource.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "custom" -> SessionSource.Custom(
                json.decodeFromJsonElement<String>(payload),
            )
            "subAgent" -> SessionSource.SubAgent(
                json.decodeFromJsonElement<SubAgentSource>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { SessionSource.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is SessionSource.Preset -> JsonPrimitive(value.value.wire)
            is SessionSource.Custom -> buildJsonObject {
                put("custom", json.encodeToJsonElement(value.custom))
            }
            is SessionSource.SubAgent -> buildJsonObject {
                put("subAgent", json.encodeToJsonElement(value.subAgent))
            }
            is SessionSource.Unknown -> value.raw
        }
    },
)

/**
 * On the wire this is either one of the bare-string presets or a single-key object,
 * which is why it is sealed rather than one class: `when` over it and the compiler
 * checks that every variant is handled. A variant a newer app-server introduces
 * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.
 */
@Serializable(with = SubAgentSourceSerializer::class)
public sealed interface SubAgentSource {
    /** One of the closed set of presets. Encodes as a bare string, never an object. */
    @Serializable
    public data class Preset(public val value: SubAgentSourcePreset) : SubAgentSource

    /**
     * Wire form: `{"thread_spawn": …}`.
     */
    @Serializable
    public data class ThreadSpawn(@SerialName("thread_spawn") public val threadSpawn: SubAgentSourceThreadSpawn) : SubAgentSource

    /**
     * Wire form: `{"other": …}`.
     */
    @Serializable
    public data class Other(public val other: String) : SubAgentSource

    /**
     * A [SubAgentSource] variant this SDK version does not model — a preset or an object key
     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.
     */
    @Serializable(with = UnknownSubAgentSourceSerializer::class)
    public data class Unknown(override val raw: JsonElement) : SubAgentSource, RawValue

    public companion object {
        /** The `review` preset. */
        public val REVIEW: SubAgentSource = Preset(SubAgentSourcePreset.REVIEW)
        /** The `compact` preset. */
        public val COMPACT: SubAgentSource = Preset(SubAgentSourcePreset.COMPACT)
        /** The `memory_consolidation` preset. */
        public val MEMORY_CONSOLIDATION: SubAgentSource = Preset(SubAgentSourcePreset.MEMORY_CONSOLIDATION)

        /**
         * Wrap a bare-string preset, including one this SDK version does not model:
         * an unrecognized value becomes [Unknown] carrying that exact string, rather
         * than collapsing onto [SubAgentSourcePreset.UNKNOWN] and encoding as empty.
         */
        public fun of(preset: String): SubAgentSource {
            val known = SubAgentSourcePreset.fromWire(preset)
            return if (known == SubAgentSourcePreset.UNKNOWN) {
                Unknown(JsonPrimitive(preset))
            } else {
                Preset(known)
            }
        }
    }
}

/**
 * The bare-string presets of [SubAgentSource].
 *
 * Split out so [SubAgentSource.Preset] can be matched exhaustively while the union's object
 * variants keep their payloads.
 */
@Serializable(with = SubAgentSourcePresetSerializer::class)
public enum class SubAgentSourcePreset(override val wire: String) : WireEnum {
    REVIEW("review"),
    COMPACT("compact"),
    MEMORY_CONSOLIDATION("memory_consolidation"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, SubAgentSourcePreset> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): SubAgentSourcePreset = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object SubAgentSourcePresetSerializer : WireEnumSerializer<SubAgentSourcePreset>("SubAgentSourcePreset", SubAgentSourcePreset::fromWire)

internal object UnknownSubAgentSourceSerializer : RawValueSerializer<SubAgentSource.Unknown>({ SubAgentSource.Unknown(it) })

internal object SubAgentSourceSerializer : MixedUnionSerializer<SubAgentSource>(
    fromPreset = { SubAgentSource.of(it) },
    fromKeyed = { json, key, payload ->
        when (key) {
            "thread_spawn" -> SubAgentSource.ThreadSpawn(
                json.decodeFromJsonElement<SubAgentSourceThreadSpawn>(payload),
            )
            "other" -> SubAgentSource.Other(
                json.decodeFromJsonElement<String>(payload),
            )
            else -> null
        }
    },
    fromUnknown = { SubAgentSource.Unknown(it) },
    toElement = { json, value ->
        when (value) {
            is SubAgentSource.Preset -> JsonPrimitive(value.value.wire)
            is SubAgentSource.ThreadSpawn -> buildJsonObject {
                put("thread_spawn", json.encodeToJsonElement(value.threadSpawn))
            }
            is SubAgentSource.Other -> buildJsonObject {
                put("other", json.encodeToJsonElement(value.other))
            }
            is SubAgentSource.Unknown -> value.raw
        }
    },
)

@Serializable(with = ThreadItemSerializer::class)
public sealed interface ThreadItem {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
    public val id: String
}

@Serializable
public data class UserMessageThreadItem(
    val content: List<UserInput>,
    override val id: String,
    val clientId: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "userMessage",
) : ThreadItem

@Serializable
public data class HookPromptThreadItem(
    val fragments: List<HookPromptFragment>,
    override val id: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "hookPrompt",
) : ThreadItem

@Serializable
public data class AgentMessageThreadItem(
    override val id: String,
    val text: String,
    val memoryCitation: MemoryCitation? = null,
    val phase: MessagePhase? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "agentMessage",
) : ThreadItem

/**
 * EXPERIMENTAL - proposed plan item content. The completed plan item is authoritative and may
 * not match the concatenation of `PlanDelta` text.
 */
@Serializable
public data class PlanThreadItem(
    override val id: String,
    val text: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "plan",
) : ThreadItem

@Serializable
public data class ReasoningThreadItem(
    override val id: String,
    val content: List<String> = emptyList(),
    val summary: List<String> = emptyList(),
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "reasoning",
) : ThreadItem

@Serializable
public data class CommandExecutionThreadItem(
    val command: String,
    val commandActions: List<CommandAction>,
    val cwd: String,
    override val id: String,
    val status: CommandExecutionStatus,
    val aggregatedOutput: String? = null,
    val durationMs: Long? = null,
    val exitCode: Int? = null,
    val pluginId: String? = null,
    val processId: String? = null,
    val scriptPath: String? = null,
    val source: CommandExecutionSource? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "commandExecution",
) : ThreadItem

@Serializable
public data class FileChangeThreadItem(
    val changes: List<FileUpdateChange>,
    override val id: String,
    val status: PatchApplyStatus,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "fileChange",
) : ThreadItem

@Serializable
public data class McpToolCallThreadItem(
    val arguments: JsonElement,
    override val id: String,
    val server: String,
    val status: McpToolCallStatus,
    val tool: String,
    val appContext: McpToolCallAppContext? = null,
    val durationMs: Long? = null,
    val error: McpToolCallError? = null,
    val mcpAppResourceUri: String? = null,
    val pluginId: String? = null,
    val result: McpToolCallResult? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "mcpToolCall",
) : ThreadItem

@Serializable
public data class DynamicToolCallThreadItem(
    val arguments: JsonElement,
    override val id: String,
    val status: DynamicToolCallStatus,
    val tool: String,
    val contentItems: List<DynamicToolCallOutputContentItem>? = null,
    val durationMs: Long? = null,
    val namespace: String? = null,
    val success: Boolean? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "dynamicToolCall",
) : ThreadItem

@Serializable
public data class CollabAgentToolCallThreadItem(
    val agentsStates: Map<String, CollabAgentState>,
    override val id: String,
    val receiverThreadIds: List<String>,
    val senderThreadId: String,
    val status: CollabAgentToolCallStatus,
    val tool: CollabAgentTool,
    val model: String? = null,
    val prompt: String? = null,
    val reasoningEffort: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "collabAgentToolCall",
) : ThreadItem

@Serializable
public data class SubAgentActivityThreadItem(
    val agentPath: String,
    val agentThreadId: String,
    override val id: String,
    val kind: SubAgentActivityKind,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "subAgentActivity",
) : ThreadItem

@Serializable
public data class WebSearchThreadItem(
    override val id: String,
    val query: String,
    val action: WebSearchAction? = null,
    val results: List<JsonElement>? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "webSearch",
) : ThreadItem

@Serializable
public data class ImageViewThreadItem(
    override val id: String,
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "imageView",
) : ThreadItem

/**
 * Display item emitted by the interruptible `clock.sleep` tool.
 */
@Serializable
public data class SleepThreadItem(
    val durationMs: Long,
    override val id: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "sleep",
) : ThreadItem

@Serializable
public data class ImageGenerationThreadItem(
    override val id: String,
    val result: String,
    val status: String,
    val revisedPrompt: String? = null,
    val savedPath: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "imageGeneration",
) : ThreadItem

@Serializable
public data class EnteredReviewModeThreadItem(
    override val id: String,
    val review: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "enteredReviewMode",
) : ThreadItem

@Serializable
public data class ExitedReviewModeThreadItem(
    override val id: String,
    val review: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "exitedReviewMode",
) : ThreadItem

@Serializable
public data class ContextCompactionThreadItem(
    override val id: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "contextCompaction",
) : ThreadItem

/**
 * A [ThreadItem] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownThreadItemSerializer::class)
public data class UnknownThreadItem(override val raw: JsonObject) : ThreadItem, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
    override val id: String get() = raw.stringOrEmpty("id")
}

internal object UnknownThreadItemSerializer : RawPayloadSerializer<UnknownThreadItem>({ UnknownThreadItem(it) })

internal object ThreadItemSerializer : TaggedUnionSerializer<ThreadItem>(ThreadItem::class, "type", mapOf(
    "userMessage" to UserMessageThreadItem.serializer(),
    "hookPrompt" to HookPromptThreadItem.serializer(),
    "agentMessage" to AgentMessageThreadItem.serializer(),
    "plan" to PlanThreadItem.serializer(),
    "reasoning" to ReasoningThreadItem.serializer(),
    "commandExecution" to CommandExecutionThreadItem.serializer(),
    "fileChange" to FileChangeThreadItem.serializer(),
    "mcpToolCall" to McpToolCallThreadItem.serializer(),
    "dynamicToolCall" to DynamicToolCallThreadItem.serializer(),
    "collabAgentToolCall" to CollabAgentToolCallThreadItem.serializer(),
    "subAgentActivity" to SubAgentActivityThreadItem.serializer(),
    "webSearch" to WebSearchThreadItem.serializer(),
    "imageView" to ImageViewThreadItem.serializer(),
    "sleep" to SleepThreadItem.serializer(),
    "imageGeneration" to ImageGenerationThreadItem.serializer(),
    "enteredReviewMode" to EnteredReviewModeThreadItem.serializer(),
    "exitedReviewMode" to ExitedReviewModeThreadItem.serializer(),
    "contextCompaction" to ContextCompactionThreadItem.serializer(),
), UnknownThreadItemSerializer)

/**
 * EXPERIMENTAL - transport used by thread realtime.
 */
@Serializable(with = ThreadRealtimeStartTransportSerializer::class)
public sealed interface ThreadRealtimeStartTransport {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class WebsocketThreadRealtimeStartTransport(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "websocket",
) : ThreadRealtimeStartTransport

@Serializable
public data class WebrtcThreadRealtimeStartTransport(
    val sdp: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "webrtc",
) : ThreadRealtimeStartTransport

/**
 * A [ThreadRealtimeStartTransport] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownThreadRealtimeStartTransportSerializer::class)
public data class UnknownThreadRealtimeStartTransport(override val raw: JsonObject) : ThreadRealtimeStartTransport, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownThreadRealtimeStartTransportSerializer : RawPayloadSerializer<UnknownThreadRealtimeStartTransport>({ UnknownThreadRealtimeStartTransport(it) })

internal object ThreadRealtimeStartTransportSerializer : TaggedUnionSerializer<ThreadRealtimeStartTransport>(ThreadRealtimeStartTransport::class, "type", mapOf(
    "websocket" to WebsocketThreadRealtimeStartTransport.serializer(),
    "webrtc" to WebrtcThreadRealtimeStartTransport.serializer(),
), UnknownThreadRealtimeStartTransportSerializer)

@Serializable(with = ThreadStatusSerializer::class)
public sealed interface ThreadStatus {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class NotLoadedThreadStatus(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "notLoaded",
) : ThreadStatus

@Serializable
public data class IdleThreadStatus(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "idle",
) : ThreadStatus

@Serializable
public data class SystemErrorThreadStatus(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "systemError",
) : ThreadStatus

@Serializable
public data class ActiveThreadStatus(
    val activeFlags: List<ThreadActiveFlag>,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "active",
) : ThreadStatus

/**
 * A [ThreadStatus] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownThreadStatusSerializer::class)
public data class UnknownThreadStatus(override val raw: JsonObject) : ThreadStatus, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownThreadStatusSerializer : RawPayloadSerializer<UnknownThreadStatus>({ UnknownThreadStatus(it) })

internal object ThreadStatusSerializer : TaggedUnionSerializer<ThreadStatus>(ThreadStatus::class, "type", mapOf(
    "notLoaded" to NotLoadedThreadStatus.serializer(),
    "idle" to IdleThreadStatus.serializer(),
    "systemError" to SystemErrorThreadStatus.serializer(),
    "active" to ActiveThreadStatus.serializer(),
), UnknownThreadStatusSerializer)

@Serializable(with = UserInputSerializer::class)
public sealed interface UserInput {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class TextUserInput(
    val text: String,
    @SerialName("text_elements") val textElements: List<TextElement> = emptyList(),
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "text",
) : UserInput

@Serializable
public data class ImageUserInput(
    val url: String,
    val detail: ImageDetail? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "image",
) : UserInput

@Serializable
public data class LocalImageUserInput(
    val path: String,
    val detail: ImageDetail? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "localImage",
) : UserInput

@Serializable
public data class AudioUserInput(
    val url: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "audio",
) : UserInput

@Serializable
public data class LocalAudioUserInput(
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "localAudio",
) : UserInput

@Serializable
public data class SkillUserInput(
    val name: String,
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "skill",
) : UserInput

@Serializable
public data class MentionUserInput(
    val name: String,
    val path: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "mention",
) : UserInput

/**
 * A [UserInput] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownUserInputSerializer::class)
public data class UnknownUserInput(override val raw: JsonObject) : UserInput, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownUserInputSerializer : RawPayloadSerializer<UnknownUserInput>({ UnknownUserInput(it) })

internal object UserInputSerializer : TaggedUnionSerializer<UserInput>(UserInput::class, "type", mapOf(
    "text" to TextUserInput.serializer(),
    "image" to ImageUserInput.serializer(),
    "localImage" to LocalImageUserInput.serializer(),
    "audio" to AudioUserInput.serializer(),
    "localAudio" to LocalAudioUserInput.serializer(),
    "skill" to SkillUserInput.serializer(),
    "mention" to MentionUserInput.serializer(),
), UnknownUserInputSerializer)

@Serializable(with = WebSearchActionSerializer::class)
public sealed interface WebSearchAction {
    /** Wire discriminator for this variant (`type`). */
    public val type: String
}

@Serializable
public data class SearchWebSearchAction(
    val queries: List<String>? = null,
    val query: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "search",
) : WebSearchAction

@Serializable
public data class OpenPageWebSearchAction(
    val url: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "openPage",
) : WebSearchAction

@Serializable
public data class FindInPageWebSearchAction(
    val pattern: String? = null,
    val url: String? = null,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "findInPage",
) : WebSearchAction

@Serializable
public data class OtherWebSearchAction(
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    override val type: String = "other",
) : WebSearchAction

/**
 * A [WebSearchAction] variant this SDK version does not model. [raw] keeps the payload
 * so nothing is lost, and no unrecognized variant can fail a decode.
 */
@Serializable(with = UnknownWebSearchActionSerializer::class)
public data class UnknownWebSearchAction(override val raw: JsonObject) : WebSearchAction, RawPayload {
    override val type: String get() = raw.stringOrEmpty("type")
}

internal object UnknownWebSearchActionSerializer : RawPayloadSerializer<UnknownWebSearchAction>({ UnknownWebSearchAction(it) })

internal object WebSearchActionSerializer : TaggedUnionSerializer<WebSearchAction>(WebSearchAction::class, "type", mapOf(
    "search" to SearchWebSearchAction.serializer(),
    "openPage" to OpenPageWebSearchAction.serializer(),
    "findInPage" to FindInPageWebSearchAction.serializer(),
    "other" to OtherWebSearchAction.serializer(),
), UnknownWebSearchActionSerializer)

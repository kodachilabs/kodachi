// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Enum types.

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

@Serializable(with = AddCreditsNudgeCreditTypeSerializer::class)
public enum class AddCreditsNudgeCreditType(override val wire: String) : WireEnum {
    CREDITS("credits"),
    USAGE_LIMIT("usage_limit"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AddCreditsNudgeCreditType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AddCreditsNudgeCreditType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AddCreditsNudgeCreditTypeSerializer : WireEnumSerializer<AddCreditsNudgeCreditType>("AddCreditsNudgeCreditType", AddCreditsNudgeCreditType::fromWire)

@Serializable(with = AddCreditsNudgeEmailStatusSerializer::class)
public enum class AddCreditsNudgeEmailStatus(override val wire: String) : WireEnum {
    SENT("sent"),
    COOLDOWN_ACTIVE("cooldown_active"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AddCreditsNudgeEmailStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AddCreditsNudgeEmailStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AddCreditsNudgeEmailStatusSerializer : WireEnumSerializer<AddCreditsNudgeEmailStatus>("AddCreditsNudgeEmailStatus", AddCreditsNudgeEmailStatus::fromWire)

@Serializable(with = AdditionalContextKindSerializer::class)
public enum class AdditionalContextKind(override val wire: String) : WireEnum {
    UNTRUSTED("untrusted"),
    APPLICATION("application"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AdditionalContextKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AdditionalContextKind = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AdditionalContextKindSerializer : WireEnumSerializer<AdditionalContextKind>("AdditionalContextKind", AdditionalContextKind::fromWire)

@Serializable(with = AppTemplateUnavailableReasonSerializer::class)
public enum class AppTemplateUnavailableReason(override val wire: String) : WireEnum {
    NOT_CONFIGURED_FOR_WORKSPACE("NOT_CONFIGURED_FOR_WORKSPACE"),
    NO_ACTIVE_WORKSPACE("NO_ACTIVE_WORKSPACE"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AppTemplateUnavailableReason> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AppTemplateUnavailableReason = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AppTemplateUnavailableReasonSerializer : WireEnumSerializer<AppTemplateUnavailableReason>("AppTemplateUnavailableReason", AppTemplateUnavailableReason::fromWire)

@Serializable(with = AppToolApprovalSerializer::class)
public enum class AppToolApproval(override val wire: String) : WireEnum {
    AUTO("auto"),
    PROMPT("prompt"),
    WRITES("writes"),
    APPROVE("approve"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AppToolApproval> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AppToolApproval = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AppToolApprovalSerializer : WireEnumSerializer<AppToolApproval>("AppToolApproval", AppToolApproval::fromWire)

/**
 * Configures who approval requests are routed to for review. Examples include sandbox escapes,
 * blocked network access, MCP approval prompts, and ARC escalations. Defaults to `user`.
 * `auto_review` uses a carefully prompted subagent to gather relevant context and apply a
 * risk-based decision framework before approving or denying the request. The legacy value
 * `guardian_subagent` is accepted for compatibility.
 */
@Serializable(with = ApprovalsReviewerSerializer::class)
public enum class ApprovalsReviewer(override val wire: String) : WireEnum {
    USER("user"),
    AUTO_REVIEW("auto_review"),
    GUARDIAN_SUBAGENT("guardian_subagent"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ApprovalsReviewer> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ApprovalsReviewer = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ApprovalsReviewerSerializer : WireEnumSerializer<ApprovalsReviewer>("ApprovalsReviewer", ApprovalsReviewer::fromWire)

/**
 * Authentication mode for OpenAI-backed providers.
 */
@Serializable(with = AuthModeSerializer::class)
public enum class AuthMode(override val wire: String) : WireEnum {
    APIKEY("apikey"),
    CHATGPT("chatgpt"),
    CHATGPT_AUTH_TOKENS("chatgptAuthTokens"),
    HEADERS("headers"),
    AGENT_IDENTITY("agentIdentity"),
    PERSONAL_ACCESS_TOKEN("personalAccessToken"),
    BEDROCK_API_KEY("bedrockApiKey"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AuthMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AuthMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AuthModeSerializer : WireEnumSerializer<AuthMode>("AuthMode", AuthMode::fromWire)

/**
 * Selects which part of the active context is charged against
 * `model_auto_compact_token_limit`.
 */
@Serializable(with = AutoCompactTokenLimitScopeSerializer::class)
public enum class AutoCompactTokenLimitScope(override val wire: String) : WireEnum {
    TOTAL("total"),
    BODY_AFTER_PREFIX("body_after_prefix"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AutoCompactTokenLimitScope> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AutoCompactTokenLimitScope = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AutoCompactTokenLimitScopeSerializer : WireEnumSerializer<AutoCompactTokenLimitScope>("AutoCompactTokenLimitScope", AutoCompactTokenLimitScope::fromWire)

/**
 * [UNSTABLE] Source that produced a terminal approval auto-review decision.
 */
@Serializable(with = AutoReviewDecisionSourceSerializer::class)
public enum class AutoReviewDecisionSource(override val wire: String) : WireEnum {
    AGENT("agent"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, AutoReviewDecisionSource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): AutoReviewDecisionSource = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object AutoReviewDecisionSourceSerializer : WireEnumSerializer<AutoReviewDecisionSource>("AutoReviewDecisionSource", AutoReviewDecisionSource::fromWire)

@Serializable(with = CancelLoginAccountStatusSerializer::class)
public enum class CancelLoginAccountStatus(override val wire: String) : WireEnum {
    CANCELED("canceled"),
    NOT_FOUND("notFound"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CancelLoginAccountStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CancelLoginAccountStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CancelLoginAccountStatusSerializer : WireEnumSerializer<CancelLoginAccountStatus>("CancelLoginAccountStatus", CancelLoginAccountStatus::fromWire)

@Serializable(with = ChatgptAuthTokensRefreshReasonSerializer::class)
public enum class ChatgptAuthTokensRefreshReason(override val wire: String) : WireEnum {
    UNAUTHORIZED("unauthorized"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ChatgptAuthTokensRefreshReason> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ChatgptAuthTokensRefreshReason = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ChatgptAuthTokensRefreshReasonSerializer : WireEnumSerializer<ChatgptAuthTokensRefreshReason>("ChatgptAuthTokensRefreshReason", ChatgptAuthTokensRefreshReason::fromWire)

@Serializable(with = CodexResponseHandoffModeSerializer::class)
public enum class CodexResponseHandoffMode(override val wire: String) : WireEnum {
    THINKING("thinking"),
    COMMENTARY("commentary"),
    BEM_TAGS("bemTags"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CodexResponseHandoffMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CodexResponseHandoffMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CodexResponseHandoffModeSerializer : WireEnumSerializer<CodexResponseHandoffMode>("CodexResponseHandoffMode", CodexResponseHandoffMode::fromWire)

@Serializable(with = CollabAgentStatusSerializer::class)
public enum class CollabAgentStatus(override val wire: String) : WireEnum {
    PENDING_INIT("pendingInit"),
    RUNNING("running"),
    INTERRUPTED("interrupted"),
    COMPLETED("completed"),
    ERRORED("errored"),
    SHUTDOWN("shutdown"),
    NOT_FOUND("notFound"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CollabAgentStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CollabAgentStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CollabAgentStatusSerializer : WireEnumSerializer<CollabAgentStatus>("CollabAgentStatus", CollabAgentStatus::fromWire)

@Serializable(with = CollabAgentToolSerializer::class)
public enum class CollabAgentTool(override val wire: String) : WireEnum {
    SPAWN_AGENT("spawnAgent"),
    SEND_INPUT("sendInput"),
    RESUME_AGENT("resumeAgent"),
    WAIT("wait"),
    CLOSE_AGENT("closeAgent"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CollabAgentTool> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CollabAgentTool = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CollabAgentToolSerializer : WireEnumSerializer<CollabAgentTool>("CollabAgentTool", CollabAgentTool::fromWire)

@Serializable(with = CollabAgentToolCallStatusSerializer::class)
public enum class CollabAgentToolCallStatus(override val wire: String) : WireEnum {
    IN_PROGRESS("inProgress"),
    COMPLETED("completed"),
    FAILED("failed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CollabAgentToolCallStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CollabAgentToolCallStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CollabAgentToolCallStatusSerializer : WireEnumSerializer<CollabAgentToolCallStatus>("CollabAgentToolCallStatus", CollabAgentToolCallStatus::fromWire)

/**
 * Stream label for `command/exec/outputDelta` notifications.
 */
@Serializable(with = CommandExecOutputStreamSerializer::class)
public enum class CommandExecOutputStream(override val wire: String) : WireEnum {
    STDOUT("stdout"),
    STDERR("stderr"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CommandExecOutputStream> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CommandExecOutputStream = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CommandExecOutputStreamSerializer : WireEnumSerializer<CommandExecOutputStream>("CommandExecOutputStream", CommandExecOutputStream::fromWire)

@Serializable(with = CommandExecutionSourceSerializer::class)
public enum class CommandExecutionSource(override val wire: String) : WireEnum {
    AGENT("agent"),
    USER_SHELL("userShell"),
    UNIFIED_EXEC_STARTUP("unifiedExecStartup"),
    UNIFIED_EXEC_INTERACTION("unifiedExecInteraction"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CommandExecutionSource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CommandExecutionSource = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CommandExecutionSourceSerializer : WireEnumSerializer<CommandExecutionSource>("CommandExecutionSource", CommandExecutionSource::fromWire)

@Serializable(with = CommandExecutionStatusSerializer::class)
public enum class CommandExecutionStatus(override val wire: String) : WireEnum {
    IN_PROGRESS("inProgress"),
    COMPLETED("completed"),
    FAILED("failed"),
    DECLINED("declined"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, CommandExecutionStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): CommandExecutionStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object CommandExecutionStatusSerializer : WireEnumSerializer<CommandExecutionStatus>("CommandExecutionStatus", CommandExecutionStatus::fromWire)

@Serializable(with = ConsumeAccountRateLimitResetCreditOutcomeSerializer::class)
public enum class ConsumeAccountRateLimitResetCreditOutcome(override val wire: String) : WireEnum {
    RESET("reset"),
    NOTHING_TO_RESET("nothingToReset"),
    NO_CREDIT("noCredit"),
    ALREADY_REDEEMED("alreadyRedeemed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ConsumeAccountRateLimitResetCreditOutcome> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ConsumeAccountRateLimitResetCreditOutcome = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ConsumeAccountRateLimitResetCreditOutcomeSerializer : WireEnumSerializer<ConsumeAccountRateLimitResetCreditOutcome>("ConsumeAccountRateLimitResetCreditOutcome", ConsumeAccountRateLimitResetCreditOutcome::fromWire)

@Serializable(with = ConversationTextRoleSerializer::class)
public enum class ConversationTextRole(override val wire: String) : WireEnum {
    USER("user"),
    DEVELOPER("developer"),
    ASSISTANT("assistant"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ConversationTextRole> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ConversationTextRole = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ConversationTextRoleSerializer : WireEnumSerializer<ConversationTextRole>("ConversationTextRole", ConversationTextRole::fromWire)

@Serializable(with = DynamicToolCallStatusSerializer::class)
public enum class DynamicToolCallStatus(override val wire: String) : WireEnum {
    IN_PROGRESS("inProgress"),
    COMPLETED("completed"),
    FAILED("failed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, DynamicToolCallStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): DynamicToolCallStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object DynamicToolCallStatusSerializer : WireEnumSerializer<DynamicToolCallStatus>("DynamicToolCallStatus", DynamicToolCallStatus::fromWire)

@Serializable(with = ExperimentalFeatureStageSerializer::class)
public enum class ExperimentalFeatureStage(override val wire: String) : WireEnum {
    BETA("beta"),
    UNDER_DEVELOPMENT("underDevelopment"),
    STABLE("stable"),
    DEPRECATED("deprecated"),
    REMOVED("removed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ExperimentalFeatureStage> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ExperimentalFeatureStage = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ExperimentalFeatureStageSerializer : WireEnumSerializer<ExperimentalFeatureStage>("ExperimentalFeatureStage", ExperimentalFeatureStage::fromWire)

@Serializable(with = ExternalAgentConfigMigrationItemTypeSerializer::class)
public enum class ExternalAgentConfigMigrationItemType(override val wire: String) : WireEnum {
    AGENTS_MD("AGENTS_MD"),
    CONFIG("CONFIG"),
    SKILLS("SKILLS"),
    PLUGINS("PLUGINS"),
    MCP_SERVER_CONFIG("MCP_SERVER_CONFIG"),
    SUBAGENTS("SUBAGENTS"),
    HOOKS("HOOKS"),
    COMMANDS("COMMANDS"),
    MEMORY("MEMORY"),
    SESSIONS("SESSIONS"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ExternalAgentConfigMigrationItemType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ExternalAgentConfigMigrationItemType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ExternalAgentConfigMigrationItemTypeSerializer : WireEnumSerializer<ExternalAgentConfigMigrationItemType>("ExternalAgentConfigMigrationItemType", ExternalAgentConfigMigrationItemType::fromWire)

@Serializable(with = ExternalAgentImportedConnectorSourceSerializer::class)
public enum class ExternalAgentImportedConnectorSource(override val wire: String) : WireEnum {
    REMOTE_MCP_SERVERS_CONFIG("remoteMcpServersConfig"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ExternalAgentImportedConnectorSource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ExternalAgentImportedConnectorSource = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ExternalAgentImportedConnectorSourceSerializer : WireEnumSerializer<ExternalAgentImportedConnectorSource>("ExternalAgentImportedConnectorSource", ExternalAgentImportedConnectorSource::fromWire)

@Serializable(with = FileChangeApprovalDecisionSerializer::class)
public enum class FileChangeApprovalDecision(override val wire: String) : WireEnum {
    ACCEPT("accept"),
    ACCEPT_FOR_SESSION("acceptForSession"),
    DECLINE("decline"),
    CANCEL("cancel"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, FileChangeApprovalDecision> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): FileChangeApprovalDecision = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object FileChangeApprovalDecisionSerializer : WireEnumSerializer<FileChangeApprovalDecision>("FileChangeApprovalDecision", FileChangeApprovalDecision::fromWire)

@Serializable(with = FileSystemAccessModeSerializer::class)
public enum class FileSystemAccessMode(override val wire: String) : WireEnum {
    READ("read"),
    WRITE("write"),
    DENY("deny"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, FileSystemAccessMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): FileSystemAccessMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object FileSystemAccessModeSerializer : WireEnumSerializer<FileSystemAccessMode>("FileSystemAccessMode", FileSystemAccessMode::fromWire)

@Serializable(with = ForcedLoginMethodSerializer::class)
public enum class ForcedLoginMethod(override val wire: String) : WireEnum {
    CHATGPT("chatgpt"),
    API("api"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ForcedLoginMethod> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ForcedLoginMethod = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ForcedLoginMethodSerializer : WireEnumSerializer<ForcedLoginMethod>("ForcedLoginMethod", ForcedLoginMethod::fromWire)

@Serializable(with = FuzzyFileSearchMatchTypeSerializer::class)
public enum class FuzzyFileSearchMatchType(override val wire: String) : WireEnum {
    FILE("file"),
    DIRECTORY("directory"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, FuzzyFileSearchMatchType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): FuzzyFileSearchMatchType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object FuzzyFileSearchMatchTypeSerializer : WireEnumSerializer<FuzzyFileSearchMatchType>("FuzzyFileSearchMatchType", FuzzyFileSearchMatchType::fromWire)

/**
 * [UNSTABLE] Lifecycle state for an approval auto-review.
 */
@Serializable(with = GuardianApprovalReviewStatusSerializer::class)
public enum class GuardianApprovalReviewStatus(override val wire: String) : WireEnum {
    IN_PROGRESS("inProgress"),
    APPROVED("approved"),
    DENIED("denied"),
    TIMED_OUT("timedOut"),
    ABORTED("aborted"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, GuardianApprovalReviewStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): GuardianApprovalReviewStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object GuardianApprovalReviewStatusSerializer : WireEnumSerializer<GuardianApprovalReviewStatus>("GuardianApprovalReviewStatus", GuardianApprovalReviewStatus::fromWire)

@Serializable(with = GuardianCommandSourceSerializer::class)
public enum class GuardianCommandSource(override val wire: String) : WireEnum {
    SHELL("shell"),
    UNIFIED_EXEC("unifiedExec"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, GuardianCommandSource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): GuardianCommandSource = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object GuardianCommandSourceSerializer : WireEnumSerializer<GuardianCommandSource>("GuardianCommandSource", GuardianCommandSource::fromWire)

/**
 * [UNSTABLE] Risk level assigned by approval auto-review.
 */
@Serializable(with = GuardianRiskLevelSerializer::class)
public enum class GuardianRiskLevel(override val wire: String) : WireEnum {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    CRITICAL("critical"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, GuardianRiskLevel> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): GuardianRiskLevel = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object GuardianRiskLevelSerializer : WireEnumSerializer<GuardianRiskLevel>("GuardianRiskLevel", GuardianRiskLevel::fromWire)

/**
 * [UNSTABLE] Authorization level assigned by approval auto-review.
 */
@Serializable(with = GuardianUserAuthorizationSerializer::class)
public enum class GuardianUserAuthorization(override val wire: String) : WireEnum {
    UNKNOWN("unknown"),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, GuardianUserAuthorization> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): GuardianUserAuthorization = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object GuardianUserAuthorizationSerializer : WireEnumSerializer<GuardianUserAuthorization>("GuardianUserAuthorization", GuardianUserAuthorization::fromWire)

@Serializable(with = HookEventNameSerializer::class)
public enum class HookEventName(override val wire: String) : WireEnum {
    PRE_TOOL_USE("preToolUse"),
    PERMISSION_REQUEST("permissionRequest"),
    POST_TOOL_USE("postToolUse"),
    PRE_COMPACT("preCompact"),
    POST_COMPACT("postCompact"),
    SESSION_START("sessionStart"),
    SESSION_END("sessionEnd"),
    USER_PROMPT_SUBMIT("userPromptSubmit"),
    SUBAGENT_START("subagentStart"),
    SUBAGENT_STOP("subagentStop"),
    STOP("stop"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookEventName> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookEventName = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookEventNameSerializer : WireEnumSerializer<HookEventName>("HookEventName", HookEventName::fromWire)

@Serializable(with = HookExecutionModeSerializer::class)
public enum class HookExecutionMode(override val wire: String) : WireEnum {
    SYNC("sync"),
    ASYNC("async"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookExecutionMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookExecutionMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookExecutionModeSerializer : WireEnumSerializer<HookExecutionMode>("HookExecutionMode", HookExecutionMode::fromWire)

@Serializable(with = HookHandlerTypeSerializer::class)
public enum class HookHandlerType(override val wire: String) : WireEnum {
    COMMAND("command"),
    PROMPT("prompt"),
    AGENT("agent"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookHandlerType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookHandlerType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookHandlerTypeSerializer : WireEnumSerializer<HookHandlerType>("HookHandlerType", HookHandlerType::fromWire)

@Serializable(with = HookOutputEntryKindSerializer::class)
public enum class HookOutputEntryKind(override val wire: String) : WireEnum {
    WARNING("warning"),
    STOP("stop"),
    FEEDBACK("feedback"),
    CONTEXT("context"),
    ERROR("error"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookOutputEntryKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookOutputEntryKind = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookOutputEntryKindSerializer : WireEnumSerializer<HookOutputEntryKind>("HookOutputEntryKind", HookOutputEntryKind::fromWire)

@Serializable(with = HookRunStatusSerializer::class)
public enum class HookRunStatus(override val wire: String) : WireEnum {
    RUNNING("running"),
    COMPLETED("completed"),
    FAILED("failed"),
    BLOCKED("blocked"),
    STOPPED("stopped"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookRunStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookRunStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookRunStatusSerializer : WireEnumSerializer<HookRunStatus>("HookRunStatus", HookRunStatus::fromWire)

@Serializable(with = HookScopeSerializer::class)
public enum class HookScope(override val wire: String) : WireEnum {
    THREAD("thread"),
    TURN("turn"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookScope> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookScope = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookScopeSerializer : WireEnumSerializer<HookScope>("HookScope", HookScope::fromWire)

@Serializable(with = HookSourceSerializer::class)
public enum class HookSource(override val wire: String) : WireEnum {
    SYSTEM("system"),
    USER("user"),
    PROJECT("project"),
    MDM("mdm"),
    SESSION_FLAGS("sessionFlags"),
    PLUGIN("plugin"),
    CLOUD_REQUIREMENTS("cloudRequirements"),
    CLOUD_MANAGED_CONFIG("cloudManagedConfig"),
    LEGACY_MANAGED_CONFIG_FILE("legacyManagedConfigFile"),
    LEGACY_MANAGED_CONFIG_MDM("legacyManagedConfigMdm"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookSource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): HookSource = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object HookSourceSerializer : WireEnumSerializer<HookSource>("HookSource", HookSource::fromWire)

@Serializable(with = HookTrustStatusSerializer::class)
public enum class HookTrustStatus(override val wire: String) : WireEnum {
    MANAGED("managed"),
    UNTRUSTED("untrusted"),
    TRUSTED("trusted"),
    MODIFIED("modified"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, HookTrustStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): HookTrustStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object HookTrustStatusSerializer : WireEnumSerializer<HookTrustStatus>("HookTrustStatus", HookTrustStatus::fromWire)

@Serializable(with = ImageDetailSerializer::class)
public enum class ImageDetail(override val wire: String) : WireEnum {
    AUTO("auto"),
    LOW("low"),
    HIGH("high"),
    ORIGINAL("original"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ImageDetail> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ImageDetail = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ImageDetailSerializer : WireEnumSerializer<ImageDetail>("ImageDetail", ImageDetail::fromWire)

/**
 * Canonical user-input modality tags advertised by a model.
 */
@Serializable(with = InputModalitySerializer::class)
public enum class InputModality(override val wire: String) : WireEnum {
    TEXT("text"),
    IMAGE("image"),
    AUDIO("audio"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, InputModality> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): InputModality = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object InputModalitySerializer : WireEnumSerializer<InputModality>("InputModality", InputModality::fromWire)

@Serializable(with = LocalShellStatusSerializer::class)
public enum class LocalShellStatus(override val wire: String) : WireEnum {
    COMPLETED("completed"),
    IN_PROGRESS("in_progress"),
    INCOMPLETE("incomplete"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, LocalShellStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): LocalShellStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object LocalShellStatusSerializer : WireEnumSerializer<LocalShellStatus>("LocalShellStatus", LocalShellStatus::fromWire)

@Serializable(with = LoginAppBrandSerializer::class)
public enum class LoginAppBrand(override val wire: String) : WireEnum {
    CODEX("codex"),
    CHATGPT("chatgpt"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, LoginAppBrand> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): LoginAppBrand = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object LoginAppBrandSerializer : WireEnumSerializer<LoginAppBrand>("LoginAppBrand", LoginAppBrand::fromWire)

@Serializable(with = McpAuthStatusSerializer::class)
public enum class McpAuthStatus(override val wire: String) : WireEnum {
    UNSUPPORTED("unsupported"),
    NOT_LOGGED_IN("notLoggedIn"),
    BEARER_TOKEN("bearerToken"),
    O_AUTH("oAuth"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpAuthStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpAuthStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpAuthStatusSerializer : WireEnumSerializer<McpAuthStatus>("McpAuthStatus", McpAuthStatus::fromWire)

@Serializable(with = McpElicitationArrayTypeSerializer::class)
public enum class McpElicitationArrayType(override val wire: String) : WireEnum {
    ARRAY("array"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpElicitationArrayType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpElicitationArrayType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpElicitationArrayTypeSerializer : WireEnumSerializer<McpElicitationArrayType>("McpElicitationArrayType", McpElicitationArrayType::fromWire)

@Serializable(with = McpElicitationBooleanTypeSerializer::class)
public enum class McpElicitationBooleanType(override val wire: String) : WireEnum {
    BOOLEAN("boolean"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpElicitationBooleanType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpElicitationBooleanType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpElicitationBooleanTypeSerializer : WireEnumSerializer<McpElicitationBooleanType>("McpElicitationBooleanType", McpElicitationBooleanType::fromWire)

@Serializable(with = McpElicitationNumberTypeSerializer::class)
public enum class McpElicitationNumberType(override val wire: String) : WireEnum {
    NUMBER("number"),
    INTEGER("integer"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpElicitationNumberType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpElicitationNumberType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpElicitationNumberTypeSerializer : WireEnumSerializer<McpElicitationNumberType>("McpElicitationNumberType", McpElicitationNumberType::fromWire)

@Serializable(with = McpElicitationObjectTypeSerializer::class)
public enum class McpElicitationObjectType(override val wire: String) : WireEnum {
    OBJECT("object"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpElicitationObjectType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpElicitationObjectType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpElicitationObjectTypeSerializer : WireEnumSerializer<McpElicitationObjectType>("McpElicitationObjectType", McpElicitationObjectType::fromWire)

@Serializable(with = McpElicitationStringFormatSerializer::class)
public enum class McpElicitationStringFormat(override val wire: String) : WireEnum {
    EMAIL("email"),
    URI("uri"),
    DATE("date"),
    DATE_TIME("date-time"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpElicitationStringFormat> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpElicitationStringFormat = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpElicitationStringFormatSerializer : WireEnumSerializer<McpElicitationStringFormat>("McpElicitationStringFormat", McpElicitationStringFormat::fromWire)

@Serializable(with = McpElicitationStringTypeSerializer::class)
public enum class McpElicitationStringType(override val wire: String) : WireEnum {
    STRING("string"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpElicitationStringType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpElicitationStringType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpElicitationStringTypeSerializer : WireEnumSerializer<McpElicitationStringType>("McpElicitationStringType", McpElicitationStringType::fromWire)

@Serializable(with = McpServerElicitationActionSerializer::class)
public enum class McpServerElicitationAction(override val wire: String) : WireEnum {
    ACCEPT("accept"),
    DECLINE("decline"),
    CANCEL("cancel"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpServerElicitationAction> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpServerElicitationAction = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpServerElicitationActionSerializer : WireEnumSerializer<McpServerElicitationAction>("McpServerElicitationAction", McpServerElicitationAction::fromWire)

@Serializable(with = McpServerStartupFailureReasonSerializer::class)
public enum class McpServerStartupFailureReason(override val wire: String) : WireEnum {
    REAUTHENTICATION_REQUIRED("reauthenticationRequired"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpServerStartupFailureReason> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpServerStartupFailureReason = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpServerStartupFailureReasonSerializer : WireEnumSerializer<McpServerStartupFailureReason>("McpServerStartupFailureReason", McpServerStartupFailureReason::fromWire)

@Serializable(with = McpServerStartupStateSerializer::class)
public enum class McpServerStartupState(override val wire: String) : WireEnum {
    STARTING("starting"),
    READY("ready"),
    FAILED("failed"),
    CANCELLED("cancelled"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpServerStartupState> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpServerStartupState = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpServerStartupStateSerializer : WireEnumSerializer<McpServerStartupState>("McpServerStartupState", McpServerStartupState::fromWire)

@Serializable(with = McpServerStatusDetailSerializer::class)
public enum class McpServerStatusDetail(override val wire: String) : WireEnum {
    FULL("full"),
    TOOLS_AND_AUTH_ONLY("toolsAndAuthOnly"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpServerStatusDetail> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpServerStatusDetail = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpServerStatusDetailSerializer : WireEnumSerializer<McpServerStatusDetail>("McpServerStatusDetail", McpServerStatusDetail::fromWire)

@Serializable(with = McpToolCallStatusSerializer::class)
public enum class McpToolCallStatus(override val wire: String) : WireEnum {
    IN_PROGRESS("inProgress"),
    COMPLETED("completed"),
    FAILED("failed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, McpToolCallStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): McpToolCallStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object McpToolCallStatusSerializer : WireEnumSerializer<McpToolCallStatus>("McpToolCallStatus", McpToolCallStatus::fromWire)

@Serializable(with = MergeStrategySerializer::class)
public enum class MergeStrategy(override val wire: String) : WireEnum {
    REPLACE("replace"),
    UPSERT("upsert"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, MergeStrategy> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): MergeStrategy = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object MergeStrategySerializer : WireEnumSerializer<MergeStrategy>("MergeStrategy", MergeStrategy::fromWire)

/**
 * Classifies an assistant message as interim commentary or final answer text.
 *
 * Providers do not emit this consistently, so callers must treat `None` as "phase unknown" and
 * keep compatibility behavior for legacy models.
 */
@Serializable(with = MessagePhaseSerializer::class)
public enum class MessagePhase(override val wire: String) : WireEnum {
    COMMENTARY("commentary"),
    FINAL_ANSWER("final_answer"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, MessagePhase> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): MessagePhase = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object MessagePhaseSerializer : WireEnumSerializer<MessagePhase>("MessagePhase", MessagePhase::fromWire)

/**
 * Initial collaboration mode to use when the TUI starts.
 */
@Serializable(with = ModeKindSerializer::class)
public enum class ModeKind(override val wire: String) : WireEnum {
    PLAN("plan"),
    DEFAULT("default"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ModeKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ModeKind = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ModeKindSerializer : WireEnumSerializer<ModeKind>("ModeKind", ModeKind::fromWire)

@Serializable(with = ModelRerouteReasonSerializer::class)
public enum class ModelRerouteReason(override val wire: String) : WireEnum {
    HIGH_RISK_CYBER_ACTIVITY("highRiskCyberActivity"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ModelRerouteReason> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ModelRerouteReason = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ModelRerouteReasonSerializer : WireEnumSerializer<ModelRerouteReason>("ModelRerouteReason", ModelRerouteReason::fromWire)

@Serializable(with = ModelVerificationSerializer::class)
public enum class ModelVerification(override val wire: String) : WireEnum {
    TRUSTED_ACCESS_FOR_CYBER("trustedAccessForCyber"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ModelVerification> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ModelVerification = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ModelVerificationSerializer : WireEnumSerializer<ModelVerification>("ModelVerification", ModelVerification::fromWire)

@Serializable(with = NetworkAccessSerializer::class)
public enum class NetworkAccess(override val wire: String) : WireEnum {
    RESTRICTED("restricted"),
    ENABLED("enabled"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, NetworkAccess> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): NetworkAccess = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object NetworkAccessSerializer : WireEnumSerializer<NetworkAccess>("NetworkAccess", NetworkAccess::fromWire)

@Serializable(with = NetworkApprovalProtocolSerializer::class)
public enum class NetworkApprovalProtocol(override val wire: String) : WireEnum {
    HTTP("http"),
    HTTPS("https"),
    SOCKS5_TCP("socks5Tcp"),
    SOCKS5_UDP("socks5Udp"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, NetworkApprovalProtocol> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): NetworkApprovalProtocol = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object NetworkApprovalProtocolSerializer : WireEnumSerializer<NetworkApprovalProtocol>("NetworkApprovalProtocol", NetworkApprovalProtocol::fromWire)

@Serializable(with = NetworkDomainPermissionSerializer::class)
public enum class NetworkDomainPermission(override val wire: String) : WireEnum {
    ALLOW("allow"),
    DENY("deny"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, NetworkDomainPermission> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): NetworkDomainPermission = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object NetworkDomainPermissionSerializer : WireEnumSerializer<NetworkDomainPermission>("NetworkDomainPermission", NetworkDomainPermission::fromWire)

@Serializable(with = NetworkPolicyRuleActionSerializer::class)
public enum class NetworkPolicyRuleAction(override val wire: String) : WireEnum {
    ALLOW("allow"),
    DENY("deny"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, NetworkPolicyRuleAction> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): NetworkPolicyRuleAction = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object NetworkPolicyRuleActionSerializer : WireEnumSerializer<NetworkPolicyRuleAction>("NetworkPolicyRuleAction", NetworkPolicyRuleAction::fromWire)

@Serializable(with = NetworkUnixSocketPermissionSerializer::class)
public enum class NetworkUnixSocketPermission(override val wire: String) : WireEnum {
    ALLOW("allow"),
    DENY("deny"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, NetworkUnixSocketPermission> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): NetworkUnixSocketPermission = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object NetworkUnixSocketPermissionSerializer : WireEnumSerializer<NetworkUnixSocketPermission>("NetworkUnixSocketPermission", NetworkUnixSocketPermission::fromWire)

@Serializable(with = NonSteerableTurnKindSerializer::class)
public enum class NonSteerableTurnKind(override val wire: String) : WireEnum {
    REVIEW("review"),
    COMPACT("compact"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, NonSteerableTurnKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): NonSteerableTurnKind = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object NonSteerableTurnKindSerializer : WireEnumSerializer<NonSteerableTurnKind>("NonSteerableTurnKind", NonSteerableTurnKind::fromWire)

@Serializable(with = PatchApplyStatusSerializer::class)
public enum class PatchApplyStatus(override val wire: String) : WireEnum {
    IN_PROGRESS("inProgress"),
    COMPLETED("completed"),
    FAILED("failed"),
    DECLINED("declined"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PatchApplyStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PatchApplyStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PatchApplyStatusSerializer : WireEnumSerializer<PatchApplyStatus>("PatchApplyStatus", PatchApplyStatus::fromWire)

@Serializable(with = PermissionGrantScopeSerializer::class)
public enum class PermissionGrantScope(override val wire: String) : WireEnum {
    TURN("turn"),
    SESSION("session"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PermissionGrantScope> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PermissionGrantScope = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PermissionGrantScopeSerializer : WireEnumSerializer<PermissionGrantScope>("PermissionGrantScope", PermissionGrantScope::fromWire)

@Serializable(with = PersonalitySerializer::class)
public enum class Personality(override val wire: String) : WireEnum {
    NONE("none"),
    FRIENDLY("friendly"),
    PRAGMATIC("pragmatic"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, Personality> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): Personality = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PersonalitySerializer : WireEnumSerializer<Personality>("Personality", Personality::fromWire)

@Serializable(with = PlanTypeSerializer::class)
public enum class PlanType(override val wire: String) : WireEnum {
    FREE("free"),
    GO("go"),
    PLUS("plus"),
    PRO("pro"),
    PROLITE("prolite"),
    TEAM("team"),
    SELF_SERVE_BUSINESS_USAGE_BASED("self_serve_business_usage_based"),
    BUSINESS("business"),
    ENT26("ent26"),
    ENTERPRISE_CBP_USAGE_BASED("enterprise_cbp_usage_based"),
    ENTERPRISE("enterprise"),
    EDU("edu"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PlanType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): PlanType = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object PlanTypeSerializer : WireEnumSerializer<PlanType>("PlanType", PlanType::fromWire)

@Serializable(with = PluginAuthPolicySerializer::class)
public enum class PluginAuthPolicy(override val wire: String) : WireEnum {
    ON_INSTALL("ON_INSTALL"),
    ON_USE("ON_USE"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginAuthPolicy> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginAuthPolicy = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginAuthPolicySerializer : WireEnumSerializer<PluginAuthPolicy>("PluginAuthPolicy", PluginAuthPolicy::fromWire)

@Serializable(with = PluginAvailabilitySerializer::class)
public enum class PluginAvailability(override val wire: String) : WireEnum {
    DISABLED_BY_ADMIN("DISABLED_BY_ADMIN"),
    AVAILABLE("AVAILABLE"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginAvailability> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginAvailability = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginAvailabilitySerializer : WireEnumSerializer<PluginAvailability>("PluginAvailability", PluginAvailability::fromWire)

@Serializable(with = PluginInstallPolicySerializer::class)
public enum class PluginInstallPolicy(override val wire: String) : WireEnum {
    NOT_AVAILABLE("NOT_AVAILABLE"),
    AVAILABLE("AVAILABLE"),
    INSTALLED_BY_DEFAULT("INSTALLED_BY_DEFAULT"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginInstallPolicy> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginInstallPolicy = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginInstallPolicySerializer : WireEnumSerializer<PluginInstallPolicy>("PluginInstallPolicy", PluginInstallPolicy::fromWire)

@Serializable(with = PluginInstallPolicySourceSerializer::class)
public enum class PluginInstallPolicySource(override val wire: String) : WireEnum {
    WORKSPACE_SETTING("WORKSPACE_SETTING"),
    IMPLICIT_CANONICAL_APP("IMPLICIT_CANONICAL_APP"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginInstallPolicySource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginInstallPolicySource = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginInstallPolicySourceSerializer : WireEnumSerializer<PluginInstallPolicySource>("PluginInstallPolicySource", PluginInstallPolicySource::fromWire)

@Serializable(with = PluginListMarketplaceKindSerializer::class)
public enum class PluginListMarketplaceKind(override val wire: String) : WireEnum {
    LOCAL("local"),
    VERTICAL("vertical"),
    WORKSPACE_DIRECTORY("workspace-directory"),
    SHARED_WITH_ME("shared-with-me"),
    CREATED_BY_ME_REMOTE("created-by-me-remote"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginListMarketplaceKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginListMarketplaceKind = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginListMarketplaceKindSerializer : WireEnumSerializer<PluginListMarketplaceKind>("PluginListMarketplaceKind", PluginListMarketplaceKind::fromWire)

@Serializable(with = PluginShareDiscoverabilitySerializer::class)
public enum class PluginShareDiscoverability(override val wire: String) : WireEnum {
    LISTED("LISTED"),
    UNLISTED("UNLISTED"),
    PRIVATE("PRIVATE"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginShareDiscoverability> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginShareDiscoverability = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginShareDiscoverabilitySerializer : WireEnumSerializer<PluginShareDiscoverability>("PluginShareDiscoverability", PluginShareDiscoverability::fromWire)

@Serializable(with = PluginSharePrincipalRoleSerializer::class)
public enum class PluginSharePrincipalRole(override val wire: String) : WireEnum {
    READER("reader"),
    EDITOR("editor"),
    OWNER("owner"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginSharePrincipalRole> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginSharePrincipalRole = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginSharePrincipalRoleSerializer : WireEnumSerializer<PluginSharePrincipalRole>("PluginSharePrincipalRole", PluginSharePrincipalRole::fromWire)

@Serializable(with = PluginSharePrincipalTypeSerializer::class)
public enum class PluginSharePrincipalType(override val wire: String) : WireEnum {
    USER("user"),
    GROUP("group"),
    WORKSPACE("workspace"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginSharePrincipalType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginSharePrincipalType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginSharePrincipalTypeSerializer : WireEnumSerializer<PluginSharePrincipalType>("PluginSharePrincipalType", PluginSharePrincipalType::fromWire)

@Serializable(with = PluginShareTargetRoleSerializer::class)
public enum class PluginShareTargetRole(override val wire: String) : WireEnum {
    READER("reader"),
    EDITOR("editor"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginShareTargetRole> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginShareTargetRole = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginShareTargetRoleSerializer : WireEnumSerializer<PluginShareTargetRole>("PluginShareTargetRole", PluginShareTargetRole::fromWire)

@Serializable(with = PluginShareUpdateDiscoverabilitySerializer::class)
public enum class PluginShareUpdateDiscoverability(override val wire: String) : WireEnum {
    UNLISTED("UNLISTED"),
    PRIVATE("PRIVATE"),
    LISTED("LISTED"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, PluginShareUpdateDiscoverability> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): PluginShareUpdateDiscoverability = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object PluginShareUpdateDiscoverabilitySerializer : WireEnumSerializer<PluginShareUpdateDiscoverability>("PluginShareUpdateDiscoverability", PluginShareUpdateDiscoverability::fromWire)

/**
 * Stream label for `process/outputDelta` notifications.
 */
@Serializable(with = ProcessOutputStreamSerializer::class)
public enum class ProcessOutputStream(override val wire: String) : WireEnum {
    STDOUT("stdout"),
    STDERR("stderr"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ProcessOutputStream> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ProcessOutputStream = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ProcessOutputStreamSerializer : WireEnumSerializer<ProcessOutputStream>("ProcessOutputStream", ProcessOutputStream::fromWire)

@Serializable(with = RateLimitReachedTypeSerializer::class)
public enum class RateLimitReachedType(override val wire: String) : WireEnum {
    RATE_LIMIT_REACHED("rate_limit_reached"),
    WORKSPACE_OWNER_CREDITS_DEPLETED("workspace_owner_credits_depleted"),
    WORKSPACE_MEMBER_CREDITS_DEPLETED("workspace_member_credits_depleted"),
    WORKSPACE_OWNER_USAGE_LIMIT_REACHED("workspace_owner_usage_limit_reached"),
    WORKSPACE_MEMBER_USAGE_LIMIT_REACHED("workspace_member_usage_limit_reached"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RateLimitReachedType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): RateLimitReachedType = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object RateLimitReachedTypeSerializer : WireEnumSerializer<RateLimitReachedType>("RateLimitReachedType", RateLimitReachedType::fromWire)

@Serializable(with = RateLimitResetCreditStatusSerializer::class)
public enum class RateLimitResetCreditStatus(override val wire: String) : WireEnum {
    AVAILABLE("available"),
    REDEEMING("redeeming"),
    REDEEMED("redeemed"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RateLimitResetCreditStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): RateLimitResetCreditStatus = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object RateLimitResetCreditStatusSerializer : WireEnumSerializer<RateLimitResetCreditStatus>("RateLimitResetCreditStatus", RateLimitResetCreditStatus::fromWire)

@Serializable(with = RateLimitResetTypeSerializer::class)
public enum class RateLimitResetType(override val wire: String) : WireEnum {
    CODEX_RATE_LIMITS("codexRateLimits"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RateLimitResetType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): RateLimitResetType = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object RateLimitResetTypeSerializer : WireEnumSerializer<RateLimitResetType>("RateLimitResetType", RateLimitResetType::fromWire)

@Serializable(with = RealtimeConversationVersionSerializer::class)
public enum class RealtimeConversationVersion(override val wire: String) : WireEnum {
    V1("v1"),
    V2("v2"),
    V3("v3"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RealtimeConversationVersion> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): RealtimeConversationVersion = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object RealtimeConversationVersionSerializer : WireEnumSerializer<RealtimeConversationVersion>("RealtimeConversationVersion", RealtimeConversationVersion::fromWire)

@Serializable(with = RealtimeOutputModalitySerializer::class)
public enum class RealtimeOutputModality(override val wire: String) : WireEnum {
    TEXT("text"),
    AUDIO("audio"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RealtimeOutputModality> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): RealtimeOutputModality = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object RealtimeOutputModalitySerializer : WireEnumSerializer<RealtimeOutputModality>("RealtimeOutputModality", RealtimeOutputModality::fromWire)

@Serializable(with = RealtimeVoiceSerializer::class)
public enum class RealtimeVoice(override val wire: String) : WireEnum {
    ALLOY("alloy"),
    ARBOR("arbor"),
    ASH("ash"),
    BALLAD("ballad"),
    BREEZE("breeze"),
    CEDAR("cedar"),
    CORAL("coral"),
    COVE("cove"),
    ECHO("echo"),
    EMBER("ember"),
    JUNIPER("juniper"),
    MAPLE("maple"),
    MARIN("marin"),
    SAGE("sage"),
    SHIMMER("shimmer"),
    SOL("sol"),
    SPRUCE("spruce"),
    VALE("vale"),
    VERSE("verse"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RealtimeVoice> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): RealtimeVoice = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object RealtimeVoiceSerializer : WireEnumSerializer<RealtimeVoice>("RealtimeVoice", RealtimeVoice::fromWire)

/**
 * A summary of the reasoning performed by the model. This can be useful for debugging and
 * understanding the model's reasoning process. See
 * https://platform.openai.com/docs/guides/reasoning?api-mode=responses#reasoning-summaries
 */
@Serializable(with = ReasoningSummarySerializer::class)
public enum class ReasoningSummary(override val wire: String) : WireEnum {
    AUTO("auto"),
    CONCISE("concise"),
    DETAILED("detailed"),
    NONE("none"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ReasoningSummary> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ReasoningSummary = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ReasoningSummarySerializer : WireEnumSerializer<ReasoningSummary>("ReasoningSummary", ReasoningSummary::fromWire)

@Serializable(with = RemoteControlConnectionStatusSerializer::class)
public enum class RemoteControlConnectionStatus(override val wire: String) : WireEnum {
    DISABLED("disabled"),
    CONNECTING("connecting"),
    CONNECTED("connected"),
    ERRORED("errored"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, RemoteControlConnectionStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): RemoteControlConnectionStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object RemoteControlConnectionStatusSerializer : WireEnumSerializer<RemoteControlConnectionStatus>("RemoteControlConnectionStatus", RemoteControlConnectionStatus::fromWire)

@Serializable(with = ResidencyRequirementSerializer::class)
public enum class ResidencyRequirement(override val wire: String) : WireEnum {
    US("us"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ResidencyRequirement> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ResidencyRequirement = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ResidencyRequirementSerializer : WireEnumSerializer<ResidencyRequirement>("ResidencyRequirement", ResidencyRequirement::fromWire)

@Serializable(with = ReviewDeliverySerializer::class)
public enum class ReviewDelivery(override val wire: String) : WireEnum {
    INLINE("inline"),
    DETACHED("detached"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ReviewDelivery> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ReviewDelivery = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ReviewDeliverySerializer : WireEnumSerializer<ReviewDelivery>("ReviewDelivery", ReviewDelivery::fromWire)

@Serializable(with = SandboxModeSerializer::class)
public enum class SandboxMode(override val wire: String) : WireEnum {
    READ_ONLY("read-only"),
    WORKSPACE_WRITE("workspace-write"),
    DANGER_FULL_ACCESS("danger-full-access"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, SandboxMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): SandboxMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object SandboxModeSerializer : WireEnumSerializer<SandboxMode>("SandboxMode", SandboxMode::fromWire)

@Serializable(with = ScheduledTaskWeekdaySerializer::class)
public enum class ScheduledTaskWeekday(override val wire: String) : WireEnum {
    MO("MO"),
    TU("TU"),
    WE("WE"),
    TH("TH"),
    FR("FR"),
    SA("SA"),
    SU("SU"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ScheduledTaskWeekday> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ScheduledTaskWeekday = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ScheduledTaskWeekdaySerializer : WireEnumSerializer<ScheduledTaskWeekday>("ScheduledTaskWeekday", ScheduledTaskWeekday::fromWire)

@Serializable(with = SkillScopeSerializer::class)
public enum class SkillScope(override val wire: String) : WireEnum {
    USER("user"),
    REPO("repo"),
    SYSTEM("system"),
    ADMIN("admin"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, SkillScope> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): SkillScope = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object SkillScopeSerializer : WireEnumSerializer<SkillScope>("SkillScope", SkillScope::fromWire)

@Serializable(with = SortDirectionSerializer::class)
public enum class SortDirection(override val wire: String) : WireEnum {
    ASC("asc"),
    DESC("desc"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, SortDirection> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): SortDirection = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object SortDirectionSerializer : WireEnumSerializer<SortDirection>("SortDirection", SortDirection::fromWire)

@Serializable(with = SubAgentActivityKindSerializer::class)
public enum class SubAgentActivityKind(override val wire: String) : WireEnum {
    STARTED("started"),
    INTERACTED("interacted"),
    INTERRUPTED("interrupted"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, SubAgentActivityKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): SubAgentActivityKind = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object SubAgentActivityKindSerializer : WireEnumSerializer<SubAgentActivityKind>("SubAgentActivityKind", SubAgentActivityKind::fromWire)

@Serializable(with = ThreadActiveFlagSerializer::class)
public enum class ThreadActiveFlag(override val wire: String) : WireEnum {
    WAITING_ON_APPROVAL("waitingOnApproval"),
    WAITING_ON_USER_INPUT("waitingOnUserInput"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadActiveFlag> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadActiveFlag = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadActiveFlagSerializer : WireEnumSerializer<ThreadActiveFlag>("ThreadActiveFlag", ThreadActiveFlag::fromWire)

@Serializable(with = ThreadGoalStatusSerializer::class)
public enum class ThreadGoalStatus(override val wire: String) : WireEnum {
    ACTIVE("active"),
    PAUSED("paused"),
    BLOCKED("blocked"),
    USAGE_LIMITED("usageLimited"),
    BUDGET_LIMITED("budgetLimited"),
    COMPLETE("complete"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadGoalStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadGoalStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadGoalStatusSerializer : WireEnumSerializer<ThreadGoalStatus>("ThreadGoalStatus", ThreadGoalStatus::fromWire)

@Serializable(with = ThreadHistoryModeSerializer::class)
public enum class ThreadHistoryMode(override val wire: String) : WireEnum {
    LEGACY("legacy"),
    PAGINATED("paginated"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadHistoryMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadHistoryMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadHistoryModeSerializer : WireEnumSerializer<ThreadHistoryMode>("ThreadHistoryMode", ThreadHistoryMode::fromWire)

@Serializable(with = ThreadMemoryModeSerializer::class)
public enum class ThreadMemoryMode(override val wire: String) : WireEnum {
    ENABLED("enabled"),
    DISABLED("disabled"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadMemoryMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadMemoryMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadMemoryModeSerializer : WireEnumSerializer<ThreadMemoryMode>("ThreadMemoryMode", ThreadMemoryMode::fromWire)

@Serializable(with = ThreadSortKeySerializer::class)
public enum class ThreadSortKey(override val wire: String) : WireEnum {
    CREATED_AT("created_at"),
    UPDATED_AT("updated_at"),
    RECENCY_AT("recency_at"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadSortKey> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadSortKey = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadSortKeySerializer : WireEnumSerializer<ThreadSortKey>("ThreadSortKey", ThreadSortKey::fromWire)

@Serializable(with = ThreadSourceKindSerializer::class)
public enum class ThreadSourceKind(override val wire: String) : WireEnum {
    CLI("cli"),
    VSCODE("vscode"),
    EXEC("exec"),
    APP_SERVER("appServer"),
    SUB_AGENT("subAgent"),
    SUB_AGENT_REVIEW("subAgentReview"),
    SUB_AGENT_COMPACT("subAgentCompact"),
    SUB_AGENT_THREAD_SPAWN("subAgentThreadSpawn"),
    SUB_AGENT_OTHER("subAgentOther"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadSourceKind> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): ThreadSourceKind = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object ThreadSourceKindSerializer : WireEnumSerializer<ThreadSourceKind>("ThreadSourceKind", ThreadSourceKind::fromWire)

@Serializable(with = ThreadStartSourceSerializer::class)
public enum class ThreadStartSource(override val wire: String) : WireEnum {
    STARTUP("startup"),
    CLEAR("clear"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadStartSource> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadStartSource = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadStartSourceSerializer : WireEnumSerializer<ThreadStartSource>("ThreadStartSource", ThreadStartSource::fromWire)

@Serializable(with = ThreadUnsubscribeStatusSerializer::class)
public enum class ThreadUnsubscribeStatus(override val wire: String) : WireEnum {
    NOT_LOADED("notLoaded"),
    NOT_SUBSCRIBED("notSubscribed"),
    UNSUBSCRIBED("unsubscribed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, ThreadUnsubscribeStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): ThreadUnsubscribeStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object ThreadUnsubscribeStatusSerializer : WireEnumSerializer<ThreadUnsubscribeStatus>("ThreadUnsubscribeStatus", ThreadUnsubscribeStatus::fromWire)

@Serializable(with = TurnItemsViewSerializer::class)
public enum class TurnItemsView(override val wire: String) : WireEnum {
    NOT_LOADED("notLoaded"),
    SUMMARY("summary"),
    FULL("full"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, TurnItemsView> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): TurnItemsView = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object TurnItemsViewSerializer : WireEnumSerializer<TurnItemsView>("TurnItemsView", TurnItemsView::fromWire)

@Serializable(with = TurnPlanStepStatusSerializer::class)
public enum class TurnPlanStepStatus(override val wire: String) : WireEnum {
    PENDING("pending"),
    IN_PROGRESS("inProgress"),
    COMPLETED("completed"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, TurnPlanStepStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): TurnPlanStepStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object TurnPlanStepStatusSerializer : WireEnumSerializer<TurnPlanStepStatus>("TurnPlanStepStatus", TurnPlanStepStatus::fromWire)

@Serializable(with = TurnStatusSerializer::class)
public enum class TurnStatus(override val wire: String) : WireEnum {
    COMPLETED("completed"),
    INTERRUPTED("interrupted"),
    FAILED("failed"),
    IN_PROGRESS("inProgress"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, TurnStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): TurnStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object TurnStatusSerializer : WireEnumSerializer<TurnStatus>("TurnStatus", TurnStatus::fromWire)

/**
 * Controls output length/detail on GPT-5 models via the Responses API. Serialized with
 * lowercase values to match the OpenAI API.
 */
@Serializable(with = VerbositySerializer::class)
public enum class Verbosity(override val wire: String) : WireEnum {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, Verbosity> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): Verbosity = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object VerbositySerializer : WireEnumSerializer<Verbosity>("Verbosity", Verbosity::fromWire)

@Serializable(with = WebSearchContextSizeSerializer::class)
public enum class WebSearchContextSize(override val wire: String) : WireEnum {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, WebSearchContextSize> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): WebSearchContextSize = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object WebSearchContextSizeSerializer : WireEnumSerializer<WebSearchContextSize>("WebSearchContextSize", WebSearchContextSize::fromWire)

@Serializable(with = WebSearchModeSerializer::class)
public enum class WebSearchMode(override val wire: String) : WireEnum {
    DISABLED("disabled"),
    CACHED("cached"),
    INDEXED("indexed"),
    LIVE("live"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, WebSearchMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): WebSearchMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object WebSearchModeSerializer : WireEnumSerializer<WebSearchMode>("WebSearchMode", WebSearchMode::fromWire)

@Serializable(with = WindowsSandboxReadinessSerializer::class)
public enum class WindowsSandboxReadiness(override val wire: String) : WireEnum {
    READY("ready"),
    NOT_CONFIGURED("notConfigured"),
    UPDATE_REQUIRED("updateRequired"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, WindowsSandboxReadiness> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): WindowsSandboxReadiness = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object WindowsSandboxReadinessSerializer : WireEnumSerializer<WindowsSandboxReadiness>("WindowsSandboxReadiness", WindowsSandboxReadiness::fromWire)

@Serializable(with = WindowsSandboxSetupModeSerializer::class)
public enum class WindowsSandboxSetupMode(override val wire: String) : WireEnum {
    ELEVATED("elevated"),
    UNELEVATED("unelevated"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, WindowsSandboxSetupMode> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): WindowsSandboxSetupMode = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object WindowsSandboxSetupModeSerializer : WireEnumSerializer<WindowsSandboxSetupMode>("WindowsSandboxSetupMode", WindowsSandboxSetupMode::fromWire)

@Serializable(with = WorkspaceMessageTypeSerializer::class)
public enum class WorkspaceMessageType(override val wire: String) : WireEnum {
    HEADLINE("headline"),
    ANNOUNCEMENT("announcement"),
    UNKNOWN("unknown"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN_(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, WorkspaceMessageType> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN_] for anything newer. */
        public fun fromWire(value: String): WorkspaceMessageType = BY_WIRE[value] ?: UNKNOWN_
    }
}

internal object WorkspaceMessageTypeSerializer : WireEnumSerializer<WorkspaceMessageType>("WorkspaceMessageType", WorkspaceMessageType::fromWire)

@Serializable(with = WriteStatusSerializer::class)
public enum class WriteStatus(override val wire: String) : WireEnum {
    OK("ok"),
    OK_OVERRIDDEN("okOverridden"),

    /** A value this SDK version does not know. Never produced by [wire] round-trips. */
    UNKNOWN(""),
    ;

    public companion object {
        private val BY_WIRE: Map<String, WriteStatus> =
            entries.associateBy { it.wire }

        /** Decode a wire value, falling back to [UNKNOWN] for anything newer. */
        public fun fromWire(value: String): WriteStatus = BY_WIRE[value] ?: UNKNOWN
    }
}

internal object WriteStatusSerializer : WireEnumSerializer<WriteStatus>("WriteStatus", WriteStatus::fromWire)

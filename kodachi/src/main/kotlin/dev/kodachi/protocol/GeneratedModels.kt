// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Object payloads, including every notification payload.

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
import kotlinx.serialization.json.put

@Serializable
public data class AccountLoginCompletedNotification(
    val success: Boolean,
    val error: String? = null,
    val loginId: String? = null,
) : CodexNotification {
    override val method: String get() = "account/login/completed"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

/**
 * Sparse rolling rate-limit update.
 *
 * Clients should merge available values into the most recent `account/rateLimits/read`
 * response or refetch that snapshot. Nullable account metadata may be unavailable in a rolling
 * update and does not clear a previously observed value.
 */
@Serializable
public data class AccountRateLimitsUpdatedNotification(
    val rateLimits: RateLimitSnapshot,
) : CodexNotification {
    override val method: String get() = "account/rateLimits/updated"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class AccountTokenUsageDailyBucket(
    val startDate: String,
    val tokens: Long,
)

@Serializable
public data class AccountTokenUsageSummary(
    val currentStreakDays: Long? = null,
    val lifetimeTokens: Long? = null,
    val longestRunningTurnSec: Long? = null,
    val longestStreakDays: Long? = null,
    val peakDailyTokens: Long? = null,
)

@Serializable
public data class AccountUpdatedNotification(
    val authMode: AuthMode? = null,
    val planType: PlanType? = null,
) : CodexNotification {
    override val method: String get() = "account/updated"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class ActivePermissionProfile(
    val id: String,
    val extends: String? = null,
)

@Serializable
public data class AdditionalContextEntry(
    val kind: AdditionalContextKind,
    val value: String,
)

@Serializable
public data class AdditionalFileSystemPermissions(
    val entries: List<FileSystemSandboxEntry>? = null,
    val globScanMaxDepth: Long? = null,
    val read: List<String>? = null,
    val write: List<String>? = null,
)

@Serializable
public data class AdditionalNetworkPermissions(
    val enabled: Boolean? = null,
)

@Serializable
public data class AdditionalPermissionProfile(
    val fileSystem: AdditionalFileSystemPermissions? = null,
    val network: AdditionalNetworkPermissions? = null,
)

@Serializable
public data class AgentMessageDeltaNotification(
    val delta: String,
    val itemId: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/agentMessage/delta"
}

@Serializable
public data class AnalyticsConfig(
    val enabled: Boolean? = null,
)

/**
 * EXPERIMENTAL - app metadata returned by app-list APIs.
 */
@Serializable
public data class AppBranding(
    val isDiscoverableApp: Boolean,
    val category: String? = null,
    val developer: String? = null,
    val privacyPolicy: String? = null,
    val termsOfService: String? = null,
    val website: String? = null,
)

@Serializable
public data class AppConfig(
    @SerialName("approvals_reviewer") val approvalsReviewer: ApprovalsReviewer? = null,
    @SerialName("default_tools_approval_mode") val defaultToolsApprovalMode: AppToolApproval? = null,
    @SerialName("default_tools_enabled") val defaultToolsEnabled: Boolean? = null,
    @SerialName("destructive_enabled") val destructiveEnabled: Boolean? = null,
    val enabled: Boolean? = null,
    @SerialName("open_world_enabled") val openWorldEnabled: Boolean? = null,
    val tools: AppToolsConfig? = null,
)

/**
 * EXPERIMENTAL - app metadata returned by app-list APIs.
 */
@Serializable
public data class AppInfo(
    val id: String,
    val name: String,
    val appMetadata: AppMetadata? = null,
    val branding: AppBranding? = null,
    val description: String? = null,
    val distributionChannel: String? = null,
    val iconAssets: Map<String, String>? = null,
    val iconDarkAssets: Map<String, String>? = null,
    val installUrl: String? = null,
    val isAccessible: Boolean? = null,
    val isEnabled: Boolean? = null,
    val labels: Map<String, String>? = null,
    val logoUrl: String? = null,
    val logoUrlDark: String? = null,
    val pluginDisplayNames: List<String> = emptyList(),
)

/**
 * EXPERIMENTAL - notification emitted when the app list changes.
 */
@Serializable
public data class AppListUpdatedNotification(
    val data: List<AppInfo>,
) : CodexNotification {
    override val method: String get() = "app/list/updated"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class AppMetadata(
    val categories: List<String>? = null,
    val developer: String? = null,
    val firstPartyRequiresInstall: Boolean? = null,
    val review: AppReview? = null,
    val screenshots: List<AppScreenshot>? = null,
    val seoDescription: String? = null,
    val showInComposerWhenUnlinked: Boolean? = null,
    val subCategories: List<String>? = null,
    val version: String? = null,
    val versionId: String? = null,
    val versionNotes: String? = null,
)

@Serializable
public data class AppReview(
    val status: String,
)

@Serializable
public data class AppScreenshot(
    val userPrompt: String,
    val fileId: String? = null,
    val url: String? = null,
)

/**
 * EXPERIMENTAL - app metadata summary for plugin responses.
 */
@Serializable
public data class AppSummary(
    val id: String,
    val name: String,
    val category: String? = null,
    val description: String? = null,
    val installUrl: String? = null,
)

@Serializable
public data class AppTemplateSummary(
    val materializedAppIds: List<String>,
    val name: String,
    val templateId: String,
    val canonicalConnectorId: String? = null,
    val category: String? = null,
    val description: String? = null,
    val logoUrl: String? = null,
    val logoUrlDark: String? = null,
    val reason: AppTemplateUnavailableReason? = null,
)

@Serializable
public data class AppToolConfig(
    @SerialName("approval_mode") val approvalMode: AppToolApproval? = null,
    val enabled: Boolean? = null,
)

/**
 * EXPERIMENTAL - metadata returned by app/read.
 */
@Serializable
public data class AppToolSummary(
    val description: String,
    val name: String,
    val disabledReason: String? = null,
    val isEnabled: Boolean? = null,
    val isReadOnly: Boolean? = null,
    val title: String? = null,
)

@Serializable
public class AppToolsConfig

@Serializable
public data class ApplyPatchApprovalParams(
    val callId: String,
    val conversationId: ThreadId,
    val fileChanges: Map<String, FileChange>,
    val grantRoot: String? = null,
    val reason: String? = null,
)

@Serializable
public data class ApplyPatchApprovalResponse(
    val decision: ReviewDecision,
)

@Serializable
public data class AppsConfig(
    @SerialName("_default") val default: AppsDefaultConfig? = null,
)

@Serializable
public data class AppsDefaultConfig(
    @SerialName("approvals_reviewer") val approvalsReviewer: ApprovalsReviewer? = null,
    @SerialName("default_tools_approval_mode") val defaultToolsApprovalMode: AppToolApproval? = null,
    @SerialName("destructive_enabled") val destructiveEnabled: Boolean? = null,
    val enabled: Boolean? = null,
    @SerialName("open_world_enabled") val openWorldEnabled: Boolean? = null,
)

/**
 * Read the committed installed connector runtime snapshot.
 */
@Serializable
public data class AppsInstalledParams(
    val forceRefresh: Boolean? = null,
    val threadId: String? = null,
)

/**
 * The installed connectors in one committed runtime snapshot.
 */
@Serializable
public data class AppsInstalledResponse(
    val apps: List<InstalledApp>,
)

/**
 * EXPERIMENTAL - list available apps/connectors.
 */
@Serializable
public data class AppsListParams(
    val cursor: String? = null,
    val forceRefetch: Boolean? = null,
    val limit: Int? = null,
    val threadId: String? = null,
)

/**
 * EXPERIMENTAL - app list response.
 */
@Serializable
public data class AppsListResponse(
    val data: List<AppInfo>,
    val nextCursor: String? = null,
)

/**
 * EXPERIMENTAL - read metadata for specific apps/connectors.
 */
@Serializable
public data class AppsReadParams(
    val appIds: List<String>,
    val includeTools: Boolean? = null,
)

/**
 * EXPERIMENTAL - app/read response.
 */
@Serializable
public data class AppsReadResponse(
    val apps: List<ConnectorMetadata>,
    val missingAppIds: List<String>,
)

@Serializable
public class AttestationGenerateParams

@Serializable
public data class AttestationGenerateResponse(
    val token: String,
)

@Serializable
public data class BrowserUseRequirements(
    val disableAutoReview: Boolean? = null,
)

@Serializable
public data class ByteRange(
    val end: Long,
    val start: Long,
)

@Serializable
public data class CancelLoginAccountParams(
    val loginId: String,
)

@Serializable
public data class CancelLoginAccountResponse(
    val status: CancelLoginAccountStatus,
)

@Serializable
public data class ChatgptAuthTokensRefreshParams(
    val reason: ChatgptAuthTokensRefreshReason,
    val previousAccountId: String? = null,
)

@Serializable
public data class ChatgptAuthTokensRefreshResponse(
    val accessToken: String,
    val chatgptAccountId: String,
    val chatgptPlanType: String? = null,
)

@Serializable
public data class ClientInfo(
    val name: String,
    val version: String,
    val title: String? = null,
)

@Serializable
public data class CollabAgentState(
    val status: CollabAgentStatus,
    val message: String? = null,
)

/**
 * Collaboration mode for a Codex session.
 */
@Serializable
public data class CollaborationMode(
    val mode: ModeKind,
    val settings: Settings,
)

/**
 * EXPERIMENTAL - collaboration mode preset metadata for clients.
 */
@Serializable
public data class CollaborationModeMask(
    val name: String,
    val mode: ModeKind? = null,
    val model: String? = null,
    @SerialName("reasoning_effort") val reasoningEffort: String? = null,
)

/**
 * Base64-encoded output chunk emitted for a streaming `command/exec` request.
 *
 * These notifications are connection-scoped. If the originating connection closes, the server
 * terminates the process.
 */
@Serializable
public data class CommandExecOutputDeltaNotification(
    val capReached: Boolean,
    val deltaBase64: String,
    val processId: String,
    val stream: CommandExecOutputStream,
) : CodexNotification {
    override val method: String get() = "command/exec/outputDelta"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

/**
 * Run a standalone command (argv vector) in the server sandbox without creating a thread or
 * turn.
 *
 * The final `command/exec` response is deferred until the process exits and is sent only after
 * all `command/exec/outputDelta` notifications for that connection have been emitted.
 */
@Serializable
public data class CommandExecParams(
    val command: List<String>,
    val cwd: String? = null,
    val disableOutputCap: Boolean? = null,
    val disableTimeout: Boolean? = null,
    val env: Map<String, String>? = null,
    val outputBytesCap: Long? = null,
    val tty: Boolean? = null,
    val processId: String? = null,
    val sandboxPolicy: SandboxPolicy? = null,
    val size: CommandExecTerminalSize? = null,
    val streamStdin: Boolean? = null,
    val streamStdoutStderr: Boolean? = null,
    val timeoutMs: Long? = null,
)

/**
 * Resize a running PTY-backed `command/exec` session.
 */
@Serializable
public data class CommandExecResizeParams(
    val processId: String,
    val size: CommandExecTerminalSize,
)

/**
 * Empty success response for `command/exec/resize`.
 */
@Serializable
public class CommandExecResizeResponse

/**
 * Final buffered result for `command/exec`.
 */
@Serializable
public data class CommandExecResponse(
    val exitCode: Int,
    val stderr: String,
    val stdout: String,
)

/**
 * PTY size in character cells for `command/exec` PTY sessions.
 */
@Serializable
public data class CommandExecTerminalSize(
    val cols: Int,
    val rows: Int,
)

/**
 * Terminate a running `command/exec` session.
 */
@Serializable
public data class CommandExecTerminateParams(
    val processId: String,
)

/**
 * Empty success response for `command/exec/terminate`.
 */
@Serializable
public class CommandExecTerminateResponse

/**
 * Write stdin bytes to a running `command/exec` session, close stdin, or both.
 */
@Serializable
public data class CommandExecWriteParams(
    val processId: String,
    val closeStdin: Boolean? = null,
    val deltaBase64: String? = null,
)

/**
 * Empty success response for `command/exec/write`.
 */
@Serializable
public class CommandExecWriteResponse

@Serializable
public data class CommandExecutionOutputDeltaNotification(
    val delta: String,
    val itemId: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/commandExecution/outputDelta"
}

@Serializable
public data class CommandExecutionRequestApprovalParams(
    val threadId: String,
    val turnId: String,
    val itemId: String,
    val startedAtMs: Long,
    val approvalId: String? = null,
    val command: String? = null,
    val commandActions: List<CommandAction>? = null,
    val cwd: String? = null,
    val environmentId: String? = null,
    val networkApprovalContext: NetworkApprovalContext? = null,
    val proposedExecpolicyAmendment: List<String>? = null,
    val proposedNetworkPolicyAmendments: List<NetworkPolicyAmendment>? = null,
    val reason: String? = null,
)

@Serializable
public data class CommandExecutionRequestApprovalResponse(
    val decision: CommandExecutionApprovalDecision,
)

@Serializable
public data class CommandMigration(
    val name: String,
)

@Serializable
public data class ComputerUseRequirements(
    val allowLockedComputerUse: Boolean? = null,
)

@Serializable
public data class Config(
    val analytics: AnalyticsConfig? = null,
    @SerialName("approval_policy") val approvalPolicy: AskForApproval? = null,
    @SerialName("approvals_reviewer") val approvalsReviewer: ApprovalsReviewer? = null,
    @SerialName("web_search") val webSearch: WebSearchMode? = null,
    @SerialName("compact_prompt") val compactPrompt: String? = null,
    val desktop: JsonObject? = null,
    @SerialName("developer_instructions") val developerInstructions: String? = null,
    @SerialName("forced_chatgpt_workspace_id") val forcedChatgptWorkspaceId: ForcedChatgptWorkspaceIds? = null,
    @SerialName("forced_login_method") val forcedLoginMethod: ForcedLoginMethod? = null,
    val instructions: String? = null,
    val model: String? = null,
    @SerialName("model_auto_compact_token_limit") val modelAutoCompactTokenLimit: Long? = null,
    @SerialName("model_auto_compact_token_limit_scope") val modelAutoCompactTokenLimitScope: AutoCompactTokenLimitScope? = null,
    @SerialName("model_context_window") val modelContextWindow: Long? = null,
    @SerialName("model_provider") val modelProvider: String? = null,
    @SerialName("model_reasoning_effort") val modelReasoningEffort: String? = null,
    @SerialName("model_reasoning_summary") val modelReasoningSummary: ReasoningSummary? = null,
    @SerialName("model_verbosity") val modelVerbosity: Verbosity? = null,
    @SerialName("review_model") val reviewModel: String? = null,
    @SerialName("sandbox_mode") val sandboxMode: SandboxMode? = null,
    @SerialName("sandbox_workspace_write") val sandboxWorkspaceWrite: SandboxWorkspaceWrite? = null,
    @SerialName("service_tier") val serviceTier: String? = null,
    val tools: ToolsV2? = null,
)

@Serializable
public data class ConfigBatchWriteParams(
    val edits: List<ConfigEdit>,
    val expectedVersion: String? = null,
    val filePath: String? = null,
    val reloadUserConfig: Boolean? = null,
)

@Serializable
public data class ConfigEdit(
    val keyPath: String,
    val mergeStrategy: MergeStrategy,
    val value: JsonElement,
)

@Serializable
public data class ConfigLayer(
    val config: JsonElement,
    val name: ConfigLayerSource,
    val version: String,
    val disabledReason: String? = null,
)

@Serializable
public data class ConfigLayerMetadata(
    val name: ConfigLayerSource,
    val version: String,
)

@Serializable
public data class ConfigReadParams(
    val cwd: String? = null,
    val includeLayers: Boolean? = null,
)

@Serializable
public data class ConfigReadResponse(
    val config: Config,
    val origins: Map<String, ConfigLayerMetadata>,
    val layers: List<ConfigLayer>? = null,
)

@Serializable
public data class ConfigRequirements(
    val allowAppshots: Boolean? = null,
    val allowLoginShell: Boolean? = null,
    val allowManagedHooksOnly: Boolean? = null,
    val allowRemoteControl: Boolean? = null,
    val allowedApprovalPolicies: List<AskForApproval>? = null,
    val sqliteHome: String? = null,
    val allowedPermissionProfiles: Map<String, Boolean>? = null,
    val allowedSandboxModes: List<SandboxMode>? = null,
    val allowedWebSearchModes: List<WebSearchMode>? = null,
    val allowedWindowsSandboxImplementations: List<WindowsSandboxSetupMode>? = null,
    val browserUse: BrowserUseRequirements? = null,
    val checkForUpdateOnStartup: Boolean? = null,
    val computerUse: ComputerUseRequirements? = null,
    val defaultPermissions: String? = null,
    val enforceResidency: ResidencyRequirement? = null,
    val featureRequirements: Map<String, Boolean>? = null,
    val feedback: FeedbackRequirements? = null,
    val windowsSandboxPrivateDesktop: Boolean? = null,
    val logDir: String? = null,
    val modelCatalogJson: String? = null,
    val models: ModelsRequirements? = null,
)

@Serializable
public data class ConfigRequirementsReadResponse(
    val requirements: ConfigRequirements? = null,
)

@Serializable
public data class ConfigValueWriteParams(
    val keyPath: String,
    val mergeStrategy: MergeStrategy,
    val value: JsonElement,
    val expectedVersion: String? = null,
    val filePath: String? = null,
)

@Serializable
public data class ConfigWarningNotification(
    val summary: String,
    val details: String? = null,
    val path: String? = null,
    val range: TextRange? = null,
) : CodexNotification {
    override val method: String get() = "configWarning"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class ConfigWriteResponse(
    val filePath: String,
    val status: WriteStatus,
    val version: String,
    val overriddenMetadata: OverriddenMetadata? = null,
)

@Serializable
public data class ConfiguredHookMatcherGroup(
    val hooks: List<ConfiguredHookHandler>,
    val matcher: String? = null,
)

/**
 * EXPERIMENTAL - metadata returned by app/read.
 */
@Serializable
public data class ConnectorMetadata(
    val id: String,
    val name: String,
    val description: String? = null,
    val distributionChannel: String? = null,
    val iconUrl: String? = null,
    val iconUrlDark: String? = null,
    val installUrl: String? = null,
    val pluginDisplayNames: List<String> = emptyList(),
    val toolSummaries: List<AppToolSummary>? = null,
)

@Serializable
public data class ConsumeAccountRateLimitResetCreditParams(
    val idempotencyKey: String,
    val creditId: String? = null,
)

@Serializable
public data class ConsumeAccountRateLimitResetCreditResponse(
    val outcome: ConsumeAccountRateLimitResetCreditOutcome,
)

/**
 * Deprecated: Use `ContextCompaction` item type instead.
 */
@Serializable
public data class ContextCompactedNotification(
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "thread/compacted"
}

@Serializable
public data class CreditsSnapshot(
    val hasCredits: Boolean,
    val unlimited: Boolean,
    val balance: String? = null,
)

@Serializable
public data class DeprecationNoticeNotification(
    val summary: String,
    val details: String? = null,
) : CodexNotification {
    override val method: String get() = "deprecationNotice"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class DynamicToolCallParams(
    val arguments: JsonElement,
    val callId: String,
    val threadId: String,
    val tool: String,
    val turnId: String,
    val namespace: String? = null,
)

@Serializable
public data class DynamicToolCallResponse(
    val contentItems: List<DynamicToolCallOutputContentItem>,
    val success: Boolean,
)

@Serializable
public data class EnvironmentConnectionNotification(
    val environmentId: String,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/environment/disconnected"
    override val turnId: String? get() = null
}

@Serializable
public data class ErrorNotification(
    val error: TurnError,
    override val threadId: String,
    override val turnId: String,
    val willRetry: Boolean,
) : CodexNotification {
    override val method: String get() = "error"
}

@Serializable
public data class ExecCommandApprovalParams(
    val callId: String,
    val command: List<String>,
    val conversationId: ThreadId,
    val cwd: String,
    val parsedCmd: List<ParsedCommand>,
    val approvalId: String? = null,
    val reason: String? = null,
)

@Serializable
public data class ExecCommandApprovalResponse(
    val decision: ReviewDecision,
)

@Serializable
public data class ExperimentalFeature(
    val defaultEnabled: Boolean,
    val enabled: Boolean,
    val name: String,
    val stage: ExperimentalFeatureStage,
    val announcement: String? = null,
    val description: String? = null,
    val displayName: String? = null,
)

@Serializable
public data class ExperimentalFeatureEnablementSetParams(
    val enablement: Map<String, Boolean>,
)

@Serializable
public data class ExperimentalFeatureEnablementSetResponse(
    val enablement: Map<String, Boolean>,
)

@Serializable
public data class ExperimentalFeatureListParams(
    val cursor: String? = null,
    val limit: Int? = null,
    val threadId: String? = null,
)

@Serializable
public data class ExperimentalFeatureListResponse(
    val data: List<ExperimentalFeature>,
    val nextCursor: String? = null,
)

@Serializable
public data class ExternalAgentConfigDetectParams(
    val cwds: List<String>? = null,
    val includeHome: Boolean? = null,
    val maxSessionAgeDays: Int? = null,
    val maxSessions: Int? = null,
    val migrationSource: String? = null,
    val source: String? = null,
)

@Serializable
public data class ExternalAgentConfigDetectResponse(
    val items: List<ExternalAgentConfigMigrationItem>,
)

@Serializable
public data class ExternalAgentConfigImportCompletedNotification(
    val importId: String,
    val itemTypeResults: List<ExternalAgentConfigImportTypeResult>,
) : CodexNotification {
    override val method: String get() = "externalAgentConfig/import/completed"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class ExternalAgentConfigImportHistoriesReadResponse(
    val connectors: List<ExternalAgentImportedConnectorCandidate>,
    val data: List<ExternalAgentConfigImportHistory>,
)

@Serializable
public data class ExternalAgentConfigImportHistory(
    val completedAtMs: Long,
    val failures: List<ExternalAgentConfigImportItemTypeFailure>,
    val importId: String,
    val successes: List<ExternalAgentConfigImportItemTypeSuccess>,
    val providerId: String? = null,
)

@Serializable
public data class ExternalAgentConfigImportHistoryRecordParams(
    val itemTypeResults: List<ExternalAgentConfigImportTypeResult>,
    val providerId: String,
)

@Serializable
public data class ExternalAgentConfigImportHistoryRecordResponse(
    val importId: String,
)

@Serializable
public data class ExternalAgentConfigImportItemTypeFailure(
    val failureStage: String,
    val itemType: ExternalAgentConfigMigrationItemType,
    val message: String,
    val cwd: String? = null,
    val errorType: String? = null,
    val source: String? = null,
    val subErrorType: String? = null,
)

@Serializable
public data class ExternalAgentConfigImportItemTypeSuccess(
    val itemType: ExternalAgentConfigMigrationItemType,
    val cwd: String? = null,
    val source: String? = null,
    val target: String? = null,
)

@Serializable
public data class ExternalAgentConfigImportParams(
    val migrationItems: List<ExternalAgentConfigMigrationItem>,
    val migrationSource: String? = null,
    val providerId: String? = null,
    val source: String? = null,
)

@Serializable
public data class ExternalAgentConfigImportProgressNotification(
    val importId: String,
    val itemTypeResults: List<ExternalAgentConfigImportTypeResult>,
) : CodexNotification {
    override val method: String get() = "externalAgentConfig/import/progress"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class ExternalAgentConfigImportResponse(
    val importId: String,
)

@Serializable
public data class ExternalAgentConfigImportTypeResult(
    val failures: List<ExternalAgentConfigImportItemTypeFailure>,
    val itemType: ExternalAgentConfigMigrationItemType,
    val successes: List<ExternalAgentConfigImportItemTypeSuccess>,
)

@Serializable
public data class ExternalAgentConfigMigrationItem(
    val description: String,
    val itemType: ExternalAgentConfigMigrationItemType,
    val cwd: String? = null,
    val details: MigrationDetails? = null,
)

@Serializable
public data class ExternalAgentImportedConnectorCandidate(
    val name: String,
    val sessionCount: Int,
    val source: ExternalAgentImportedConnectorSource,
)

@Serializable
public data class FeedbackRequirements(
    val enabled: Boolean? = null,
)

@Serializable
public data class FeedbackUploadParams(
    val classification: String,
    val extraLogFiles: List<String>? = null,
    val includeLogs: Boolean? = null,
    val reason: String? = null,
    val tags: Map<String, String>? = null,
    val threadId: String? = null,
)

@Serializable
public data class FeedbackUploadResponse(
    val threadId: String,
)

/**
 * Deprecated legacy notification for `apply_patch` textual output.
 *
 * The server no longer emits this notification.
 */
@Serializable
public data class FileChangeOutputDeltaNotification(
    val delta: String,
    val itemId: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/fileChange/outputDelta"
}

@Serializable
public data class FileChangePatchUpdatedNotification(
    val changes: List<FileUpdateChange>,
    val itemId: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/fileChange/patchUpdated"
}

@Serializable
public data class FileChangeRequestApprovalParams(
    val itemId: String,
    val startedAtMs: Long,
    val threadId: String,
    val turnId: String,
    val grantRoot: String? = null,
    val reason: String? = null,
)

@Serializable
public data class FileChangeRequestApprovalResponse(
    val decision: FileChangeApprovalDecision,
)

@Serializable
public data class FileSystemSandboxEntry(
    val access: FileSystemAccessMode,
    val path: FileSystemPath,
)

@Serializable
public data class FileUpdateChange(
    val diff: String,
    val kind: PatchChangeKind,
    val path: String,
)

/**
 * Filesystem watch notification emitted for `fs/watch` subscribers.
 */
@Serializable
public data class FsChangedNotification(
    val changedPaths: List<String>,
    val watchId: String,
) : CodexNotification {
    override val method: String get() = "fs/changed"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

/**
 * Copy a file or directory tree on the host filesystem.
 */
@Serializable
public data class FsCopyParams(
    val destinationPath: String,
    val sourcePath: String,
    val recursive: Boolean? = null,
)

/**
 * Successful response for `fs/copy`.
 */
@Serializable
public class FsCopyResponse

/**
 * Create a directory on the host filesystem.
 */
@Serializable
public data class FsCreateDirectoryParams(
    val path: String,
    val recursive: Boolean? = null,
)

/**
 * Successful response for `fs/createDirectory`.
 */
@Serializable
public class FsCreateDirectoryResponse

/**
 * Request metadata for an absolute path.
 */
@Serializable
public data class FsGetMetadataParams(
    val path: String,
)

/**
 * Metadata returned by `fs/getMetadata`.
 */
@Serializable
public data class FsGetMetadataResponse(
    val createdAtMs: Long,
    val isDirectory: Boolean,
    val isFile: Boolean,
    val isSymlink: Boolean,
    val modifiedAtMs: Long,
)

/**
 * A directory entry returned by `fs/readDirectory`.
 */
@Serializable
public data class FsReadDirectoryEntry(
    val fileName: String,
    val isDirectory: Boolean,
    val isFile: Boolean,
)

/**
 * List direct child names for a directory.
 */
@Serializable
public data class FsReadDirectoryParams(
    val path: String,
)

/**
 * Directory entries returned by `fs/readDirectory`.
 */
@Serializable
public data class FsReadDirectoryResponse(
    val entries: List<FsReadDirectoryEntry>,
)

/**
 * Read a file from the host filesystem.
 */
@Serializable
public data class FsReadFileParams(
    val path: String,
)

/**
 * Base64-encoded file contents returned by `fs/readFile`.
 */
@Serializable
public data class FsReadFileResponse(
    val dataBase64: String,
)

/**
 * Remove a file or directory tree from the host filesystem.
 */
@Serializable
public data class FsRemoveParams(
    val path: String,
    val force: Boolean? = null,
    val recursive: Boolean? = null,
)

/**
 * Successful response for `fs/remove`.
 */
@Serializable
public class FsRemoveResponse

/**
 * Stop filesystem watch notifications for a prior `fs/watch`.
 */
@Serializable
public data class FsUnwatchParams(
    val watchId: String,
)

/**
 * Successful response for `fs/unwatch`.
 */
@Serializable
public class FsUnwatchResponse

/**
 * Start filesystem watch notifications for an absolute path.
 */
@Serializable
public data class FsWatchParams(
    val path: String,
    val watchId: String,
)

/**
 * Successful response for `fs/watch`.
 */
@Serializable
public data class FsWatchResponse(
    val path: String,
)

/**
 * Write a file on the host filesystem.
 */
@Serializable
public data class FsWriteFileParams(
    val dataBase64: String,
    val path: String,
)

/**
 * Successful response for `fs/writeFile`.
 */
@Serializable
public class FsWriteFileResponse

@Serializable
public data class FuzzyFileSearchParams(
    val query: String,
    val roots: List<String>,
    val cancellationToken: String? = null,
)

@Serializable
public data class FuzzyFileSearchResponse(
    val files: List<FuzzyFileSearchResult>,
)

/**
 * Superset of [`codex_file_search::FileMatch`]
 */
@Serializable
public data class FuzzyFileSearchResult(
    @SerialName("file_name") val fileName: String,
    @SerialName("match_type") val matchType: FuzzyFileSearchMatchType,
    val path: String,
    val root: String,
    val score: Int,
    val indices: List<Int>? = null,
)

@Serializable
public data class FuzzyFileSearchSessionCompletedNotification(
    val sessionId: String,
) : CodexNotification {
    override val method: String get() = "fuzzyFileSearch/sessionCompleted"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class FuzzyFileSearchSessionUpdatedNotification(
    val files: List<FuzzyFileSearchResult>,
    val query: String,
    val sessionId: String,
) : CodexNotification {
    override val method: String get() = "fuzzyFileSearch/sessionUpdated"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class GetAccountParams(
    val refreshToken: Boolean? = null,
)

@Serializable
public data class GetAccountRateLimitsResponse(
    val rateLimits: RateLimitSnapshot,
    val rateLimitResetCredits: RateLimitResetCreditsSummary? = null,
    val rateLimitsByLimitId: Map<String, RateLimitSnapshot>? = null,
)

@Serializable
public data class GetAccountResponse(
    val requiresOpenaiAuth: Boolean,
    val account: Account? = null,
)

@Serializable
public data class GetAccountTokenUsageResponse(
    val summary: AccountTokenUsageSummary,
    val dailyUsageBuckets: List<AccountTokenUsageDailyBucket>? = null,
)

@Serializable
public data class GetWorkspaceMessagesResponse(
    val featureEnabled: Boolean,
    val messages: List<WorkspaceMessage>,
)

@Serializable
public data class GitInfo(
    val branch: String? = null,
    val originUrl: String? = null,
    val sha: String? = null,
)

@Serializable
public data class GrantedPermissionProfile(
    val fileSystem: AdditionalFileSystemPermissions? = null,
    val network: AdditionalNetworkPermissions? = null,
)

/**
 * [UNSTABLE] Temporary approval auto-review payload used by `item/autoApprovalReview&#47;*`
 * notifications. This shape is expected to change soon.
 */
@Serializable
public data class GuardianApprovalReview(
    val status: GuardianApprovalReviewStatus,
    val rationale: String? = null,
    val riskLevel: GuardianRiskLevel? = null,
    val userAuthorization: GuardianUserAuthorization? = null,
)

@Serializable
public data class GuardianWarningNotification(
    val message: String,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "guardianWarning"
    override val turnId: String? get() = null
}

@Serializable
public data class HookCompletedNotification(
    val run: HookRunSummary,
    override val threadId: String,
    override val turnId: String? = null,
) : CodexNotification {
    override val method: String get() = "hook/completed"
}

@Serializable
public data class HookErrorInfo(
    val message: String,
    val path: String,
)

@Serializable
public data class HookMetadata(
    val currentHash: String,
    val displayOrder: Long,
    val enabled: Boolean,
    val eventName: HookEventName,
    val handlerType: HookHandlerType,
    val isManaged: Boolean,
    val key: String,
    val source: HookSource,
    val sourcePath: String,
    val timeoutSec: Long,
    val trustStatus: HookTrustStatus,
    val additionalContextLimit: Long? = null,
    val command: String? = null,
    val matcher: String? = null,
    val pluginId: String? = null,
    val statusMessage: String? = null,
)

@Serializable
public data class HookMigration(
    val name: String,
)

@Serializable
public data class HookOutputEntry(
    val kind: HookOutputEntryKind,
    val text: String,
)

@Serializable
public data class HookPromptFragment(
    val hookRunId: String,
    val text: String,
)

@Serializable
public data class HookRunSummary(
    val displayOrder: Long,
    val entries: List<HookOutputEntry>,
    val eventName: HookEventName,
    val executionMode: HookExecutionMode,
    val handlerType: HookHandlerType,
    val id: String,
    val scope: HookScope,
    val sourcePath: String,
    val startedAt: Long,
    val status: HookRunStatus,
    val completedAt: Long? = null,
    val durationMs: Long? = null,
    val source: HookSource? = null,
    val statusMessage: String? = null,
)

@Serializable
public data class HookStartedNotification(
    val run: HookRunSummary,
    override val threadId: String,
    override val turnId: String? = null,
) : CodexNotification {
    override val method: String get() = "hook/started"
}

@Serializable
public data class HooksListEntry(
    val cwd: String,
    val errors: List<HookErrorInfo>,
    val hooks: List<HookMetadata>,
    val warnings: List<String>,
)

@Serializable
public data class HooksListParams(
    val cwds: List<String> = emptyList(),
)

@Serializable
public data class HooksListResponse(
    val data: List<HooksListEntry>,
)

/**
 * Client-declared capabilities negotiated during initialize.
 */
@Serializable
public data class InitializeCapabilities(
    val experimentalApi: Boolean? = null,
    val mcpServerOpenaiFormElicitation: Boolean? = null,
    val optOutNotificationMethods: List<String>? = null,
    val requestAttestation: Boolean? = null,
)

@Serializable
public data class InitializeParams(
    val clientInfo: ClientInfo,
    val capabilities: InitializeCapabilities? = null,
)

@Serializable
public data class InitializeResponse(
    val codexHome: String,
    val platformFamily: String,
    val platformOs: String,
    val userAgent: String,
)

/**
 * Installed connector runtime state.
 */
@Serializable
public data class InstalledApp(
    val callable: Boolean,
    val enabled: Boolean,
    val id: String,
    val runtimeName: String? = null,
)

/**
 * Internal Responses API passthrough metadata copied into underlying chat messages.
 *
 * Responses API strongly types this payload. Do not modify it without first getting API
 * approval and making the corresponding Responses API change.
 */
@Serializable
public data class InternalChatMessageMetadataPassthrough(
    @SerialName("turn_id") val turnId: String? = null,
)

@Serializable
public data class ItemCompletedNotification(
    val completedAtMs: Long,
    val item: ThreadItem,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/completed"
}

/**
 * [UNSTABLE] Temporary notification payload for approval auto-review. This shape is expected
 * to change soon.
 */
@Serializable
public data class ItemGuardianApprovalReviewCompletedNotification(
    val action: GuardianApprovalReviewAction,
    val completedAtMs: Long,
    val decisionSource: AutoReviewDecisionSource,
    val review: GuardianApprovalReview,
    val reviewId: String,
    val startedAtMs: Long,
    override val threadId: String,
    override val turnId: String,
    val targetItemId: String? = null,
) : CodexNotification {
    override val method: String get() = "item/autoApprovalReview/completed"
}

/**
 * [UNSTABLE] Temporary notification payload for approval auto-review. This shape is expected
 * to change soon.
 */
@Serializable
public data class ItemGuardianApprovalReviewStartedNotification(
    val action: GuardianApprovalReviewAction,
    val review: GuardianApprovalReview,
    val reviewId: String,
    val startedAtMs: Long,
    override val threadId: String,
    override val turnId: String,
    val targetItemId: String? = null,
) : CodexNotification {
    override val method: String get() = "item/autoApprovalReview/started"
}

@Serializable
public data class ItemStartedNotification(
    val item: ThreadItem,
    val startedAtMs: Long,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/started"
}

@Serializable
public data class ListMcpServerStatusParams(
    val cursor: String? = null,
    val detail: McpServerStatusDetail? = null,
    val limit: Int? = null,
    val threadId: String? = null,
)

@Serializable
public data class ListMcpServerStatusResponse(
    val data: List<McpServerStatus>,
    val nextCursor: String? = null,
)

@Serializable
public class LogoutAccountResponse

@Serializable
public data class ManagedHooksRequirements(
    @SerialName("PermissionRequest") val permissionRequest: List<ConfiguredHookMatcherGroup>,
    @SerialName("PostCompact") val postCompact: List<ConfiguredHookMatcherGroup>,
    @SerialName("PostToolUse") val postToolUse: List<ConfiguredHookMatcherGroup>,
    @SerialName("PreCompact") val preCompact: List<ConfiguredHookMatcherGroup>,
    @SerialName("PreToolUse") val preToolUse: List<ConfiguredHookMatcherGroup>,
    @SerialName("SessionStart") val sessionStart: List<ConfiguredHookMatcherGroup>,
    @SerialName("Stop") val stop: List<ConfiguredHookMatcherGroup>,
    @SerialName("SubagentStart") val subagentStart: List<ConfiguredHookMatcherGroup>,
    @SerialName("SubagentStop") val subagentStop: List<ConfiguredHookMatcherGroup>,
    @SerialName("UserPromptSubmit") val userPromptSubmit: List<ConfiguredHookMatcherGroup>,
    @SerialName("SessionEnd") val sessionEnd: List<ConfiguredHookMatcherGroup> = emptyList(),
    val managedDir: String? = null,
    val windowsManagedDir: String? = null,
)

@Serializable
public data class MarketplaceAddParams(
    val source: String,
    val refName: String? = null,
    val sparsePaths: List<String>? = null,
)

@Serializable
public data class MarketplaceAddResponse(
    val alreadyAdded: Boolean,
    val installedRoot: String,
    val marketplaceName: String,
)

@Serializable
public data class MarketplaceInterface(
    val displayName: String? = null,
)

@Serializable
public data class MarketplaceLoadErrorInfo(
    val marketplacePath: String,
    val message: String,
)

@Serializable
public data class MarketplaceRemoveParams(
    val marketplaceName: String,
)

@Serializable
public data class MarketplaceRemoveResponse(
    val marketplaceName: String,
    val installedRoot: String? = null,
)

@Serializable
public data class MarketplaceUpgradeErrorInfo(
    val marketplaceName: String,
    val message: String,
)

@Serializable
public data class MarketplaceUpgradeParams(
    val marketplaceName: String? = null,
)

@Serializable
public data class MarketplaceUpgradeResponse(
    val errors: List<MarketplaceUpgradeErrorInfo>,
    val selectedMarketplaces: List<String>,
    val upgradedRoots: List<String>,
)

@Serializable
public data class McpElicitationBooleanSchema(
    val type: McpElicitationBooleanType,
    val default: Boolean? = null,
    val description: String? = null,
    val title: String? = null,
)

@Serializable
public data class McpElicitationConstOption(
    val const: String,
    val title: String,
)

@Serializable
public data class McpElicitationLegacyTitledEnumSchema(
    val enum: List<String>,
    val type: McpElicitationStringType,
    val default: String? = null,
    val description: String? = null,
    val enumNames: List<String>? = null,
    val title: String? = null,
)

@Serializable
public data class McpElicitationNumberSchema(
    val type: McpElicitationNumberType,
    val default: Double? = null,
    val description: String? = null,
    val maximum: Double? = null,
    val minimum: Double? = null,
    val title: String? = null,
)

/**
 * Typed form schema for MCP `elicitation/create` requests.
 *
 * This matches the `requestedSchema` shape from the MCP 2025-11-25 `ElicitRequestFormParams`
 * schema.
 */
@Serializable
public data class McpElicitationSchema(
    val properties: Map<String, McpElicitationPrimitiveSchema>,
    val type: McpElicitationObjectType,
    val `$schema`: String? = null,
    val required: List<String>? = null,
)

@Serializable
public data class McpElicitationStringSchema(
    val type: McpElicitationStringType,
    val default: String? = null,
    val description: String? = null,
    val format: McpElicitationStringFormat? = null,
    val maxLength: Int? = null,
    val minLength: Int? = null,
    val title: String? = null,
)

@Serializable
public data class McpElicitationTitledEnumItems(
    val anyOf: List<McpElicitationConstOption>,
)

@Serializable
public data class McpElicitationTitledMultiSelectEnumSchema(
    val items: McpElicitationTitledEnumItems,
    val type: McpElicitationArrayType,
    val default: List<String>? = null,
    val description: String? = null,
    val maxItems: Long? = null,
    val minItems: Long? = null,
    val title: String? = null,
)

@Serializable
public data class McpElicitationTitledSingleSelectEnumSchema(
    val oneOf: List<McpElicitationConstOption>,
    val type: McpElicitationStringType,
    val default: String? = null,
    val description: String? = null,
    val title: String? = null,
)

@Serializable
public data class McpElicitationUntitledEnumItems(
    val enum: List<String>,
    val type: McpElicitationStringType,
)

@Serializable
public data class McpElicitationUntitledMultiSelectEnumSchema(
    val items: McpElicitationUntitledEnumItems,
    val type: McpElicitationArrayType,
    val default: List<String>? = null,
    val description: String? = null,
    val maxItems: Long? = null,
    val minItems: Long? = null,
    val title: String? = null,
)

@Serializable
public data class McpElicitationUntitledSingleSelectEnumSchema(
    val enum: List<String>,
    val type: McpElicitationStringType,
    val default: String? = null,
    val description: String? = null,
    val title: String? = null,
)

@Serializable
public data class McpResourceReadParams(
    val server: String,
    val uri: String,
    val threadId: String? = null,
)

@Serializable
public data class McpResourceReadResponse(
    val contents: List<ResourceContent>,
)

@Serializable
public data class McpServerElicitationRequestParams(
    val serverName: String,
    val threadId: String,
    val turnId: String? = null,
    @SerialName("_meta") val meta: JsonElement? = null,
    val message: String? = null,
    val mode: String? = null,
    val requestedSchema: McpElicitationSchema? = null,
    val elicitationId: String? = null,
    val url: String? = null,
)

@Serializable
public data class McpServerElicitationRequestResponse(
    val action: McpServerElicitationAction,
    @SerialName("_meta") val meta: JsonElement? = null,
    val content: JsonElement? = null,
)

/**
 * Presentation metadata advertised by an initialized MCP server.
 */
@Serializable
public data class McpServerInfo(
    val name: String,
    val version: String,
    val description: String? = null,
    val icons: List<JsonElement>? = null,
    val title: String? = null,
    val websiteUrl: String? = null,
)

@Serializable
public data class McpServerMigration(
    val name: String,
)

@Serializable
public data class McpServerOauthLoginCompletedNotification(
    val name: String,
    val success: Boolean,
    val error: String? = null,
    override val threadId: String? = null,
) : CodexNotification {
    override val method: String get() = "mcpServer/oauthLogin/completed"
    override val turnId: String? get() = null
}

@Serializable
public data class McpServerOauthLoginParams(
    val name: String,
    val scopes: List<String>? = null,
    val threadId: String? = null,
    val timeoutSecs: Long? = null,
)

@Serializable
public data class McpServerOauthLoginResponse(
    val authorizationUrl: String,
)

@Serializable
public class McpServerRefreshResponse

@Serializable
public data class McpServerStatus(
    val authStatus: McpAuthStatus,
    val name: String,
    val resourceTemplates: List<ResourceTemplate>,
    val resources: List<Resource>,
    val tools: Map<String, Tool>,
    val serverInfo: McpServerInfo? = null,
)

@Serializable
public data class McpServerStatusUpdatedNotification(
    val name: String,
    val status: McpServerStartupState,
    val error: String? = null,
    val failureReason: McpServerStartupFailureReason? = null,
    override val threadId: String? = null,
) : CodexNotification {
    override val method: String get() = "mcpServer/startupStatus/updated"
    override val turnId: String? get() = null
}

@Serializable
public data class McpServerToolCallParams(
    val server: String,
    val threadId: String,
    val tool: String,
    @SerialName("_meta") val meta: JsonElement? = null,
    val arguments: JsonElement? = null,
)

@Serializable
public data class McpServerToolCallResponse(
    val content: List<JsonElement>,
    @SerialName("_meta") val meta: JsonElement? = null,
    val isError: Boolean? = null,
    val structuredContent: JsonElement? = null,
)

@Serializable
public data class McpToolCallAppContext(
    val connectorId: String,
    val actionName: String? = null,
    val appName: String? = null,
    val linkId: String? = null,
    val resourceUri: String? = null,
)

@Serializable
public data class McpToolCallError(
    val message: String,
)

@Serializable
public data class McpToolCallProgressNotification(
    val itemId: String,
    val message: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/mcpToolCall/progress"
}

@Serializable
public data class McpToolCallResult(
    val content: List<JsonElement>,
    @SerialName("_meta") val meta: JsonElement? = null,
    val structuredContent: JsonElement? = null,
)

@Serializable
public data class MemoryCitation(
    val entries: List<MemoryCitationEntry>,
    val threadIds: List<String>,
)

@Serializable
public data class MemoryCitationEntry(
    val lineEnd: Int,
    val lineStart: Int,
    val note: String,
    val path: String,
)

@Serializable
public data class MigrationDetails(
    val commands: List<CommandMigration> = emptyList(),
    val hooks: List<HookMigration> = emptyList(),
    val mcpServers: List<McpServerMigration> = emptyList(),
    val memory: List<String> = emptyList(),
    val plugins: List<PluginsMigration> = emptyList(),
    val sessions: List<SessionMigration> = emptyList(),
    val skills: List<SkillMigration> = emptyList(),
    val subagents: List<SubagentMigration> = emptyList(),
)

@Serializable
public data class Model(
    val defaultReasoningEffort: String,
    val description: String,
    val displayName: String,
    val hidden: Boolean,
    val id: String,
    val isDefault: Boolean,
    val model: String,
    val supportedReasoningEfforts: List<ReasoningEffortOption>,
    val additionalSpeedTiers: List<String> = emptyList(),
    val availabilityNux: ModelAvailabilityNux? = null,
    val defaultServiceTier: String? = null,
    val inputModalities: List<InputModality>? = null,
    val serviceTiers: List<ModelServiceTier> = emptyList(),
    val supportsPersonality: Boolean? = null,
    val upgrade: String? = null,
    val upgradeInfo: ModelUpgradeInfo? = null,
)

@Serializable
public data class ModelAvailabilityNux(
    val message: String,
)

@Serializable
public data class ModelListParams(
    val cursor: String? = null,
    val includeHidden: Boolean? = null,
    val limit: Int? = null,
)

@Serializable
public data class ModelListResponse(
    val data: List<Model>,
    val nextCursor: String? = null,
)

@Serializable
public class ModelProviderCapabilitiesReadParams

@Serializable
public data class ModelProviderCapabilitiesReadResponse(
    val imageGeneration: Boolean,
    val namespaceTools: Boolean,
    val webSearch: Boolean,
)

@Serializable
public data class ModelReroutedNotification(
    val fromModel: String,
    val reason: ModelRerouteReason,
    override val threadId: String,
    val toModel: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "model/rerouted"
}

@Serializable
public data class ModelSafetyBufferingUpdatedNotification(
    val model: String,
    val reasons: List<String>,
    val showBufferingUi: Boolean,
    override val threadId: String,
    override val turnId: String,
    val useCases: List<String>,
    val fasterModel: String? = null,
) : CodexNotification {
    override val method: String get() = "model/safetyBuffering/updated"
}

@Serializable
public data class ModelServiceTier(
    val description: String,
    val id: String,
    val name: String,
)

@Serializable
public data class ModelUpgradeInfo(
    val model: String,
    val migrationMarkdown: String? = null,
    val modelLink: String? = null,
    val upgradeCopy: String? = null,
)

@Serializable
public data class ModelVerificationNotification(
    override val threadId: String,
    override val turnId: String,
    val verifications: List<ModelVerification>,
) : CodexNotification {
    override val method: String get() = "model/verification"
}

@Serializable
public data class ModelsRequirements(
    val newThread: NewThreadModelDefaults? = null,
)

@Serializable
public data class NetworkApprovalContext(
    val host: String,
    val protocol: NetworkApprovalProtocol,
)

@Serializable
public data class NetworkPolicyAmendment(
    val action: NetworkPolicyRuleAction,
    val host: String,
)

@Serializable
public data class NetworkRequirements(
    val allowLocalBinding: Boolean? = null,
    val allowUnixSockets: List<String>? = null,
    val allowUpstreamProxy: Boolean? = null,
    val allowedDomains: List<String>? = null,
    val dangerouslyAllowAllUnixSockets: Boolean? = null,
    val dangerouslyAllowNonLoopbackProxy: Boolean? = null,
    val deniedDomains: List<String>? = null,
    val domains: Map<String, NetworkDomainPermission>? = null,
    val enabled: Boolean? = null,
    val httpPort: Int? = null,
    val managedAllowedDomainsOnly: Boolean? = null,
    val socksPort: Int? = null,
    val unixSockets: Map<String, NetworkUnixSocketPermission>? = null,
)

@Serializable
public data class NewThreadModelDefaults(
    val model: String? = null,
    val modelReasoningEffort: String? = null,
    val serviceTier: String? = null,
)

@Serializable
public data class OverriddenMetadata(
    val effectiveValue: JsonElement,
    val message: String,
    val overridingLayer: ConfigLayerMetadata,
)

@Serializable
public data class PermissionProfileListParams(
    val cursor: String? = null,
    val cwd: String? = null,
    val limit: Int? = null,
)

@Serializable
public data class PermissionProfileListResponse(
    val data: List<PermissionProfileSummary>,
    val nextCursor: String? = null,
)

@Serializable
public data class PermissionProfileSummary(
    val allowed: Boolean,
    val id: String,
    val description: String? = null,
)

@Serializable
public data class PermissionsRequestApprovalParams(
    val cwd: String,
    val itemId: String,
    val permissions: RequestPermissionProfile,
    val startedAtMs: Long,
    val threadId: String,
    val turnId: String,
    val environmentId: String? = null,
    val reason: String? = null,
)

@Serializable
public data class PermissionsRequestApprovalResponse(
    val permissions: GrantedPermissionProfile,
    val scope: PermissionGrantScope? = null,
    val strictAutoReview: Boolean? = null,
)

/**
 * EXPERIMENTAL - proposed plan streaming deltas for plan items. Clients should not assume
 * concatenated deltas match the completed plan item content.
 */
@Serializable
public data class PlanDeltaNotification(
    val delta: String,
    val itemId: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/plan/delta"
}

@Serializable
public data class PluginDetail(
    val appTemplates: List<AppTemplateSummary>,
    val apps: List<AppSummary>,
    val hooks: List<PluginHookSummary>,
    val marketplaceName: String,
    val mcpServers: List<String>,
    val skills: List<SkillSummary>,
    val summary: PluginSummary,
    val description: String? = null,
    val marketplacePath: String? = null,
    val scheduledTasks: List<ScheduledTaskSummary>? = null,
    val shareUrl: String? = null,
)

@Serializable
public data class PluginHookSummary(
    val eventName: HookEventName,
    val key: String,
)

@Serializable
public data class PluginInstallParams(
    val pluginName: String,
    val marketplacePath: String? = null,
    val remoteMarketplaceName: String? = null,
)

@Serializable
public data class PluginInstallResponse(
    val appsNeedingAuth: List<AppSummary>,
    val authPolicy: PluginAuthPolicy,
)

@Serializable
public data class PluginInstalledParams(
    val cwds: List<String>? = null,
    val installSuggestionPluginNames: List<String>? = null,
)

@Serializable
public data class PluginInstalledResponse(
    val marketplaces: List<PluginMarketplaceEntry>,
    val marketplaceLoadErrors: List<MarketplaceLoadErrorInfo> = emptyList(),
)

@Serializable
public data class PluginInterface(
    val capabilities: List<String>,
    val screenshotUrls: List<String>,
    val screenshots: List<String>,
    val brandColor: String? = null,
    val category: String? = null,
    val composerIcon: String? = null,
    val composerIconUrl: String? = null,
    val defaultPrompt: List<String>? = null,
    val developerName: String? = null,
    val displayName: String? = null,
    val logo: String? = null,
    val logoDark: String? = null,
    val logoUrl: String? = null,
    val logoUrlDark: String? = null,
    val longDescription: String? = null,
    val privacyPolicyUrl: String? = null,
    val shortDescription: String? = null,
    val termsOfServiceUrl: String? = null,
    val websiteUrl: String? = null,
)

@Serializable
public data class PluginListParams(
    val cwds: List<String>? = null,
    val forceRefetch: Boolean? = null,
    val marketplaceKinds: List<PluginListMarketplaceKind>? = null,
)

@Serializable
public data class PluginListResponse(
    val marketplaces: List<PluginMarketplaceEntry>,
    val featuredPluginIds: List<String> = emptyList(),
    val marketplaceLoadErrors: List<MarketplaceLoadErrorInfo> = emptyList(),
)

@Serializable
public data class PluginMarketplaceEntry(
    val name: String,
    val plugins: List<PluginSummary>,
    val `interface`: MarketplaceInterface? = null,
    val path: String? = null,
)

@Serializable
public data class PluginReadParams(
    val pluginName: String,
    val marketplacePath: String? = null,
    val remoteMarketplaceName: String? = null,
)

@Serializable
public data class PluginReadResponse(
    val plugin: PluginDetail,
)

@Serializable
public data class PluginShareCheckoutParams(
    val remotePluginId: String,
)

@Serializable
public data class PluginShareCheckoutResponse(
    val marketplaceName: String,
    val marketplacePath: String,
    val pluginId: String,
    val pluginName: String,
    val pluginPath: String,
    val remotePluginId: String,
    val remoteVersion: String? = null,
)

@Serializable
public data class PluginShareContext(
    val remotePluginId: String,
    val canPublishToWorkspace: Boolean? = null,
    val creatorAccountUserId: String? = null,
    val creatorName: String? = null,
    val discoverability: PluginShareDiscoverability? = null,
    val remoteVersion: String? = null,
    val sharePrincipals: List<PluginSharePrincipal>? = null,
    val shareUrl: String? = null,
)

@Serializable
public data class PluginShareDeleteParams(
    val remotePluginId: String,
)

@Serializable
public class PluginShareDeleteResponse

@Serializable
public data class PluginShareListItem(
    val plugin: PluginSummary,
    val localPluginPath: String? = null,
)

@Serializable
public class PluginShareListParams

@Serializable
public data class PluginShareListResponse(
    val data: List<PluginShareListItem>,
)

@Serializable
public data class PluginSharePrincipal(
    val name: String,
    val principalId: String,
    val principalType: PluginSharePrincipalType,
    val role: PluginSharePrincipalRole,
)

@Serializable
public data class PluginShareSaveParams(
    val pluginPath: String,
    val discoverability: PluginShareDiscoverability? = null,
    val remotePluginId: String? = null,
    val shareTargets: List<PluginShareTarget>? = null,
)

@Serializable
public data class PluginShareSaveResponse(
    val remotePluginId: String,
    val shareUrl: String,
    val canPublishToWorkspace: Boolean? = null,
)

@Serializable
public data class PluginShareTarget(
    val principalId: String,
    val principalType: PluginSharePrincipalType,
    val role: PluginShareTargetRole,
)

@Serializable
public data class PluginShareUpdateTargetsParams(
    val discoverability: PluginShareUpdateDiscoverability,
    val remotePluginId: String,
    val shareTargets: List<PluginShareTarget>,
)

@Serializable
public data class PluginShareUpdateTargetsResponse(
    val discoverability: PluginShareDiscoverability,
    val principals: List<PluginSharePrincipal>,
)

@Serializable
public data class PluginSkillReadParams(
    val remoteMarketplaceName: String,
    val remotePluginId: String,
    val skillName: String,
)

@Serializable
public data class PluginSkillReadResponse(
    val contents: String? = null,
)

@Serializable
public data class PluginSummary(
    val authPolicy: PluginAuthPolicy,
    val enabled: Boolean,
    val id: String,
    val installPolicy: PluginInstallPolicy,
    val installed: Boolean,
    val name: String,
    val source: PluginSource,
    val availability: PluginAvailability? = null,
    val installPolicySource: PluginInstallPolicySource? = null,
    val `interface`: PluginInterface? = null,
    val keywords: List<String> = emptyList(),
    val localVersion: String? = null,
    val mustShowInstallationInterstitial: Boolean? = null,
    val remotePluginId: String? = null,
    val shareContext: PluginShareContext? = null,
    val version: String? = null,
)

@Serializable
public data class PluginUninstallParams(
    val pluginId: String,
)

@Serializable
public class PluginUninstallResponse

@Serializable
public data class PluginsMigration(
    val marketplaceName: String,
    val pluginNames: List<String>,
)

/**
 * Final process exit notification for `process/spawn`.
 */
@Serializable
public data class ProcessExitedNotification(
    val exitCode: Int,
    val processHandle: String,
    val stderr: String,
    val stderrCapReached: Boolean,
    val stdout: String,
    val stdoutCapReached: Boolean,
) : CodexNotification {
    override val method: String get() = "process/exited"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

/**
 * Base64-encoded output chunk emitted for a streaming `process/spawn` request.
 */
@Serializable
public data class ProcessOutputDeltaNotification(
    val capReached: Boolean,
    val deltaBase64: String,
    val processHandle: String,
    val stream: ProcessOutputStream,
) : CodexNotification {
    override val method: String get() = "process/outputDelta"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

/**
 * PTY size in character cells for `process/spawn` PTY sessions.
 */
@Serializable
public data class ProcessTerminalSize(
    val cols: Int,
    val rows: Int,
)

@Serializable
public data class RateLimitResetCredit(
    val grantedAt: Long,
    val id: String,
    val resetType: RateLimitResetType,
    val status: RateLimitResetCreditStatus,
    val description: String? = null,
    val expiresAt: Long? = null,
    val title: String? = null,
)

@Serializable
public data class RateLimitResetCreditsSummary(
    val availableCount: Long,
    val credits: List<RateLimitResetCredit>? = null,
)

@Serializable
public data class RateLimitSnapshot(
    val credits: CreditsSnapshot? = null,
    val individualLimit: SpendControlLimitSnapshot? = null,
    val limitId: String? = null,
    val limitName: String? = null,
    val planType: PlanType? = null,
    val primary: RateLimitWindow? = null,
    val rateLimitReachedType: RateLimitReachedType? = null,
    val secondary: RateLimitWindow? = null,
    val spendControlReached: Boolean? = null,
)

@Serializable
public data class RateLimitWindow(
    val usedPercent: Int,
    val resetsAt: Long? = null,
    val windowDurationMins: Long? = null,
)

/**
 * Internal-only notification containing the exact usage from one upstream Responses API
 * completion.
 */
@Serializable
public data class RawResponseCompletedNotification(
    val responseId: String,
    val threadId: String,
    val turnId: String,
    val usage: TokenUsageBreakdown? = null,
)

@Serializable
public data class RawResponseItemCompletedNotification(
    val item: ResponseItem,
    val threadId: String,
    val turnId: String,
)

@Serializable
public data class RealtimeVoicesList(
    val defaultV1: RealtimeVoice,
    val defaultV2: RealtimeVoice,
    val v1: List<RealtimeVoice>,
    val v2: List<RealtimeVoice>,
)

@Serializable
public data class ReasoningEffortOption(
    val description: String,
    val reasoningEffort: String,
)

@Serializable
public data class ReasoningSummaryPartAddedNotification(
    val itemId: String,
    val summaryIndex: Long,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/reasoning/summaryPartAdded"
}

@Serializable
public data class ReasoningSummaryTextDeltaNotification(
    val delta: String,
    val itemId: String,
    val summaryIndex: Long,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/reasoning/summaryTextDelta"
}

@Serializable
public data class ReasoningTextDeltaNotification(
    val contentIndex: Long,
    val delta: String,
    val itemId: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/reasoning/textDelta"
}

@Serializable
public data class RemoteControlDisableParams(
    val ephemeral: Boolean? = null,
)

@Serializable
public data class RemoteControlEnableParams(
    val ephemeral: Boolean? = null,
)

/**
 * Current remote-control connection status and remote identity exposed to clients.
 */
@Serializable
public data class RemoteControlStatusChangedNotification(
    val installationId: String,
    val serverName: String,
    val status: RemoteControlConnectionStatus,
    val environmentId: String? = null,
) : CodexNotification {
    override val method: String get() = "remoteControl/status/changed"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class RequestPermissionProfile(
    val fileSystem: AdditionalFileSystemPermissions? = null,
    val network: AdditionalNetworkPermissions? = null,
)

/**
 * A known resource that the server is capable of reading.
 */
@Serializable
public data class Resource(
    val name: String,
    val uri: String,
    @SerialName("_meta") val meta: JsonElement? = null,
    val annotations: JsonElement? = null,
    val description: String? = null,
    val icons: List<JsonElement>? = null,
    val mimeType: String? = null,
    val size: Long? = null,
    val title: String? = null,
)

/**
 * A template description for resources available on the server.
 */
@Serializable
public data class ResourceTemplate(
    val name: String,
    val uriTemplate: String,
    val annotations: JsonElement? = null,
    val description: String? = null,
    val mimeType: String? = null,
    val title: String? = null,
)

@Serializable
public data class ReviewStartParams(
    val target: ReviewTarget,
    val threadId: String,
    val delivery: ReviewDelivery? = null,
)

@Serializable
public data class ReviewStartResponse(
    val reviewThreadId: String,
    val turn: Turn,
)

@Serializable
public data class SandboxWorkspaceWrite(
    @SerialName("exclude_slash_tmp") val excludeSlashTmp: Boolean? = null,
    @SerialName("exclude_tmpdir_env_var") val excludeTmpdirEnvVar: Boolean? = null,
    @SerialName("network_access") val networkAccess: Boolean? = null,
    @SerialName("writable_roots") val writableRoots: List<String> = emptyList(),
)

@Serializable
public data class ScheduledTaskSummary(
    val key: String,
    val name: String,
    val prompt: String,
    val schedule: ScheduledTaskSchedule,
)

/**
 * A user-selected root that can expose one or more runtime capabilities.
 */
@Serializable
public data class SelectedCapabilityRoot(
    val id: String,
    val location: CapabilityRootLocation,
)

@Serializable
public data class SendAddCreditsNudgeEmailParams(
    val creditType: AddCreditsNudgeCreditType,
)

@Serializable
public data class SendAddCreditsNudgeEmailResponse(
    val status: AddCreditsNudgeEmailStatus,
)

@Serializable
public data class ServerRequestResolvedNotification(
    val requestId: JsonPrimitive,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "serverRequest/resolved"
    override val turnId: String? get() = null
}

@Serializable
public data class SessionMigration(
    val cwd: String,
    val path: String,
    val title: String? = null,
)

/**
 * Settings for a collaboration mode.
 */
@Serializable
public data class Settings(
    val model: String,
    @SerialName("developer_instructions") val developerInstructions: String? = null,
    @SerialName("reasoning_effort") val reasoningEffort: String? = null,
)

@Serializable
public data class SkillDependencies(
    val tools: List<SkillToolDependency>,
)

@Serializable
public data class SkillErrorInfo(
    val message: String,
    val path: String,
)

@Serializable
public data class SkillInterface(
    val brandColor: String? = null,
    val defaultPrompt: String? = null,
    val displayName: String? = null,
    val iconLarge: String? = null,
    val iconLargeUrl: String? = null,
    val iconSmall: String? = null,
    val iconSmallUrl: String? = null,
    val shortDescription: String? = null,
)

@Serializable
public data class SkillMetadata(
    val description: String,
    val enabled: Boolean,
    val name: String,
    val path: String,
    val scope: SkillScope,
    val dependencies: SkillDependencies? = null,
    val `interface`: SkillInterface? = null,
    val shortDescription: String? = null,
)

@Serializable
public data class SkillMigration(
    val name: String,
)

@Serializable
public data class SkillSummary(
    val description: String,
    val enabled: Boolean,
    val name: String,
    val `interface`: SkillInterface? = null,
    val path: String? = null,
    val shortDescription: String? = null,
)

@Serializable
public data class SkillToolDependency(
    val type: String,
    val value: String,
    val command: String? = null,
    val description: String? = null,
    val transport: String? = null,
    val url: String? = null,
)

/**
 * Notification emitted when watched local skill files change.
 *
 * Treat this as an invalidation signal and re-run `skills/list` with the client's current
 * parameters when refreshed skill metadata is needed.
 */
@Serializable
public class SkillsChangedNotification : CodexNotification {
    override val method: String get() = "skills/changed"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class SkillsConfigWriteParams(
    val enabled: Boolean,
    val name: String? = null,
    val path: String? = null,
)

@Serializable
public data class SkillsConfigWriteResponse(
    val effectiveEnabled: Boolean,
)

@Serializable
public data class SkillsExtraRootsSetParams(
    val extraRoots: List<String>,
)

@Serializable
public class SkillsExtraRootsSetResponse

@Serializable
public data class SkillsListEntry(
    val cwd: String,
    val errors: List<SkillErrorInfo>,
    val skills: List<SkillMetadata>,
)

@Serializable
public data class SkillsListParams(
    val cwds: List<String> = emptyList(),
    val forceReload: Boolean? = null,
)

@Serializable
public data class SkillsListResponse(
    val data: List<SkillsListEntry>,
)

@Serializable
public data class SpendControlLimitSnapshot(
    val limit: String,
    val remainingPercent: Int,
    val resetsAt: Long,
    val used: String,
)

@Serializable
public data class SubagentMigration(
    val name: String,
)

@Serializable
public data class TerminalInteractionNotification(
    val itemId: String,
    val processId: String,
    val stdin: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "item/commandExecution/terminalInteraction"
}

@Serializable
public data class TextElement(
    val byteRange: ByteRange,
    val placeholder: String? = null,
)

@Serializable
public data class TextPosition(
    val column: Long,
    val line: Long,
)

@Serializable
public data class TextRange(
    val end: TextPosition,
    val start: TextPosition,
)

@Serializable
public data class Thread(
    val updatedAt: Long,
    val cliVersion: String,
    val createdAt: Long,
    val cwd: String,
    val ephemeral: Boolean,
    val turns: List<Turn>,
    val id: String,
    val modelProvider: String,
    val preview: String,
    val sessionId: String,
    val source: SessionSource,
    val status: ThreadStatus,
    val agentNickname: String? = null,
    val agentRole: String? = null,
    val threadSource: String? = null,
    val forkedFromId: String? = null,
    val gitInfo: GitInfo? = null,
    val isPinned: Boolean? = null,
    val name: String? = null,
    val parentThreadId: String? = null,
    val path: String? = null,
    val recencyAt: Long? = null,
)

@Serializable
public data class ThreadApproveGuardianDeniedActionParams(
    val event: JsonElement,
    val threadId: String,
)

@Serializable
public class ThreadApproveGuardianDeniedActionResponse

@Serializable
public data class ThreadArchiveParams(
    val threadId: String,
)

@Serializable
public class ThreadArchiveResponse

@Serializable
public data class ThreadArchivedNotification(
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/archived"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadClosedNotification(
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/closed"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadCompactStartParams(
    val threadId: String,
)

@Serializable
public class ThreadCompactStartResponse

@Serializable
public data class ThreadDeleteParams(
    val threadId: String,
)

@Serializable
public class ThreadDeleteResponse

@Serializable
public data class ThreadDeletedNotification(
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/deleted"
    override val turnId: String? get() = null
}

/**
 * Extra app-server data for a thread.
 */
@Serializable
public class ThreadExtra

/**
 * There are two ways to fork a thread: 1. By thread_id: load the thread from disk by thread_id
 * and fork it into a new thread. 2. By path: load the thread from disk by path and fork it
 * into a new thread.
 *
 * If using a non-empty path, the thread_id param will be ignored. Empty string path values are
 * treated as absent.
 *
 * Prefer using thread_id whenever possible.
 */
@Serializable
public data class ThreadForkParams(
    val threadId: String,
    val approvalPolicy: AskForApproval? = null,
    val approvalsReviewer: ApprovalsReviewer? = null,
    val baseInstructions: String? = null,
    val serviceTier: String? = null,
    val config: JsonObject? = null,
    val cwd: String? = null,
    val sandbox: SandboxMode? = null,
    val developerInstructions: String? = null,
    val ephemeral: Boolean? = null,
    val threadSource: String? = null,
    val lastTurnId: String? = null,
    val model: String? = null,
    val modelProvider: String? = null,
)

@Serializable
public data class ThreadForkResponse(
    val sandbox: SandboxPolicy,
    val approvalPolicy: AskForApproval,
    val approvalsReviewer: ApprovalsReviewer,
    val cwd: String,
    val model: String,
    val modelProvider: String,
    val thread: Thread,
    val instructionSources: List<String> = emptyList(),
    val reasoningEffort: String? = null,
    val serviceTier: String? = null,
)

@Serializable
public data class ThreadGoal(
    val createdAt: Long,
    val objective: String,
    val status: ThreadGoalStatus,
    val threadId: String,
    val timeUsedSeconds: Long,
    val tokensUsed: Long,
    val updatedAt: Long,
    val tokenBudget: Long? = null,
)

@Serializable
public data class ThreadGoalClearParams(
    val threadId: String,
)

@Serializable
public data class ThreadGoalClearResponse(
    val cleared: Boolean,
)

@Serializable
public data class ThreadGoalClearedNotification(
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/goal/cleared"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadGoalGetParams(
    val threadId: String,
)

@Serializable
public data class ThreadGoalGetResponse(
    val goal: ThreadGoal? = null,
)

@Serializable
public data class ThreadGoalSetParams(
    val threadId: String,
    val objective: String? = null,
    val status: ThreadGoalStatus? = null,
    val tokenBudget: Long? = null,
)

@Serializable
public data class ThreadGoalSetResponse(
    val goal: ThreadGoal,
)

@Serializable
public data class ThreadGoalUpdatedNotification(
    val goal: ThreadGoal,
    override val threadId: String,
    override val turnId: String? = null,
) : CodexNotification {
    override val method: String get() = "thread/goal/updated"
}

@Serializable
public data class ThreadInjectItemsParams(
    val items: List<JsonElement>,
    val threadId: String,
)

@Serializable
public class ThreadInjectItemsResponse

@Serializable
public data class ThreadItemEntry(
    val item: ThreadItem,
    val turnId: String,
)

@Serializable
public data class ThreadListParams(
    val sourceKinds: List<ThreadSourceKind>? = null,
    val archived: Boolean? = null,
    val cursor: String? = null,
    val cwd: JsonElement? = null,
    val isPinned: Boolean? = null,
    val limit: Int? = null,
    val modelProviders: List<String>? = null,
    val useStateDbOnly: Boolean? = null,
    val searchTerm: String? = null,
    val sortDirection: SortDirection? = null,
    val sortKey: ThreadSortKey? = null,
)

@Serializable
public data class ThreadListResponse(
    val data: List<Thread>,
    val backwardsCursor: String? = null,
    val nextCursor: String? = null,
)

@Serializable
public data class ThreadLoadedListParams(
    val cursor: String? = null,
    val limit: Int? = null,
)

@Serializable
public data class ThreadLoadedListResponse(
    val data: List<String>,
    val nextCursor: String? = null,
)

@Serializable
public data class ThreadMetadataGitInfoUpdateParams(
    val branch: String? = null,
    val originUrl: String? = null,
    val sha: String? = null,
)

@Serializable
public data class ThreadMetadataUpdateParams(
    val threadId: String,
    val gitInfo: ThreadMetadataGitInfoUpdateParams? = null,
    val isPinned: Boolean? = null,
)

@Serializable
public data class ThreadMetadataUpdateResponse(
    val thread: Thread,
)

@Serializable
public data class ThreadNameUpdatedNotification(
    override val threadId: String,
    val threadName: String? = null,
) : CodexNotification {
    override val method: String get() = "thread/name/updated"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadReadParams(
    val threadId: String,
    val includeTurns: Boolean? = null,
)

@Serializable
public data class ThreadReadResponse(
    val thread: Thread,
)

/**
 * EXPERIMENTAL - thread realtime audio chunk.
 */
@Serializable
public data class ThreadRealtimeAudioChunk(
    val data: String,
    val numChannels: Int,
    val sampleRate: Int,
    val itemId: String? = null,
    val samplesPerChannel: Int? = null,
)

/**
 * EXPERIMENTAL - emitted when thread realtime transport closes.
 */
@Serializable
public data class ThreadRealtimeClosedNotification(
    override val threadId: String,
    val reason: String? = null,
) : CodexNotification {
    override val method: String get() = "thread/realtime/closed"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - emitted when thread realtime encounters an error.
 */
@Serializable
public data class ThreadRealtimeErrorNotification(
    val message: String,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/realtime/error"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - role-bearing text item included when a realtime V3 session starts.
 */
@Serializable
public data class ThreadRealtimeInitialItem(
    val role: ConversationTextRole,
    val text: String,
)

/**
 * EXPERIMENTAL - raw non-audio thread realtime item emitted by the backend.
 */
@Serializable
public data class ThreadRealtimeItemAddedNotification(
    val item: JsonElement,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/realtime/itemAdded"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - streamed output audio emitted by thread realtime.
 */
@Serializable
public data class ThreadRealtimeOutputAudioDeltaNotification(
    val audio: ThreadRealtimeAudioChunk,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/realtime/outputAudio/delta"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - emitted with the remote SDP for a WebRTC realtime session.
 */
@Serializable
public data class ThreadRealtimeSdpNotification(
    val sdp: String,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/realtime/sdp"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - emitted when thread realtime startup is accepted.
 */
@Serializable
public data class ThreadRealtimeStartedNotification(
    override val threadId: String,
    val version: RealtimeConversationVersion,
    val realtimeSessionId: String? = null,
) : CodexNotification {
    override val method: String get() = "thread/realtime/started"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - flat transcript delta emitted whenever realtime transcript text changes.
 */
@Serializable
public data class ThreadRealtimeTranscriptDeltaNotification(
    val delta: String,
    val role: String,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/realtime/transcript/delta"
    override val turnId: String? get() = null
}

/**
 * EXPERIMENTAL - final transcript text emitted when realtime completes a transcript part.
 */
@Serializable
public data class ThreadRealtimeTranscriptDoneNotification(
    val role: String,
    val text: String,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/realtime/transcript/done"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadResumeInitialTurnsPageParams(
    val itemsView: TurnItemsView? = null,
    val limit: Int? = null,
    val sortDirection: SortDirection? = null,
)

/**
 * There are three ways to resume a thread: 1. By thread_id: load the thread from disk by
 * thread_id and resume it. 2. By history: instantiate the thread from memory and resume it. 3.
 * By path: load the thread from disk by path and resume it.
 *
 * For non-running threads, the precedence is: history > non-empty path > thread_id. If using
 * history or a non-empty path for a non-running thread, the thread_id param will be ignored.
 *
 * If thread_id identifies a running thread, app-server rejoins that thread and treats a
 * non-empty path as a consistency check against the active rollout path. Empty string path
 * values are treated as absent.
 *
 * Prefer using thread_id whenever possible.
 */
@Serializable
public data class ThreadResumeParams(
    val threadId: String,
    val approvalPolicy: AskForApproval? = null,
    val approvalsReviewer: ApprovalsReviewer? = null,
    val baseInstructions: String? = null,
    val config: JsonObject? = null,
    val cwd: String? = null,
    val developerInstructions: String? = null,
    val personality: Personality? = null,
    val sandbox: SandboxMode? = null,
    val model: String? = null,
    val modelProvider: String? = null,
    val serviceTier: String? = null,
)

@Serializable
public data class ThreadResumeResponse(
    val approvalPolicy: AskForApproval,
    val approvalsReviewer: ApprovalsReviewer,
    val cwd: String,
    val thread: Thread,
    val sandbox: SandboxPolicy,
    val model: String,
    val modelProvider: String,
    val reasoningEffort: String? = null,
    val instructionSources: List<String> = emptyList(),
    val serviceTier: String? = null,
)

/**
 * DEPRECATED: `thread/rollback` will be removed soon.
 */
@Serializable
public data class ThreadRollbackParams(
    val numTurns: Int,
    val threadId: String,
)

@Serializable
public data class ThreadRollbackResponse(
    val thread: Thread,
)

@Serializable
public data class ThreadSearchResult(
    val snippet: String,
    val thread: Thread,
)

@Serializable
public data class ThreadSetNameParams(
    val name: String,
    val threadId: String,
)

@Serializable
public class ThreadSetNameResponse

@Serializable
public data class ThreadSettings(
    val approvalPolicy: AskForApproval,
    val approvalsReviewer: ApprovalsReviewer,
    val collaborationMode: CollaborationMode,
    val cwd: String,
    val model: String,
    val modelProvider: String,
    val sandboxPolicy: SandboxPolicy,
    val activePermissionProfile: ActivePermissionProfile? = null,
    val effort: String? = null,
    val summary: ReasoningSummary? = null,
    val personality: Personality? = null,
    val serviceTier: String? = null,
)

@Serializable
public data class ThreadSettingsUpdatedNotification(
    override val threadId: String,
    val threadSettings: ThreadSettings,
) : CodexNotification {
    override val method: String get() = "thread/settings/updated"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadShellCommandParams(
    val command: String,
    val threadId: String,
)

@Serializable
public class ThreadShellCommandResponse

@Serializable
public data class ThreadStartParams(
    val serviceTier: String? = null,
    val approvalPolicy: AskForApproval? = null,
    val approvalsReviewer: ApprovalsReviewer? = null,
    val baseInstructions: String? = null,
    val config: JsonObject? = null,
    val cwd: String? = null,
    val developerInstructions: String? = null,
    val serviceName: String? = null,
    val sessionStartSource: ThreadStartSource? = null,
    val ephemeral: Boolean? = null,
    val personality: Personality? = null,
    val sandbox: SandboxMode? = null,
    val threadSource: String? = null,
    val model: String? = null,
    val modelProvider: String? = null,
)

@Serializable
public data class ThreadStartResponse(
    val approvalPolicy: AskForApproval,
    val approvalsReviewer: ApprovalsReviewer,
    val cwd: String,
    val model: String,
    val modelProvider: String,
    val sandbox: SandboxPolicy,
    val thread: Thread,
    val serviceTier: String? = null,
    val instructionSources: List<String> = emptyList(),
    val reasoningEffort: String? = null,
)

@Serializable
public data class ThreadStartedNotification(
    val thread: Thread,
) : CodexNotification {
    override val method: String get() = "thread/started"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadStatusChangedNotification(
    val status: ThreadStatus,
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/status/changed"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadTokenUsage(
    val last: TokenUsageBreakdown,
    val total: TokenUsageBreakdown,
    val modelContextWindow: Long? = null,
)

@Serializable
public data class ThreadTokenUsageUpdatedNotification(
    override val threadId: String,
    val tokenUsage: ThreadTokenUsage,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "thread/tokenUsage/updated"
}

@Serializable
public data class ThreadUnarchiveParams(
    val threadId: String,
)

@Serializable
public data class ThreadUnarchiveResponse(
    val thread: Thread,
)

@Serializable
public data class ThreadUnarchivedNotification(
    override val threadId: String,
) : CodexNotification {
    override val method: String get() = "thread/unarchived"
    override val turnId: String? get() = null
}

@Serializable
public data class ThreadUnsubscribeParams(
    val threadId: String,
)

@Serializable
public data class ThreadUnsubscribeResponse(
    val status: ThreadUnsubscribeStatus,
)

@Serializable
public data class TokenUsageBreakdown(
    val cachedInputTokens: Long,
    val inputTokens: Long,
    val outputTokens: Long,
    val reasoningOutputTokens: Long,
    val totalTokens: Long,
    val cacheWriteInputTokens: Long? = null,
)

/**
 * Definition for a tool the client can call.
 */
@Serializable
public data class Tool(
    val inputSchema: JsonElement,
    val name: String,
    @SerialName("_meta") val meta: JsonElement? = null,
    val annotations: JsonElement? = null,
    val description: String? = null,
    val icons: List<JsonElement>? = null,
    val outputSchema: JsonElement? = null,
    val title: String? = null,
)

/**
 * EXPERIMENTAL. Captures a user's answer to a request_user_input question.
 */
@Serializable
public data class ToolRequestUserInputAnswer(
    val answers: List<String>,
)

/**
 * EXPERIMENTAL. Defines a single selectable option for request_user_input.
 */
@Serializable
public data class ToolRequestUserInputOption(
    val description: String,
    val label: String,
)

/**
 * EXPERIMENTAL. Params sent with a request_user_input event.
 */
@Serializable
public data class ToolRequestUserInputParams(
    val itemId: String,
    val questions: List<ToolRequestUserInputQuestion>,
    val threadId: String,
    val turnId: String,
    val autoResolutionMs: Long? = null,
)

/**
 * EXPERIMENTAL. Represents one request_user_input question and its required options.
 */
@Serializable
public data class ToolRequestUserInputQuestion(
    val header: String,
    val id: String,
    val question: String,
    val isOther: Boolean? = null,
    val isSecret: Boolean? = null,
    val options: List<ToolRequestUserInputOption>? = null,
)

/**
 * EXPERIMENTAL. Response payload mapping question ids to answers.
 */
@Serializable
public data class ToolRequestUserInputResponse(
    val answers: Map<String, ToolRequestUserInputAnswer>,
)

@Serializable
public data class ToolsV2(
    @SerialName("web_search") val webSearch: WebSearchToolConfig? = null,
)

@Serializable
public data class Turn(
    val id: String,
    val items: List<ThreadItem>,
    val status: TurnStatus,
    val completedAt: Long? = null,
    val durationMs: Long? = null,
    val error: TurnError? = null,
    val itemsView: TurnItemsView? = null,
    val startedAt: Long? = null,
)

@Serializable
public data class TurnCompletedNotification(
    override val threadId: String,
    val turn: Turn,
) : CodexNotification {
    override val method: String get() = "turn/completed"
    override val turnId: String get() = turn.id
}

/**
 * Notification that the turn-level unified diff has changed. Contains the latest aggregated
 * diff across all file changes in the turn.
 */
@Serializable
public data class TurnDiffUpdatedNotification(
    val diff: String,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "turn/diff/updated"
}

@Serializable
public data class TurnEnvironmentParams(
    val cwd: String,
    val environmentId: String,
    val runtimeWorkspaceRoots: List<String>? = null,
)

@Serializable
public data class TurnError(
    val message: String,
    val additionalDetails: String? = null,
    val codexErrorInfo: CodexErrorInfo? = null,
)

@Serializable
public data class TurnInterruptParams(
    val threadId: String,
    val turnId: String,
)

@Serializable
public class TurnInterruptResponse

@Serializable
public data class TurnModerationMetadataNotification(
    val metadata: JsonElement,
    override val threadId: String,
    override val turnId: String,
) : CodexNotification {
    override val method: String get() = "turn/moderationMetadata"
}

@Serializable
public data class TurnPlanStep(
    val status: TurnPlanStepStatus,
    val step: String,
)

@Serializable
public data class TurnPlanUpdatedNotification(
    val plan: List<TurnPlanStep>,
    override val threadId: String,
    override val turnId: String,
    val explanation: String? = null,
) : CodexNotification {
    override val method: String get() = "turn/plan/updated"
}

@Serializable
public data class TurnStartParams(
    val threadId: String,
    val input: List<UserInput>,
    val approvalPolicy: AskForApproval? = null,
    val approvalsReviewer: ApprovalsReviewer? = null,
    val clientUserMessageId: String? = null,
    val serviceTier: String? = null,
    val cwd: String? = null,
    val effort: String? = null,
    val sandboxPolicy: SandboxPolicy? = null,
    val model: String? = null,
    val summary: ReasoningSummary? = null,
    val outputSchema: JsonElement? = null,
    val personality: Personality? = null,
)

@Serializable
public data class TurnStartResponse(
    val turn: Turn,
)

@Serializable
public data class TurnStartedNotification(
    override val threadId: String,
    val turn: Turn,
) : CodexNotification {
    override val method: String get() = "turn/started"
    override val turnId: String get() = turn.id
}

@Serializable
public data class TurnSteerParams(
    val threadId: String,
    val expectedTurnId: String,
    val input: List<UserInput>,
    val clientUserMessageId: String? = null,
)

@Serializable
public data class TurnSteerResponse(
    val turnId: String,
)

@Serializable
public data class TurnsPage(
    val data: List<Turn>,
    val backwardsCursor: String? = null,
    val nextCursor: String? = null,
)

@Serializable
public data class W3cTraceContext(
    val traceparent: String? = null,
    val tracestate: String? = null,
)

@Serializable
public data class WarningNotification(
    val message: String,
    override val threadId: String? = null,
) : CodexNotification {
    override val method: String get() = "warning"
    override val turnId: String? get() = null
}

@Serializable
public data class WebSearchLocation(
    val city: String? = null,
    val country: String? = null,
    val region: String? = null,
    val timezone: String? = null,
)

@Serializable
public data class WebSearchToolConfig(
    @SerialName("allowed_domains") val allowedDomains: List<String>? = null,
    @SerialName("context_size") val contextSize: WebSearchContextSize? = null,
    val location: WebSearchLocation? = null,
)

@Serializable
public data class WindowsSandboxReadinessResponse(
    val status: WindowsSandboxReadiness,
)

@Serializable
public data class WindowsSandboxSetupCompletedNotification(
    val mode: WindowsSandboxSetupMode,
    val success: Boolean,
    val error: String? = null,
) : CodexNotification {
    override val method: String get() = "windowsSandbox/setupCompleted"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class WindowsSandboxSetupStartParams(
    val mode: WindowsSandboxSetupMode,
    val cwd: String? = null,
)

@Serializable
public data class WindowsSandboxSetupStartResponse(
    val started: Boolean,
)

@Serializable
public data class WindowsWorldWritableWarningNotification(
    val extraCount: Long,
    val failedScan: Boolean,
    val samplePaths: List<String>,
) : CodexNotification {
    override val method: String get() = "windows/worldWritableWarning"
    override val threadId: String? get() = null
    override val turnId: String? get() = null
}

@Serializable
public data class WorkspaceMessage(
    val messageBody: String,
    val messageId: String,
    val messageType: WorkspaceMessageType,
    val archivedAt: Long? = null,
    val createdAt: Long? = null,
)


@Serializable
public data class AskForApprovalGranular(
    @SerialName("mcp_elicitations") val mcpElicitations: Boolean,
    val rules: Boolean,
    @SerialName("sandbox_approval") val sandboxApproval: Boolean,
    @SerialName("request_permissions") val requestPermissions: Boolean? = null,
    @SerialName("skill_approval") val skillApproval: Boolean? = null,
)

@Serializable
public data class CodexErrorInfoHttpConnectionFailed(
    val httpStatusCode: Int? = null,
)

@Serializable
public data class CodexErrorInfoResponseStreamConnectionFailed(
    val httpStatusCode: Int? = null,
)

@Serializable
public data class CodexErrorInfoResponseStreamDisconnected(
    val httpStatusCode: Int? = null,
)

@Serializable
public data class CodexErrorInfoResponseTooManyFailedAttempts(
    val httpStatusCode: Int? = null,
)

@Serializable
public data class CodexErrorInfoActiveTurnNotSteerable(
    val turnKind: NonSteerableTurnKind,
)

@Serializable
public data class CommandExecutionApprovalDecisionAcceptWithExecpolicyAmendment(
    @SerialName("execpolicy_amendment") val execpolicyAmendment: List<String>,
)

@Serializable
public data class CommandExecutionApprovalDecisionApplyNetworkPolicyAmendment(
    @SerialName("network_policy_amendment") val networkPolicyAmendment: NetworkPolicyAmendment,
)

@Serializable
public data class ReviewDecisionApprovedExecpolicyAmendment(
    @SerialName("proposed_execpolicy_amendment") val proposedExecpolicyAmendment: List<String>,
)

@Serializable
public data class ReviewDecisionNetworkPolicyAmendment(
    @SerialName("network_policy_amendment") val networkPolicyAmendment: NetworkPolicyAmendment,
)

@Serializable
public data class ReviewDecisionDenied(
    val rejection: String,
)

@Serializable
public data class SubAgentSourceThreadSpawn(
    val depth: Int,
    @SerialName("parent_thread_id") val parentThreadId: ThreadId,
    @SerialName("agent_nickname") val agentNickname: String? = null,
    @SerialName("agent_path") val agentPath: String? = null,
    @SerialName("agent_role") val agentRole: String? = null,
)

// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Protocol method inventory.

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
 * Every request method the client may send to the app-server.
 */
public object ClientRequests {
    /** `initialize` (params: InitializeParams) */
    public const val INITIALIZE: String = "initialize"
    /** `thread/start` (params: ThreadStartParams) */
    public const val THREAD_START: String = "thread/start"
    /** `thread/resume` (params: ThreadResumeParams) */
    public const val THREAD_RESUME: String = "thread/resume"
    /** `thread/fork` (params: ThreadForkParams) */
    public const val THREAD_FORK: String = "thread/fork"
    /** `thread/archive` (params: ThreadArchiveParams) */
    public const val THREAD_ARCHIVE: String = "thread/archive"
    /** `thread/delete` (params: ThreadDeleteParams) */
    public const val THREAD_DELETE: String = "thread/delete"
    /** `thread/unsubscribe` (params: ThreadUnsubscribeParams) */
    public const val THREAD_UNSUBSCRIBE: String = "thread/unsubscribe"
    /** `thread/name/set` (params: ThreadSetNameParams) */
    public const val THREAD_NAME_SET: String = "thread/name/set"
    /** `thread/goal/set` (params: ThreadGoalSetParams) */
    public const val THREAD_GOAL_SET: String = "thread/goal/set"
    /** `thread/goal/get` (params: ThreadGoalGetParams) */
    public const val THREAD_GOAL_GET: String = "thread/goal/get"
    /** `thread/goal/clear` (params: ThreadGoalClearParams) */
    public const val THREAD_GOAL_CLEAR: String = "thread/goal/clear"
    /** `thread/metadata/update` (params: ThreadMetadataUpdateParams) */
    public const val THREAD_METADATA_UPDATE: String = "thread/metadata/update"
    /** `thread/unarchive` (params: ThreadUnarchiveParams) */
    public const val THREAD_UNARCHIVE: String = "thread/unarchive"
    /** `thread/compact/start` (params: ThreadCompactStartParams) */
    public const val THREAD_COMPACT_START: String = "thread/compact/start"
    /** `thread/shellCommand` (params: ThreadShellCommandParams) */
    public const val THREAD_SHELL_COMMAND: String = "thread/shellCommand"
    /** `thread/approveGuardianDeniedAction` (params: ThreadApproveGuardianDeniedActionParams) */
    public const val THREAD_APPROVE_GUARDIAN_DENIED_ACTION: String = "thread/approveGuardianDeniedAction"
    /** `thread/rollback` (params: ThreadRollbackParams) */
    public const val THREAD_ROLLBACK: String = "thread/rollback"
    /** `thread/list` (params: ThreadListParams) */
    public const val THREAD_LIST: String = "thread/list"
    /** `thread/loaded/list` (params: ThreadLoadedListParams) */
    public const val THREAD_LOADED_LIST: String = "thread/loaded/list"
    /** `thread/read` (params: ThreadReadParams) */
    public const val THREAD_READ: String = "thread/read"
    /** `thread/inject_items` (params: ThreadInjectItemsParams) */
    public const val THREAD_INJECT_ITEMS: String = "thread/inject_items"
    /** `skills/list` (params: SkillsListParams) */
    public const val SKILLS_LIST: String = "skills/list"
    /** `skills/extraRoots/set` (params: SkillsExtraRootsSetParams) */
    public const val SKILLS_EXTRA_ROOTS_SET: String = "skills/extraRoots/set"
    /** `hooks/list` (params: HooksListParams) */
    public const val HOOKS_LIST: String = "hooks/list"
    /** `marketplace/add` (params: MarketplaceAddParams) */
    public const val MARKETPLACE_ADD: String = "marketplace/add"
    /** `marketplace/remove` (params: MarketplaceRemoveParams) */
    public const val MARKETPLACE_REMOVE: String = "marketplace/remove"
    /** `marketplace/upgrade` (params: MarketplaceUpgradeParams) */
    public const val MARKETPLACE_UPGRADE: String = "marketplace/upgrade"
    /** `plugin/list` (params: PluginListParams) */
    public const val PLUGIN_LIST: String = "plugin/list"
    /** `plugin/installed` (params: PluginInstalledParams) */
    public const val PLUGIN_INSTALLED: String = "plugin/installed"
    /** `plugin/read` (params: PluginReadParams) */
    public const val PLUGIN_READ: String = "plugin/read"
    /** `plugin/skill/read` (params: PluginSkillReadParams) */
    public const val PLUGIN_SKILL_READ: String = "plugin/skill/read"
    /** `plugin/share/save` (params: PluginShareSaveParams) */
    public const val PLUGIN_SHARE_SAVE: String = "plugin/share/save"
    /** `plugin/share/updateTargets` (params: PluginShareUpdateTargetsParams) */
    public const val PLUGIN_SHARE_UPDATE_TARGETS: String = "plugin/share/updateTargets"
    /** `plugin/share/list` (params: PluginShareListParams) */
    public const val PLUGIN_SHARE_LIST: String = "plugin/share/list"
    /** `plugin/share/checkout` (params: PluginShareCheckoutParams) */
    public const val PLUGIN_SHARE_CHECKOUT: String = "plugin/share/checkout"
    /** `plugin/share/delete` (params: PluginShareDeleteParams) */
    public const val PLUGIN_SHARE_DELETE: String = "plugin/share/delete"
    /** `app/read` (params: AppsReadParams) */
    public const val APP_READ: String = "app/read"
    /** `app/list` (params: AppsListParams) */
    public const val APP_LIST: String = "app/list"
    /** `app/installed` (params: AppsInstalledParams) */
    public const val APP_INSTALLED: String = "app/installed"
    /** `fs/readFile` (params: FsReadFileParams) */
    public const val FS_READ_FILE: String = "fs/readFile"
    /** `fs/writeFile` (params: FsWriteFileParams) */
    public const val FS_WRITE_FILE: String = "fs/writeFile"
    /** `fs/createDirectory` (params: FsCreateDirectoryParams) */
    public const val FS_CREATE_DIRECTORY: String = "fs/createDirectory"
    /** `fs/getMetadata` (params: FsGetMetadataParams) */
    public const val FS_GET_METADATA: String = "fs/getMetadata"
    /** `fs/readDirectory` (params: FsReadDirectoryParams) */
    public const val FS_READ_DIRECTORY: String = "fs/readDirectory"
    /** `fs/remove` (params: FsRemoveParams) */
    public const val FS_REMOVE: String = "fs/remove"
    /** `fs/copy` (params: FsCopyParams) */
    public const val FS_COPY: String = "fs/copy"
    /** `fs/watch` (params: FsWatchParams) */
    public const val FS_WATCH: String = "fs/watch"
    /** `fs/unwatch` (params: FsUnwatchParams) */
    public const val FS_UNWATCH: String = "fs/unwatch"
    /** `skills/config/write` (params: SkillsConfigWriteParams) */
    public const val SKILLS_CONFIG_WRITE: String = "skills/config/write"
    /** `plugin/install` (params: PluginInstallParams) */
    public const val PLUGIN_INSTALL: String = "plugin/install"
    /** `plugin/uninstall` (params: PluginUninstallParams) */
    public const val PLUGIN_UNINSTALL: String = "plugin/uninstall"
    /** `turn/start` (params: TurnStartParams) */
    public const val TURN_START: String = "turn/start"
    /** `turn/steer` (params: TurnSteerParams) */
    public const val TURN_STEER: String = "turn/steer"
    /** `turn/interrupt` (params: TurnInterruptParams) */
    public const val TURN_INTERRUPT: String = "turn/interrupt"
    /** `review/start` (params: ReviewStartParams) */
    public const val REVIEW_START: String = "review/start"
    /** `model/list` (params: ModelListParams) */
    public const val MODEL_LIST: String = "model/list"
    /** `modelProvider/capabilities/read` (params: ModelProviderCapabilitiesReadParams) */
    public const val MODEL_PROVIDER_CAPABILITIES_READ: String = "modelProvider/capabilities/read"
    /** `experimentalFeature/list` (params: ExperimentalFeatureListParams) */
    public const val EXPERIMENTAL_FEATURE_LIST: String = "experimentalFeature/list"
    /** `permissionProfile/list` (params: PermissionProfileListParams) */
    public const val PERMISSION_PROFILE_LIST: String = "permissionProfile/list"
    /** `experimentalFeature/enablement/set` (params: ExperimentalFeatureEnablementSetParams) */
    public const val EXPERIMENTAL_FEATURE_ENABLEMENT_SET: String = "experimentalFeature/enablement/set"
    /** `mcpServer/oauth/login` (params: McpServerOauthLoginParams) */
    public const val MCP_SERVER_OAUTH_LOGIN: String = "mcpServer/oauth/login"
    /** `config/mcpServer/reload` (params: —) */
    public const val CONFIG_MCP_SERVER_RELOAD: String = "config/mcpServer/reload"
    /** `mcpServerStatus/list` (params: ListMcpServerStatusParams) */
    public const val MCP_SERVER_STATUS_LIST: String = "mcpServerStatus/list"
    /** `mcpServer/resource/read` (params: McpResourceReadParams) */
    public const val MCP_SERVER_RESOURCE_READ: String = "mcpServer/resource/read"
    /** `mcpServer/tool/call` (params: McpServerToolCallParams) */
    public const val MCP_SERVER_TOOL_CALL: String = "mcpServer/tool/call"
    /** `windowsSandbox/setupStart` (params: WindowsSandboxSetupStartParams) */
    public const val WINDOWS_SANDBOX_SETUP_START: String = "windowsSandbox/setupStart"
    /** `windowsSandbox/readiness` (params: —) */
    public const val WINDOWS_SANDBOX_READINESS: String = "windowsSandbox/readiness"
    /** `account/login/start` (params: LoginAccountParams) */
    public const val ACCOUNT_LOGIN_START: String = "account/login/start"
    /** `account/login/cancel` (params: CancelLoginAccountParams) */
    public const val ACCOUNT_LOGIN_CANCEL: String = "account/login/cancel"
    /** `account/logout` (params: —) */
    public const val ACCOUNT_LOGOUT: String = "account/logout"
    /** `account/rateLimits/read` (params: —) */
    public const val ACCOUNT_RATE_LIMITS_READ: String = "account/rateLimits/read"
    /** `account/rateLimitResetCredit/consume` (params: ConsumeAccountRateLimitResetCreditParams) */
    public const val ACCOUNT_RATE_LIMIT_RESET_CREDIT_CONSUME: String = "account/rateLimitResetCredit/consume"
    /** `account/usage/read` (params: —) */
    public const val ACCOUNT_USAGE_READ: String = "account/usage/read"
    /** `account/workspaceMessages/read` (params: —) */
    public const val ACCOUNT_WORKSPACE_MESSAGES_READ: String = "account/workspaceMessages/read"
    /** `account/sendAddCreditsNudgeEmail` (params: SendAddCreditsNudgeEmailParams) */
    public const val ACCOUNT_SEND_ADD_CREDITS_NUDGE_EMAIL: String = "account/sendAddCreditsNudgeEmail"
    /** `feedback/upload` (params: FeedbackUploadParams) */
    public const val FEEDBACK_UPLOAD: String = "feedback/upload"
    /** `command/exec` (params: CommandExecParams) */
    public const val COMMAND_EXEC: String = "command/exec"
    /** `command/exec/write` (params: CommandExecWriteParams) */
    public const val COMMAND_EXEC_WRITE: String = "command/exec/write"
    /** `command/exec/terminate` (params: CommandExecTerminateParams) */
    public const val COMMAND_EXEC_TERMINATE: String = "command/exec/terminate"
    /** `command/exec/resize` (params: CommandExecResizeParams) */
    public const val COMMAND_EXEC_RESIZE: String = "command/exec/resize"
    /** `config/read` (params: ConfigReadParams) */
    public const val CONFIG_READ: String = "config/read"
    /** `externalAgentConfig/detect` (params: ExternalAgentConfigDetectParams) */
    public const val EXTERNAL_AGENT_CONFIG_DETECT: String = "externalAgentConfig/detect"
    /** `externalAgentConfig/import` (params: ExternalAgentConfigImportParams) */
    public const val EXTERNAL_AGENT_CONFIG_IMPORT: String = "externalAgentConfig/import"
    /** `externalAgentConfig/import/recordHistory` (params: ExternalAgentConfigImportHistoryRecordParams) */
    public const val EXTERNAL_AGENT_CONFIG_IMPORT_RECORD_HISTORY: String = "externalAgentConfig/import/recordHistory"
    /** `externalAgentConfig/import/readHistories` (params: —) */
    public const val EXTERNAL_AGENT_CONFIG_IMPORT_READ_HISTORIES: String = "externalAgentConfig/import/readHistories"
    /** `config/value/write` (params: ConfigValueWriteParams) */
    public const val CONFIG_VALUE_WRITE: String = "config/value/write"
    /** `config/batchWrite` (params: ConfigBatchWriteParams) */
    public const val CONFIG_BATCH_WRITE: String = "config/batchWrite"
    /** `configRequirements/read` (params: —) */
    public const val CONFIG_REQUIREMENTS_READ: String = "configRequirements/read"
    /** `account/read` (params: GetAccountParams) */
    public const val ACCOUNT_READ: String = "account/read"
    /** `fuzzyFileSearch` (params: FuzzyFileSearchParams) */
    public const val FUZZY_FILE_SEARCH: String = "fuzzyFileSearch"
}

/**
 * Every request the app-server may send to the client. Each one MUST be answered or the turn stalls.
 */
public object ServerRequests {
    /** `item/commandExecution/requestApproval` (params: CommandExecutionRequestApprovalParams, result: —) */
    public const val ITEM_COMMAND_EXECUTION_REQUEST_APPROVAL: String = "item/commandExecution/requestApproval"
    /** `item/fileChange/requestApproval` (params: FileChangeRequestApprovalParams, result: —) */
    public const val ITEM_FILE_CHANGE_REQUEST_APPROVAL: String = "item/fileChange/requestApproval"
    /** `item/tool/requestUserInput` (params: ToolRequestUserInputParams, result: —) */
    public const val ITEM_TOOL_REQUEST_USER_INPUT: String = "item/tool/requestUserInput"
    /** `mcpServer/elicitation/request` (params: McpServerElicitationRequestParams, result: —) */
    public const val MCP_SERVER_ELICITATION_REQUEST: String = "mcpServer/elicitation/request"
    /** `item/permissions/requestApproval` (params: PermissionsRequestApprovalParams, result: —) */
    public const val ITEM_PERMISSIONS_REQUEST_APPROVAL: String = "item/permissions/requestApproval"
    /** `item/tool/call` (params: DynamicToolCallParams, result: —) */
    public const val ITEM_TOOL_CALL: String = "item/tool/call"
    /** `account/chatgptAuthTokens/refresh` (params: ChatgptAuthTokensRefreshParams, result: —) */
    public const val ACCOUNT_CHATGPT_AUTH_TOKENS_REFRESH: String = "account/chatgptAuthTokens/refresh"
    /** `attestation/generate` (params: AttestationGenerateParams, result: —) */
    public const val ATTESTATION_GENERATE: String = "attestation/generate"
    /** `applyPatchApproval` (params: ApplyPatchApprovalParams, result: —) */
    public const val APPLY_PATCH_APPROVAL: String = "applyPatchApproval"
    /** `execCommandApproval` (params: ExecCommandApprovalParams, result: —) */
    public const val EXEC_COMMAND_APPROVAL: String = "execCommandApproval"
}

/**
 * Every notification the client may send to the app-server.
 */
public object ClientNotifications {
    /** `initialized` (params: —) */
    public const val INITIALIZED: String = "initialized"
}

/** Params type name for each client request, keyed by wire method. */
public val CLIENT_REQUEST_PARAMS: Map<String, String?> = mapOf(
    "initialize" to "InitializeParams",
    "thread/start" to "ThreadStartParams",
    "thread/resume" to "ThreadResumeParams",
    "thread/fork" to "ThreadForkParams",
    "thread/archive" to "ThreadArchiveParams",
    "thread/delete" to "ThreadDeleteParams",
    "thread/unsubscribe" to "ThreadUnsubscribeParams",
    "thread/name/set" to "ThreadSetNameParams",
    "thread/goal/set" to "ThreadGoalSetParams",
    "thread/goal/get" to "ThreadGoalGetParams",
    "thread/goal/clear" to "ThreadGoalClearParams",
    "thread/metadata/update" to "ThreadMetadataUpdateParams",
    "thread/unarchive" to "ThreadUnarchiveParams",
    "thread/compact/start" to "ThreadCompactStartParams",
    "thread/shellCommand" to "ThreadShellCommandParams",
    "thread/approveGuardianDeniedAction" to "ThreadApproveGuardianDeniedActionParams",
    "thread/rollback" to "ThreadRollbackParams",
    "thread/list" to "ThreadListParams",
    "thread/loaded/list" to "ThreadLoadedListParams",
    "thread/read" to "ThreadReadParams",
    "thread/inject_items" to "ThreadInjectItemsParams",
    "skills/list" to "SkillsListParams",
    "skills/extraRoots/set" to "SkillsExtraRootsSetParams",
    "hooks/list" to "HooksListParams",
    "marketplace/add" to "MarketplaceAddParams",
    "marketplace/remove" to "MarketplaceRemoveParams",
    "marketplace/upgrade" to "MarketplaceUpgradeParams",
    "plugin/list" to "PluginListParams",
    "plugin/installed" to "PluginInstalledParams",
    "plugin/read" to "PluginReadParams",
    "plugin/skill/read" to "PluginSkillReadParams",
    "plugin/share/save" to "PluginShareSaveParams",
    "plugin/share/updateTargets" to "PluginShareUpdateTargetsParams",
    "plugin/share/list" to "PluginShareListParams",
    "plugin/share/checkout" to "PluginShareCheckoutParams",
    "plugin/share/delete" to "PluginShareDeleteParams",
    "app/read" to "AppsReadParams",
    "app/list" to "AppsListParams",
    "app/installed" to "AppsInstalledParams",
    "fs/readFile" to "FsReadFileParams",
    "fs/writeFile" to "FsWriteFileParams",
    "fs/createDirectory" to "FsCreateDirectoryParams",
    "fs/getMetadata" to "FsGetMetadataParams",
    "fs/readDirectory" to "FsReadDirectoryParams",
    "fs/remove" to "FsRemoveParams",
    "fs/copy" to "FsCopyParams",
    "fs/watch" to "FsWatchParams",
    "fs/unwatch" to "FsUnwatchParams",
    "skills/config/write" to "SkillsConfigWriteParams",
    "plugin/install" to "PluginInstallParams",
    "plugin/uninstall" to "PluginUninstallParams",
    "turn/start" to "TurnStartParams",
    "turn/steer" to "TurnSteerParams",
    "turn/interrupt" to "TurnInterruptParams",
    "review/start" to "ReviewStartParams",
    "model/list" to "ModelListParams",
    "modelProvider/capabilities/read" to "ModelProviderCapabilitiesReadParams",
    "experimentalFeature/list" to "ExperimentalFeatureListParams",
    "permissionProfile/list" to "PermissionProfileListParams",
    "experimentalFeature/enablement/set" to "ExperimentalFeatureEnablementSetParams",
    "mcpServer/oauth/login" to "McpServerOauthLoginParams",
    "config/mcpServer/reload" to null,
    "mcpServerStatus/list" to "ListMcpServerStatusParams",
    "mcpServer/resource/read" to "McpResourceReadParams",
    "mcpServer/tool/call" to "McpServerToolCallParams",
    "windowsSandbox/setupStart" to "WindowsSandboxSetupStartParams",
    "windowsSandbox/readiness" to null,
    "account/login/start" to "LoginAccountParams",
    "account/login/cancel" to "CancelLoginAccountParams",
    "account/logout" to null,
    "account/rateLimits/read" to null,
    "account/rateLimitResetCredit/consume" to "ConsumeAccountRateLimitResetCreditParams",
    "account/usage/read" to null,
    "account/workspaceMessages/read" to null,
    "account/sendAddCreditsNudgeEmail" to "SendAddCreditsNudgeEmailParams",
    "feedback/upload" to "FeedbackUploadParams",
    "command/exec" to "CommandExecParams",
    "command/exec/write" to "CommandExecWriteParams",
    "command/exec/terminate" to "CommandExecTerminateParams",
    "command/exec/resize" to "CommandExecResizeParams",
    "config/read" to "ConfigReadParams",
    "externalAgentConfig/detect" to "ExternalAgentConfigDetectParams",
    "externalAgentConfig/import" to "ExternalAgentConfigImportParams",
    "externalAgentConfig/import/recordHistory" to "ExternalAgentConfigImportHistoryRecordParams",
    "externalAgentConfig/import/readHistories" to null,
    "config/value/write" to "ConfigValueWriteParams",
    "config/batchWrite" to "ConfigBatchWriteParams",
    "configRequirements/read" to null,
    "account/read" to "GetAccountParams",
    "fuzzyFileSearch" to "FuzzyFileSearchParams",
)

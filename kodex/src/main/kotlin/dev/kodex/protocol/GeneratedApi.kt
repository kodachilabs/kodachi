// GENERATED FILE — DO NOT EDIT.
// Produced by scripts/generate_protocol.py from the app-server JSON Schema
// (`codex app-server generate-json-schema`). Regenerate instead of editing.
// Typed request API for every client request method.

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
 * Typed access to every app-server request method, grouped by protocol namespace.
 * Reach these through the properties on [dev.kodex.Codex].
 */

/**
 * `thread/…` requests (20 methods).
 */
public class ThreadsApi internal constructor(
    private val caller: ProtocolCaller,
) {
/**
 * NEW APIs
 */
    /** `thread/start` */
    public suspend fun start(params: ThreadStartParams): ThreadStartResponse =
        caller.call(
            ClientRequests.THREAD_START,
            encodeParams(ThreadStartParams.serializer(), params),
            ThreadStartResponse.serializer(),
        )

    /** `thread/resume` */
    public suspend fun resume(params: ThreadResumeParams): ThreadResumeResponse =
        caller.call(
            ClientRequests.THREAD_RESUME,
            encodeParams(ThreadResumeParams.serializer(), params),
            ThreadResumeResponse.serializer(),
        )

    /** `thread/fork` */
    public suspend fun fork(params: ThreadForkParams): ThreadForkResponse =
        caller.call(
            ClientRequests.THREAD_FORK,
            encodeParams(ThreadForkParams.serializer(), params),
            ThreadForkResponse.serializer(),
        )

    /** `thread/archive` */
    public suspend fun archive(params: ThreadArchiveParams): ThreadArchiveResponse =
        caller.call(
            ClientRequests.THREAD_ARCHIVE,
            encodeParams(ThreadArchiveParams.serializer(), params),
            ThreadArchiveResponse.serializer(),
        )

    /** `thread/delete` */
    public suspend fun delete(params: ThreadDeleteParams): ThreadDeleteResponse =
        caller.call(
            ClientRequests.THREAD_DELETE,
            encodeParams(ThreadDeleteParams.serializer(), params),
            ThreadDeleteResponse.serializer(),
        )

    /** `thread/unsubscribe` */
    public suspend fun unsubscribe(params: ThreadUnsubscribeParams): ThreadUnsubscribeResponse =
        caller.call(
            ClientRequests.THREAD_UNSUBSCRIBE,
            encodeParams(ThreadUnsubscribeParams.serializer(), params),
            ThreadUnsubscribeResponse.serializer(),
        )

    /** `thread/name/set` */
    public suspend fun nameSet(params: ThreadSetNameParams): ThreadSetNameResponse =
        caller.call(
            ClientRequests.THREAD_NAME_SET,
            encodeParams(ThreadSetNameParams.serializer(), params),
            ThreadSetNameResponse.serializer(),
        )

    /** `thread/goal/set` */
    public suspend fun goalSet(params: ThreadGoalSetParams): ThreadGoalSetResponse =
        caller.call(
            ClientRequests.THREAD_GOAL_SET,
            encodeParams(ThreadGoalSetParams.serializer(), params),
            ThreadGoalSetResponse.serializer(),
        )

    /** `thread/goal/get` */
    public suspend fun goalGet(params: ThreadGoalGetParams): ThreadGoalGetResponse =
        caller.call(
            ClientRequests.THREAD_GOAL_GET,
            encodeParams(ThreadGoalGetParams.serializer(), params),
            ThreadGoalGetResponse.serializer(),
        )

    /** `thread/goal/clear` */
    public suspend fun goalClear(params: ThreadGoalClearParams): ThreadGoalClearResponse =
        caller.call(
            ClientRequests.THREAD_GOAL_CLEAR,
            encodeParams(ThreadGoalClearParams.serializer(), params),
            ThreadGoalClearResponse.serializer(),
        )

    /** `thread/metadata/update` */
    public suspend fun metadataUpdate(params: ThreadMetadataUpdateParams): ThreadMetadataUpdateResponse =
        caller.call(
            ClientRequests.THREAD_METADATA_UPDATE,
            encodeParams(ThreadMetadataUpdateParams.serializer(), params),
            ThreadMetadataUpdateResponse.serializer(),
        )

    /** `thread/unarchive` */
    public suspend fun unarchive(params: ThreadUnarchiveParams): ThreadUnarchiveResponse =
        caller.call(
            ClientRequests.THREAD_UNARCHIVE,
            encodeParams(ThreadUnarchiveParams.serializer(), params),
            ThreadUnarchiveResponse.serializer(),
        )

    /** `thread/compact/start` */
    public suspend fun compactStart(params: ThreadCompactStartParams): ThreadCompactStartResponse =
        caller.call(
            ClientRequests.THREAD_COMPACT_START,
            encodeParams(ThreadCompactStartParams.serializer(), params),
            ThreadCompactStartResponse.serializer(),
        )

    /** `thread/shellCommand` */
    public suspend fun shellCommand(params: ThreadShellCommandParams): ThreadShellCommandResponse =
        caller.call(
            ClientRequests.THREAD_SHELL_COMMAND,
            encodeParams(ThreadShellCommandParams.serializer(), params),
            ThreadShellCommandResponse.serializer(),
        )

    /** `thread/approveGuardianDeniedAction` */
    public suspend fun approveGuardianDeniedAction(params: ThreadApproveGuardianDeniedActionParams): ThreadApproveGuardianDeniedActionResponse =
        caller.call(
            ClientRequests.THREAD_APPROVE_GUARDIAN_DENIED_ACTION,
            encodeParams(ThreadApproveGuardianDeniedActionParams.serializer(), params),
            ThreadApproveGuardianDeniedActionResponse.serializer(),
        )

    /** `thread/rollback` */
    public suspend fun rollback(params: ThreadRollbackParams): ThreadRollbackResponse =
        caller.call(
            ClientRequests.THREAD_ROLLBACK,
            encodeParams(ThreadRollbackParams.serializer(), params),
            ThreadRollbackResponse.serializer(),
        )

    /** `thread/list` */
    public suspend fun list(params: ThreadListParams): ThreadListResponse =
        caller.call(
            ClientRequests.THREAD_LIST,
            encodeParams(ThreadListParams.serializer(), params),
            ThreadListResponse.serializer(),
        )

    /** `thread/loaded/list` */
    public suspend fun loadedList(params: ThreadLoadedListParams): ThreadLoadedListResponse =
        caller.call(
            ClientRequests.THREAD_LOADED_LIST,
            encodeParams(ThreadLoadedListParams.serializer(), params),
            ThreadLoadedListResponse.serializer(),
        )

    /** `thread/read` */
    public suspend fun read(params: ThreadReadParams): ThreadReadResponse =
        caller.call(
            ClientRequests.THREAD_READ,
            encodeParams(ThreadReadParams.serializer(), params),
            ThreadReadResponse.serializer(),
        )

/**
 * Append raw Responses API items to the thread history without starting a user turn.
 */
    /** `thread/inject_items` */
    public suspend fun inject_items(params: ThreadInjectItemsParams): ThreadInjectItemsResponse =
        caller.call(
            ClientRequests.THREAD_INJECT_ITEMS,
            encodeParams(ThreadInjectItemsParams.serializer(), params),
            ThreadInjectItemsResponse.serializer(),
        )

}

/**
 * `skills/…` requests (3 methods).
 */
public class SkillsApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `skills/list` */
    public suspend fun list(params: SkillsListParams): SkillsListResponse =
        caller.call(
            ClientRequests.SKILLS_LIST,
            encodeParams(SkillsListParams.serializer(), params),
            SkillsListResponse.serializer(),
        )

    /** `skills/extraRoots/set` */
    public suspend fun extraRootsSet(params: SkillsExtraRootsSetParams): SkillsExtraRootsSetResponse =
        caller.call(
            ClientRequests.SKILLS_EXTRA_ROOTS_SET,
            encodeParams(SkillsExtraRootsSetParams.serializer(), params),
            SkillsExtraRootsSetResponse.serializer(),
        )

    /** `skills/config/write` */
    public suspend fun configWrite(params: SkillsConfigWriteParams): SkillsConfigWriteResponse =
        caller.call(
            ClientRequests.SKILLS_CONFIG_WRITE,
            encodeParams(SkillsConfigWriteParams.serializer(), params),
            SkillsConfigWriteResponse.serializer(),
        )

}

/**
 * `hooks/…` requests (1 methods).
 */
public class HooksApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `hooks/list` */
    public suspend fun list(params: HooksListParams): HooksListResponse =
        caller.call(
            ClientRequests.HOOKS_LIST,
            encodeParams(HooksListParams.serializer(), params),
            HooksListResponse.serializer(),
        )

}

/**
 * `marketplace/…` requests (3 methods).
 */
public class MarketplaceApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `marketplace/add` */
    public suspend fun add(params: MarketplaceAddParams): MarketplaceAddResponse =
        caller.call(
            ClientRequests.MARKETPLACE_ADD,
            encodeParams(MarketplaceAddParams.serializer(), params),
            MarketplaceAddResponse.serializer(),
        )

    /** `marketplace/remove` */
    public suspend fun remove(params: MarketplaceRemoveParams): MarketplaceRemoveResponse =
        caller.call(
            ClientRequests.MARKETPLACE_REMOVE,
            encodeParams(MarketplaceRemoveParams.serializer(), params),
            MarketplaceRemoveResponse.serializer(),
        )

    /** `marketplace/upgrade` */
    public suspend fun upgrade(params: MarketplaceUpgradeParams): MarketplaceUpgradeResponse =
        caller.call(
            ClientRequests.MARKETPLACE_UPGRADE,
            encodeParams(MarketplaceUpgradeParams.serializer(), params),
            MarketplaceUpgradeResponse.serializer(),
        )

}

/**
 * `plugin/…` requests (11 methods).
 */
public class PluginsApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `plugin/list` */
    public suspend fun list(params: PluginListParams): PluginListResponse =
        caller.call(
            ClientRequests.PLUGIN_LIST,
            encodeParams(PluginListParams.serializer(), params),
            PluginListResponse.serializer(),
        )

    /** `plugin/installed` */
    public suspend fun installed(params: PluginInstalledParams): PluginInstalledResponse =
        caller.call(
            ClientRequests.PLUGIN_INSTALLED,
            encodeParams(PluginInstalledParams.serializer(), params),
            PluginInstalledResponse.serializer(),
        )

    /** `plugin/read` */
    public suspend fun read(params: PluginReadParams): PluginReadResponse =
        caller.call(
            ClientRequests.PLUGIN_READ,
            encodeParams(PluginReadParams.serializer(), params),
            PluginReadResponse.serializer(),
        )

    /** `plugin/skill/read` */
    public suspend fun skillRead(params: PluginSkillReadParams): PluginSkillReadResponse =
        caller.call(
            ClientRequests.PLUGIN_SKILL_READ,
            encodeParams(PluginSkillReadParams.serializer(), params),
            PluginSkillReadResponse.serializer(),
        )

    /** `plugin/share/save` */
    public suspend fun shareSave(params: PluginShareSaveParams): PluginShareSaveResponse =
        caller.call(
            ClientRequests.PLUGIN_SHARE_SAVE,
            encodeParams(PluginShareSaveParams.serializer(), params),
            PluginShareSaveResponse.serializer(),
        )

    /** `plugin/share/updateTargets` */
    public suspend fun shareUpdateTargets(params: PluginShareUpdateTargetsParams): PluginShareUpdateTargetsResponse =
        caller.call(
            ClientRequests.PLUGIN_SHARE_UPDATE_TARGETS,
            encodeParams(PluginShareUpdateTargetsParams.serializer(), params),
            PluginShareUpdateTargetsResponse.serializer(),
        )

    /** `plugin/share/list` */
    public suspend fun shareList(params: PluginShareListParams): PluginShareListResponse =
        caller.call(
            ClientRequests.PLUGIN_SHARE_LIST,
            encodeParams(PluginShareListParams.serializer(), params),
            PluginShareListResponse.serializer(),
        )

    /** `plugin/share/checkout` */
    public suspend fun shareCheckout(params: PluginShareCheckoutParams): PluginShareCheckoutResponse =
        caller.call(
            ClientRequests.PLUGIN_SHARE_CHECKOUT,
            encodeParams(PluginShareCheckoutParams.serializer(), params),
            PluginShareCheckoutResponse.serializer(),
        )

    /** `plugin/share/delete` */
    public suspend fun shareDelete(params: PluginShareDeleteParams): PluginShareDeleteResponse =
        caller.call(
            ClientRequests.PLUGIN_SHARE_DELETE,
            encodeParams(PluginShareDeleteParams.serializer(), params),
            PluginShareDeleteResponse.serializer(),
        )

    /** `plugin/install` */
    public suspend fun install(params: PluginInstallParams): PluginInstallResponse =
        caller.call(
            ClientRequests.PLUGIN_INSTALL,
            encodeParams(PluginInstallParams.serializer(), params),
            PluginInstallResponse.serializer(),
        )

    /** `plugin/uninstall` */
    public suspend fun uninstall(params: PluginUninstallParams): PluginUninstallResponse =
        caller.call(
            ClientRequests.PLUGIN_UNINSTALL,
            encodeParams(PluginUninstallParams.serializer(), params),
            PluginUninstallResponse.serializer(),
        )

}

/**
 * `app/…` requests (3 methods).
 */
public class AppsApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `app/read` */
    public suspend fun read(params: AppsReadParams): AppsReadResponse =
        caller.call(
            ClientRequests.APP_READ,
            encodeParams(AppsReadParams.serializer(), params),
            AppsReadResponse.serializer(),
        )

    /** `app/list` */
    public suspend fun list(params: AppsListParams): AppsListResponse =
        caller.call(
            ClientRequests.APP_LIST,
            encodeParams(AppsListParams.serializer(), params),
            AppsListResponse.serializer(),
        )

    /** `app/installed` */
    public suspend fun installed(params: AppsInstalledParams): AppsInstalledResponse =
        caller.call(
            ClientRequests.APP_INSTALLED,
            encodeParams(AppsInstalledParams.serializer(), params),
            AppsInstalledResponse.serializer(),
        )

}

/**
 * `fs/…` requests (9 methods).
 */
public class FsApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `fs/readFile` */
    public suspend fun readFile(params: FsReadFileParams): FsReadFileResponse =
        caller.call(
            ClientRequests.FS_READ_FILE,
            encodeParams(FsReadFileParams.serializer(), params),
            FsReadFileResponse.serializer(),
        )

    /** `fs/writeFile` */
    public suspend fun writeFile(params: FsWriteFileParams): FsWriteFileResponse =
        caller.call(
            ClientRequests.FS_WRITE_FILE,
            encodeParams(FsWriteFileParams.serializer(), params),
            FsWriteFileResponse.serializer(),
        )

    /** `fs/createDirectory` */
    public suspend fun createDirectory(params: FsCreateDirectoryParams): FsCreateDirectoryResponse =
        caller.call(
            ClientRequests.FS_CREATE_DIRECTORY,
            encodeParams(FsCreateDirectoryParams.serializer(), params),
            FsCreateDirectoryResponse.serializer(),
        )

    /** `fs/getMetadata` */
    public suspend fun getMetadata(params: FsGetMetadataParams): FsGetMetadataResponse =
        caller.call(
            ClientRequests.FS_GET_METADATA,
            encodeParams(FsGetMetadataParams.serializer(), params),
            FsGetMetadataResponse.serializer(),
        )

    /** `fs/readDirectory` */
    public suspend fun readDirectory(params: FsReadDirectoryParams): FsReadDirectoryResponse =
        caller.call(
            ClientRequests.FS_READ_DIRECTORY,
            encodeParams(FsReadDirectoryParams.serializer(), params),
            FsReadDirectoryResponse.serializer(),
        )

    /** `fs/remove` */
    public suspend fun remove(params: FsRemoveParams): FsRemoveResponse =
        caller.call(
            ClientRequests.FS_REMOVE,
            encodeParams(FsRemoveParams.serializer(), params),
            FsRemoveResponse.serializer(),
        )

    /** `fs/copy` */
    public suspend fun copy(params: FsCopyParams): FsCopyResponse =
        caller.call(
            ClientRequests.FS_COPY,
            encodeParams(FsCopyParams.serializer(), params),
            FsCopyResponse.serializer(),
        )

    /** `fs/watch` */
    public suspend fun watch(params: FsWatchParams): FsWatchResponse =
        caller.call(
            ClientRequests.FS_WATCH,
            encodeParams(FsWatchParams.serializer(), params),
            FsWatchResponse.serializer(),
        )

    /** `fs/unwatch` */
    public suspend fun unwatch(params: FsUnwatchParams): FsUnwatchResponse =
        caller.call(
            ClientRequests.FS_UNWATCH,
            encodeParams(FsUnwatchParams.serializer(), params),
            FsUnwatchResponse.serializer(),
        )

}

/**
 * `turn/…` requests (3 methods).
 */
public class TurnsApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `turn/start` */
    public suspend fun start(params: TurnStartParams): TurnStartResponse =
        caller.call(
            ClientRequests.TURN_START,
            encodeParams(TurnStartParams.serializer(), params),
            TurnStartResponse.serializer(),
        )

    /** `turn/steer` */
    public suspend fun steer(params: TurnSteerParams): TurnSteerResponse =
        caller.call(
            ClientRequests.TURN_STEER,
            encodeParams(TurnSteerParams.serializer(), params),
            TurnSteerResponse.serializer(),
        )

    /** `turn/interrupt` */
    public suspend fun interrupt(params: TurnInterruptParams): TurnInterruptResponse =
        caller.call(
            ClientRequests.TURN_INTERRUPT,
            encodeParams(TurnInterruptParams.serializer(), params),
            TurnInterruptResponse.serializer(),
        )

}

/**
 * `review/…` requests (1 methods).
 */
public class ReviewApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `review/start` */
    public suspend fun start(params: ReviewStartParams): ReviewStartResponse =
        caller.call(
            ClientRequests.REVIEW_START,
            encodeParams(ReviewStartParams.serializer(), params),
            ReviewStartResponse.serializer(),
        )

}

/**
 * `model/…` requests (1 methods).
 */
public class ModelApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `model/list` */
    public suspend fun list(params: ModelListParams): ModelListResponse =
        caller.call(
            ClientRequests.MODEL_LIST,
            encodeParams(ModelListParams.serializer(), params),
            ModelListResponse.serializer(),
        )

}

/**
 * `modelProvider/…` requests (1 methods).
 */
public class ModelProviderApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `modelProvider/capabilities/read` */
    public suspend fun capabilitiesRead(params: ModelProviderCapabilitiesReadParams): ModelProviderCapabilitiesReadResponse =
        caller.call(
            ClientRequests.MODEL_PROVIDER_CAPABILITIES_READ,
            encodeParams(ModelProviderCapabilitiesReadParams.serializer(), params),
            ModelProviderCapabilitiesReadResponse.serializer(),
        )

}

/**
 * `experimentalFeature/…` requests (2 methods).
 */
public class ExperimentalFeatureApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `experimentalFeature/list` */
    public suspend fun list(params: ExperimentalFeatureListParams): ExperimentalFeatureListResponse =
        caller.call(
            ClientRequests.EXPERIMENTAL_FEATURE_LIST,
            encodeParams(ExperimentalFeatureListParams.serializer(), params),
            ExperimentalFeatureListResponse.serializer(),
        )

    /** `experimentalFeature/enablement/set` */
    public suspend fun enablementSet(params: ExperimentalFeatureEnablementSetParams): ExperimentalFeatureEnablementSetResponse =
        caller.call(
            ClientRequests.EXPERIMENTAL_FEATURE_ENABLEMENT_SET,
            encodeParams(ExperimentalFeatureEnablementSetParams.serializer(), params),
            ExperimentalFeatureEnablementSetResponse.serializer(),
        )

}

/**
 * `permissionProfile/…` requests (1 methods).
 */
public class PermissionProfileApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `permissionProfile/list` */
    public suspend fun list(params: PermissionProfileListParams): PermissionProfileListResponse =
        caller.call(
            ClientRequests.PERMISSION_PROFILE_LIST,
            encodeParams(PermissionProfileListParams.serializer(), params),
            PermissionProfileListResponse.serializer(),
        )

}

/**
 * `mcpServer/…` requests (3 methods).
 */
public class McpServerApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `mcpServer/oauth/login` */
    public suspend fun oauthLogin(params: McpServerOauthLoginParams): McpServerOauthLoginResponse =
        caller.call(
            ClientRequests.MCP_SERVER_OAUTH_LOGIN,
            encodeParams(McpServerOauthLoginParams.serializer(), params),
            McpServerOauthLoginResponse.serializer(),
        )

    /** `mcpServer/resource/read` */
    public suspend fun resourceRead(params: McpResourceReadParams): McpResourceReadResponse =
        caller.call(
            ClientRequests.MCP_SERVER_RESOURCE_READ,
            encodeParams(McpResourceReadParams.serializer(), params),
            McpResourceReadResponse.serializer(),
        )

    /** `mcpServer/tool/call` */
    public suspend fun toolCall(params: McpServerToolCallParams): McpServerToolCallResponse =
        caller.call(
            ClientRequests.MCP_SERVER_TOOL_CALL,
            encodeParams(McpServerToolCallParams.serializer(), params),
            McpServerToolCallResponse.serializer(),
        )

}

/**
 * `config/…` requests (4 methods).
 */
public class ConfigApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `config/mcpServer/reload` */
    public suspend fun mcpServerReload(): McpServerRefreshResponse =
        caller.call(ClientRequests.CONFIG_MCP_SERVER_RELOAD, null, McpServerRefreshResponse.serializer())

    /** `config/read` */
    public suspend fun read(params: ConfigReadParams): ConfigReadResponse =
        caller.call(
            ClientRequests.CONFIG_READ,
            encodeParams(ConfigReadParams.serializer(), params),
            ConfigReadResponse.serializer(),
        )

    /** `config/value/write` */
    public suspend fun valueWrite(params: ConfigValueWriteParams): ConfigWriteResponse =
        caller.call(
            ClientRequests.CONFIG_VALUE_WRITE,
            encodeParams(ConfigValueWriteParams.serializer(), params),
            ConfigWriteResponse.serializer(),
        )

    /** `config/batchWrite` */
    public suspend fun batchWrite(params: ConfigBatchWriteParams): ConfigWriteResponse =
        caller.call(
            ClientRequests.CONFIG_BATCH_WRITE,
            encodeParams(ConfigBatchWriteParams.serializer(), params),
            ConfigWriteResponse.serializer(),
        )

}

/**
 * `mcpServerStatus/…` requests (1 methods).
 */
public class McpServerStatusApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `mcpServerStatus/list` */
    public suspend fun list(params: ListMcpServerStatusParams): ListMcpServerStatusResponse =
        caller.call(
            ClientRequests.MCP_SERVER_STATUS_LIST,
            encodeParams(ListMcpServerStatusParams.serializer(), params),
            ListMcpServerStatusResponse.serializer(),
        )

}

/**
 * `windowsSandbox/…` requests (2 methods).
 */
public class WindowsSandboxApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `windowsSandbox/setupStart` */
    public suspend fun setupStart(params: WindowsSandboxSetupStartParams): WindowsSandboxSetupStartResponse =
        caller.call(
            ClientRequests.WINDOWS_SANDBOX_SETUP_START,
            encodeParams(WindowsSandboxSetupStartParams.serializer(), params),
            WindowsSandboxSetupStartResponse.serializer(),
        )

    /** `windowsSandbox/readiness` */
    public suspend fun readiness(): WindowsSandboxReadinessResponse =
        caller.call(ClientRequests.WINDOWS_SANDBOX_READINESS, null, WindowsSandboxReadinessResponse.serializer())

}

/**
 * `account/…` requests (9 methods).
 */
public class AccountApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `account/login/start` */
    public suspend fun loginStart(params: LoginAccountParams): LoginAccountResponse =
        caller.call(
            ClientRequests.ACCOUNT_LOGIN_START,
            encodeParams(LoginAccountParams.serializer(), params),
            LoginAccountResponse.serializer(),
        )

    /** `account/login/cancel` */
    public suspend fun loginCancel(params: CancelLoginAccountParams): CancelLoginAccountResponse =
        caller.call(
            ClientRequests.ACCOUNT_LOGIN_CANCEL,
            encodeParams(CancelLoginAccountParams.serializer(), params),
            CancelLoginAccountResponse.serializer(),
        )

    /** `account/logout` */
    public suspend fun logout(): LogoutAccountResponse =
        caller.call(ClientRequests.ACCOUNT_LOGOUT, null, LogoutAccountResponse.serializer())

    /** `account/rateLimits/read` */
    public suspend fun rateLimitsRead(): GetAccountRateLimitsResponse =
        caller.call(ClientRequests.ACCOUNT_RATE_LIMITS_READ, null, GetAccountRateLimitsResponse.serializer())

    /** `account/rateLimitResetCredit/consume` */
    public suspend fun rateLimitResetCreditConsume(params: ConsumeAccountRateLimitResetCreditParams): ConsumeAccountRateLimitResetCreditResponse =
        caller.call(
            ClientRequests.ACCOUNT_RATE_LIMIT_RESET_CREDIT_CONSUME,
            encodeParams(ConsumeAccountRateLimitResetCreditParams.serializer(), params),
            ConsumeAccountRateLimitResetCreditResponse.serializer(),
        )

    /** `account/usage/read` */
    public suspend fun usageRead(): GetAccountTokenUsageResponse =
        caller.call(ClientRequests.ACCOUNT_USAGE_READ, null, GetAccountTokenUsageResponse.serializer())

    /** `account/workspaceMessages/read` */
    public suspend fun workspaceMessagesRead(): JsonElement =
        caller.call(ClientRequests.ACCOUNT_WORKSPACE_MESSAGES_READ, null, JsonElement.serializer())

    /** `account/sendAddCreditsNudgeEmail` */
    public suspend fun sendAddCreditsNudgeEmail(params: SendAddCreditsNudgeEmailParams): SendAddCreditsNudgeEmailResponse =
        caller.call(
            ClientRequests.ACCOUNT_SEND_ADD_CREDITS_NUDGE_EMAIL,
            encodeParams(SendAddCreditsNudgeEmailParams.serializer(), params),
            SendAddCreditsNudgeEmailResponse.serializer(),
        )

    /** `account/read` */
    public suspend fun read(params: GetAccountParams): GetAccountResponse =
        caller.call(
            ClientRequests.ACCOUNT_READ,
            encodeParams(GetAccountParams.serializer(), params),
            GetAccountResponse.serializer(),
        )

}

/**
 * `feedback/…` requests (1 methods).
 */
public class FeedbackApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `feedback/upload` */
    public suspend fun upload(params: FeedbackUploadParams): FeedbackUploadResponse =
        caller.call(
            ClientRequests.FEEDBACK_UPLOAD,
            encodeParams(FeedbackUploadParams.serializer(), params),
            FeedbackUploadResponse.serializer(),
        )

}

/**
 * `command/…` requests (4 methods).
 */
public class CommandApi internal constructor(
    private val caller: ProtocolCaller,
) {
/**
 * Execute a standalone command (argv vector) under the server's sandbox.
 */
    /** `command/exec` */
    public suspend fun exec(params: CommandExecParams): CommandExecResponse =
        caller.call(
            ClientRequests.COMMAND_EXEC,
            encodeParams(CommandExecParams.serializer(), params),
            CommandExecResponse.serializer(),
        )

/**
 * Write stdin bytes to a running `command/exec` session or close stdin.
 */
    /** `command/exec/write` */
    public suspend fun execWrite(params: CommandExecWriteParams): CommandExecWriteResponse =
        caller.call(
            ClientRequests.COMMAND_EXEC_WRITE,
            encodeParams(CommandExecWriteParams.serializer(), params),
            CommandExecWriteResponse.serializer(),
        )

/**
 * Terminate a running `command/exec` session by client-supplied `processId`.
 */
    /** `command/exec/terminate` */
    public suspend fun execTerminate(params: CommandExecTerminateParams): CommandExecTerminateResponse =
        caller.call(
            ClientRequests.COMMAND_EXEC_TERMINATE,
            encodeParams(CommandExecTerminateParams.serializer(), params),
            CommandExecTerminateResponse.serializer(),
        )

/**
 * Resize a running PTY-backed `command/exec` session by client-supplied `processId`.
 */
    /** `command/exec/resize` */
    public suspend fun execResize(params: CommandExecResizeParams): CommandExecResizeResponse =
        caller.call(
            ClientRequests.COMMAND_EXEC_RESIZE,
            encodeParams(CommandExecResizeParams.serializer(), params),
            CommandExecResizeResponse.serializer(),
        )

}

/**
 * `externalAgentConfig/…` requests (4 methods).
 */
public class ExternalAgentConfigApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `externalAgentConfig/detect` */
    public suspend fun detect(params: ExternalAgentConfigDetectParams): ExternalAgentConfigDetectResponse =
        caller.call(
            ClientRequests.EXTERNAL_AGENT_CONFIG_DETECT,
            encodeParams(ExternalAgentConfigDetectParams.serializer(), params),
            ExternalAgentConfigDetectResponse.serializer(),
        )

    /** `externalAgentConfig/import` */
    public suspend fun `import`(params: ExternalAgentConfigImportParams): ExternalAgentConfigImportResponse =
        caller.call(
            ClientRequests.EXTERNAL_AGENT_CONFIG_IMPORT,
            encodeParams(ExternalAgentConfigImportParams.serializer(), params),
            ExternalAgentConfigImportResponse.serializer(),
        )

    /** `externalAgentConfig/import/recordHistory` */
    public suspend fun importRecordHistory(params: ExternalAgentConfigImportHistoryRecordParams): ExternalAgentConfigImportHistoryRecordResponse =
        caller.call(
            ClientRequests.EXTERNAL_AGENT_CONFIG_IMPORT_RECORD_HISTORY,
            encodeParams(ExternalAgentConfigImportHistoryRecordParams.serializer(), params),
            ExternalAgentConfigImportHistoryRecordResponse.serializer(),
        )

    /** `externalAgentConfig/import/readHistories` */
    public suspend fun importReadHistories(): JsonElement =
        caller.call(ClientRequests.EXTERNAL_AGENT_CONFIG_IMPORT_READ_HISTORIES, null, JsonElement.serializer())

}

/**
 * `configRequirements/…` requests (1 methods).
 */
public class ConfigRequirementsApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `configRequirements/read` */
    public suspend fun read(): ConfigRequirementsReadResponse =
        caller.call(ClientRequests.CONFIG_REQUIREMENTS_READ, null, ConfigRequirementsReadResponse.serializer())

}

/**
 * `fuzzyFileSearch/…` requests (1 methods).
 */
public class FuzzyFileSearchApi internal constructor(
    private val caller: ProtocolCaller,
) {
    /** `fuzzyFileSearch` */
    public suspend fun fuzzyFileSearch(params: FuzzyFileSearchParams): FuzzyFileSearchResponse =
        caller.call(
            ClientRequests.FUZZY_FILE_SEARCH,
            encodeParams(FuzzyFileSearchParams.serializer(), params),
            FuzzyFileSearchResponse.serializer(),
        )

}

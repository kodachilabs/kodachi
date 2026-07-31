package dev.kodex

import dev.kodex.protocol.ApplyPatchApprovalParams
import dev.kodex.protocol.ApplyPatchApprovalResponse
import dev.kodex.protocol.AttestationGenerateParams
import dev.kodex.protocol.AttestationGenerateResponse
import dev.kodex.protocol.ChatgptAuthTokensRefreshParams
import dev.kodex.protocol.ChatgptAuthTokensRefreshResponse
import dev.kodex.protocol.CommandExecutionApprovalDecision
import dev.kodex.protocol.CommandExecutionRequestApprovalParams
import dev.kodex.protocol.CommandExecutionRequestApprovalResponse
import dev.kodex.protocol.DynamicToolCallParams
import dev.kodex.protocol.DynamicToolCallResponse
import dev.kodex.protocol.ExecCommandApprovalParams
import dev.kodex.protocol.ExecCommandApprovalResponse
import dev.kodex.protocol.FileChangeApprovalDecision
import dev.kodex.protocol.FileChangeRequestApprovalParams
import dev.kodex.protocol.FileChangeRequestApprovalResponse
import dev.kodex.protocol.McpServerElicitationAction
import dev.kodex.protocol.McpServerElicitationRequestParams
import dev.kodex.protocol.McpServerElicitationRequestResponse
import dev.kodex.protocol.PermissionsRequestApprovalParams
import dev.kodex.protocol.PermissionsRequestApprovalResponse
import dev.kodex.protocol.ReviewDecision
import dev.kodex.protocol.ReviewDecisionDenied
import dev.kodex.protocol.ToolRequestUserInputParams
import dev.kodex.protocol.ToolRequestUserInputResponse
import kotlinx.serialization.json.JsonObject

/**
 * How the client answers one server request.
 *
 * A server request is a question the app-server asks *the client*, and the turn blocks
 * until it is answered. [Failure] answers with a JSON-RPC error, which is the honest
 * response when the client cannot do what was asked — a fabricated success would be
 * worse than an error the server can handle.
 */
public sealed interface ServerRequestOutcome {
    /** Answer with a result payload. */
    public data class Success(public val value: JsonObject) : ServerRequestOutcome

    /** Answer with a JSON-RPC error. */
    public data class Failure(
        public val code: Int = -32601,
        public val message: String = "unsupported by this client",
    ) : ServerRequestOutcome
}

/**
 * Answers the requests the app-server sends to the client.
 *
 * Every method has a default, so override only what you need. The defaults are
 * deliberately conservative: approvals follow [approvalDecision], capability requests the
 * SDK cannot fulfill on your behalf (dynamic tool calls, auth token refresh, attestation)
 * answer with a JSON-RPC error rather than a fabricated success.
 *
 * Handlers run on their own coroutine, so a slow answer does not block the event stream —
 * but the turn waits for it, so do not stall indefinitely.
 *
 * @param approvalDecision the default answer for the four approval-shaped requests
 */
public open class ServerRequestHandler(
    protected val approvalDecision: ApprovalDecision = ApprovalDecision.ACCEPT,
) {

    /** `item/commandExecution/requestApproval` — the agent wants to run a command. */
    public open suspend fun onCommandApproval(
        params: CommandExecutionRequestApprovalParams,
    ): CommandExecutionRequestApprovalResponse =
        CommandExecutionRequestApprovalResponse(approvalDecision.commandDecision)

    /** `item/fileChange/requestApproval` — the agent wants to apply a patch. */
    public open suspend fun onFileChangeApproval(
        params: FileChangeRequestApprovalParams,
    ): FileChangeRequestApprovalResponse =
        FileChangeRequestApprovalResponse(approvalDecision.fileChangeDecision)

    /** `execCommandApproval` — the legacy command-approval request. */
    public open suspend fun onExecCommandApproval(
        params: ExecCommandApprovalParams,
    ): ExecCommandApprovalResponse =
        ExecCommandApprovalResponse(approvalDecision.reviewDecision)

    /** `applyPatchApproval` — the legacy patch-approval request. */
    public open suspend fun onApplyPatchApproval(
        params: ApplyPatchApprovalParams,
    ): ApplyPatchApprovalResponse =
        ApplyPatchApprovalResponse(approvalDecision.reviewDecision)

    /**
     * `item/permissions/requestApproval` — the agent wants additional permissions.
     *
     * Defaults to a JSON-RPC error: granting permissions requires naming a concrete
     * profile, and silently granting or denying an unstated set would both be wrong.
     */
    public open suspend fun onPermissionsApproval(
        params: PermissionsRequestApprovalParams,
    ): PermissionsRequestApprovalResponse? = null

    /**
     * `item/tool/requestUserInput` — a tool is asking the user a question.
     *
     * Defaults to no answers, which the server treats as "the user did not respond".
     */
    public open suspend fun onToolUserInput(
        params: ToolRequestUserInputParams,
    ): ToolRequestUserInputResponse = ToolRequestUserInputResponse(answers = emptyMap())

    /** `mcpServer/elicitation/request` — an MCP server is asking the user for input. */
    public open suspend fun onMcpElicitation(
        params: McpServerElicitationRequestParams,
    ): McpServerElicitationRequestResponse =
        McpServerElicitationRequestResponse(action = McpServerElicitationAction.DECLINE)

    /**
     * `item/tool/call` — the server is asking this client to execute a tool it hosts.
     *
     * Defaults to an unsuccessful call: only a client that registered dynamic tools can
     * meaningfully answer.
     */
    public open suspend fun onDynamicToolCall(
        params: DynamicToolCallParams,
    ): DynamicToolCallResponse =
        DynamicToolCallResponse(contentItems = emptyList(), success = false)

    /**
     * `account/chatgptAuthTokens/refresh` — the server wants refreshed ChatGPT tokens.
     *
     * Defaults to a JSON-RPC error; only a client that owns the auth flow can mint these.
     */
    public open suspend fun onAuthTokenRefresh(
        params: ChatgptAuthTokensRefreshParams,
    ): ChatgptAuthTokensRefreshResponse? = null

    /** `attestation/generate` — the server wants a client-generated attestation token. */
    public open suspend fun onAttestation(
        params: AttestationGenerateParams,
    ): AttestationGenerateResponse? = null

    /**
     * Any server request this SDK version does not model.
     *
     * Defaults to a JSON-RPC error, which tells the server the client cannot help and
     * lets it proceed — unlike an empty result, which claims success.
     */
    public open suspend fun onUnknownRequest(
        method: String,
        params: JsonObject,
    ): ServerRequestOutcome = ServerRequestOutcome.Failure(
        message = "method '$method' is not handled by this client",
    )

    public companion object {
        /** Approves every approval-shaped request. Convenient for trusted workspaces. */
        public val ACCEPT_ALL: ServerRequestHandler = ServerRequestHandler(ApprovalDecision.ACCEPT)

        /** Refuses every approval while letting turns continue. */
        public val DECLINE_ALL: ServerRequestHandler = ServerRequestHandler(ApprovalDecision.DECLINE)
    }
}

/**
 * A decision for the approval-shaped server requests.
 *
 * The protocol uses three different decision enums across the four approval requests;
 * this maps one choice onto all of them.
 */
public enum class ApprovalDecision {
    /** Allow this one time. */
    ACCEPT,

    /** Allow, and stop asking for equivalent requests for the rest of the session. */
    ACCEPT_FOR_SESSION,

    /** Refuse; the agent continues the turn and may try something else. */
    DECLINE,

    /** Refuse and interrupt the turn immediately. */
    CANCEL,
    ;

    internal companion object {
        /** Sent as the rejection reason when a coarse DECLINE maps to `denied`. */
        const val DECLINE_REASON: String = "declined by client policy"
    }

    internal val commandDecision: CommandExecutionApprovalDecision
        get() = when (this) {
            ACCEPT -> CommandExecutionApprovalDecision.ACCEPT
            ACCEPT_FOR_SESSION -> CommandExecutionApprovalDecision.ACCEPT_FOR_SESSION
            DECLINE -> CommandExecutionApprovalDecision.DECLINE
            CANCEL -> CommandExecutionApprovalDecision.CANCEL
        }

    internal val fileChangeDecision: FileChangeApprovalDecision
        get() = when (this) {
            ACCEPT -> FileChangeApprovalDecision.ACCEPT
            ACCEPT_FOR_SESSION -> FileChangeApprovalDecision.ACCEPT_FOR_SESSION
            DECLINE -> FileChangeApprovalDecision.DECLINE
            CANCEL -> FileChangeApprovalDecision.CANCEL
        }

    internal val reviewDecision: ReviewDecision
        get() = when (this) {
            ACCEPT -> ReviewDecision.APPROVED
            ACCEPT_FOR_SESSION -> ReviewDecision.APPROVED_FOR_SESSION
            // `denied` became an object variant carrying a reason in Codex 0.146.
            DECLINE -> ReviewDecision.Denied(ReviewDecisionDenied(rejection = DECLINE_REASON))
            CANCEL -> ReviewDecision.ABORT
        }
}

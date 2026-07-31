package dev.kodachi.internal

import dev.kodachi.ServerRequestHandler
import dev.kodachi.ServerRequestOutcome
import dev.kodachi.protocol.ApplyPatchApprovalParams
import dev.kodachi.protocol.ApplyPatchApprovalResponse
import dev.kodachi.protocol.AttestationGenerateParams
import dev.kodachi.protocol.AttestationGenerateResponse
import dev.kodachi.protocol.ChatgptAuthTokensRefreshParams
import dev.kodachi.protocol.ChatgptAuthTokensRefreshResponse
import dev.kodachi.protocol.CommandExecutionRequestApprovalParams
import dev.kodachi.protocol.CommandExecutionRequestApprovalResponse
import dev.kodachi.protocol.DynamicToolCallParams
import dev.kodachi.protocol.DynamicToolCallResponse
import dev.kodachi.protocol.ExecCommandApprovalParams
import dev.kodachi.protocol.ExecCommandApprovalResponse
import dev.kodachi.protocol.FileChangeRequestApprovalParams
import dev.kodachi.protocol.FileChangeRequestApprovalResponse
import dev.kodachi.protocol.McpServerElicitationRequestParams
import dev.kodachi.protocol.McpServerElicitationRequestResponse
import dev.kodachi.protocol.PermissionsRequestApprovalParams
import dev.kodachi.protocol.PermissionsRequestApprovalResponse
import dev.kodachi.protocol.ServerRequests
import dev.kodachi.protocol.ToolRequestUserInputParams
import dev.kodachi.protocol.ToolRequestUserInputResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.JsonObject

/**
 * Routes each server request to its typed handler method.
 *
 * A request the server sends must be answered or the turn stalls, so every branch here
 * produces an outcome — a result, or a JSON-RPC error when the client genuinely cannot
 * answer. Requests with no branch fall through to
 * [ServerRequestHandler.onUnknownRequest].
 */
internal object ServerRequestDispatcher {

    suspend fun dispatch(
        handler: ServerRequestHandler,
        method: String,
        params: JsonObject,
    ): ServerRequestOutcome = when (method) {
        ServerRequests.ITEM_COMMAND_EXECUTION_REQUEST_APPROVAL -> answer(
            params,
            CommandExecutionRequestApprovalParams.serializer(),
            CommandExecutionRequestApprovalResponse.serializer(),
        ) { handler.onCommandApproval(it) }

        ServerRequests.ITEM_FILE_CHANGE_REQUEST_APPROVAL -> answer(
            params,
            FileChangeRequestApprovalParams.serializer(),
            FileChangeRequestApprovalResponse.serializer(),
        ) { handler.onFileChangeApproval(it) }

        ServerRequests.EXEC_COMMAND_APPROVAL -> answer(
            params,
            ExecCommandApprovalParams.serializer(),
            ExecCommandApprovalResponse.serializer(),
        ) { handler.onExecCommandApproval(it) }

        ServerRequests.APPLY_PATCH_APPROVAL -> answer(
            params,
            ApplyPatchApprovalParams.serializer(),
            ApplyPatchApprovalResponse.serializer(),
        ) { handler.onApplyPatchApproval(it) }

        ServerRequests.ITEM_PERMISSIONS_REQUEST_APPROVAL -> answerNullable(
            params,
            PermissionsRequestApprovalParams.serializer(),
            PermissionsRequestApprovalResponse.serializer(),
            "no permission profile to grant; override onPermissionsApproval to grant one",
        ) { handler.onPermissionsApproval(it) }

        ServerRequests.ITEM_TOOL_REQUEST_USER_INPUT -> answer(
            params,
            ToolRequestUserInputParams.serializer(),
            ToolRequestUserInputResponse.serializer(),
        ) { handler.onToolUserInput(it) }

        ServerRequests.MCP_SERVER_ELICITATION_REQUEST -> answer(
            params,
            McpServerElicitationRequestParams.serializer(),
            McpServerElicitationRequestResponse.serializer(),
        ) { handler.onMcpElicitation(it) }

        ServerRequests.ITEM_TOOL_CALL -> answer(
            params,
            DynamicToolCallParams.serializer(),
            DynamicToolCallResponse.serializer(),
        ) { handler.onDynamicToolCall(it) }

        ServerRequests.ACCOUNT_CHATGPT_AUTH_TOKENS_REFRESH -> answerNullable(
            params,
            ChatgptAuthTokensRefreshParams.serializer(),
            ChatgptAuthTokensRefreshResponse.serializer(),
            "this client cannot mint ChatGPT auth tokens; override onAuthTokenRefresh",
        ) { handler.onAuthTokenRefresh(it) }

        ServerRequests.ATTESTATION_GENERATE -> answerNullable(
            params,
            AttestationGenerateParams.serializer(),
            AttestationGenerateResponse.serializer(),
            "this client cannot generate attestations; override onAttestation",
        ) { handler.onAttestation(it) }

        else -> handler.onUnknownRequest(method, params)
    }

    private suspend fun <P, R> answer(
        params: JsonObject,
        paramsSerializer: DeserializationStrategy<P>,
        resultSerializer: SerializationStrategy<R>,
        handle: suspend (P) -> R,
    ): ServerRequestOutcome {
        val decoded = CodexJson.decodeFromJsonElement(paramsSerializer, params)
        val result = handle(decoded)
        return ServerRequestOutcome.Success(
            CodexJson.encodeToJsonElement(resultSerializer, result) as JsonObject,
        )
    }

    /** A handler returning null means "I cannot answer this", which becomes an error. */
    private suspend fun <P, R> answerNullable(
        params: JsonObject,
        paramsSerializer: DeserializationStrategy<P>,
        resultSerializer: SerializationStrategy<R>,
        unsupportedMessage: String,
        handle: suspend (P) -> R?,
    ): ServerRequestOutcome {
        val decoded = CodexJson.decodeFromJsonElement(paramsSerializer, params)
        val result = handle(decoded)
            ?: return ServerRequestOutcome.Failure(message = unsupportedMessage)
        return ServerRequestOutcome.Success(
            CodexJson.encodeToJsonElement(resultSerializer, result) as JsonObject,
        )
    }
}

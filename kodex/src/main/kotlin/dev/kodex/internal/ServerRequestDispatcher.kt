package dev.kodex.internal

import dev.kodex.ServerRequestHandler
import dev.kodex.ServerRequestOutcome
import dev.kodex.protocol.ApplyPatchApprovalParams
import dev.kodex.protocol.ApplyPatchApprovalResponse
import dev.kodex.protocol.AttestationGenerateParams
import dev.kodex.protocol.AttestationGenerateResponse
import dev.kodex.protocol.ChatgptAuthTokensRefreshParams
import dev.kodex.protocol.ChatgptAuthTokensRefreshResponse
import dev.kodex.protocol.CommandExecutionRequestApprovalParams
import dev.kodex.protocol.CommandExecutionRequestApprovalResponse
import dev.kodex.protocol.DynamicToolCallParams
import dev.kodex.protocol.DynamicToolCallResponse
import dev.kodex.protocol.ExecCommandApprovalParams
import dev.kodex.protocol.ExecCommandApprovalResponse
import dev.kodex.protocol.FileChangeRequestApprovalParams
import dev.kodex.protocol.FileChangeRequestApprovalResponse
import dev.kodex.protocol.McpServerElicitationRequestParams
import dev.kodex.protocol.McpServerElicitationRequestResponse
import dev.kodex.protocol.PermissionsRequestApprovalParams
import dev.kodex.protocol.PermissionsRequestApprovalResponse
import dev.kodex.protocol.ServerRequests
import dev.kodex.protocol.ToolRequestUserInputParams
import dev.kodex.protocol.ToolRequestUserInputResponse
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

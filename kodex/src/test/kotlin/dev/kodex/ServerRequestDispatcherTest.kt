package dev.kodex

import dev.kodex.internal.ServerRequestDispatcher
import dev.kodex.protocol.CommandExecutionApprovalDecision
import dev.kodex.protocol.CommandExecutionRequestApprovalParams
import dev.kodex.protocol.CommandExecutionRequestApprovalResponse
import dev.kodex.protocol.ServerRequests
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Every request the server sends must be answered or the turn stalls, so each of the ten
 * defined server requests needs a branch that produces *some* outcome. These tests walk
 * all ten, plus an unmodelled method, and assert the shape of each answer.
 */
class ServerRequestDispatcherTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun params(raw: String): JsonObject = json.parseToJsonElement(raw).jsonObject

    private fun dispatch(
        method: String,
        raw: String,
        handler: ServerRequestHandler = ServerRequestHandler.ACCEPT_ALL,
    ): ServerRequestOutcome = runBlocking {
        ServerRequestDispatcher.dispatch(handler, method, params(raw))
    }

    private fun ServerRequestOutcome.decision(): String? =
        (this as? ServerRequestOutcome.Success)
            ?.value?.get("decision")?.jsonPrimitive?.contentOrNull

    // Minimal wire payloads carrying each params type's required fields.
    private val commandApproval = """
        {"threadId":"t1","turnId":"turn1","itemId":"i1","startedAtMs":1,"command":"rm -rf build","cwd":"/repo"}
    """.trimIndent()
    private val fileChangeApproval = """
        {"threadId":"t1","turnId":"turn1","itemId":"i1","startedAtMs":1}
    """.trimIndent()
    private val execCommandApproval = """
        {"callId":"c1","command":["ls"],"conversationId":"t1","cwd":"/repo","parsedCmd":[]}
    """.trimIndent()
    private val applyPatchApproval = """
        {"callId":"c1","conversationId":"t1","fileChanges":{}}
    """.trimIndent()

    @Test
    fun `all four approval requests answer with the configured decision`() {
        val accepting = ServerRequestHandler(ApprovalDecision.ACCEPT)

        assertEquals(
            "accept",
            dispatch(ServerRequests.ITEM_COMMAND_EXECUTION_REQUEST_APPROVAL, commandApproval, accepting).decision(),
        )
        assertEquals(
            "accept",
            dispatch(ServerRequests.ITEM_FILE_CHANGE_REQUEST_APPROVAL, fileChangeApproval, accepting).decision(),
        )
        // The two legacy approvals use ReviewDecision, whose accept value is `approved`.
        assertEquals(
            "approved",
            dispatch(ServerRequests.EXEC_COMMAND_APPROVAL, execCommandApproval, accepting).decision(),
        )
        assertEquals(
            "approved",
            dispatch(ServerRequests.APPLY_PATCH_APPROVAL, applyPatchApproval, accepting).decision(),
        )
    }

    @Test
    fun `each decision maps onto the right wire value per approval kind`() {
        val declining = ServerRequestHandler(ApprovalDecision.DECLINE)
        assertEquals(
            "decline",
            dispatch(ServerRequests.ITEM_COMMAND_EXECUTION_REQUEST_APPROVAL, commandApproval, declining).decision(),
        )
        // ReviewDecision's `denied` is an object variant carrying a rejection reason,
        // not a bare string, so it has no primitive decision value.
        val deniedOutcome = dispatch(ServerRequests.EXEC_COMMAND_APPROVAL, execCommandApproval, declining)
        val denied = assertIs<ServerRequestOutcome.Success>(deniedOutcome)
        assertTrue(
            denied.value["decision"]!!.jsonObject["denied"]!!
                .jsonObject["rejection"]!!.jsonPrimitive.content.isNotBlank(),
            "a denied review decision must carry a rejection reason",
        )

        val cancelling = ServerRequestHandler(ApprovalDecision.CANCEL)
        assertEquals(
            "cancel",
            dispatch(ServerRequests.ITEM_FILE_CHANGE_REQUEST_APPROVAL, fileChangeApproval, cancelling).decision(),
        )
        assertEquals(
            "abort",
            dispatch(ServerRequests.APPLY_PATCH_APPROVAL, applyPatchApproval, cancelling).decision(),
        )

        val sessionScoped = ServerRequestHandler(ApprovalDecision.ACCEPT_FOR_SESSION)
        assertEquals(
            "acceptForSession",
            dispatch(ServerRequests.ITEM_COMMAND_EXECUTION_REQUEST_APPROVAL, commandApproval, sessionScoped).decision(),
        )
    }

    @Test
    fun `a custom handler sees the decoded params and its answer is used`() {
        var seen: CommandExecutionRequestApprovalParams? = null
        val handler = object : ServerRequestHandler() {
            override suspend fun onCommandApproval(
                params: CommandExecutionRequestApprovalParams,
            ): CommandExecutionRequestApprovalResponse {
                seen = params
                return CommandExecutionRequestApprovalResponse(
                    CommandExecutionApprovalDecision.DECLINE,
                )
            }
        }

        val outcome = dispatch(
            ServerRequests.ITEM_COMMAND_EXECUTION_REQUEST_APPROVAL,
            commandApproval,
            handler,
        )

        assertEquals("decline", outcome.decision())
        val params = requireNotNull(seen)
        assertEquals("rm -rf build", params.command)
        assertEquals("/repo", params.cwd)
        assertEquals("turn1", params.turnId)
    }

    @Test
    fun `capability requests answer without a decision but still succeed`() {
        val userInput = dispatch(
            ServerRequests.ITEM_TOOL_REQUEST_USER_INPUT,
            """{"itemId":"i1","questions":[],"threadId":"t1","turnId":"turn1"}""",
        )
        assertIs<ServerRequestOutcome.Success>(userInput)
        assertTrue(userInput.value.containsKey("answers"))

        val elicitation = dispatch(
            ServerRequests.MCP_SERVER_ELICITATION_REQUEST,
            """{"serverName":"srv","threadId":"t1"}""",
        )
        assertIs<ServerRequestOutcome.Success>(elicitation)
        assertEquals(
            "decline",
            elicitation.value["action"]!!.jsonPrimitive.content,
        )

        val toolCall = dispatch(
            ServerRequests.ITEM_TOOL_CALL,
            """{"arguments":{},"callId":"c1","threadId":"t1","tool":"t","turnId":"turn1"}""",
        )
        assertIs<ServerRequestOutcome.Success>(toolCall)
        assertEquals(false, toolCall.value["success"]!!.jsonPrimitive.content.toBoolean())
    }

    @Test
    fun `requests the SDK cannot fulfil answer with an error, not a fake success`() {
        // Fabricating a token or a permission grant would be worse than telling the
        // server this client cannot help.
        val permissions = dispatch(
            ServerRequests.ITEM_PERMISSIONS_REQUEST_APPROVAL,
            """{"cwd":"/repo","itemId":"i1","permissions":{},"startedAtMs":1,"threadId":"t1","turnId":"turn1"}""",
        )
        assertIs<ServerRequestOutcome.Failure>(permissions)

        val refresh = dispatch(
            ServerRequests.ACCOUNT_CHATGPT_AUTH_TOKENS_REFRESH,
            """{"reason":"expired"}""",
        )
        assertIs<ServerRequestOutcome.Failure>(refresh)
        assertTrue(refresh.message.contains("auth token", ignoreCase = true))

        val attestation = dispatch(ServerRequests.ATTESTATION_GENERATE, "{}")
        assertIs<ServerRequestOutcome.Failure>(attestation)
    }

    @Test
    fun `an unmodelled server request still gets an answer`() {
        val outcome = dispatch("some/future/serverRequest", """{"anything":1}""")
        val failure = assertIs<ServerRequestOutcome.Failure>(outcome)
        assertTrue(failure.message.contains("some/future/serverRequest"))
        assertEquals(-32601, failure.code)
    }

    @Test
    fun `an overriding handler can fulfil a request the default declines`() {
        val handler = object : ServerRequestHandler() {
            override suspend fun onAttestation(
                params: dev.kodex.protocol.AttestationGenerateParams,
            ) = dev.kodex.protocol.AttestationGenerateResponse(token = "tok-123")
        }

        val outcome = dispatch(ServerRequests.ATTESTATION_GENERATE, "{}", handler)
        val success = assertIs<ServerRequestOutcome.Success>(outcome)
        assertEquals("tok-123", success.value["token"]!!.jsonPrimitive.content)
    }
}

package dev.kodex.samples.approvals

import dev.kodex.ApprovalDecision
import dev.kodex.ApprovalMode
import dev.kodex.Codex
import dev.kodex.CodexConfig
import dev.kodex.Sandbox
import dev.kodex.ServerRequestHandler
import dev.kodex.ServerRequestOutcome
import dev.kodex.protocol.AgentMessageDeltaNotification
import dev.kodex.protocol.CommandExecutionApprovalDecision
import dev.kodex.protocol.CommandExecutionRequestApprovalParams
import dev.kodex.protocol.CommandExecutionRequestApprovalResponse
import dev.kodex.protocol.FileChangeApprovalDecision
import dev.kodex.protocol.FileChangeRequestApprovalParams
import dev.kodex.protocol.FileChangeRequestApprovalResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject

/**
 * Gate the agent's escalated actions with your own policy.
 *
 * With [ApprovalMode.ASK_CLIENT], anything the sandbox would block comes to the handler
 * instead of being auto-decided. The turn waits on your answer, so answer promptly.
 *
 * ```
 * ./gradlew :samples:run -Psample=dev.kodex.samples.ApprovalsKt
 * ```
 */
fun main(args: Array<String>): Unit = runBlocking {
    val workdir = args.firstOrNull() ?: System.getProperty("java.io.tmpdir")

    val policy = object : ServerRequestHandler(ApprovalDecision.ACCEPT) {
        override suspend fun onCommandApproval(
            params: CommandExecutionRequestApprovalParams,
        ): CommandExecutionRequestApprovalResponse {
            val command = params.command.orEmpty()
            println("[approval] command: $command")
            println("           reason: ${params.reason}")

            // Refuse anything destructive; allow the rest for the session.
            val destructive = listOf("rm ", "rm -rf", "git push", "sudo ")
            return if (destructive.any { command.contains(it) }) {
                println("           -> DECLINE")
                CommandExecutionRequestApprovalResponse(
                    CommandExecutionApprovalDecision.DECLINE,
                )
            } else {
                println("           -> ACCEPT_FOR_SESSION")
                CommandExecutionRequestApprovalResponse(
                    CommandExecutionApprovalDecision.ACCEPT_FOR_SESSION,
                )
            }
        }

        override suspend fun onFileChangeApproval(
            params: FileChangeRequestApprovalParams,
        ): FileChangeRequestApprovalResponse {
            println("[approval] file change on item ${params.itemId} -> ACCEPT")
            return FileChangeRequestApprovalResponse(FileChangeApprovalDecision.ACCEPT)
        }

        override suspend fun onUnknownRequest(
            method: String,
            params: JsonObject,
        ): ServerRequestOutcome {
            println("[approval] unmodelled request $method -> DECLINE")
            return ServerRequestOutcome.Failure(message = "declined by this client's policy")
        }
    }

    val config = CodexConfig(
        cwd = workdir,
        serverRequestHandler = policy,
        // Surface server-side problems instead of losing them.
        onStderrLine = { line -> System.err.println("[codex] $line") },
    )

    Codex.connect(config).use { codex ->
        val thread = codex.startThread(
            sandbox = Sandbox.READ_ONLY,
            approvalMode = ApprovalMode.ASK_CLIENT,
        )

        val handle = thread.turn(
            "Create a file called approved.txt containing the word hello, then read it back.",
        )

        handle.stream().collect { event ->
            if (event is AgentMessageDeltaNotification) print(event.delta)
        }
        println()
    }
}

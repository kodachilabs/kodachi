package dev.kodachi.samples.approvals

import dev.kodachi.ApprovalDecision
import dev.kodachi.ApprovalMode
import dev.kodachi.Codex
import dev.kodachi.CodexConfig
import dev.kodachi.Sandbox
import dev.kodachi.ServerRequestHandler
import dev.kodachi.ServerRequestOutcome
import dev.kodachi.protocol.AgentMessageDeltaNotification
import dev.kodachi.protocol.CommandExecutionApprovalDecision
import dev.kodachi.protocol.CommandExecutionRequestApprovalParams
import dev.kodachi.protocol.CommandExecutionRequestApprovalResponse
import dev.kodachi.protocol.FileChangeApprovalDecision
import dev.kodachi.protocol.FileChangeRequestApprovalParams
import dev.kodachi.protocol.FileChangeRequestApprovalResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject

/**
 * Gate the agent's escalated actions with your own policy.
 *
 * With [ApprovalMode.ASK_CLIENT], anything the sandbox would block comes to the handler
 * instead of being auto-decided. The turn waits on your answer, so answer promptly.
 *
 * ```
 * ./gradlew :samples:run -Psample=dev.kodachi.samples.ApprovalsKt
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

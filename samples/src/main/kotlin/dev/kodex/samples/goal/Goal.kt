package dev.kodex.samples.goal

import dev.kodex.Codex
import dev.kodex.CodexConfig
import dev.kodex.Sandbox
import dev.kodex.protocol.AgentMessageDeltaNotification
import dev.kodex.protocol.CommandExecutionThreadItem
import dev.kodex.protocol.ItemCompletedNotification
import dev.kodex.protocol.ThreadGoalUpdatedNotification
import dev.kodex.protocol.TurnCompletedNotification
import dev.kodex.protocol.TurnStartedNotification
import kotlinx.coroutines.runBlocking

/**
 * Give Codex a standing objective and watch it work across as many turns as it needs.
 *
 * A turn is one exchange; a goal is a project. The server decides when to start another turn, so
 * this one stream covers all of them — turn boundaries stay visible, but you never have to stitch
 * them together yourself.
 *
 * ```
 * ./gradlew :samples:run -Psample=dev.kodex.samples.goal.GoalKt --args="/path/to/repo"
 * ```
 */
fun main(args: Array<String>) = runBlocking {
    val workdir = args.firstOrNull() ?: System.getProperty("user.dir")
    val objective = args.drop(1).joinToString(" ").ifBlank {
        "Summarize what this project does, then list its three largest source files."
    }

    Codex.connect(CodexConfig(cwd = workdir)).use { codex ->
        val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)

        // A token budget is the safety belt: a goal drives turns on its own, so bound it.
        val goal = thread.startGoal(objective = objective, tokenBudget = 200_000)
        println("goal: ${goal.goal.objective}")
        println("budget: ${goal.goal.tokenBudget} tokens\n")

        var turn = 0
        goal.stream().collect { event ->
            when (event) {
                is TurnStartedNotification -> println("\n--- turn ${++turn} (${event.turn.id}) ---")

                is AgentMessageDeltaNotification -> print(event.delta)

                is ItemCompletedNotification -> (event.item as? CommandExecutionThreadItem)
                    ?.let { println("\n$ ${it.command}  -> exit ${it.exitCode}") }

                is TurnCompletedNotification -> println("\n[turn ${event.turn.status}]")

                is ThreadGoalUpdatedNotification ->
                    println("[goal ${event.goal.status} | ${event.goal.tokensUsed} tokens used]")

                else -> Unit
            }
        }

        println("\n=== goal finished after $turn turn(s) ===")
        println("status: ${goal.status}")
        goal.refresh()?.let { println("spent: ${it.tokensUsed} tokens over ${it.timeUsedSeconds}s") }
    }
}

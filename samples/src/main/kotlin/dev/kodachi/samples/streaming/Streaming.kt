package dev.kodachi.samples.streaming

import dev.kodachi.Codex
import dev.kodachi.CodexConfig
import dev.kodachi.Sandbox
import dev.kodachi.protocol.AgentMessageDeltaNotification
import dev.kodachi.protocol.CommandExecutionThreadItem
import dev.kodachi.protocol.ErrorNotification
import dev.kodachi.protocol.FileChangeThreadItem
import dev.kodachi.protocol.ItemCompletedNotification
import dev.kodachi.protocol.ItemStartedNotification
import dev.kodachi.protocol.ReasoningSummaryTextDeltaNotification
import dev.kodachi.protocol.ThreadTokenUsageUpdatedNotification
import dev.kodachi.protocol.TurnCompletedNotification
import dev.kodachi.protocol.UnknownNotification
import kotlinx.coroutines.runBlocking

/**
 * Watch a turn as it happens: reasoning, commands, file edits, and streamed text.
 *
 * ```
 * ./gradlew :samples:run -Psample=dev.kodachi.samples.StreamingKt
 * ```
 */
fun main(args: Array<String>): Unit = runBlocking {
    val workdir = args.firstOrNull() ?: System.getProperty("user.dir")
    val prompt = args.drop(1).joinToString(" ").ifBlank {
        "List the files in this directory and tell me what kind of project it is."
    }

    Codex.connect(CodexConfig(cwd = workdir)).use { codex ->
        val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
        val handle = thread.turn(prompt)

        handle.stream().collect { event ->
            when (event) {
                is AgentMessageDeltaNotification -> print(event.delta)

                is ReasoningSummaryTextDeltaNotification -> print("[2m${event.delta}[0m")

                is ItemStartedNotification -> when (val item = event.item) {
                    is CommandExecutionThreadItem -> println("\n\$ ${item.command}")
                    else -> Unit
                }

                is ItemCompletedNotification -> when (val item = event.item) {
                    is CommandExecutionThreadItem -> println("  -> exit ${item.exitCode}")
                    is FileChangeThreadItem ->
                        item.changes.forEach { println("  ${it.kind} ${it.path}") }
                    else -> Unit
                }

                is ThreadTokenUsageUpdatedNotification ->
                    println("\n[tokens ${event.tokenUsage.total.totalTokens}]")

                is ErrorNotification ->
                    println("\n[error${if (event.willRetry) ", retrying" else ""}] ${event.error.message}")

                is TurnCompletedNotification ->
                    println("\n[turn ${event.turn.status} in ${event.turn.durationMs}ms]")

                // Events this SDK version does not model still arrive, intact.
                is UnknownNotification -> Unit

                else -> Unit
            }
        }
    }
}

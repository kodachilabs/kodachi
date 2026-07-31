package dev.kodex.cli

import dev.kodex.ApprovalMode
import dev.kodex.Codex
import dev.kodex.CodexConfig
import dev.kodex.CodexException
import dev.kodex.Sandbox
import dev.kodex.loginWithApiKey
import dev.kodex.protocol.AgentMessageDeltaNotification
import dev.kodex.protocol.CommandExecutionThreadItem
import dev.kodex.protocol.FileChangeThreadItem
import dev.kodex.protocol.ItemCompletedNotification
import dev.kodex.protocol.ItemStartedNotification
import dev.kodex.protocol.GetAccountParams
import dev.kodex.protocol.ProtocolInfo
import dev.kodex.protocol.ReasoningSummaryTextDeltaNotification
import dev.kodex.protocol.ThreadGoalUpdatedNotification
import dev.kodex.protocol.ThreadTokenUsageUpdatedNotification
import dev.kodex.protocol.TurnCompletedNotification
import dev.kodex.protocol.TurnStartedNotification
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.system.exitProcess

private const val USAGE = """
kodex — exercise the Kotlin Codex SDK from a terminal

USAGE
  kodex doctor                       Check the setup. Spends no model quota.
  kodex login                        Store an API key (read from stdin or ${'$'}OPENAI_API_KEY).
  kodex whoami                       Show the stored account and rate limits.
  kodex models                       List models this account can use.
  kodex chat <prompt>                Run one turn and stream it.
  kodex goal <objective>             Run a goal across as many turns as it takes.
  kodex exec <prompt>                Run one turn with writes allowed, approving everything.

OPTIONS
  --cwd <dir>          Working directory for the agent (default: current directory)
  --model <name>       Override the model
  --effort <level>     minimal | low | medium | high | max
  --budget <tokens>    Token budget, goals only (default 200000)
  --isolated           Use a throwaway CODEX_HOME so this cannot touch your real auth
  --quiet              Only print the final answer

AUTH
  An API key is read from ${'$'}OPENAI_API_KEY or stdin — never from a flag, because argv is
  visible to other processes and lands in shell history. `kodex login` stores it in the
  Codex home; note the server accepts a key without validating it, so a bad key only
  surfaces on the first turn.
"""

private class Options(args: List<String>) {
    val positional: List<String>
    val cwd: String
    val model: String?
    val effort: String?
    val budget: Long
    val isolated: Boolean
    val quiet: Boolean

    init {
        val rest = mutableListOf<String>()
        var cwdArg: String? = null
        var modelArg: String? = null
        var effortArg: String? = null
        var budgetArg: Long? = null
        var iso = false
        var q = false

        var i = 0
        while (i < args.size) {
            when (val a = args[i]) {
                "--cwd" -> cwdArg = args.getOrNull(++i)
                "--model" -> modelArg = args.getOrNull(++i)
                "--effort" -> effortArg = args.getOrNull(++i)
                "--budget" -> budgetArg = args.getOrNull(++i)?.toLongOrNull()
                "--isolated" -> iso = true
                "--quiet", "-q" -> q = true
                else -> rest += a
            }
            i++
        }

        positional = rest
        cwd = cwdArg ?: System.getProperty("user.dir")
        model = modelArg
        effort = effortArg
        budget = budgetArg ?: 200_000
        isolated = iso
        quiet = q
    }
}

public fun main(argv: Array<String>): Unit {
    val args = argv.toList()
    if (args.isEmpty() || args.first() in listOf("-h", "--help", "help")) {
        println(USAGE.trim())
        return
    }

    val command = args.first()
    val options = Options(args.drop(1))

    try {
        runBlocking {
            when (command) {
                "doctor" -> doctor(options)
                "login" -> login(options)
                "whoami" -> whoami(options)
                "models" -> models(options)
                "chat" -> chat(options, writable = false)
                "exec" -> chat(options, writable = true)
                "goal" -> goal(options)
                else -> {
                    System.err.println("unknown command: $command\n")
                    println(USAGE.trim())
                    exitProcess(2)
                }
            }
        }
    } catch (e: CodexException) {
        // The SDK's exceptions already explain themselves; a stack trace would only bury that.
        System.err.println("\nkodex: ${e.message}")
        exitProcess(1)
    }
}

/** Build a client, optionally against a throwaway Codex home. */
private fun open(options: Options): Codex {
    val env = buildMap {
        if (options.isolated) {
            val home = File(System.getProperty("java.io.tmpdir"), "kodex-cli-home-${System.nanoTime()}")
            home.mkdirs()
            put("CODEX_HOME", home.absolutePath)
            System.err.println("[isolated CODEX_HOME: ${home.absolutePath}]")
        }
    }
    return Codex(
        CodexConfig(
            cwd = options.cwd,
            env = env,
            // Surface server-side failures instead of losing them; the SDK stays quiet otherwise.
            onStderrLine = if (options.quiet) null else { line -> System.err.println("[codex] $line") },
        ),
    )
}

/**
 * Everything checkable without spending a token: which binary, which protocol, does the
 * handshake work, is an account present, are the namespaces wired.
 */
private suspend fun doctor(options: Options) {
    println("kodex ${ProtocolInfo.CODEX_VERSION} protocol layer")
    println("  definitions      ${ProtocolInfo.DEFINITION_COUNT}")
    println("  notifications    ${ProtocolInfo.NOTIFICATION_COUNT}")
    println("  client requests  ${ProtocolInfo.CLIENT_REQUEST_COUNT}")
    println("  server requests  ${ProtocolInfo.SERVER_REQUEST_COUNT}")
    println("  fingerprint      ${ProtocolInfo.SCHEMA_FINGERPRINT}")

    val binary = runCatching { CodexConfig(cwd = options.cwd).resolveCodexBin() }
    println()
    binary.onFailure {
        println("binary           NOT FOUND — ${it.message}")
        return
    }
    println("binary           ${binary.getOrThrow()}")

    open(options).use { codex ->
        val metadata = codex.initialize()
        println("handshake        OK")
        println("  userAgent      ${metadata.userAgent}")
        println("  codexHome      ${metadata.codexHome}")
        println("  platform       ${metadata.platformOs} (${metadata.platformFamily})")

        val account = runCatching { codex.account.read(GetAccountParams()) }
        println()
        account.fold(
            onSuccess = { println("account          present") },
            onFailure = { println("account          none or unreadable — run `kodex login`") },
        )

        // A cheap real request, so this proves the round trip and not just the handshake.
        val models = runCatching { codex.models() }
        models.fold(
            onSuccess = { println("model/list       OK (${it.data.size} models)") },
            onFailure = { println("model/list       FAILED — ${it.message}") },
        )
        println()
        println("Ready. Try:  kodex chat \"say hello\"")
    }
}

/**
 * Store an API key.
 *
 * Read from stdin when piped, else `$OPENAI_API_KEY`. Never a flag: argv is readable by other
 * processes on the machine and gets saved into shell history.
 */
private suspend fun login(options: Options) {
    val piped = if (System.`in`.available() > 0) System.`in`.bufferedReader().readText().trim() else ""
    val key = piped.ifBlank { System.getenv("OPENAI_API_KEY").orEmpty() }.trim()

    if (key.isBlank()) {
        System.err.println(
            """
            No API key found.

              export OPENAI_API_KEY=...   &&  kodex login
              printenv OPENAI_API_KEY | kodex login

            A key is never accepted as a command-line flag.
            """.trimIndent(),
        )
        exitProcess(2)
    }

    open(options).use { codex ->
        codex.loginWithApiKey(key)
        // Deliberately not echoing any part of the key.
        println("stored an API key (${key.length} chars) in the Codex home")
        println(
            "NOTE: the server stores a key without validating it, so this does not prove the " +
                "key works. Run `kodex chat \"hi\"` to find out.",
        )
    }
}

private suspend fun whoami(options: Options) {
    open(options).use { codex ->
        runCatching { codex.account.read(GetAccountParams()) }.fold(
            onSuccess = { println("account: $it") },
            onFailure = { println("no account readable — run `kodex login`") },
        )
        runCatching { codex.account.rateLimitsRead() }
            .onSuccess { println("rate limits: $it") }
    }
}

private suspend fun models(options: Options) {
    open(options).use { codex ->
        codex.models().data.forEach { model ->
            val marker = if (model.isDefault) " (default)" else ""
            val efforts = model.supportedReasoningEfforts.joinToString(",") { it.reasoningEffort }
            println("${model.id}$marker  [$efforts]")
        }
    }
}

private suspend fun chat(options: Options, writable: Boolean) {
    val prompt = options.positional.joinToString(" ").ifBlank {
        System.err.println("kodex: nothing to send. Try: kodex chat \"say hello\"")
        exitProcess(2)
    }

    open(options).use { codex ->
        val thread = codex.startThread(
            model = options.model,
            sandbox = if (writable) Sandbox.WORKSPACE_WRITE else Sandbox.READ_ONLY,
            // `exec` approves its own escalations; `chat` cannot escalate at all.
            approvalMode = if (writable) ApprovalMode.AUTO_REVIEW else ApprovalMode.DENY_ALL,
        )
        if (!options.quiet) System.err.println("[thread ${thread.id}]")

        val handle = thread.turn(prompt = prompt, effort = options.effort)
        stream(handle.stream(), options.quiet)
    }
}

private suspend fun goal(options: Options) {
    val objective = options.positional.joinToString(" ").ifBlank {
        System.err.println("kodex: no objective. Try: kodex goal \"make the tests pass\"")
        exitProcess(2)
    }

    open(options).use { codex ->
        val thread = codex.startThread(
            model = options.model,
            sandbox = Sandbox.WORKSPACE_WRITE,
            approvalMode = ApprovalMode.AUTO_REVIEW,
        )
        val handle = thread.startGoal(objective = objective, tokenBudget = options.budget)
        if (!options.quiet) System.err.println("[goal on thread ${thread.id}, budget ${options.budget}]")

        stream(handle.stream(), options.quiet)

        println()
        println("goal ${handle.status} after ${handle.turnIds.size} turn(s)")
    }
}

/** Render a turn or goal stream. Identical handling either way — a goal is just more turns. */
private suspend fun stream(
    flow: kotlinx.coroutines.flow.Flow<dev.kodex.protocol.CodexNotification>,
    quiet: Boolean,
) {
    var turns = 0
    flow.collect { event ->
        when (event) {
            is AgentMessageDeltaNotification -> print(event.delta)

            is ReasoningSummaryTextDeltaNotification ->
                if (!quiet) System.err.print(event.delta)

            is TurnStartedNotification ->
                if (!quiet && ++turns > 1) System.err.println("\n[turn $turns]")

            is ItemStartedNotification -> if (!quiet) {
                (event.item as? CommandExecutionThreadItem)?.let { System.err.println("\n$ ${it.command}") }
            }

            is ItemCompletedNotification -> if (!quiet) {
                when (val item = event.item) {
                    is CommandExecutionThreadItem -> System.err.println("  exit ${item.exitCode}")
                    is FileChangeThreadItem ->
                        item.changes.forEach { System.err.println("  ${it.kind} ${it.path}") }
                    else -> Unit
                }
            }

            is ThreadTokenUsageUpdatedNotification -> if (!quiet) {
                System.err.println("\n[${event.tokenUsage.total.totalTokens} tokens]")
            }

            is ThreadGoalUpdatedNotification ->
                if (!quiet) System.err.println("[goal ${event.goal.status}]")

            is TurnCompletedNotification -> if (!quiet) {
                System.err.println("\n[turn ${event.turn.status} in ${event.turn.durationMs}ms]")
            }

            else -> Unit
        }
    }
    println()
}

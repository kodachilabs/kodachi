package dev.kodex.samples.quickstart

import dev.kodex.Codex
import dev.kodex.CodexConfig
import dev.kodex.Sandbox
import kotlinx.coroutines.runBlocking

/**
 * Run one turn to completion and print the answer.
 *
 * ```
 * ./gradlew :samples:run
 * ```
 */
fun main(args: Array<String>) = runBlocking {
    val workdir = args.firstOrNull() ?: System.getProperty("user.dir")

    Codex.connect(CodexConfig(cwd = workdir)).use { codex ->
        println("connected: ${codex.metadata?.userAgent}")

        val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
        println("thread: ${thread.id}")

        val result = thread.run("Summarize what this project does in two sentences.")

        println("\n--- response ---")
        println(result.finalResponse)
        println("\nstatus=${result.status} items=${result.items.size}")
        result.usage?.let { println("tokens=${it.total.totalTokens} (cached ${it.total.cachedInputTokens})") }
        result.error?.let { println("error: ${it.message}") }
    }
}

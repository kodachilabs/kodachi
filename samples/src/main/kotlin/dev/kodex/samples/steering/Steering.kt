package dev.kodex.samples.steering

import dev.kodex.Codex
import dev.kodex.CodexConfig
import dev.kodex.Sandbox
import dev.kodex.protocol.AgentMessageDeltaNotification
import dev.kodex.protocol.TurnCompletedNotification
import kotlinx.coroutines.runBlocking

/**
 * Redirect a turn while it is still running.
 *
 * Steering hands new input to the model mid-flight — it is not an interrupt-and-restart,
 * so the work already done is kept. Use [dev.kodex.TurnHandle.interrupt] to stop instead.
 *
 * ```
 * ./gradlew :samples:run -Psample=dev.kodex.samples.SteeringKt
 * ```
 */
fun main(args: Array<String>): Unit = runBlocking {
    val workdir = args.firstOrNull() ?: System.getProperty("user.dir")

    Codex.connect(CodexConfig(cwd = workdir)).use { codex ->
        val thread = codex.startThread(sandbox = Sandbox.READ_ONLY)
        val handle = thread.turn("Count slowly from 1 to 20, one number per line.")

        // Steering mid-collect is safe: the turn's event channel is buffered, so nothing
        // is dropped while this coroutine waits on the steer request.
        var streamed = 0
        var steered = false

        handle.stream().collect { event ->
            when (event) {
                is AgentMessageDeltaNotification -> {
                    print(event.delta)
                    streamed += event.delta.length
                    if (streamed > 40 && !steered) {
                        steered = true
                        println("\n[steering...]")
                        val response = handle.steer("Actually stop counting and just say DONE.")
                        println("[steer accepted: turn ${response.turnId}]")
                    }
                }

                is TurnCompletedNotification -> println("\n[${event.turn.status}]")
                else -> Unit
            }
        }
    }
}

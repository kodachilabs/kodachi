package dev.kodachi.internal

import dev.kodachi.CodexConfig
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * A newline-delimited JSON message channel to the app-server.
 *
 * Extracted behind an interface so the client can be tested without spawning a process.
 * All three methods block; callers run them on an IO dispatcher.
 */
internal interface Transport : AutoCloseable {
    fun writeLine(line: String)

    /** Next inbound line, or null at end of stream. */
    fun readLine(): String?

    /** Last stderr lines from the child, for error messages. */
    fun stderrTail(): String
}

/** Drives a real `codex app-server` child process over its stdio pipes. */
internal class ProcessTransport(config: CodexConfig) : Transport {

    private val process: Process
    private val input: BufferedReader
    private val output: BufferedWriter
    private val stderr = ArrayDeque<String>()
    private val stderrLock = Any()

    init {
        // Validate cwd BEFORE resolving the binary. It is the cheaper check and the more common
        // mistake, and doing it first means the diagnosis does not depend on whether a codex
        // binary happens to be installed — otherwise a bad cwd on a machine without codex
        // reports "binary not found", which sends you looking in the wrong place.
        val workingDir = config.cwd?.let { cwd ->
            val dir = File(cwd)
            if (!dir.isDirectory) {
                throw dev.kodachi.TransportClosedException(
                    "Cannot start codex: cwd '$cwd' " +
                        (if (dir.exists()) "is not a directory" else "does not exist"),
                )
            }
            dir
        }

        val builder = ProcessBuilder(config.launchArgs())
        workingDir?.let { builder.directory(it) }
        builder.environment().putAll(config.env)
        process = try {
            builder.start()
        } catch (e: IOException) {
            throw dev.kodachi.TransportClosedException(
                "Failed to launch codex app-server: ${config.launchArgs().joinToString(" ")}",
                cause = e,
            )
        }

        // The JDK default is 8 KiB. Command output deltas and turn diffs regularly exceed
        // that, and every refill is a syscall on the hot streaming path.
        input = BufferedReader(InputStreamReader(process.inputStream, Charsets.UTF_8), IO_BUFFER_BYTES)
        output = BufferedWriter(OutputStreamWriter(process.outputStream, Charsets.UTF_8), IO_BUFFER_BYTES)

        // A full stderr pipe would deadlock the child, so drain it unconditionally.
        Thread {
            try {
                process.errorStream.bufferedReader().forEachLine { line ->
                    synchronized(stderrLock) {
                        stderr.addLast(line)
                        if (stderr.size > STDERR_TAIL_LINES) stderr.removeFirst()
                    }
                    config.onStderrLine?.invoke(line)
                }
            } catch (_: IOException) {
                // Closed during shutdown; nothing useful left to read.
            }
        }.apply {
            isDaemon = true
            name = "codex-appserver-stderr"
            start()
        }
    }

    override fun writeLine(line: String) {
        output.write(line)
        output.write("\n")
        output.flush()
    }

    override fun readLine(): String? = input.readLine()

    override fun stderrTail(): String = synchronized(stderrLock) { stderr.joinToString("\n") }

    override fun close() {
        runCatching { output.close() }
        runCatching { process.destroy() }
        if (!process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
            runCatching { process.destroyForcibly() }
        }
        runCatching { input.close() }
    }

    private companion object {
        const val STDERR_TAIL_LINES = 200
        const val IO_BUFFER_BYTES = 64 * 1024
    }
}

package dev.kodachi

import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class CodexConfigTest {

    @Test
    fun `an explicit executable path is used as given`() {
        val fake = File.createTempFile("codex-fake", "").apply {
            setExecutable(true)
            deleteOnExit()
        }

        val resolved = CodexConfig(codexBin = fake.absolutePath).resolveCodexBin()
        assertEquals(fake.absolutePath, resolved)
    }

    @Test
    fun `a non-executable path is rejected with a clear message`() {
        val notExecutable = File.createTempFile("codex-not-exec", "").apply {
            setExecutable(false)
            deleteOnExit()
        }

        val error = assertFailsWith<CodexBinaryNotFoundException> {
            CodexConfig(codexBin = notExecutable.absolutePath).resolveCodexBin()
        }
        assertContains(error.message!!, "not an executable")
    }

    @Test
    fun `a missing path is rejected rather than silently falling back to PATH`() {
        assertFailsWith<CodexBinaryNotFoundException> {
            CodexConfig(codexBin = "/nonexistent/codex").resolveCodexBin()
        }
    }

    @Test
    fun `launch args request the stdio app-server and carry config overrides`() {
        val fake = File.createTempFile("codex-fake", "").apply {
            setExecutable(true)
            deleteOnExit()
        }

        val args = CodexConfig(
            codexBin = fake.absolutePath,
            configOverrides = listOf("model_reasoning_effort=high", "sandbox_mode=read-only"),
        ).launchArgs()

        assertEquals(fake.absolutePath, args.first())
        assertEquals(listOf("app-server", "--listen", "stdio://"), args.takeLast(3))
        assertTrue(args.containsAll(listOf("--config", "model_reasoning_effort=high")))
        assertEquals(2, args.count { it == "--config" })
    }
}

/**
 * Launch failures used to surface as a bare "Failed to launch codex app-server", which reads as a
 * broken binary when the real cause is usually a bad `cwd`.
 */
class LaunchDiagnosticsTest {

    @Test
    fun `a missing cwd is named as the cause`() {
        val error = assertFailsWith<dev.kodachi.TransportClosedException> {
            Codex(CodexConfig(cwd = "/definitely/not/a/real/directory"))
        }
        assertContains(error.message!!, "/definitely/not/a/real/directory")
        assertContains(error.message!!, "does not exist")
    }

    @Test
    fun `a cwd that is a file, not a directory, says so`() {
        val file = File.createTempFile("kodachi-not-a-dir", "").apply { deleteOnExit() }
        val error = assertFailsWith<dev.kodachi.TransportClosedException> {
            Codex(CodexConfig(cwd = file.absolutePath))
        }
        assertContains(error.message!!, "is not a directory")
    }

    @Test
    fun `a failed launch reports the command it tried`() {
        val fake = File.createTempFile("kodachi-not-executable", ".sh").apply {
            writeText("#!/bin/sh\nexit 0\n")
            setExecutable(false)
            deleteOnExit()
        }
        // Not executable, so resolution itself fails before any launch is attempted.
        assertFailsWith<dev.kodachi.CodexBinaryNotFoundException> {
            CodexConfig(codexBin = fake.absolutePath).resolveCodexBin()
        }
    }
}

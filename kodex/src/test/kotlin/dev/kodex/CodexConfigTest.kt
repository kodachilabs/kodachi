package dev.kodex

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

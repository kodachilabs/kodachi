package dev.kodex

import java.io.File

/** This SDK's version, reported to the server in the handshake's client info. */
public const val SDK_VERSION: String = "0.1.0"

/**
 * How to launch and talk to the `codex` app-server.
 *
 * This SDK does not bundle a Codex binary; it drives whichever one you point it at.
 * That is deliberate — the reference Python SDK pins a bundled binary, and installs
 * pinned to an old build cannot use newer models or reasoning efforts. Resolution
 * order is [codexBin], then `$CODEX_BIN`, then `codex` on `PATH`.
 *
 * @property codexBin explicit path to the `codex` executable
 * @property cwd working directory for the child process; also the default thread cwd
 * @property env extra environment entries, merged over the inherited environment
 * @property configOverrides `--config key=value` pairs applied at launch
 * @property clientName identifies this client in the server's user agent
 * @property serverRequestHandler answers requests the server sends to this client
 * @property requestTimeoutMillis how long to wait for a request response; null waits forever
 * @property turnEventBufferSize how many unconsumed events one turn may buffer before its
 *   stream fails with [TurnStreamOverflowException] instead of growing the heap without bound
 */
public data class CodexConfig(
    val codexBin: String? = null,
    val cwd: String? = null,
    val env: Map<String, String> = emptyMap(),
    val configOverrides: List<String> = emptyList(),
    val clientName: String = "kodex",
    val clientTitle: String = "Codex Kotlin SDK",
    val clientVersion: String = SDK_VERSION,
    val experimentalApi: Boolean = false,
    val serverRequestHandler: ServerRequestHandler = ServerRequestHandler.ACCEPT_ALL,
    val requestTimeoutMillis: Long? = 120_000,
    val turnEventBufferSize: Int = 8_192,
    /** Receives the child's stderr lines. Useful for surfacing server-side failures. */
    val onStderrLine: ((String) -> Unit)? = null,
) {
    /**
     * Resolve the executable to launch.
     *
     * @throws CodexBinaryNotFoundException when no `codex` can be found
     */
    public fun resolveCodexBin(): String {
        codexBin?.let { explicit ->
            val file = File(explicit)
            if (!file.canExecute()) {
                throw CodexBinaryNotFoundException(
                    "codexBin '$explicit' is not an executable file",
                )
            }
            return file.absolutePath
        }

        System.getenv("CODEX_BIN")?.takeIf { it.isNotBlank() }?.let { fromEnv ->
            val file = File(fromEnv)
            if (!file.canExecute()) {
                throw CodexBinaryNotFoundException(
                    "CODEX_BIN='$fromEnv' is not an executable file",
                )
            }
            return file.absolutePath
        }

        findOnPath("codex")?.let { return it }

        throw CodexBinaryNotFoundException(
            "Could not find the 'codex' executable. Install the Codex CLI " +
                "(npm i -g @openai/codex), or set CodexConfig.codexBin / \$CODEX_BIN.",
        )
    }

    internal fun launchArgs(): List<String> = buildList {
        add(resolveCodexBin())
        configOverrides.forEach {
            add("--config")
            add(it)
        }
        add("app-server")
        add("--listen")
        add("stdio://")
    }

    private fun findOnPath(name: String): String? {
        val path = System.getenv("PATH") ?: return null
        return path.split(File.pathSeparator)
            .asSequence()
            .filter { it.isNotBlank() }
            .map { File(it, name) }
            .firstOrNull { it.canExecute() }
            ?.absolutePath
    }
}

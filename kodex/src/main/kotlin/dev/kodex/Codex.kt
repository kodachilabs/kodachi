package dev.kodex

import dev.kodex.internal.AppServerClient
import dev.kodex.internal.CodexJson
import dev.kodex.protocol.AccountApi
import dev.kodex.protocol.AppsApi
import dev.kodex.protocol.ClientInfo
import dev.kodex.protocol.ClientNotifications
import dev.kodex.protocol.ClientRequests
import dev.kodex.protocol.CodexNotification
import dev.kodex.protocol.CommandApi
import dev.kodex.protocol.ConfigApi
import dev.kodex.protocol.ConfigRequirementsApi
import dev.kodex.protocol.ExperimentalFeatureApi
import dev.kodex.protocol.ExternalAgentConfigApi
import dev.kodex.protocol.FeedbackApi
import dev.kodex.protocol.FsApi
import dev.kodex.protocol.FuzzyFileSearchApi
import dev.kodex.protocol.HooksApi
import dev.kodex.protocol.InitializeCapabilities
import dev.kodex.protocol.InitializeParams
import dev.kodex.protocol.InitializeResponse
import dev.kodex.protocol.MarketplaceApi
import dev.kodex.protocol.McpServerApi
import dev.kodex.protocol.McpServerStatusApi
import dev.kodex.protocol.ModelApi
import dev.kodex.protocol.ModelListParams
import dev.kodex.protocol.ModelListResponse
import dev.kodex.protocol.ModelProviderApi
import dev.kodex.protocol.PermissionProfileApi
import dev.kodex.protocol.Personality
import dev.kodex.protocol.PluginsApi
import dev.kodex.protocol.ProtocolCaller
import dev.kodex.protocol.ReviewApi
import dev.kodex.protocol.SkillsApi
import dev.kodex.protocol.ThreadForkParams
import dev.kodex.protocol.ThreadForkResponse
import dev.kodex.protocol.ThreadListParams
import dev.kodex.protocol.ThreadListResponse
import dev.kodex.protocol.ThreadResumeParams
import dev.kodex.protocol.ThreadResumeResponse
import dev.kodex.protocol.ThreadStartParams
import dev.kodex.protocol.ThreadStartResponse
import dev.kodex.protocol.ThreadsApi
import dev.kodex.protocol.TurnsApi
import dev.kodex.protocol.WindowsSandboxApi
import dev.kodex.protocol.encodeParams
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * A connection to a local Codex agent.
 *
 * Constructing a `Codex` launches `codex app-server` as a child process; the JSON-RPC
 * handshake happens on first use. Close it to terminate the child — or use
 * [kotlin.AutoCloseable.use].
 *
 * ```kotlin
 * Codex().use { codex ->
 *     val thread = codex.startThread(cwd = "/repo", sandbox = Sandbox.WORKSPACE_WRITE)
 *     println(thread.run("Explain this repository in three bullets.").finalResponse)
 * }
 * ```
 *
 * Three layers are available, from most to least ergonomic:
 *  - [startThread] / [CodexThread] / [TurnHandle] for the conversation flow;
 *  - the namespace properties ([fs], [account], [plugins], …) for every other typed
 *    request the protocol defines;
 *  - [request] and [notify] for raw access, including methods newer than this SDK.
 *
 * Instances are safe to share across coroutines: requests are multiplexed over one
 * transport and responses are routed back to their caller.
 */
public class Codex(
    internal val config: CodexConfig = CodexConfig(),
) : AutoCloseable, ProtocolCaller {

    internal val client: AppServerClient = AppServerClient(config)

    private val initMutex = Mutex()
    private var initialized: InitializeResponse? = null

    /**
     * Every notification the server sends, including turn-scoped ones.
     *
     * This is a lossy tap for observability: it drops the oldest events when a slow
     * collector falls behind. Use [TurnHandle.stream] when you need every event of a
     * turn in order.
     */
    public val events: SharedFlow<CodexNotification> get() = client.events

    /** Server metadata from the handshake, or null before the first call. */
    public val metadata: InitializeResponse? get() = initialized

    // -----------------------------------------------------------------------
    // Protocol namespaces — every typed request method the app-server exposes
    // -----------------------------------------------------------------------

    /** `account/…` — login, logout, rate limits, usage. See also [loginWithApiKey]. */
    public val account: AccountApi = AccountApi(this)

    /** `app/…` — installed app listing. */
    public val apps: AppsApi = AppsApi(this)

    /** `command/…` — run and drive commands outside a turn. */
    public val command: CommandApi = CommandApi(this)

    /** `config/…` — read and write `config.toml` values. */
    public val serverConfig: ConfigApi = ConfigApi(this)

    /** `configRequirements/…` — required configuration reported by the server. */
    public val configRequirements: ConfigRequirementsApi = ConfigRequirementsApi(this)

    /** `experimentalFeature/…` — list and toggle experimental features. */
    public val experimentalFeatures: ExperimentalFeatureApi = ExperimentalFeatureApi(this)

    /** `externalAgentConfig/…` — detect and import configs from other agents. */
    public val externalAgentConfig: ExternalAgentConfigApi = ExternalAgentConfigApi(this)

    /** `feedback/…` — upload user feedback. */
    public val feedback: FeedbackApi = FeedbackApi(this)

    /** `fs/…` — filesystem reads, writes, and watches performed by the server. */
    public val fs: FsApi = FsApi(this)

    /** `fuzzyFileSearch` — fuzzy path search across configured roots. */
    public val fuzzyFileSearch: FuzzyFileSearchApi = FuzzyFileSearchApi(this)

    /** `hooks/…` — list configured hooks. */
    public val hooks: HooksApi = HooksApi(this)

    /** `marketplace/…` — add, remove, and upgrade marketplace entries. */
    public val marketplace: MarketplaceApi = MarketplaceApi(this)

    /** `mcpServer/…` — MCP OAuth login, resource reads, tool calls. */
    public val mcpServer: McpServerApi = McpServerApi(this)

    /** `mcpServerStatus/…` — MCP server startup status. */
    public val mcpServerStatus: McpServerStatusApi = McpServerStatusApi(this)

    /** `model/…` — list available models. See also [models]. */
    public val model: ModelApi = ModelApi(this)

    /** `modelProvider/…` — provider capability discovery. */
    public val modelProvider: ModelProviderApi = ModelProviderApi(this)

    /** `permissionProfile/…` — list permission profiles. */
    public val permissionProfiles: PermissionProfileApi = PermissionProfileApi(this)

    /** `plugin/…` — install, list, share, and uninstall plugins. */
    public val plugins: PluginsApi = PluginsApi(this)

    /** `review/…` — start a code review. */
    public val review: ReviewApi = ReviewApi(this)

    /** `skills/…` — list skills and manage skill roots. */
    public val skills: SkillsApi = SkillsApi(this)

    /**
     * `thread/…` — the full low-level thread surface, including operations without an
     * ergonomic wrapper (delete, rollback, sections, metadata, goals, inject items).
     */
    public val threads: ThreadsApi = ThreadsApi(this)

    /** `turn/…` — low-level turn control; prefer [TurnHandle]. */
    public val turns: TurnsApi = TurnsApi(this)

    /** `windowsSandbox/…` — Windows sandbox readiness and setup. */
    public val windowsSandbox: WindowsSandboxApi = WindowsSandboxApi(this)

    // -----------------------------------------------------------------------
    // Handshake
    // -----------------------------------------------------------------------

    /**
     * Perform the handshake. Idempotent — every other method calls it first, so you only
     * need this to read [InitializeResponse] up front.
     */
    public suspend fun initialize(): InitializeResponse {
        initialized?.let { return it }
        return initMutex.withLock {
            initialized?.let { return it }

            val params = InitializeParams(
                clientInfo = ClientInfo(
                    name = config.clientName,
                    title = config.clientTitle,
                    version = config.clientVersion,
                ),
                capabilities = InitializeCapabilities(experimentalApi = config.experimentalApi),
            )
            val result = client.request(
                ClientRequests.INITIALIZE,
                encodeParams(InitializeParams.serializer(), params),
            )
            // The server rejects thread calls until it sees this acknowledgement.
            client.notify(ClientNotifications.INITIALIZED)

            decode(InitializeResponse.serializer(), result).also { initialized = it }
        }
    }

    // -----------------------------------------------------------------------
    // Threads
    // -----------------------------------------------------------------------

    /**
     * Create a new conversation.
     *
     * @param cwd working directory the agent operates in; defaults to the child's cwd
     * @param sandbox filesystem access for the thread
     * @param approvalMode where escalated permission requests are routed
     * @param developerInstructions session-level instructions; these stick for the
     *   thread's life and are *not* updated by [resumeThread], so per-turn guidance
     *   belongs in the turn input
     * @param overrides any [ThreadStartParams] field this signature does not name
     */
    public suspend fun startThread(
        cwd: String? = null,
        model: String? = null,
        modelProvider: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode = ApprovalMode.AUTO_REVIEW,
        baseInstructions: String? = null,
        developerInstructions: String? = null,
        ephemeral: Boolean? = null,
        personality: Personality? = null,
        serviceTier: String? = null,
        threadConfig: JsonObject? = null,
        overrides: (ThreadStartParams) -> ThreadStartParams = { it },
    ): CodexThread {
        initialize()
        val params = overrides(
            ThreadStartParams(
                cwd = cwd,
                model = model,
                modelProvider = modelProvider,
                sandbox = sandbox?.mode,
                approvalPolicy = approvalMode.askForApproval,
                approvalsReviewer = approvalMode.reviewer,
                baseInstructions = baseInstructions,
                developerInstructions = developerInstructions,
                ephemeral = ephemeral,
                personality = personality,
                serviceTier = serviceTier,
                config = threadConfig,
            ),
        )
        val response = threads.start(params)
        return CodexThread(this, response.thread.id, response)
    }

    /** Reopen a stored conversation by id, optionally overriding its settings. */
    public suspend fun resumeThread(
        threadId: String,
        cwd: String? = null,
        model: String? = null,
        modelProvider: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode? = null,
        developerInstructions: String? = null,
        threadConfig: JsonObject? = null,
        overrides: (ThreadResumeParams) -> ThreadResumeParams = { it },
    ): CodexThread {
        initialize()
        val params = overrides(
            ThreadResumeParams(
                threadId = threadId,
                cwd = cwd,
                model = model,
                modelProvider = modelProvider,
                sandbox = sandbox?.mode,
                approvalPolicy = approvalMode?.askForApproval,
                approvalsReviewer = approvalMode?.reviewer,
                developerInstructions = developerInstructions,
                config = threadConfig,
            ),
        )
        val response: ThreadResumeResponse = threads.resume(params)
        return CodexThread(this, response.thread.id)
    }

    /** Branch a new thread from an existing one's state, leaving the original untouched. */
    public suspend fun forkThread(
        threadId: String,
        cwd: String? = null,
        model: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode? = null,
        overrides: (ThreadForkParams) -> ThreadForkParams = { it },
    ): CodexThread {
        initialize()
        val params = overrides(
            ThreadForkParams(
                threadId = threadId,
                cwd = cwd,
                model = model,
                sandbox = sandbox?.mode,
                approvalPolicy = approvalMode?.askForApproval,
                approvalsReviewer = approvalMode?.reviewer,
            ),
        )
        val response: ThreadForkResponse = threads.fork(params)
        return CodexThread(this, response.thread.id)
    }

    /** Attach to a known thread id without a round trip. */
    public fun thread(threadId: String): CodexThread = CodexThread(this, threadId)

    /** List stored threads. */
    public suspend fun listThreads(
        limit: Int? = null,
        cursor: String? = null,
        archived: Boolean? = null,
        searchTerm: String? = null,
    ): ThreadListResponse {
        initialize()
        return threads.list(
            ThreadListParams(
                limit = limit,
                cursor = cursor,
                archived = archived,
                searchTerm = searchTerm,
            ),
        )
    }

    /** Models this installation can use. */
    public suspend fun models(includeHidden: Boolean? = null): ModelListResponse {
        initialize()
        return model.list(ModelListParams(includeHidden = includeHidden))
    }

    // -----------------------------------------------------------------------
    // Raw access
    // -----------------------------------------------------------------------

    /**
     * Send any request and get the raw result.
     *
     * The escape hatch for methods newer than this SDK. Prefer the typed namespaces;
     * [dev.kodex.protocol.ClientRequests] holds every method name this protocol
     * version defines.
     */
    public suspend fun request(method: String, params: JsonObject? = null): JsonElement {
        initialize()
        return client.request(method, params)
    }

    /**
     * Send a request, retrying transient server-overload failures.
     *
     * Opt-in and per-call, mirroring the reference Python SDK's
     * `request_with_retry_on_overload`. Retrying is not safe in general — a retried
     * `turn/start` could begin a second turn — so this is never applied automatically.
     * Use it for idempotent reads.
     */
    public suspend fun requestWithRetry(
        method: String,
        params: JsonObject? = null,
        policy: RetryPolicy = RetryPolicy(),
    ): JsonElement = withRetry(policy) { request(method, params) }

    /** Send a fire-and-forget notification. */
    public suspend fun notify(method: String, params: JsonObject? = null) {
        initialize()
        client.notify(method, params)
    }

    /** Typed request plumbing for the generated namespaces. */
    override suspend fun <R> call(
        method: String,
        params: JsonElement?,
        resultSerializer: DeserializationStrategy<R>,
    ): R {
        if (method != ClientRequests.INITIALIZE) initialize()
        val result = client.request(method, params as? JsonObject)
        return decode(resultSerializer, result)
    }

    /** Terminate the app-server process and fail anything still in flight. */
    override fun close() {
        client.close()
    }

    internal fun <T> decode(serializer: DeserializationStrategy<T>, element: JsonElement): T =
        CodexJson.decodeFromJsonElement(serializer, element)

    public companion object {
        /** Connect and complete the handshake eagerly, so setup failures surface here. */
        public suspend fun connect(config: CodexConfig = CodexConfig()): Codex =
            Codex(config).also { codex ->
                runCatching { codex.initialize() }.onFailure {
                    codex.close()
                    throw it
                }
            }
    }
}

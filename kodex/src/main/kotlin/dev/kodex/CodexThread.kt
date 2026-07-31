package dev.kodex

import dev.kodex.protocol.ThreadArchiveParams
import dev.kodex.protocol.ThreadArchiveResponse
import dev.kodex.protocol.ThreadCompactStartParams
import dev.kodex.protocol.ThreadCompactStartResponse
import dev.kodex.protocol.ThreadDeleteParams
import dev.kodex.protocol.ThreadDeleteResponse
import dev.kodex.protocol.ThreadGoalClearParams
import dev.kodex.protocol.ThreadGoalGetParams
import dev.kodex.protocol.ThreadGoalGetResponse
import dev.kodex.protocol.ThreadGoalSetParams
import dev.kodex.protocol.ThreadGoalSetResponse
import dev.kodex.protocol.ThreadGoalStatus
import dev.kodex.protocol.ThreadReadParams
import dev.kodex.protocol.ThreadReadResponse
import dev.kodex.protocol.ThreadRollbackParams
import dev.kodex.protocol.ThreadRollbackResponse
import dev.kodex.protocol.ThreadSetNameParams
import dev.kodex.protocol.ThreadSetNameResponse
import dev.kodex.protocol.ThreadShellCommandParams
import dev.kodex.protocol.ThreadShellCommandResponse
import dev.kodex.protocol.ThreadStartResponse
import dev.kodex.protocol.ThreadUnarchiveParams
import dev.kodex.protocol.ThreadUnarchiveResponse
import dev.kodex.protocol.TurnStartParams
import dev.kodex.protocol.UserInput
import kotlinx.serialization.json.JsonElement

/**
 * A conversation. Turns run one at a time within a thread; start the next one only
 * after the previous has completed (or been interrupted).
 *
 * Operations without a wrapper here are on [Codex.threads], which covers the whole
 * `thread/…` namespace.
 */
public class CodexThread internal constructor(
    private val codex: Codex,
    /** Server-assigned thread id. Pass it to [Codex.resumeThread] to reopen later. */
    public val id: String,
    /** The `thread/start` response, when this thread was created by this client. */
    public val startInfo: ThreadStartResponse? = null,
) {

    /**
     * Start a turn and return immediately with a handle to its event stream.
     *
     * Use this when you want to watch the turn as it happens, steer it mid-flight, or
     * interrupt it. For a one-shot call, [run] is simpler.
     *
     * Every override here applies to this turn *and subsequent ones* — that is the
     * server's behavior, not a quirk of this SDK.
     *
     * @param overrides reach any [TurnStartParams] field this signature does not name
     */
    public suspend fun turn(
        input: List<UserInput>,
        model: String? = null,
        effort: String? = null,
        cwd: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode? = null,
        outputSchema: JsonElement? = null,
        overrides: (TurnStartParams) -> TurnStartParams = { it },
    ): TurnHandle {
        require(input.isNotEmpty()) { "turn input must not be empty" }

        val params = overrides(
            TurnStartParams(
                threadId = id,
                input = input,
                model = model,
                effort = effort,
                cwd = cwd,
                sandboxPolicy = sandbox?.policy,
                approvalPolicy = approvalMode?.askForApproval,
                approvalsReviewer = approvalMode?.reviewer,
                outputSchema = outputSchema,
            ),
        )

        val response = codex.turns.start(params)
        // Claim the stream before returning: events may already be queued for this turn.
        val channel = codex.client.registerTurn(response.turn.id)
        return TurnHandle(codex, threadId = id, turn = response.turn, channel = channel)
    }

    /** Convenience overload for a single text prompt. */
    public suspend fun turn(
        prompt: String,
        model: String? = null,
        effort: String? = null,
        cwd: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode? = null,
        outputSchema: JsonElement? = null,
        overrides: (TurnStartParams) -> TurnStartParams = { it },
    ): TurnHandle = turn(
        input = promptInput(prompt),
        model = model,
        effort = effort,
        cwd = cwd,
        sandbox = sandbox,
        approvalMode = approvalMode,
        outputSchema = outputSchema,
        overrides = overrides,
    )

    /**
     * Run a turn to completion and return everything it produced.
     *
     * Suspends until the turn ends. Throws [TransportClosedException] if the server dies
     * mid-turn; a turn that fails on the server's side comes back as a [TurnResult] with
     * a non-null [TurnResult.error] instead.
     */
    public suspend fun run(
        input: List<UserInput>,
        model: String? = null,
        effort: String? = null,
        cwd: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode? = null,
        outputSchema: JsonElement? = null,
        overrides: (TurnStartParams) -> TurnStartParams = { it },
    ): TurnResult = turn(
        input = input,
        model = model,
        effort = effort,
        cwd = cwd,
        sandbox = sandbox,
        approvalMode = approvalMode,
        outputSchema = outputSchema,
        overrides = overrides,
    ).collect()

    /** Convenience overload for a single text prompt. */
    public suspend fun run(
        prompt: String,
        model: String? = null,
        effort: String? = null,
        cwd: String? = null,
        sandbox: Sandbox? = null,
        approvalMode: ApprovalMode? = null,
        outputSchema: JsonElement? = null,
        overrides: (TurnStartParams) -> TurnStartParams = { it },
    ): TurnResult = run(
        input = promptInput(prompt),
        model = model,
        effort = effort,
        cwd = cwd,
        sandbox = sandbox,
        approvalMode = approvalMode,
        outputSchema = outputSchema,
        overrides = overrides,
    )

    /** Read the stored thread, optionally with its full turn history. */
    public suspend fun read(includeTurns: Boolean = false): ThreadReadResponse =
        codex.threads.read(ThreadReadParams(threadId = id, includeTurns = includeTurns))

    /** Set the thread's display name. */
    public suspend fun setName(name: String): ThreadSetNameResponse =
        codex.threads.nameSet(ThreadSetNameParams(threadId = id, name = name))

    /** Compact the thread's context, freeing window space at the cost of detail. */
    public suspend fun compact(): ThreadCompactStartResponse =
        codex.threads.compactStart(ThreadCompactStartParams(threadId = id))

    /** Archive the thread so it stops appearing in default listings. */
    public suspend fun archive(): ThreadArchiveResponse =
        codex.threads.archive(ThreadArchiveParams(threadId = id))

    /** Undo [archive]. */
    public suspend fun unarchive(): ThreadUnarchiveResponse =
        codex.threads.unarchive(ThreadUnarchiveParams(threadId = id))

    /** Delete the thread permanently. */
    public suspend fun delete(): ThreadDeleteResponse =
        codex.threads.delete(ThreadDeleteParams(threadId = id))

    /** Drop the last [numTurns] turns from the thread's history. */
    public suspend fun rollback(numTurns: Int): ThreadRollbackResponse =
        codex.threads.rollback(ThreadRollbackParams(threadId = id, numTurns = numTurns))

    /** Run a shell command in the thread's workspace, outside a model turn. */
    public suspend fun shellCommand(command: String): ThreadShellCommandResponse =
        codex.threads.shellCommand(ThreadShellCommandParams(threadId = id, command = command))

    /**
     * Start a goal and get one stream covering every turn the server runs to pursue it.
     *
     * This is the aggregated form of [setGoal]. The server drives turns on its own until the goal
     * reaches a terminal status; [GoalHandle.stream] spans all of them, so the caller never has to
     * work out which turn an event belonged to.
     *
     * Only one goal stream per thread at a time. While it is open the SDK routes this thread's
     * turn events to the goal rather than to individual turn streams, so do not also call [turn]
     * on this thread — start a separate thread for unrelated work.
     *
     * @param objective what the goal is, in plain language
     * @param tokenBudget stop the goal once it has spent this many tokens
     */
    public suspend fun startGoal(
        objective: String,
        tokenBudget: Long? = null,
    ): GoalHandle {
        // Claim the route BEFORE setting the goal: the server can start the first turn as soon
        // as it accepts the goal, and an event that arrives before the route exists is gone.
        val route = codex.client.registerGoal(id)
        val response = try {
            codex.threads.goalSet(
                ThreadGoalSetParams(
                    threadId = id,
                    objective = objective,
                    status = ThreadGoalStatus.ACTIVE,
                    tokenBudget = tokenBudget,
                ),
            )
        } catch (t: Throwable) {
            codex.client.unregisterGoal(route)
            throw t
        }

        // Only now accept physical turns: replacing a previous goal emits a `cleared` for the old
        // one first, and its trailing turn must not be attributed to this goal.
        route.activateTurnRouting()
        return GoalHandle(codex, route, response.goal)
    }

    /**
     * Set a standing objective for the thread.
     *
     * A goal makes the server drive multiple turns toward it; progress arrives as
     * `thread/goal/updated` notifications on [Codex.events].
     */
    public suspend fun setGoal(
        objective: String? = null,
        status: ThreadGoalStatus? = null,
        tokenBudget: Long? = null,
    ): ThreadGoalSetResponse = codex.threads.goalSet(
        ThreadGoalSetParams(
            threadId = id,
            objective = objective,
            status = status,
            tokenBudget = tokenBudget,
        ),
    )

    /** Read the thread's current goal. */
    public suspend fun getGoal(): ThreadGoalGetResponse =
        codex.threads.goalGet(ThreadGoalGetParams(threadId = id))

    /** Clear the thread's goal. */
    public suspend fun clearGoal(): Unit {
        codex.threads.goalClear(ThreadGoalClearParams(threadId = id))
    }

    override fun toString(): String = "CodexThread(id=$id)"
}

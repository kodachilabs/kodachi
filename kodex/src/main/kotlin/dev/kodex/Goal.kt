package dev.kodex

import dev.kodex.internal.GoalRoute
import dev.kodex.protocol.AgentMessageThreadItem
import dev.kodex.protocol.CodexNotification
import dev.kodex.protocol.ErrorNotification
import dev.kodex.protocol.ItemCompletedNotification
import dev.kodex.protocol.ThreadGoal
import dev.kodex.protocol.ThreadGoalClearParams
import dev.kodex.protocol.ThreadGoalGetParams
import dev.kodex.protocol.ThreadGoalStatus
import dev.kodex.protocol.ThreadItem
import dev.kodex.protocol.ThreadTokenUsage
import dev.kodex.protocol.ThreadTokenUsageUpdatedNotification
import dev.kodex.protocol.TurnError
import dev.kodex.protocol.TurnInterruptParams
import dev.kodex.protocol.TurnInterruptResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A running goal: one stream covering every turn the server starts to pursue it.
 *
 * A turn is one exchange; a goal is a project. The server drives as many turns as it needs, so
 * [stream] spans all of them and ends only when the goal is genuinely over — no turn in flight
 * and the goal either cleared or in a terminal status.
 *
 * Obtained from [CodexThread.startGoal]. Interrupting has two meanings here and they are separate
 * calls: [interruptCurrentTurn] stops the turn that is running and lets the goal continue with a
 * new one, while [abandon] ends the goal itself.
 */
public class GoalHandle internal constructor(
    private val codex: Codex,
    private val route: GoalRoute,
    /** The goal as the server accepted it. */
    public val goal: ThreadGoal,
) {

    /** Thread this goal is pursuing. */
    public val threadId: String get() = route.threadId

    /** Latest status the server reported, or null before the first update arrives. */
    public val status: ThreadGoalStatus? get() = route.status()

    /** Turn ids seen so far, in order. Grows as the goal starts more turns. */
    public val turnIds: List<String> get() = route.turnIds()

    /**
     * Every event from every turn of this goal, in order, ending when the goal is over.
     *
     * Collect at most once; the underlying channel is consumed, not replayed. Turn boundaries
     * stay visible — each turn still emits its own `turn/started` and `turn/completed` — so a
     * caller that cares can group by [CodexNotification.turnId].
     *
     * Throws [TransportClosedException] if the app-server dies while the goal is open, and
     * [TurnStreamOverflowException] if collection falls far enough behind to fill the buffer.
     */
    public fun stream(): Flow<CodexNotification> = flow {
        try {
            for (event in route.channel) {
                emit(event)
            }
        } finally {
            codex.client.unregisterGoal(route)
        }
    }

    /**
     * Stop the turn currently running, leaving the goal in place.
     *
     * The server is free to start another turn afterwards, so the goal stream continues. Returns
     * null when no turn is in flight — between turns, or after the goal finished.
     */
    public suspend fun interruptCurrentTurn(): TurnInterruptResponse? {
        val turnId = route.currentTurnId() ?: return null
        return codex.turns.interrupt(TurnInterruptParams(threadId = threadId, turnId = turnId))
    }

    /**
     * End the goal.
     *
     * Clears it server-side and interrupts the turn in flight, if any. The stream closes once
     * that turn reports completion — a cleared goal still delivers its final events rather than
     * cutting them off mid-flight.
     */
    public suspend fun abandon() {
        codex.threads.goalClear(ThreadGoalClearParams(threadId = threadId))
        interruptCurrentTurn()
    }

    /** Re-read the goal from the server, including token and time spend. */
    public suspend fun refresh(): ThreadGoal? =
        codex.threads.goalGet(ThreadGoalGetParams(threadId = threadId)).goal

    /**
     * Consume the whole goal and summarize it.
     *
     * Suspends until the goal is over. Consumes [stream], so do one or the other.
     */
    public suspend fun collect(): GoalResult {
        val items = mutableListOf<ThreadItem>()
        val messages = mutableListOf<String>()
        var usage: ThreadTokenUsage? = null
        var error: TurnError? = null

        stream().collect { event ->
            when (event) {
                is ItemCompletedNotification -> {
                    items += event.item
                    (event.item as? AgentMessageThreadItem)?.let { messages += it.text }
                }

                is ThreadTokenUsageUpdatedNotification -> usage = event.tokenUsage

                is ErrorNotification -> if (!event.willRetry) error = event.error

                else -> Unit
            }
        }

        return GoalResult(
            threadId = threadId,
            objective = goal.objective,
            status = route.status(),
            wasAbandoned = route.wasCleared(),
            turnIds = route.turnIds(),
            finalResponse = messages.lastOrNull().orEmpty(),
            messages = messages.toList(),
            items = items.toList(),
            usage = usage,
            error = error,
        )
    }

    override fun toString(): String = "GoalHandle(threadId=$threadId, status=$status)"
}

/**
 * Everything a finished goal produced, across all of its turns.
 *
 * @property status the goal's terminal status; [ThreadGoalStatus.COMPLETE] is the clean outcome,
 *   while `BLOCKED`, `PAUSED`, `USAGE_LIMITED` and `BUDGET_LIMITED` all mean it stopped short
 * @property wasAbandoned true when the goal was cleared rather than reaching a status of its own
 * @property turnIds every turn the server ran, in order
 * @property finalResponse the last assistant message across the whole goal
 */
public data class GoalResult(
    val threadId: String,
    val objective: String,
    val status: ThreadGoalStatus?,
    val wasAbandoned: Boolean,
    val turnIds: List<String>,
    val finalResponse: String,
    val messages: List<String>,
    val items: List<ThreadItem>,
    val usage: ThreadTokenUsage?,
    val error: TurnError?,
) {
    /** True when the goal ran to completion rather than stopping short or being abandoned. */
    public val isSuccess: Boolean
        get() = status == ThreadGoalStatus.COMPLETE && !wasAbandoned && error == null

    /** How many turns the server needed. */
    public val turnCount: Int get() = turnIds.size
}

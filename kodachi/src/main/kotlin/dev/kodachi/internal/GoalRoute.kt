package dev.kodachi.internal

import dev.kodachi.protocol.CodexNotification
import dev.kodachi.protocol.ThreadGoalClearedNotification
import dev.kodachi.protocol.ThreadGoalStatus
import dev.kodachi.protocol.ThreadGoalUpdatedNotification
import dev.kodachi.protocol.Turn
import dev.kodachi.protocol.TurnCompletedNotification
import dev.kodachi.protocol.TurnStartedNotification
import kotlinx.coroutines.channels.Channel

/**
 * Aggregates one goal's many physical turns into a single logical stream.
 *
 * A goal is a standing objective: the server keeps starting turns on its own until the goal
 * reaches a terminal state or is cleared. Individually those turns look like any other, so
 * without this the caller would receive an unlabelled interleaving and have to work out where
 * one turn ended and the next began.
 *
 * The state machine mirrors the reference Python SDK's `_GoalOperationState`, including the
 * finish condition, which is subtler than "the goal says complete": a goal is done only once
 * no turn is in flight, at least one turn has finished, AND the goal is either cleared or in a
 * terminal status. Checking status alone would cut the stream off while a final turn was still
 * streaming its output.
 */
internal class GoalRoute(
    val threadId: String,
    bufferSize: Int,
) {

    val channel: Channel<CodexNotification> = Channel(bufferSize)

    private val lock = Any()

    /**
     * Until this is on, only goal-level events are accepted.
     *
     * Setting a goal on a thread that already had one produces `thread/goal/cleared` for the old
     * goal first. Accepting physical turns before that would attribute the previous goal's
     * trailing turn to this one.
     */
    private var turnRoutingActive = false

    private var logicalTurnId: String? = null
    private var currentTurnId: String? = null
    private var startedTurn: Turn? = null
    private var completedTurn: Turn? = null
    private var status: ThreadGoalStatus? = null
    private var cleared = false
    private var finished = false

    /** Turn ids seen for this goal, in order, so a caller can attribute events afterwards. */
    private val turnIds = LinkedHashSet<String>()

    fun activateTurnRouting() {
        synchronized(lock) { turnRoutingActive = true }
    }

    /** Latest goal status the server reported, or null before the first update. */
    fun status(): ThreadGoalStatus? = synchronized(lock) { status }

    /** Whether the goal was explicitly cleared rather than reaching a terminal status. */
    fun wasCleared(): Boolean = synchronized(lock) { cleared }

    /** The turn currently running, if any — the one [dev.kodachi.GoalHandle] interrupts. */
    fun currentTurnId(): String? = synchronized(lock) { currentTurnId }

    fun turnIds(): List<String> = synchronized(lock) { turnIds.toList() }

    fun isFinished(): Boolean = synchronized(lock) { finished }

    /**
     * Route one notification into this goal.
     *
     * @return true when the goal consumed it, meaning it must not also go to a turn stream.
     */
    fun observe(notification: CodexNotification): Boolean {
        val justFinished: Boolean
        synchronized(lock) {
            val goalLevel = notification is ThreadGoalClearedNotification ||
                notification is ThreadGoalUpdatedNotification
            if (!turnRoutingActive && !goalLevel) return false

            when (notification) {
                is TurnStartedNotification -> {
                    val id = notification.turn.id
                    if (logicalTurnId == null) logicalTurnId = id
                    currentTurnId = id
                    if (startedTurn == null) startedTurn = notification.turn
                    turnIds += id
                }

                is TurnCompletedNotification -> {
                    completedTurn = notification.turn
                    turnIds += notification.turn.id
                    if (currentTurnId == notification.turn.id) currentTurnId = null
                }

                is ThreadGoalUpdatedNotification -> {
                    status = notification.goal.status
                    // A goal moving back to active supersedes an earlier clear.
                    if (status == ThreadGoalStatus.ACTIVE) cleared = false
                }

                is ThreadGoalClearedNotification -> cleared = true

                else -> Unit
            }

            // Nothing running, something finished, and the goal is over one way or the other.
            if (!finished &&
                currentTurnId == null &&
                completedTurn != null &&
                (cleared || status.isTerminal())
            ) {
                finished = true
            }
            justFinished = finished
        }

        channel.trySend(notification)
        if (justFinished) channel.close()
        return true
    }

    /** Whether this notification belongs to a goal at all. */
    fun claims(notification: CodexNotification): Boolean =
        notification.turnId != null || notification.method.startsWith("thread/goal/")

    fun fail(cause: Throwable) {
        channel.close(cause)
    }

    fun close() {
        channel.close()
    }
}

/**
 * Every status except [ThreadGoalStatus.ACTIVE] ends the goal — it either finished, ran out of
 * budget or tokens, got blocked, or was paused. `UNKNOWN` counts as terminal too: a status this
 * SDK does not recognise is safer treated as "stop waiting" than as "keep streaming forever".
 */
internal fun ThreadGoalStatus?.isTerminal(): Boolean = when (this) {
    null, ThreadGoalStatus.ACTIVE -> false
    else -> true
}

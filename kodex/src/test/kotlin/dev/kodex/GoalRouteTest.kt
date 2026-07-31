package dev.kodex

import dev.kodex.internal.CodexJson
import dev.kodex.internal.GoalRoute
import dev.kodex.internal.NotificationCodec
import dev.kodex.protocol.CodexNotification
import dev.kodex.protocol.ThreadGoalStatus
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The goal state machine, exercised directly.
 *
 * The finish condition is the whole point and it is easy to get wrong: a goal is NOT over just
 * because its status says so — the final turn is usually still streaming when
 * `thread/goal/updated` reports `complete`. Cutting the stream there would truncate the answer.
 */
class GoalRouteTest {

    private fun route() = GoalRoute("t1", bufferSize = 256).also { it.activateTurnRouting() }

    private fun notification(json: String): CodexNotification {
        val message = CodexJson.parseToJsonElement(json).jsonObject
        val method = (message["method"] as kotlinx.serialization.json.JsonPrimitive).content
        return NotificationCodec.decode(method, message["params"]!!.jsonObject)
    }

    private fun turnStarted(turnId: String) = notification(
        """{"method":"turn/started","params":{"threadId":"t1","turn":{"id":"$turnId","items":[],"status":"inProgress"}}}""",
    )

    private fun turnCompleted(turnId: String) = notification(
        """{"method":"turn/completed","params":{"threadId":"t1","turn":{"id":"$turnId","items":[],"status":"completed"}}}""",
    )

    private fun delta(turnId: String, text: String) = notification(
        """{"method":"item/agentMessage/delta","params":{"threadId":"t1","turnId":"$turnId","itemId":"i1","delta":"$text"}}""",
    )

    private fun goalUpdated(status: String) = notification(
        """{"method":"thread/goal/updated","params":{"threadId":"t1","goal":{"createdAt":1,"objective":"make tests pass","status":"$status","threadId":"t1","timeUsedSeconds":1,"tokensUsed":10,"updatedAt":2}}}""",
    )

    private fun goalCleared() = notification(
        """{"method":"thread/goal/cleared","params":{"threadId":"t1"}}""",
    )

    @Test
    fun `a goal spanning several turns stays open until the last one completes`() {
        val route = route()

        route.observe(goalUpdated("active"))
        route.observe(turnStarted("turn-1"))
        route.observe(delta("turn-1", "working"))
        route.observe(turnCompleted("turn-1"))
        assertFalse(route.isFinished(), "still active, so more turns may follow")

        route.observe(turnStarted("turn-2"))
        route.observe(turnCompleted("turn-2"))
        assertFalse(route.isFinished())

        route.observe(turnStarted("turn-3"))
        route.observe(goalUpdated("complete"))
        // The status says complete but turn-3 is still streaming; ending here would truncate it.
        assertFalse(route.isFinished(), "a running turn must keep the goal open")

        route.observe(turnCompleted("turn-3"))
        assertTrue(route.isFinished(), "no turn in flight and a terminal status ends the goal")
        assertEquals(listOf("turn-1", "turn-2", "turn-3"), route.turnIds())
    }

    @Test
    fun `every non-active status ends the goal`() {
        for (status in listOf("complete", "paused", "blocked", "usageLimited", "budgetLimited")) {
            val route = route()
            route.observe(turnStarted("turn-1"))
            route.observe(turnCompleted("turn-1"))
            route.observe(goalUpdated(status))
            assertTrue(route.isFinished(), "status '$status' should end the goal")
        }
    }

    @Test
    fun `an active status keeps the goal open even between turns`() {
        val route = route()
        route.observe(turnStarted("turn-1"))
        route.observe(turnCompleted("turn-1"))
        route.observe(goalUpdated("active"))
        assertFalse(route.isFinished())
        assertEquals(ThreadGoalStatus.ACTIVE, route.status())
    }

    @Test
    fun `clearing a goal ends it once the running turn finishes`() {
        val route = route()
        route.observe(goalUpdated("active"))
        route.observe(turnStarted("turn-1"))
        route.observe(goalCleared())

        // Abandoning does not cut the current turn off mid-flight.
        assertFalse(route.isFinished(), "the in-flight turn should still deliver its events")

        route.observe(turnCompleted("turn-1"))
        assertTrue(route.isFinished())
        assertTrue(route.wasCleared())
    }

    @Test
    fun `a goal going back to active supersedes an earlier clear`() {
        val route = route()
        route.observe(turnStarted("turn-1"))
        route.observe(goalCleared())
        route.observe(goalUpdated("active"))
        route.observe(turnCompleted("turn-1"))

        assertFalse(route.isFinished(), "re-activation cancels the clear")
        assertFalse(route.wasCleared())
    }

    @Test
    fun `the current turn is tracked so it can be interrupted`() {
        val route = route()
        assertNull(route.currentTurnId(), "nothing running before the first turn")

        route.observe(turnStarted("turn-1"))
        assertEquals("turn-1", route.currentTurnId())

        route.observe(turnCompleted("turn-1"))
        assertNull(route.currentTurnId(), "nothing running between turns")

        route.observe(turnStarted("turn-2"))
        assertEquals("turn-2", route.currentTurnId())
    }

    @Test
    fun `physical turns are refused until turn routing is activated`() {
        // Replacing a stored goal emits `cleared` for the OLD goal first. Accepting turns before
        // that point would attribute the previous goal's trailing turn to this one.
        val route = GoalRoute("t1", bufferSize = 64) // deliberately not activated

        assertFalse(route.observe(turnStarted("stale-turn")), "turn events must not be claimed yet")
        assertTrue(route.observe(goalCleared()), "goal-level events are always claimed")

        route.activateTurnRouting()
        assertTrue(route.observe(turnStarted("turn-1")))
    }

    @Test
    fun `only goal-related events are claimed from the thread`() {
        val route = route()
        // Turn-scoped or thread/goal/… events belong to the goal.
        assertTrue(route.claims(delta("turn-1", "x")))
        assertTrue(route.claims(goalUpdated("active")))
        assertTrue(route.claims(goalCleared()))
        // A thread-level event that is neither must reach the normal event tap instead.
        val statusChanged = notification(
            """{"method":"thread/status/changed","params":{"threadId":"t1","status":{"type":"idle"}}}""",
        )
        assertFalse(route.claims(statusChanged))
    }

    @Test
    fun `the aggregated stream carries every turn's events in order`() {
        val route = route()
        route.observe(turnStarted("turn-1"))
        route.observe(delta("turn-1", "a"))
        route.observe(turnCompleted("turn-1"))
        route.observe(turnStarted("turn-2"))
        route.observe(delta("turn-2", "b"))
        route.observe(goalUpdated("complete"))
        route.observe(turnCompleted("turn-2"))

        val seen = mutableListOf<CodexNotification>()
        while (true) {
            val next = route.channel.tryReceive().getOrNull() ?: break
            seen += next
        }

        assertEquals(
            listOf(
                "turn/started", "item/agentMessage/delta", "turn/completed",
                "turn/started", "item/agentMessage/delta", "thread/goal/updated", "turn/completed",
            ),
            seen.map { it.method },
        )
        // Turn boundaries survive aggregation, so a caller can still group by turn.
        assertEquals(setOf("turn-1", "turn-2"), seen.mapNotNull { it.turnId }.toSet())
    }
}

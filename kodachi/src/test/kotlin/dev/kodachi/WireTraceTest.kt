package dev.kodachi

import dev.kodachi.internal.NotificationCodec
import dev.kodachi.protocol.ALL_SERVER_NOTIFICATION_METHODS
import dev.kodachi.protocol.AgentMessageDeltaNotification
import dev.kodachi.protocol.AgentMessageThreadItem
import dev.kodachi.protocol.CommandExecutionRequestApprovalParams
import dev.kodachi.protocol.CommandExecutionStatus
import dev.kodachi.protocol.CommandExecutionThreadItem
import dev.kodachi.protocol.ItemCompletedNotification
import dev.kodachi.protocol.TextUserInput
import dev.kodachi.protocol.ThreadStartedNotification
import dev.kodachi.protocol.ThreadStatusChangedNotification
import dev.kodachi.protocol.ThreadTokenUsageUpdatedNotification
import dev.kodachi.protocol.TurnCompletedNotification
import dev.kodachi.protocol.TurnStartedNotification
import dev.kodachi.protocol.TurnStatus
import dev.kodachi.protocol.UnknownNotification
import dev.kodachi.protocol.UserMessageThreadItem
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Decodes messages captured from a real `codex app-server` session
 * (`src/test/resources/wire-trace.jsonl`), so these assertions describe the actual
 * protocol rather than a reading of the schema.
 */
class WireTraceTest {

    private val json = Json { ignoreUnknownKeys = true }

    private val trace: List<JsonObject> by lazy {
        val text = requireNotNull(
            javaClass.classLoader.getResourceAsStream("wire-trace.jsonl"),
        ) { "wire-trace.jsonl fixture missing" }.bufferedReader().readText()

        text.lineSequence()
            .filter { it.isNotBlank() }
            .map { json.parseToJsonElement(it).jsonObject }
            .toList()
    }

    private fun notificationsFor(method: String): List<JsonObject> = trace
        .filter { it["method"]?.jsonPrimitive?.contentOrNull == method && it["id"] == null }
        .map { it["params"]?.jsonObject ?: JsonObject(emptyMap()) }

    private fun decodeFirst(method: String) =
        NotificationCodec.decode(method, notificationsFor(method).first())

    @Test
    fun `every captured notification decodes without throwing`() {
        val decoded = trace
            .filter { it["id"] == null }
            .map {
                val method = it["method"]!!.jsonPrimitive.content
                NotificationCodec.decode(method, it["params"]?.jsonObject ?: JsonObject(emptyMap()))
            }

        assertTrue(decoded.isNotEmpty(), "fixture produced no notifications")
        decoded.forEach { assertTrue(it.method.isNotBlank()) }
    }

    @Test
    fun `thread started carries the thread and its id`() {
        val event = assertIs<ThreadStartedNotification>(decodeFirst("thread/started"))
        assertTrue(event.thread.id.isNotBlank())
        // The payload carries no flat `threadId`: the id lives only under `thread.id`, so
        // consumers of this one event must read it from the thread.
        assertNull(event.threadId)
        assertEquals("openai", event.thread.modelProvider)
    }

    @Test
    fun `turn started and completed expose the nested turn id for routing`() {
        val started = assertIs<TurnStartedNotification>(decodeFirst("turn/started"))
        val completed = assertIs<TurnCompletedNotification>(decodeFirst("turn/completed"))

        // turnId is nested under `turn.id` for these two, unlike every other event.
        assertEquals(started.turn.id, started.turnId)
        assertEquals(completed.turn.id, completed.turnId)
        assertEquals(TurnStatus.COMPLETED, completed.turn.status)
        assertNotNull(completed.turn.durationMs)
    }

    @Test
    fun `agent message deltas decode to text fragments`() {
        val delta = assertIs<AgentMessageDeltaNotification>(decodeFirst("item/agentMessage/delta"))
        assertEquals("hello", delta.delta)
        assertTrue(delta.itemId.isNotBlank())
        assertTrue(delta.turnId.isNotBlank())
    }

    @Test
    fun `token usage decodes with the full breakdown`() {
        val usage = assertIs<ThreadTokenUsageUpdatedNotification>(decodeFirst("thread/tokenUsage/updated"))
        assertEquals(14591, usage.tokenUsage.total.totalTokens)
        assertEquals(4480, usage.tokenUsage.total.cachedInputTokens)
        assertEquals(258400, usage.tokenUsage.modelContextWindow)
    }

    @Test
    fun `thread status decodes its tagged shape`() {
        val status = assertIs<ThreadStatusChangedNotification>(decodeFirst("thread/status/changed"))
        assertEquals("active", status.status.type)
    }

    @Test
    fun `completed items decode to their concrete variants`() {
        val items = notificationsFor("item/completed")
            .map { assertIs<ItemCompletedNotification>(NotificationCodec.decode("item/completed", it)).item }

        val byType = items.associateBy { it.type }
        assertTrue(byType.containsKey("userMessage"), "expected a userMessage in the fixture")

        assertIs<UserMessageThreadItem>(byType.getValue("userMessage")).let { user ->
            val text = assertIs<TextUserInput>(user.content.first())
            assertEquals("Reply with exactly: hello from codex", text.text)
        }
        byType["agentMessage"]?.let { assertTrue(assertIs<AgentMessageThreadItem>(it).text.isNotBlank()) }
        byType["commandExecution"]?.let {
            val cmd = assertIs<CommandExecutionThreadItem>(it)
            assertEquals(0, cmd.exitCode)
            assertEquals(CommandExecutionStatus.COMPLETED, cmd.status)
        }
    }

    @Test
    fun `unmodelled methods fall back to Unknown while staying routable`() {
        // Every method this fixture captured is now modelled, so replay a real captured
        // payload under a method this SDK does not model: it must survive verbatim, with
        // routing ids intact.
        val method = "account/rateLimits/superseded"
        assertTrue(method !in ALL_SERVER_NOTIFICATION_METHODS, "$method is modelled after all")

        val params = notificationsFor("account/rateLimits/updated").first()
        val unknown = assertIs<UnknownNotification>(NotificationCodec.decode(method, params))
        assertEquals(method, unknown.method)
        assertTrue(unknown.params.containsKey("rateLimits"))
    }

    @Test
    fun `a malformed payload degrades to Unknown instead of throwing`() {
        // `turn` is required by TurnCompletedNotification; a missing field must not kill the reader.
        val event = NotificationCodec.decode("turn/completed", JsonObject(emptyMap()))
        assertIs<UnknownNotification>(event)
    }

    @Test
    fun `unknown turn ids are recovered from both flat and nested layouts`() {
        val flat = NotificationCodec.decode(
            "some/future/event",
            json.parseToJsonElement("""{"threadId":"t1","turnId":"turn-1"}""").jsonObject,
        )
        assertEquals("turn-1", flat.turnId)
        assertEquals("t1", flat.threadId)

        val nested = NotificationCodec.decode(
            "some/future/turnEvent",
            json.parseToJsonElement("""{"threadId":"t1","turn":{"id":"turn-2"}}""").jsonObject,
        )
        assertEquals("turn-2", nested.turnId)
    }

    @Test
    fun `the captured approval request parses into a typed command request`() {
        val raw = trace.first {
            it["method"]?.jsonPrimitive?.contentOrNull == "item/commandExecution/requestApproval"
        }
        val command = json.decodeFromJsonElement(
            CommandExecutionRequestApprovalParams.serializer(),
            raw["params"]!!.jsonObject,
        )

        assertTrue(command.command!!.contains("codex_kt_probe"))
        assertEquals("/tmp", command.cwd)
        assertNotNull(command.reason)
        assertTrue(command.turnId.isNotBlank())
    }
}

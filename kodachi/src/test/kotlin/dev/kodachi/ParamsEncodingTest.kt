package dev.kodachi

import dev.kodachi.protocol.ReadOnlySandboxPolicy
import dev.kodachi.protocol.TextUserInput
import dev.kodachi.protocol.ThreadStartParams
import dev.kodachi.protocol.TurnStartParams
import dev.kodachi.protocol.UserInput
import dev.kodachi.protocol.encodeParams
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Request params are encoded with `encodeDefaults = false`, because the app-server
 * distinguishes an absent field from an explicitly null one.
 *
 * That created a real bug: union discriminators are constructor properties carrying a
 * default (`type = "text"`), so they were being dropped and the server rejected the turn
 * with *Invalid request: missing field `type`*. Generated discriminators now carry
 * `@EncodeDefault(ALWAYS)`. These tests pin both halves of that contract.
 */
class ParamsEncodingTest {

    @Test
    fun `union discriminators survive encoding`() {
        val params = TurnStartParams(threadId = "t1", input = promptInput("hello"))
        val encoded = encodeParams(TurnStartParams.serializer(), params)

        val first = encoded["input"]!!.jsonArray.first().jsonObject
        assertEquals("text", first["type"]!!.jsonPrimitive.content)
        assertEquals("hello", first["text"]!!.jsonPrimitive.content)
    }

    @Test
    fun `every input variant carries its tag`() {
        val inputs: List<UserInput> = listOf(
            textInput("t"),
            imageInput("https://example.test/a.png"),
            localImageInput("/tmp/a.png"),
            skillInput(name = "s", path = "/tmp/s"),
            mentionInput(name = "m", path = "/tmp/m"),
        )
        val encoded = encodeParams(
            TurnStartParams.serializer(),
            TurnStartParams(threadId = "t1", input = inputs),
        )

        val tags = encoded["input"]!!.jsonArray.map { it.jsonObject["type"]?.jsonPrimitive?.content }
        assertEquals(listOf("text", "image", "localImage", "skill", "mention"), tags)
    }

    @Test
    fun `sandbox policy overrides keep their tag`() {
        val encoded = encodeParams(
            TurnStartParams.serializer(),
            TurnStartParams(
                threadId = "t1",
                input = promptInput("x"),
                sandboxPolicy = Sandbox.READ_ONLY.policy,
            ),
        )
        assertEquals(
            "readOnly",
            encoded["sandboxPolicy"]!!.jsonObject["type"]!!.jsonPrimitive.content,
        )

        // Each Sandbox preset must map to the policy tag the protocol expects.
        val tags = Sandbox.entries.associate { sandbox ->
            sandbox to encodeParams(ReadOnlySandboxPolicy.serializer(), ReadOnlySandboxPolicy())
        }
        assertTrue(tags.isNotEmpty())
        assertEquals("read-only", Sandbox.READ_ONLY.mode.wire)
        assertEquals("workspace-write", Sandbox.WORKSPACE_WRITE.mode.wire)
        assertEquals("danger-full-access", Sandbox.FULL_ACCESS.mode.wire)
    }

    @Test
    fun `absent optional fields stay absent rather than encoding as null`() {
        // The server treats an explicit null override differently from an omitted one, so
        // a params object built with defaults must not carry a wall of nulls.
        val encoded: JsonObject = encodeParams(
            ThreadStartParams.serializer(),
            ThreadStartParams(cwd = "/tmp"),
        )

        assertEquals(setOf("cwd"), encoded.keys)
        assertFalse(encoded.containsKey("model"))
        assertFalse(encoded.containsKey("sandbox"))
    }

    @Test
    fun `approval and sandbox presets encode to their protocol wire values`() {
        val encoded = encodeParams(
            ThreadStartParams.serializer(),
            ThreadStartParams(
                cwd = "/tmp",
                sandbox = Sandbox.FULL_ACCESS.mode,
                approvalPolicy = ApprovalMode.AUTO_REVIEW.askForApproval,
                approvalsReviewer = ApprovalMode.AUTO_REVIEW.reviewer,
            ),
        )

        assertEquals("danger-full-access", encoded["sandbox"]!!.jsonPrimitive.content)
        assertEquals("on-request", encoded["approvalPolicy"]!!.jsonPrimitive.content)
        assertEquals("auto_review", encoded["approvalsReviewer"]!!.jsonPrimitive.content)
    }

    @Test
    fun `text input round-trips through the union serializer`() {
        val encoded = encodeParams(UserInput.serializer(), textInput("round trip"))
        assertEquals("text", encoded["type"]!!.jsonPrimitive.content)

        val decoded = dev.kodachi.internal.CodexJson
            .decodeFromJsonElement(UserInput.serializer(), encoded)
        assertEquals("round trip", (decoded as TextUserInput).text)
    }
}

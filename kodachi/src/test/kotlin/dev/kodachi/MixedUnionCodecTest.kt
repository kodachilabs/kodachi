package dev.kodachi

import dev.kodachi.protocol.AskForApproval
import dev.kodachi.protocol.AskForApprovalGranular
import dev.kodachi.protocol.AskForApprovalPreset
import dev.kodachi.protocol.ReviewDecision
import dev.kodachi.protocol.ReviewDecisionDenied
import dev.kodachi.protocol.SessionSource
import dev.kodachi.protocol.SubAgentSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * The mixed unions are the one place the generated layer writes a wire *shape* rather than
 * just a payload, and several of them are sent, not only received: an approval policy that
 * went out as `{"never": {}}` instead of `"never"` would be rejected by the server, and the
 * SDK would look fine doing it. So both shapes are pinned here in both directions, including
 * the variants this SDK version does not model.
 */
class MixedUnionCodecTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `a preset encodes as a bare string, never as an object`() {
        assertEquals("\"never\"", json.encodeToString(AskForApproval.NEVER))
        assertEquals("\"on-request\"", json.encodeToString(AskForApproval.ON_REQUEST))
        assertEquals("\"approved\"", json.encodeToString(ReviewDecision.APPROVED))
        assertEquals("\"approved_for_session\"", json.encodeToString(ReviewDecision.APPROVED_FOR_SESSION))
    }

    @Test
    fun `a preset decodes back to the same constant`() {
        assertEquals(AskForApproval.NEVER, json.decodeFromString<AskForApproval>("\"never\""))
        assertEquals(AskForApproval.ON_REQUEST, json.decodeFromString<AskForApproval>("\"on-request\""))
        assertEquals(
            AskForApprovalPreset.ON_REQUEST,
            assertIs<AskForApproval.Preset>(json.decodeFromString<AskForApproval>("\"on-request\"")).value,
        )
    }

    @Test
    fun `an object variant round-trips with its payload typed`() {
        val wire = """{"granular":{"mcp_elicitations":true,"rules":false,"sandbox_approval":true}}"""
        val decoded = json.decodeFromString<AskForApproval>(wire)

        val granular = assertIs<AskForApproval.Granular>(decoded).granular
        assertEquals(true, granular.mcpElicitations)
        assertEquals(false, granular.rules)
        assertEquals(true, granular.sandboxApproval)
        assertEquals(json.parseToJsonElement(wire), json.encodeToJsonElement<AskForApproval>(decoded))
    }

    @Test
    fun `a hand-built object variant encodes under its wire key`() {
        val denied: ReviewDecision = ReviewDecision.Denied(ReviewDecisionDenied(rejection = "no"))
        assertEquals("""{"denied":{"rejection":"no"}}""", json.encodeToString(denied))
        assertEquals(denied, json.decodeFromString<ReviewDecision>("""{"denied":{"rejection":"no"}}"""))
    }

    @Test
    fun `a union nested in a union round-trips`() {
        val wire = """{"subAgent":{"thread_spawn":{"depth":2,"parent_thread_id":"t1"}}}"""
        val decoded = json.decodeFromString<SessionSource>(wire)

        val inner = assertIs<SessionSource.SubAgent>(decoded).subAgent
        assertEquals(2, assertIs<SubAgentSource.ThreadSpawn>(inner).threadSpawn.depth)
        assertEquals(json.parseToJsonElement(wire), json.encodeToJsonElement<SessionSource>(decoded))
    }

    @Test
    fun `an unmodelled preset keeps its exact spelling instead of decoding to empty`() {
        val decoded = json.decodeFromString<AskForApproval>("\"on-failure\"")

        assertEquals(JsonPrimitive("on-failure"), assertIs<AskForApproval.Unknown>(decoded).raw)
        assertEquals("\"on-failure\"", json.encodeToString<AskForApproval>(decoded))
        assertEquals(decoded, AskForApproval.of("on-failure"))
    }

    @Test
    fun `an unmodelled object variant survives a round trip verbatim`() {
        val wire = """{"someFutureVariant":{"detail":7}}"""
        val decoded = json.decodeFromString<ReviewDecision>(wire)

        assertIs<ReviewDecision.Unknown>(decoded)
        assertEquals(json.parseToJsonElement(wire), json.encodeToJsonElement<ReviewDecision>(decoded))
    }
}

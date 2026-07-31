package dev.kodex

import dev.kodex.internal.CodexJson
import dev.kodex.protocol.FileSystemSpecialPath
import dev.kodex.protocol.ThreadStartParams
import dev.kodex.protocol.encodeParams
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The two things `MixedUnionCodecTest` cannot cover, because they are about how these unions
 * behave *outside* their own serializer.
 */
class MixedUnionTest {

    @Test
    fun `a union nested in real request params still encodes as a bare string`() {
        // The codec test proves the union alone encodes correctly. This proves it survives being
        // embedded in params encoded with encodeDefaults = false — the exact combination that
        // produced a "missing field `type`" rejection once already.
        val encoded: JsonObject = encodeParams(
            ThreadStartParams.serializer(),
            ThreadStartParams(cwd = "/tmp", approvalPolicy = ApprovalMode.AUTO_REVIEW.askForApproval),
        )
        assertEquals("on-request", encoded["approvalPolicy"]!!.jsonPrimitive.content)
    }

    @Test
    fun `a kind-discriminated union is typed rather than raw json`() {
        // FileSystemSpecialPath is tagged by `kind`, not `type`. The generator understood only
        // `type`, so this silently degraded to an untyped JsonElement alias.
        val decoded = CodexJson.decodeFromJsonElement(
            FileSystemSpecialPath.serializer(),
            CodexJson.parseToJsonElement("""{"kind":"root"}"""),
        )
        assertEquals("root", decoded.kind)
        assertTrue(
            CodexJson.encodeToJsonElement(FileSystemSpecialPath.serializer(), decoded)
                .jsonObject.containsKey("kind"),
        )
    }
}

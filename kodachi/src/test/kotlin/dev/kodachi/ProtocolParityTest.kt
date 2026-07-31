package dev.kodachi

import dev.kodachi.protocol.ALL_SERVER_NOTIFICATION_METHODS
import dev.kodachi.protocol.CLIENT_REQUEST_PARAMS
import dev.kodachi.protocol.ClientNotifications
import dev.kodachi.protocol.ClientRequests
import dev.kodachi.protocol.NotificationSerializers
import dev.kodachi.protocol.ProtocolInfo
import dev.kodachi.protocol.ServerRequests
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Guards protocol coverage.
 *
 * The generated layer is supposed to cover the app-server protocol completely — every
 * notification routable, every request callable. These tests fail if a regeneration drops
 * something, and they pin the surface the reference Python SDK exposes so this SDK cannot
 * silently fall behind it.
 */
class ProtocolParityTest {

    // -----------------------------------------------------------------------
    // Notifications
    // -----------------------------------------------------------------------

    @Test
    fun `every declared server notification has a payload serializer`() {
        val declared = ALL_SERVER_NOTIFICATION_METHODS.toSet()
        val mapped = notificationSerializerKeys()

        assertEquals(
            emptySet(),
            declared - mapped,
            "notifications declared by the protocol but missing from the dispatch table",
        )
        assertEquals(
            emptySet(),
            mapped - declared,
            "dispatch table entries with no corresponding protocol notification",
        )
    }

    @Test
    fun `notification coverage has not regressed`() {
        // codex-cli 0.146.0 defines 70 server notifications. A drop means the generator
        // lost some; a rise is fine and expected on upgrade.
        assertTrue(
            ALL_SERVER_NOTIFICATION_METHODS.size >= 70,
            "expected at least 70 notifications, found ${ALL_SERVER_NOTIFICATION_METHODS.size}",
        )
        assertEquals(
            ALL_SERVER_NOTIFICATION_METHODS.size,
            ALL_SERVER_NOTIFICATION_METHODS.distinct().size,
            "duplicate notification methods",
        )
    }

    @Test
    fun `the notifications a streaming client depends on are all routable`() {
        // Lose any of these and turn streaming breaks in a way unit tests elsewhere
        // would not obviously catch.
        val essential = listOf(
            "thread/started",
            "thread/status/changed",
            "thread/tokenUsage/updated",
            "turn/started",
            "turn/completed",
            "turn/diff/updated",
            "turn/plan/updated",
            "item/started",
            "item/completed",
            "item/agentMessage/delta",
            "item/reasoning/textDelta",
            "item/reasoning/summaryTextDelta",
            "item/commandExecution/outputDelta",
            "item/fileChange/patchUpdated",
            "item/mcpToolCall/progress",
            "error",
        )
        val mapped = notificationSerializerKeys()
        assertEquals(emptyList(), essential.filterNot { it in mapped }, "unroutable essentials")
    }

    @Test
    fun `the recorded provenance matches what is actually generated`() {
        // A partial regeneration — say the script died between files — would leave the
        // counts and the code disagreeing. ProtocolInfo is the record; this checks it.
        assertEquals(
            ALL_SERVER_NOTIFICATION_METHODS.size,
            ProtocolInfo.NOTIFICATION_COUNT,
            "ProtocolInfo.NOTIFICATION_COUNT disagrees with the generated registry",
        )
        assertEquals(
            clientRequestConstants().size,
            ProtocolInfo.CLIENT_REQUEST_COUNT,
            "ProtocolInfo.CLIENT_REQUEST_COUNT disagrees with the generated constants",
        )
        assertEquals(
            constantsOf(ServerRequests::class.java).size,
            ProtocolInfo.SERVER_REQUEST_COUNT,
            "ProtocolInfo.SERVER_REQUEST_COUNT disagrees with the generated constants",
        )
        assertTrue(
            ProtocolInfo.CODEX_VERSION.isNotBlank() && ProtocolInfo.CODEX_VERSION != "unknown",
            "the generated layer must record which codex version produced it",
        )
        assertTrue(
            ProtocolInfo.SCHEMA_FINGERPRINT.startsWith("sha256:"),
            "expected a schema fingerprint for drift detection",
        )
    }

    // -----------------------------------------------------------------------
    // Client requests
    // -----------------------------------------------------------------------

    @Test
    fun `every client request method is declared and callable`() {
        val declared = clientRequestConstants()
        assertTrue(declared.size >= 90, "expected at least 90 client requests, found ${declared.size}")
        assertEquals(
            CLIENT_REQUEST_PARAMS.keys,
            declared,
            "ClientRequests constants and the params table disagree",
        )
    }

    @Test
    fun `every method the reference Python SDK wraps is present here`() {
        // Parity floor: openai-codex (Python) exposes ergonomic wrappers for exactly
        // these. This SDK must cover at least the same ground.
        val pythonSdkSurface = listOf(
            "initialize",
            "account/login/start",
            "account/login/cancel",
            "account/logout",
            "account/read",
            "model/list",
            "thread/start",
            "thread/resume",
            "thread/fork",
            "thread/list",
            "thread/read",
            "thread/archive",
            "thread/unarchive",
            "thread/name/set",
            "thread/compact/start",
            "thread/goal/set",
            "thread/goal/clear",
            "turn/start",
            "turn/steer",
            "turn/interrupt",
        )
        val declared = clientRequestConstants()
        assertEquals(
            emptyList(),
            pythonSdkSurface.filterNot { it in declared },
            "methods the Python SDK wraps that this SDK does not declare",
        )
    }

    @Test
    fun `the client notification used by the handshake is declared`() {
        assertEquals("initialized", ClientNotifications.INITIALIZED)
    }

    // -----------------------------------------------------------------------
    // Server requests
    // -----------------------------------------------------------------------

    @Test
    fun `every server request the protocol defines is declared`() {
        val declared = constantsOf(ServerRequests::class.java)
        assertTrue(declared.size >= 10, "expected at least 10 server requests, found ${declared.size}")

        val essential = listOf(
            "item/commandExecution/requestApproval",
            "item/fileChange/requestApproval",
            "item/permissions/requestApproval",
            "item/tool/requestUserInput",
            "item/tool/call",
            "mcpServer/elicitation/request",
            "account/chatgptAuthTokens/refresh",
            "attestation/generate",
            "applyPatchApproval",
            "execCommandApproval",
        )
        assertEquals(emptyList(), essential.filterNot { it in declared }, "undeclared server requests")
    }

    // -----------------------------------------------------------------------
    // Ergonomic wiring
    // -----------------------------------------------------------------------

    @Test
    fun `Codex exposes every generated namespace`() {
        // Reflected off the class, so no process is spawned.
        val namespaces = Codex::class.java.methods
            .filter { it.name.startsWith("get") && it.parameterCount == 0 }
            .map { it.returnType.simpleName }
            .filter { it.endsWith("Api") }
            .distinct()

        assertTrue(
            namespaces.size >= 23,
            "expected at least 23 protocol namespaces on Codex, found ${namespaces.sorted()}",
        )

        val expected = listOf(
            "AccountApi", "AppsApi", "CommandApi", "ConfigApi", "ConfigRequirementsApi",
            "ExperimentalFeatureApi", "ExternalAgentConfigApi", "FeedbackApi", "FsApi",
            "FuzzyFileSearchApi", "HooksApi", "MarketplaceApi", "McpServerApi",
            "McpServerStatusApi", "ModelApi", "ModelProviderApi", "PermissionProfileApi",
            "PluginsApi", "ReviewApi", "SkillsApi", "ThreadsApi", "TurnsApi",
            "WindowsSandboxApi",
        )
        assertEquals(emptyList(), expected.filterNot { it in namespaces }, "namespaces not wired up")
    }

    @Test
    fun `the namespaces together expose every client request`() {
        val namespaceClasses = Codex::class.java.methods
            .filter { it.name.startsWith("get") && it.parameterCount == 0 }
            .map { it.returnType }
            .filter { it.simpleName.endsWith("Api") }
            .distinct()

        val callable = namespaceClasses.sumOf { type ->
            type.declaredMethods.count { !it.isSynthetic && Modifier.isPublic(it.modifiers) }
        }

        // 90 client requests, minus `initialize`, which the handshake owns.
        assertTrue(
            callable >= 89,
            "namespaces expose $callable methods; expected at least 89 (90 requests less initialize)",
        )
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** The dispatch table is internal, and tests share the SDK's module. */
    private fun notificationSerializerKeys(): Set<String> = NotificationSerializers.keys

    private fun clientRequestConstants(): Set<String> = constantsOf(ClientRequests::class.java)

    private fun constantsOf(type: Class<*>): Set<String> = type.declaredFields
        .filter { it.type == String::class.java }
        .mapNotNull {
            it.isAccessible = true
            it.get(null) as? String
        }
        .toSet()
}

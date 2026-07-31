package dev.kodachi

import dev.kodachi.protocol.ApprovalsReviewer
import dev.kodachi.protocol.AskForApproval
import dev.kodachi.protocol.DangerFullAccessSandboxPolicy
import dev.kodachi.protocol.ReadOnlySandboxPolicy
import dev.kodachi.protocol.SandboxMode
import dev.kodachi.protocol.SandboxPolicy
import dev.kodachi.protocol.WorkspaceWriteSandboxPolicy

/**
 * Filesystem access granted to the agent.
 *
 * Codex enforces [READ_ONLY] and [WORKSPACE_WRITE] with an OS-level sandbox. That
 * sandbox does not work inside some container runtimes (notably gVisor), where every
 * command fails; [FULL_ACCESS] runs commands uncaged and is the usual choice when the
 * whole process is already inside an isolated sandbox.
 *
 * For finer control — writable roots, network access, `/tmp` handling — build a
 * [SandboxPolicy] directly and pass it through the `overrides` parameter on
 * [Codex.startThread] or [CodexThread.turn].
 */
public enum class Sandbox(
    internal val mode: SandboxMode,
    internal val policy: SandboxPolicy,
) {
    /** Reads allowed, writes denied. */
    READ_ONLY(SandboxMode.READ_ONLY, ReadOnlySandboxPolicy()),

    /** Writes allowed inside the workspace and configured writable roots. */
    WORKSPACE_WRITE(SandboxMode.WORKSPACE_WRITE, WorkspaceWriteSandboxPolicy()),

    /** No filesystem restrictions. */
    FULL_ACCESS(SandboxMode.DANGER_FULL_ACCESS, DangerFullAccessSandboxPolicy()),
}

/**
 * Where escalated permission requests are routed.
 *
 * [ASK_CLIENT] delivers them to your [ApprovalHandler]. [AUTO_REVIEW] lets the server
 * decide without a human, matching `codex exec`. [DENY_ALL] refuses every escalation.
 *
 * The protocol also supports a granular policy and a guardian sub-agent reviewer; reach
 * those with [AskForApproval.Granular] and [ApprovalsReviewer.GUARDIAN_SUBAGENT] through
 * the `overrides` parameter.
 */
public enum class ApprovalMode(
    internal val askForApproval: AskForApproval,
    internal val reviewer: ApprovalsReviewer?,
) {
    /** Route escalations to this client's [ApprovalHandler]. */
    ASK_CLIENT(AskForApproval.ON_REQUEST, ApprovalsReviewer.USER),

    /** Let the server auto-review escalations, as `codex exec` does. */
    AUTO_REVIEW(AskForApproval.ON_REQUEST, ApprovalsReviewer.AUTO_REVIEW),

    /** Refuse every escalation. */
    DENY_ALL(AskForApproval.NEVER, null),

    /** Ask only for commands the server considers untrusted. */
    UNTRUSTED(AskForApproval.UNTRUSTED, ApprovalsReviewer.USER),
}

/**
 * Reasoning budget for a turn.
 *
 * Which values a model accepts varies by model, so the wire type is an open string;
 * these constants cover what the CLI exposes today.
 */
public object Effort {
    public const val MINIMAL: String = "minimal"
    public const val LOW: String = "low"
    public const val MEDIUM: String = "medium"
    public const val HIGH: String = "high"
    public const val MAX: String = "max"
}

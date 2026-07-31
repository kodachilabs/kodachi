# kodex

A Kotlin SDK for [OpenAI Codex](https://github.com/openai/codex) — the Kotlin counterpart to the
official Python and TypeScript SDKs, at full protocol parity.

Coroutines-first, `Flow`-based streaming, no reflection, one dependency pair
(kotlinx-coroutines + kotlinx-serialization).

```kotlin
Codex.connect(CodexConfig(cwd = "/path/to/repo")).use { codex ->
    val thread = codex.startThread(sandbox = Sandbox.WORKSPACE_WRITE)

    val result = thread.run("Add a health check endpoint and a test for it.")
    println(result.finalResponse)

    // …or watch it work
    thread.turn("Now run the test suite.").stream().collect { event ->
        when (event) {
            is AgentMessageDeltaNotification -> print(event.delta)
            is ItemCompletedNotification -> (event.item as? CommandExecutionThreadItem)
                ?.let { println("$ ${it.command} -> exit ${it.exitCode}") }
            is TurnCompletedNotification -> println("[${event.turn.status}]")
            else -> Unit
        }
    }
}
```

## How this works

Codex's agent logic lives entirely inside the `codex` binary. Every SDK — Python, TypeScript,
and this one — is a **typed JSON-RPC client** that drives `codex app-server` as a child process
over stdio. This SDK reimplements no agent behavior; it speaks the protocol.

```
your Kotlin app  ──JSON-RPC over stdio──▶  codex app-server (child process)
   Codex                                          │
   ├─ CodexThread     thread/…                     ├─ model calls
   ├─ TurnHandle      turn/… + streaming           ├─ shell / file tools
   ├─ 23 namespaces   fs/… plugin/… skills/… …     ├─ MCP servers
   └─ ServerRequestHandler ◀── server→client ──────┤ hooks
                                                   └─ approvals
```

### The types are generated, not hand-written

The protocol is large — 608 type definitions, 90 client request methods, 70 server
notifications, 10 server requests — and it moves with every Codex release. So this SDK
generates its protocol layer from the schema the binary itself emits, exactly as the Python
SDK generates `v2_all.py`:

```bash
python3 scripts/generate_protocol.py          # asks your codex binary for its schema
```

That runs `codex app-server generate-json-schema`, merges the schema bundle with the
per-method files, and emits ~8,000 lines into
`kodex/src/main/kotlin/dev/kodex/protocol/Generated*.kt`. Regenerate after upgrading
Codex; never edit those files.

| Generated | Count |
| --- | --- |
| Object payloads | 425 |
| Enums (each forward-compatible) | 115 |
| Discriminated unions | 32 |
| Scalar/union wrappers and aliases | 17 |
| Server notifications, all routable | 70 |
| Client request methods, all typed | 90 |
| Server requests, all answerable | 10 |

Generated against `codex-cli 0.146.0`; the exact provenance is recorded in
`ProtocolInfo.CODEX_VERSION` and `ProtocolInfo.SCHEMA_FINGERPRINT`.

## Keeping up with upstream

Two things can move. Only one of them is really upstream.

### What happens automatically, and what does not

Nothing breaks on its own when Codex updates. New surface does not type itself.

| When Codex updates… | Automatic? |
| --- | --- |
| Your running app keeps working against the new binary | **Yes** — no recompile |
| New notifications, enum values and fields arrive safely | **Yes** — `UnknownNotification` / `UNKNOWN` / ignored |
| New methods are reachable | **Yes**, untyped — via `codex.request()` |
| New methods and notifications get *typed* wrappers | **No** — regenerate |
| A breaking change is fixed | **No** — a code edit, which the compiler locates |

`.github/workflows/protocol-drift.yml` closes the "someone has to notice" gap: it checks
daily, and on drift it regenerates, builds, and opens a PR whose description says whether the
release was additive or breaking. Review stays human, because judging a breaking change is not
mechanical.

Regeneration is deliberately **not** part of the build. A build whose output depended on
whichever binary happened to be installed would not be reproducible, and an upstream change
would surface as a mysterious compile error rather than a reviewable diff.

**The `codex` binary and its protocol — this is the upstream that matters.** Ask whether the
committed types still match the installed binary:

```bash
./gradlew checkProtocolDrift          # or: python3 scripts/generate_protocol.py --check
```

It regenerates into a temp directory, diffs against what is committed, prints the types that
appeared or vanished, and exits `1` on drift — so it works as a CI gate. Output is
deterministic (definitions are sorted), so a clean tree really means in sync.

When it reports drift:

```bash
./gradlew regenerateProtocol             # or: python3 scripts/generate_protocol.py
./gradlew build                          # hand-written layer + parity tests
```

The build is the review. Three outcomes:

1. *Compiles, tests pass* — additive release. Nothing to do; new methods and notifications are
   already typed and callable.
2. *Compiles, a parity test fails* — coverage regressed (a notification lost its serializer, a
   namespace stopped being wired). `ProtocolParityTest` names it.
3. *Does not compile* — a genuine breaking change, and the compiler points at every line that
   assumed the old shape. This happened going 0.141 → 0.146: `AskForApproval` dropped
   `on-failure`, and `ReviewDecision.denied` became an object needing a rejection reason. Two
   errors, both in the ergonomic layer, both a two-line fix.

Until you regenerate, an older SDK against a newer binary keeps working: new notifications
arrive as `UnknownNotification`, new enum values as `UNKNOWN`, and new methods are reachable
through `codex.request()`. You lose type coverage, not function.

**The Python SDK is not upstream** — it is a peer reading the same schema. It matters in exactly
one way: `ProtocolParityTest` pins the list of methods it wraps ergonomically, so this SDK
cannot silently cover less ground. If its ergonomics change in a way worth copying, that is a
deliberate design decision here, not a mechanical sync.

## Install

Not yet on Maven Central. Build and consume locally:

```bash
./gradlew :kodex:publishToMavenLocal
```

```kotlin
dependencies {
    implementation("dev.kodex:kodex:0.1.0")
}
```

You also need an authenticated Codex CLI on the machine:

```bash
npm install -g @openai/codex && codex login
```

The SDK deliberately **does not bundle a binary**. It resolves, in order:
`CodexConfig.codexBin` → `$CODEX_BIN` → `codex` on `PATH`. (The Python SDK pins a bundled
binary, and installs stuck on an old pin cannot use newer models or reasoning efforts. This
avoids that failure mode.)

## Publishing

The build stages a Central-shaped deployment and zips it for the
[Central Portal](https://central.sonatype.com), which replaced the old OSSRH/Nexus deploy.

```bash
./gradlew :kodex:centralBundle
```

That produces `kodex/build/central/kodex-<version>-central-bundle.zip`. It refuses to build an
unsigned bundle and names the unsigned files, because Central would otherwise reject the upload
minutes later with less detail.

### Step by step

**1. Decide the namespace.** Central verifies you own it.

| Namespace | Verification |
| --- | --- |
| `io.github.<user>` | automatic — sign into the Portal with GitHub |
| `dev.kodex` | a DNS TXT record on `kodex.dev`, which you must own |

Pick with a property, no code change:

```bash
./gradlew :kodex:centralBundle -PmavenGroup=io.github.saadaziz9956
```

**2. Decide the version.** Central never lets a released version be replaced, and rejects
anything ending `-SNAPSHOT`. `0.1.0` is honest for a first release; `-PmavenVersion=0.146.0` is
the alternative if you want to track the Codex CLI the way the Python SDK now does.

**3. Create a GPG key** and publish the public half, which is how Central verifies signatures.
`gnupg` may not be installed:

```bash
brew install gnupg
gpg --quick-generate-key "Your Name <you@example.com>" rsa4096 sign never
gpg --list-secret-keys --keyid-format=long          # note the key id
gpg --keyserver keys.openpgp.org --send-keys <KEY_ID>
gpg --armor --export-secret-keys <KEY_ID> > private.asc
```

Never commit `private.asc`. Put it in `~/.gradle/gradle.properties` — outside the repo:

```properties
signingInMemoryKey=<contents of private.asc>
signingInMemoryKeyPassword=<passphrase, omit the line if none>
```

Gradle signs via BouncyCastle, so `gpg` is only needed to *make* the key, not to use it. In CI,
`SIGNING_KEY` and `SIGNING_PASSWORD` work instead.

**4. Generate a Portal token** — Portal → Account → Generate User Token. It gives a username and
password; the API wants them base64-encoded together.

**5. Verify locally before anything leaves the machine.** This is the same content Central will
validate:

```bash
./gradlew clean build                    # 87 tests must pass
./gradlew :kodex:centralBundle
unzip -l kodex/build/central/kodex-0.1.0-central-bundle.zip
```

Expect, under `dev/kodex/kodex/0.1.0/`: the jar, sources jar, javadoc jar, `.pom`, `.module`,
a `.asc` for each, and `.md5`/`.sha1` for each. No `maven-metadata` — a bundle is not a deploy.

**6. Upload.**

```bash
TOKEN=$(printf '%s:%s' "<portal-username>" "<portal-password>" | base64)
curl --request POST \
  --header "Authorization: Bearer $TOKEN" \
  --form bundle=@kodex/build/central/kodex-0.1.0-central-bundle.zip \
  'https://central.sonatype.com/api/v1/publisher/upload?name=kodex-0.1.0'
```

It returns a deployment id. Omitting `publishingType` leaves it `USER_MANAGED`, so it stages for
review rather than going live — the right default for a first release. Add
`&publishingType=AUTOMATIC` once you trust the pipeline.

**7. Release it.** Portal → Deployments → your deployment. Validation failures are listed there.
On publish, expect roughly 10–30 minutes to appear on Central and up to a few hours to reach
`search.maven.org`.

**8. Tag the release**, so the published version is reproducible from source:

```bash
git tag -a v0.1.0 -m "kodex 0.1.0" && git push origin v0.1.0
```

Then it installs anywhere with:

```kotlin
implementation("dev.kodex:kodex:0.1.0")
```

## API

Three layers, from most to least ergonomic. Use whichever fits.

**1. Conversation flow**

| Type | What it does |
| --- | --- |
| `Codex` | Owns the child process. `startThread`, `resumeThread`, `forkThread`, `listThreads`, `models`. `AutoCloseable`. |
| `CodexThread` | One conversation. `turn()` for a handle, `run()` for a completed result, plus `read`, `setName`, `compact`, `archive`, `rollback`, `shellCommand`, `setGoal`. |
| `TurnHandle` | A running turn: `stream()`, `steer()`, `interrupt()`, `collect()`. |
| `TurnResult` | `status`, `finalResponse`, `messages`, `items`, `usage`, `error`, `diff`. |
| `ServerRequestHandler` | Answers the ten requests the server sends to the client. |
| `LoginHandle` | `loginWithChatgpt()` / `loginWithDeviceCode()` / `loginWithApiKey()`, then `await()` or `cancel()`. |
| `RetryPolicy` / `withRetry` | Opt-in backoff for transient server overload, matching the Python SDK. |
| `GoalHandle` / `GoalResult` | A standing objective aggregated across every turn it drives. |

**2. Protocol namespaces** — every remaining typed request, grouped as the protocol groups them:

```kotlin
codex.fs.readFile(FsReadFileParams(path = "/repo/README.md"))
codex.skills.list(SkillsListParams(cwds = listOf("/repo")))
codex.plugins.install(PluginInstallParams(/* … */))
codex.threads.rollback(ThreadRollbackParams(threadId = id, numTurns = 2))
codex.review.start(ReviewStartParams(/* … */))
codex.account.rateLimitsRead()
```

`account`, `apps`, `command`, `serverConfig`, `configRequirements`, `experimentalFeatures`,
`externalAgentConfig`, `feedback`, `fs`, `fuzzyFileSearch`, `hooks`, `marketplace`,
`mcpServer`, `mcpServerStatus`, `model`, `modelProvider`, `permissionProfiles`, `plugins`,
`review`, `skills`, `threads`, `turns`, `windowsSandbox`.

**3. Raw access** — for anything newer than this SDK:

```kotlin
codex.request("some/future/method", buildJsonObject { put("x", 1) })
codex.notify("some/future/notification")
```

### Goals: one stream across many turns

A turn is one exchange. A **goal** is a standing objective — the server keeps starting turns by
itself until the goal is done or gives up. `startGoal` aggregates all of them into one stream, so
you never have to work out which turn an event belonged to:

```kotlin
val goal = thread.startGoal("Make the test suite pass.", tokenBudget = 200_000)

goal.stream().collect { event ->
    when (event) {
        is TurnStartedNotification -> println("--- new turn ---")
        is AgentMessageDeltaNotification -> print(event.delta)
        is ThreadGoalUpdatedNotification -> println("[${event.goal.status}]")
        else -> Unit
    }
}
// …or just: val result = goal.collect()   // GoalResult across every turn
```

Turn boundaries stay visible (each turn still emits `turn/started` and `turn/completed`), so
grouping by `turnId` is still possible — it is just no longer mandatory.

Interrupting a goal means two different things, so they are two calls:

| Call | Effect |
| --- | --- |
| `goal.interruptCurrentTurn()` | Stops the turn in flight. The goal continues and may start another. |
| `goal.abandon()` | Ends the goal. The in-flight turn still delivers its final events first. |

The stream closes only when the goal is genuinely over: nothing in flight, at least one turn
finished, and the goal either cleared or in a terminal status. Closing on status alone would
truncate the final turn — `thread/goal/updated` usually reports `complete` while that turn is
still streaming. One goal stream per thread; don't also call `turn()` on a thread with a live goal.

### Streaming is lossless; the global tap is not

`TurnHandle.stream()` is backed by an unbounded channel claimed the moment the turn starts, so
events emitted before you begin collecting are replayed rather than dropped. `Codex.events` is a
separate, deliberately **lossy** tap of all traffic for logging and metrics — it drops the oldest
events under back-pressure so a slow collector can never stall the agent.

### Nothing unknown is ever fatal

The app-server gains notifications, item types, and enum values faster than any client tracks
them, so every extension point degrades instead of throwing:

- an unmapped notification method becomes `UnknownNotification` with raw params — and with its
  routing ids intact, so an unmodelled *turn-scoped* event still reaches the right turn's stream;
- an unknown union variant becomes `Unknown…` carrying the raw object (`UnknownThreadItem`,
  `UnknownUserInput`, …);
- an unrecognized enum value decodes to that enum's `UNKNOWN` entry rather than failing;
- a payload that fails to decode degrades to `UnknownNotification` rather than killing the
  reader loop.

Upgrading Codex will not crash your app on an unrecognized event.

### Server requests are answered, always

Ten requests flow server→client, and the turn blocks until each is answered. Subclass
`ServerRequestHandler` and override only what you care about:

```kotlin
val policy = object : ServerRequestHandler(ApprovalDecision.ACCEPT) {
    override suspend fun onCommandApproval(
        params: CommandExecutionRequestApprovalParams,
    ) = CommandExecutionRequestApprovalResponse(
        if (params.command?.contains("rm -rf") == true) CommandExecutionApprovalDecision.DECLINE
        else CommandExecutionApprovalDecision.ACCEPT_FOR_SESSION,
    )
}
Codex(CodexConfig(serverRequestHandler = policy)).use { codex ->
    // ASK_CLIENT is what routes escalations to your handler at all.
    codex.startThread(approvalMode = ApprovalMode.ASK_CLIENT)
}
```

Requests the SDK cannot honestly fulfil on your behalf — minting ChatGPT auth tokens,
generating an attestation, granting an unstated permission profile — answer with a JSON-RPC
**error** by default rather than a fabricated success. Override the matching method to
handle them.

### Sandboxing

`Sandbox.READ_ONLY` and `WORKSPACE_WRITE` are enforced by Codex's own OS-level cage
(bubblewrap on Linux). That cage does not work inside some container runtimes — notably gVisor,
where every command fails — so when your process is *already* inside an isolated sandbox,
`Sandbox.FULL_ACCESS` plus a `PreToolUse` hook is the working configuration. For writable
roots, network access, or `/tmp` handling, build a `SandboxPolicy` and pass it via `overrides`.

## Performance

Measured, not asserted. `PerfBenchmark` is a warmed in-JVM harness over the real transport:

```bash
./gradlew :kodex:test -DcodexBench=true --tests 'dev.kodex.PerfBenchmark'
./gradlew :kodex:test -DcodexBench=true -DcodexIntegration=true --tests 'dev.kodex.PerfBenchmark'
```

| Path | Throughput |
| --- | --- |
| Decode one `item/agentMessage/delta` | ~1.6 M/sec (~610 ns) |
| Decode one `item/completed` (command, nested) | ~0.5 M/sec (~1.9 µs) |
| Whole line → routed to a live turn stream | ~0.5 M/sec (~2.0 µs) |

**Throughput is not the bottleneck and cannot usefully be made one.** A model streams on the
order of 100–200 events per second; the transport routes ~500,000. That is roughly three orders
of magnitude of headroom, and the remaining per-event cost is dominated by coroutine channel
handoff — inherent to a `Flow`-based streaming API, and the reason the API is worth having.

Latency is what a caller actually waits on, and it is almost entirely the binary's:

| Stage | Cold | Warm |
| --- | --- | --- |
| Process spawn | ~9 ms | ~2 ms |
| `initialize` handshake | ~240 ms | ~85 ms |
| `thread/start` | ~950 ms | ~70 ms |
| **Total** | **~1.2 s** | **~150 ms** |

The SDK contributes the ~2 ms spawn; the rest is Codex loading config and starting MCP servers.
So the one optimisation that matters is architectural: **hold one `Codex` instance open and reuse
it.** Spawning per request pays the ~1 s cold cost every time.

Two changes here were made for robustness rather than speed, and neither moved the throughput
numbers measurably:

- **Turn buffers are bounded** (`CodexConfig.turnEventBufferSize`, default 8192). A stalled
  collector used to grow an unbounded channel until the heap died; it now fails with
  `TurnStreamOverflowException` naming the turn and the cap. Losslessness is unchanged in normal
  operation — nothing real comes near the cap.
- **64 KiB stdio buffers** instead of the JDK's 8 KiB, because command-output deltas and turn
  diffs routinely exceed that and every refill is a syscall. Not visible to an in-memory harness.

Routing was also made lock-free on the fast path and the lossy tap is skipped when nothing is
subscribed. Both are principled, and both measured as noise — an uncontended monitor and a
zero-subscriber `SharedFlow` were already nearly free. Recorded here so nobody re-does them
expecting a win.

## CLI

A thin terminal wrapper over the SDK, for exercising it by hand:

```bash
./gradlew :cli:installDist
export PATH="$PWD/cli/build/install/kodex/bin:$PATH"
```

```
kodex doctor                  # binary, protocol, handshake, account — spends no quota
kodex login                   # store an API key from $OPENAI_API_KEY or stdin
kodex whoami                  # account + rate limits
kodex models                  # models this account can use
kodex chat "say hello"        # one turn, streamed, read-only
kodex exec "add a README"     # one turn with writes, auto-approving
kodex goal "make tests pass"  # a goal across as many turns as it takes
```

Flags: `--cwd`, `--model`, `--effort`, `--budget`, `--quiet`, and `--isolated` (a throwaway
`CODEX_HOME`, so a test cannot touch your real credentials).

Start with `kodex doctor` — it checks the binary, the protocol layer, the handshake and the
account without spending a token, so a failure there is a setup problem rather than a code one.

### Authenticating with an API key

```bash
export OPENAI_API_KEY=sk-...
kodex login
kodex chat "say hello"
```

A key is read from the environment or stdin, **never from a flag** — argv is readable by other
processes and lands in shell history. The CLI never prints it.

Two things worth knowing:

- **`login` succeeding does not mean the key works.** The app-server stores a key without
  validating it, so a bad key only surfaces on the first turn — as a `401` in the server's stderr
  and a turn ending `FAILED`. Set `CodexConfig.onStderrLine` (the CLI does) or that diagnosis is
  invisible.
- **Storing a key replaces whatever was there**, including ChatGPT OAuth tokens. Use
  `--isolated`, or `codex login` to switch back.

## Samples

```bash
./gradlew :samples:run --args="/path/to/repo"                                  # quickstart
./gradlew :samples:run -Psample=dev.kodex.samples.streaming.StreamingKt      # live event stream
./gradlew :samples:run -Psample=dev.kodex.samples.approvals.ApprovalsKt      # approval policy
./gradlew :samples:run -Psample=dev.kodex.samples.steering.SteeringKt        # steer mid-turn
./gradlew :samples:run -Psample=dev.kodex.samples.goal.GoalKt                # multi-turn goal
```

## Tests

```bash
./gradlew test                              # 87 unit tests, no binary needed
./gradlew test -DcodexIntegration=true      # + 6 against a real codex (uses real quota)
./gradlew test -DcodexBench=true            # throughput harness
```

To exercise the whole SDK the way a user would, run every sample (each drives the real binary):

```bash
for s in quickstart.QuickstartKt streaming.StreamingKt steering.SteeringKt \
         approvals.ApprovalsKt goal.GoalKt; do
  ./gradlew :samples:run -Psample=dev.kodex.samples.$s --args="/tmp/kodex-scratch"
done
```

And to verify the *published artifact* rather than the source tree, consume it from a throwaway
project with `mavenLocal()` on the repository list and `implementation("dev.kodex:kodex:0.1.0")`.
That is the only check that catches a packaging mistake — a missing dependency in the POM, or a
class that never made it into the jar.

Three kinds:

- **Parity** (`ProtocolParityTest`) — asserts every declared notification has a serializer,
  every client request is declared and reachable through a namespace, all ten server requests
  are handled, and every method the reference Python SDK wraps exists here. This is what keeps
  "parity" from silently rotting.
- **Wire** (`WireTraceTest`) — decodes `src/test/resources/wire-trace.jsonl`, messages
  **captured from a real `codex app-server` session**, so the assertions describe the protocol
  as it behaves rather than as the schema reads.
- **Transport** (`AppServerClientTest`, `ServerRequestDispatcherTest`) — id matching,
  out-of-order responses, pre-registration buffering, approval replies, all ten server-request
  branches, transport loss. Uses an in-memory transport; no binary required.

## Protocol notes

Verified empirically against `codex-cli 0.141.0` and `0.146.0`. Useful if you extend this SDK:

- Launch: `codex [--config k=v]… app-server --listen stdio://`
- Framing: newline-delimited JSON. **No `jsonrpc` field** — present in neither direction.
- Handshake: `initialize` request, then an `initialized` **notification**. Thread calls are
  rejected before that acknowledgement.
- Client→server request ids are strings (UUIDs here); **server→client request ids are integers**.
  A reply must echo the id verbatim, preserving its JSON type.
- Server→client requests are distinguished by carrying *both* `method` and `id`. Answer with
  `{"id": <id>, "result": {…}}`, or `{"id": <id>, "error": {…}}` when you cannot.
- Turn ids arrive flat as `params.turnId` on most events, but nested as `params.turn.id` on
  `turn/started` and `turn/completed`.
- `developerInstructions` is a session-init concept: it sticks from `thread/start` and is **not**
  updated by `thread/resume`. Per-turn guidance belongs in the turn input.
- Turn-level overrides (`model`, `effort`, `sandboxPolicy`, …) apply to that turn **and subsequent
  ones**, not just the one turn.

## Mixed unions

A handful of schema types are externally tagged Rust enums whose wire shape is *either* a bare
string *or* a single-key object (`AskForApproval`, `CodexErrorInfo`, `ReviewDecision`,
`CommandExecutionApprovalDecision`, `SessionSource`, `SubAgentSource`, `MultiAgentMode`). Each
generates as a sealed interface, so a `when` over it is checked by the compiler:

```kotlin
when (val policy = decision) {
    is AskForApproval.Preset -> policy.value          // AskForApprovalPreset.NEVER, …
    is AskForApproval.Granular -> policy.granular     // typed payload
    is AskForApproval.Unknown -> policy.raw           // added upstream; kept verbatim
}
```

The string presets stay available as companion constants typed as the union
(`AskForApproval.ON_REQUEST`, `ReviewDecision.APPROVED`), and `of("…")` wraps a preset by name.
Encoding reproduces the exact wire shape — a preset goes out as a bare string, never as an
object — which matters because the approval policy and the decisions are sent, not just
received. A preset or object key this SDK version does not model decodes to `Unknown` and
encodes back unchanged instead of failing.

## License

Apache 2.0. Not an official OpenAI project.

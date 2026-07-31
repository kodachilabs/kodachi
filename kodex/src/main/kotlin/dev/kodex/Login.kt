package dev.kodex

import dev.kodex.protocol.AccountLoginCompletedNotification
import dev.kodex.protocol.ApiKeyv2LoginAccountParams
import dev.kodex.protocol.CancelLoginAccountParams
import dev.kodex.protocol.CancelLoginAccountResponse
import dev.kodex.protocol.ChatgptDeviceCodev2LoginAccountParams
import dev.kodex.protocol.ChatgptDeviceCodev2LoginAccountResponse
import dev.kodex.protocol.Chatgptv2LoginAccountParams
import dev.kodex.protocol.Chatgptv2LoginAccountResponse
import dev.kodex.protocol.LoginAccountParams
import dev.kodex.protocol.LoginAccountResponse
import dev.kodex.protocol.LoginAppBrand
import dev.kodex.protocol.UnknownLoginAccountResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * An interactive login in progress.
 *
 * The user has to do something out-of-band — open [authUrl] in a browser, or type
 * [userCode] into [verificationUrl] — and the server reports the outcome later as an
 * `account/login/completed` notification. This handle carries what the user needs to see,
 * waits for that notification, and can abandon the flow.
 *
 * ```kotlin
 * val login = codex.loginWithChatgpt()
 * println("Finish signing in at ${login.authUrl}")
 * val result = try {
 *     login.await(timeoutMillis = 5 * 60_000)
 * } catch (timeout: Exception) {
 *     login.cancel()
 *     throw timeout
 * }
 * check(result.success) { result.error ?: "login failed" }
 * ```
 *
 * A handle subscribes to [Codex.events] from before `account/login/start` is even sent,
 * so a login that completes faster than the start response comes back is still observed.
 * That subscription is released when the completion arrives or when [cancel] is called —
 * a handle that is neither awaited nor cancelled holds one idle coroutine for as long as
 * the [Codex] instance lives.
 *
 * Residual risk worth knowing about: [Codex.events] is a lossy tap that drops the oldest
 * buffered event under sustained back-pressure. A completion can therefore still be
 * missed if hundreds of unrelated notifications arrive faster than this handle drains
 * them, which in practice means only while turns are running against the same instance.
 * Pass a timeout to [await] if you need a bound in that case.
 */
public class LoginHandle internal constructor(
    private val codex: Codex,
    /** Server-assigned id for this flow. Completions are matched against it. */
    public val loginId: String,
    /** The `account/login/start` response, for fields these accessors do not name. */
    public val startResponse: LoginAccountResponse,
    private val scope: CoroutineScope,
    private val completion: Deferred<AccountLoginCompletedNotification>,
) {

    /** Browser URL for the ChatGPT flow; null for the device-code flow. */
    public val authUrl: String?
        get() = (startResponse as? Chatgptv2LoginAccountResponse)?.authUrl

    /** Code the user types on [verificationUrl]; null outside the device-code flow. */
    public val userCode: String?
        get() = (startResponse as? ChatgptDeviceCodev2LoginAccountResponse)?.userCode

    /** Page where the user enters [userCode]; null outside the device-code flow. */
    public val verificationUrl: String?
        get() = (startResponse as? ChatgptDeviceCodev2LoginAccountResponse)?.verificationUrl

    /**
     * Wait for the server's verdict on this login.
     *
     * A returned notification is not a success: the flow can end with
     * `success = false` and an `error` message, so check both.
     *
     * Nothing bounds the wait by default — if the user walks away, or the app-server dies
     * without reporting anything, this suspends indefinitely. [timeoutMillis] guards
     * against that and throws `kotlinx.coroutines.TimeoutCancellationException`; the flow
     * itself is untouched by a timeout, so a later [await] on the same handle still
     * resolves if the user eventually finishes. Call [cancel] to actually give up.
     *
     * Throws `kotlinx.coroutines.CancellationException` if [cancel] already ran.
     */
    public suspend fun await(timeoutMillis: Long? = null): AccountLoginCompletedNotification =
        if (timeoutMillis == null) completion.await() else withTimeout(timeoutMillis) { completion.await() }

    /**
     * Abandon the flow: tell the server to forget it, and stop listening.
     *
     * [CancelLoginAccountResponse.status] distinguishes a flow the server dropped from one
     * it had already forgotten. Any [await] still suspended fails with
     * `kotlinx.coroutines.CancellationException`, and a completion the server may still
     * emit for this login is ignored — so cancel only once you have given up on it.
     */
    public suspend fun cancel(): CancelLoginAccountResponse =
        try {
            codex.account.loginCancel(CancelLoginAccountParams(loginId = loginId))
        } finally {
            scope.cancel("login $loginId was cancelled")
        }

    override fun toString(): String = "LoginHandle(loginId=$loginId, type=${startResponse.type})"
}

/**
 * Start the browser-based ChatGPT login and return once the URL to visit is known.
 *
 * @param appBrand which product the consent screen presents itself as
 * @param codexStreamlinedLogin opt into the shortened consent flow
 * @param useHostedLoginSuccessPage land the user on an OpenAI-hosted success page rather
 *   than the local one served by the app-server
 */
public suspend fun Codex.loginWithChatgpt(
    appBrand: LoginAppBrand? = null,
    codexStreamlinedLogin: Boolean? = null,
    useHostedLoginSuccessPage: Boolean? = null,
): LoginHandle = startLogin(
    Chatgptv2LoginAccountParams(
        appBrand = appBrand,
        codexStreamlinedLogin = codexStreamlinedLogin,
        useHostedLoginSuccessPage = useHostedLoginSuccessPage,
    ),
)

/**
 * Start the device-code login, for machines with no browser of their own.
 *
 * The user enters [LoginHandle.userCode] at [LoginHandle.verificationUrl] on another
 * device; the server polls and reports the outcome to [LoginHandle.await].
 */
public suspend fun Codex.loginWithDeviceCode(): LoginHandle =
    startLogin(ChatgptDeviceCodev2LoginAccountParams())

/**
 * Log in with an API key.
 *
 * There is no handle here because there is nothing to wait for: the key is credential
 * enough, so the request either stores it or fails. The response carries no fields today
 * and is returned only so a future one is not swallowed.
 */
public suspend fun Codex.loginWithApiKey(apiKey: String): LoginAccountResponse =
    account.loginStart(ApiKeyv2LoginAccountParams(apiKey = apiKey))

/**
 * Subscribe first, then start — the ordering that makes [LoginHandle.await] reliable.
 *
 * `account/login/completed` can be emitted before `account/login/start` has returned, and
 * [Codex.events] has no replay, so collecting after the start response would race a fast
 * login. Issuing the start request from inside `onSubscription` fixes the order:
 * kotlinx guarantees the subscription is registered before that block runs, so anything
 * emitted while the request is in flight is buffered for this collector.
 */
private suspend fun Codex.startLogin(params: LoginAccountParams): LoginHandle {
    // Handshake out here, so the window between subscribing and the start response is one
    // round trip rather than two.
    initialize()

    // Dispatchers.IO because sending the request writes to the child's stdin, which blocks.
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineName("codex-login"))
    val started = CompletableDeferred<LoginAccountResponse>()

    // async, not launch: a failed start belongs to the caller awaiting `started`, not to
    // an uncaught exception handler.
    val completion = scope.async {
        events
            .onSubscription {
                try {
                    started.complete(account.loginStart(params))
                } catch (failure: Throwable) {
                    started.completeExceptionally(failure)
                    throw failure
                }
            }
            .filterIsInstance<AccountLoginCompletedNotification>()
            .first { notification ->
                // `started` is always complete here: onSubscription runs to completion
                // before the first event reaches this predicate.
                val expected = started.await().loginIdOrNull()
                // An unattributed completion is accepted rather than ignored — the server
                // omits the id only when it cannot tell which flow finished, and treating
                // it as someone else's would suspend await() forever. With two logins in
                // flight at once, such an event does resolve both.
                notification.loginId == null || notification.loginId == expected
            }
    }

    val response = try {
        started.await()
    } catch (failure: Throwable) {
        // Covers the caller being cancelled mid-start too; either way nothing is left
        // collecting.
        scope.cancel("account/login/start did not complete")
        throw failure
    }

    val loginId = response.loginIdOrNull()
        ?: run {
            scope.cancel("no login id to wait on")
            throw CodexException(
                "account/login/start returned a '${response.type}' response with no loginId; " +
                    "that flow has no completion to wait for",
            )
        }

    return LoginHandle(
        codex = this,
        loginId = loginId,
        startResponse = response,
        scope = scope,
        completion = completion,
    )
}

/**
 * The flow id, from whichever variant carries one.
 *
 * The unknown-variant branch is what keeps a login flow added upstream working here
 * without an SDK release, as long as its response still names the field `loginId`.
 */
private fun LoginAccountResponse.loginIdOrNull(): String? = when (this) {
    is Chatgptv2LoginAccountResponse -> loginId
    is ChatgptDeviceCodev2LoginAccountResponse -> loginId
    is UnknownLoginAccountResponse -> (raw["loginId"] as? JsonPrimitive)?.contentOrNull
    else -> null
}

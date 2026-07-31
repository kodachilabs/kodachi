plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

subprojects {
    // Maven Central verifies namespace ownership. `dev.kodex` requires a DNS TXT record on
    // kodex.dev; `io.github.saadaziz9956` is verified just by signing in with GitHub. Override
    // without touching this file:  ./gradlew -PmavenGroup=io.github.saadaziz9956 …
    group = providers.gradleProperty("mavenGroup").getOrElse("dev.kodex")
    version = providers.gradleProperty("mavenVersion").getOrElse("0.1.0")
}

// ---------------------------------------------------------------------------
// Protocol codegen
//
// The generated protocol layer is committed, not built on the fly: a build whose output
// depends on whichever `codex` happens to be installed would not be reproducible, and an
// upstream breaking change would surface as a mysterious compile error instead of a
// reviewable diff. So regeneration is explicit, and drift is something you check for.
// ---------------------------------------------------------------------------

val codexBin: String = System.getenv("CODEX_BIN") ?: "codex"

/** Fails (exit 1) when the committed protocol layer no longer matches the local binary. */
val checkProtocolDrift by tasks.registering(Exec::class) {
    group = "verification"
    description = "Check the committed protocol layer against the installed codex binary."
    commandLine("python3", "scripts/generate_protocol.py", "--check")
    environment("CODEX_BIN", codexBin)
    workingDir = rootDir
}

/** Rewrites the generated protocol layer from the installed binary's schema. */
val regenerateProtocol by tasks.registering(Exec::class) {
    group = "build"
    description = "Regenerate the protocol layer from the installed codex binary's schema."
    commandLine("python3", "scripts/generate_protocol.py")
    environment("CODEX_BIN", codexBin)
    workingDir = rootDir
    doLast {
        logger.lifecycle("Protocol regenerated. Run `./gradlew build` — the compiler and the")
        logger.lifecycle("parity tests are the review for whatever upstream changed.")
    }
}

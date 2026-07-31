plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    `maven-publish`
    signing
}

dependencies {
    api(libs.coroutines.core)
    api(libs.serialization.json)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
}

kotlin {
    jvmToolchain(21)
    // This is a library: every public declaration states its visibility and return type.
    explicitApi()
}

java {
    withSourcesJar()
}

/**
 * Maven Central rejects a publication without a javadoc jar. Kotlin has no javadoc, and Dokka is
 * a heavier dependency than this build needs, so publish an empty one — the accepted convention
 * for Kotlin libraries. Sources are what an IDE actually reads.
 */
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(layout.buildDirectory.dir("emptyJavadoc"))
    doFirst { layout.buildDirectory.dir("emptyJavadoc").get().asFile.mkdirs() }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "kodex"
            from(components["java"])
            artifact(javadocJar)

            // Central validates every one of these; a missing scm or developers block is a
            // rejected upload, not a warning.
            pom {
                name.set("kodex")
                description.set(
                    "Kotlin SDK for OpenAI Codex — a coroutines-first, typed JSON-RPC client " +
                        "for the codex app-server protocol.",
                )
                url.set("https://github.com/SaadAziz9956/kodex")
                inceptionYear.set("2026")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("SaadAziz9956")
                        name.set("Saad Aziz")
                        url.set("https://github.com/SaadAziz9956")
                    }
                }
                scm {
                    url.set("https://github.com/SaadAziz9956/kodex")
                    connection.set("scm:git:https://github.com/SaadAziz9956/kodex.git")
                    developerConnection.set("scm:git:ssh://git@github.com/SaadAziz9956/kodex.git")
                }
            }
        }
    }
}

/**
 * Signing is required by Central and irrelevant locally, so it only switches on when the key is
 * actually present. Without this guard every `publishToMavenLocal` on a machine with no GPG key
 * would fail.
 */
signing {
    val signingKey: String? = providers.gradleProperty("signingInMemoryKey").orNull
        ?: System.getenv("SIGNING_KEY")
    val signingPassword: String? = providers.gradleProperty("signingInMemoryKeyPassword").orNull
        ?: System.getenv("SIGNING_PASSWORD")

    isRequired = signingKey != null
    if (signingKey != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
    }
}

tasks.test {
    useJUnitPlatform()
    // Integration tests spawn the real codex binary; opt in with -DcodexIntegration=true.
    systemProperty("codexIntegration", System.getProperty("codexIntegration") ?: "false")
    // The throughput harness is opt-in too: -DcodexBench=true
    systemProperty("codexBench", System.getProperty("codexBench") ?: "false")
    if (System.getProperty("codexBench") == "true") {
        // Benchmarks need room to run and their output is the point.
        maxHeapSize = "2g"
        testLogging.showStandardStreams = true
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

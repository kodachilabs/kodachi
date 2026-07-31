import java.io.File

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

/**
 * Where `publishToCentralStaging` assembles the deployment in Maven repository layout.
 * `centralBundle` zips exactly this, which is what the Portal expects.
 */
val stagingDir = layout.buildDirectory.dir("central-staging")

publishing {
    repositories {
        // A file repository, not a remote one: the Central Portal takes an uploaded bundle
        // rather than a Maven deploy, and staging locally means the exact bytes Central will
        // validate can be inspected before anything leaves the machine.
        maven {
            name = "centralStaging"
            url = uri(stagingDir)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "kodachi"
            from(components["java"])
            artifact(javadocJar)

            // Central validates every one of these; a missing scm or developers block is a
            // rejected upload, not a warning.
            pom {
                name.set("kodachi")
                description.set(
                    "Kotlin SDK for OpenAI Codex — a coroutines-first, typed JSON-RPC client " +
                        "for the codex app-server protocol.",
                )
                url.set("https://github.com/SaadAziz9956/kodachi")
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
                    url.set("https://github.com/SaadAziz9956/kodachi")
                    connection.set("scm:git:https://github.com/SaadAziz9956/kodachi.git")
                    developerConnection.set("scm:git:ssh://git@github.com/SaadAziz9956/kodachi.git")
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

/**
 * The artifact to upload to https://central.sonatype.com — a zip whose root is the group path,
 * containing the jars, POM, signatures and checksums for one version.
 *
 * Central rejects a bundle missing a `.asc` for any artifact, so this fails early rather than
 * letting the upload be rejected minutes later.
 */
val centralBundle by tasks.registering(Zip::class) {
    group = "publishing"
    description = "Build the zip to upload to the Maven Central Portal."
    dependsOn(tasks.named("publishAllPublicationsToCentralStagingRepository"))

    from(stagingDir) {
        // Maven-metadata is for a deploy, not a bundle; Central rejects its presence.
        exclude("**/maven-metadata*.*")
    }
    archiveFileName.set("kodachi-${project.version}-central-bundle.zip")
    destinationDirectory.set(layout.buildDirectory.dir("central"))

    doFirst {
        val dir = stagingDir.get().asFile
        val artifacts = dir.walkTopDown()
            .filter { it.isFile && !it.name.startsWith("maven-metadata") }
            .filter { it.extension !in setOf("asc", "md5", "sha1", "sha256", "sha512") }
            .toList()
        require(artifacts.isNotEmpty()) { "nothing staged in $dir" }

        val unsigned = artifacts.filterNot { File("${it.path}.asc").exists() }
        require(unsigned.isEmpty()) {
            "Central requires a GPG signature for every artifact; unsigned:\n" +
                unsigned.joinToString("\n") { "  ${it.name}" } +
                "\n\nSet signingInMemoryKey / signingInMemoryKeyPassword (or SIGNING_KEY / " +
                "SIGNING_PASSWORD) and rerun."
        }
    }

    doLast {
        logger.lifecycle("")
        logger.lifecycle("Bundle: ${archiveFile.get().asFile.absolutePath}")
        logger.lifecycle("Upload it with the command in README > Publishing.")
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

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "kodex"
            from(components["java"])

            pom {
                name.set("Codex Kotlin SDK")
                description.set("Kotlin client for the OpenAI Codex app-server protocol")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
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

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kodachi"))
}

kotlin {
    jvmToolchain(21)
}

application {
    // Pick a sample with -Psample=dev.kodachi.samples.StreamingKt
    mainClass.set(
        providers.gradleProperty("sample").orElse("dev.kodachi.samples.quickstart.QuickstartKt"),
    )
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    // Sample args: ./gradlew :samples:run --args="/path/to/repo"
}

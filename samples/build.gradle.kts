plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kodex"))
}

kotlin {
    jvmToolchain(21)
}

application {
    // Pick a sample with -Psample=dev.kodex.samples.StreamingKt
    mainClass.set(
        providers.gradleProperty("sample").orElse("dev.kodex.samples.quickstart.QuickstartKt"),
    )
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    // Sample args: ./gradlew :samples:run --args="/path/to/repo"
}

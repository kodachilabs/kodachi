plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kodex"))
}

kotlin { jvmToolchain(21) }

application {
    applicationName = "kodex"
    mainClass.set("dev.kodex.cli.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

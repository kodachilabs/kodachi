plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

dependencies {
    implementation(project(":kodachi"))
}

kotlin { jvmToolchain(21) }

application {
    applicationName = "kodachi"
    mainClass.set("dev.kodachi.cli.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

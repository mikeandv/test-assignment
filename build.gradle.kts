plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
}

group = "com.github.mikeandv"
version = "1.0-SNAPSHOT"

val junitVersion = "5.9.1"
val ktorVersion = "2.3.7"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$junitVersion")
    testImplementation("io.kotest:kotest-assertions-core:$junitVersion")
    testImplementation("io.kotest:kotest-framework-engine:$junitVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
    reports {
        junitXml.required.set(true)
        html.required.set(true)
    }
}
kotlin {
    jvmToolchain(24)
}
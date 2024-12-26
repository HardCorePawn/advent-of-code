plugins {
    kotlin("jvm") version "2.1.0"
}

tasks {
    wrapper {
        gradleVersion = "8.11.1"
    }
}

val ktor_version: String by project

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
}
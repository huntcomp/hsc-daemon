object Versions {
    const val SLF4J = "2.0.6"
    const val KOTLINX = "1.6.4"
    const val KOTLINX_SERIALIZATION_JSON = "1.4.1"
    const val KOTLIN_LOGGING = "3.0.5"
    const val SUPABASE = "0.8.0-alpha-1"
    const val KTOR = "2.2.3"
    const val KOTEST = "5.5.4"
}

plugins {

    kotlin("jvm") version "1.8.10"
    application
}

group = "app.hsc"
version = "0.6"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.kotest:kotest-assertions-core:${Versions.KOTEST}")
    implementation("io.kotest:kotest-framework-engine:${Versions.KOTEST}")
    implementation("io.kotest:kotest-property:${Versions.KOTEST}")
    implementation(kotlin("test-common"))
    implementation(kotlin("test-annotations-common"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt:${Versions.SUPABASE}")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:${Versions.SUPABASE}")
    implementation("io.github.jan-tennert.supabase:functions-kt:${Versions.SUPABASE}")
    implementation("io.ktor:ktor-client-core:${Versions.KTOR}")
    implementation("io.ktor:ktor-client-cio:${Versions.KTOR}")
    implementation("io.ktor:ktor-client-apache:${Versions.KTOR}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLINX_SERIALIZATION_JSON}")
    implementation("io.github.microutils:kotlin-logging:${Versions.KOTLIN_LOGGING}")
    implementation("org.slf4j:slf4j-simple:${Versions.SLF4J}")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0")
    implementation("com.github.vishna:watchservice-ktx:master-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLINX}")
    implementation("org.awaitility:awaitility:4.2.0")
    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
    testImplementation(kotlin("test"))
}

tasks.jar {
    manifest.attributes["Main-Class"] = "app.hsc.Main"
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = false
    }
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

kotlin {
    jvmToolchain(11)

    application {
        mainClass.set("hsc.app.Main")
    }

}
buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

// build.gradle.kts
object Versions {
    const val KOTEST = "5.5.4"
}

buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}


plugins {
    kotlin("multiplatform") version "1.8.0"
    id("io.kotest.multiplatform") version "5.5.4"
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }


}

kotlin {
    targets {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }
        linuxX64() {
            binaries {
                executable()
            }
        }
    }


    targets.all {
        compilations.all {
            kotlinOptions {
                verbose = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.kotest:kotest-assertions-core:${Versions.KOTEST}")
                implementation("io.kotest:kotest-framework-engine:${Versions.KOTEST}")
                implementation("io.kotest:kotest-property:${Versions.KOTEST}")
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }

            val jvmTest by getting {
                dependencies {
                    implementation("io.kotest:kotest-runner-junit5-jvm:${Versions.KOTEST}")
                }

            }
        }
    }



}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        apiVersion = "1.5"
    }
}


tasks.withType<Wrapper> {
    gradleVersion = "7.6"
    distributionType = Wrapper.DistributionType.BIN
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

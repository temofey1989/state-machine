import org.gradle.api.JavaVersion.VERSION_21
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinter)
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(libs.bundles.kotlin.coroutines)
    testImplementation(libs.bundles.testing)
}

java.sourceCompatibility = VERSION_21

tasks {
    formatKotlin {
    }

    lintKotlin {
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JVM_21
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
            )
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events(PASSED, FAILED, SKIPPED)
        }
    }
}

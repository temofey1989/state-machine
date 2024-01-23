import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import java.time.Duration

plugins {
    kotlin("jvm") version "1.9.20"
    `java-library`
    `maven-publish`
    signing
    jacoco
    id("org.sonarqube") version "4.4.1.3373"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

val kotestVersion: String by project
val mockkVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")

    testImplementation("io.mockk:mockk:$mockkVersion")
}

java.sourceCompatibility = JavaVersion.VERSION_17

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
                "-Xcontext-receivers",
            )
            jvmTarget = java.sourceCompatibility.toString()
        }
    }

    test {
        useJUnitPlatform()
        testLogging {
            events(PASSED, FAILED, SKIPPED)
        }
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    withType<Sign> {
        onlyIf { !project.version.toString().endsWith("SNAPSHOT") }
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "temofey1989_state-machine")
        property("sonar.organization", "temofey1989")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("${project.group}:${project.name}")
                description.set("Simple state machine library with Kotlin DSL.")
                url.set("https://github.com/temofey1989/state-machine")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("temofey1989")
                        name.set("Artyom Gornostayev")
                        email.set("temofey1989@gmail.com")
                    }
                }
                scm {
                    url.set("https://github.com/temofey1989/state-machine")
                    connection.set("scm:git:git://github.com/temofey1989/state-machine.git")
                    developerConnection.set("scm:git:ssh://github.com:temofey1989/state-machine.git")
                }
            }
            suppressPomMetadataWarningsFor("runtimeElements")
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

nexusPublishing {
    transitionCheckOptions {
        maxRetries.set(100)
        delayBetween.set(Duration.ofSeconds(5))
    }
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

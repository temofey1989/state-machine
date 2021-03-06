import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    `java-library`
    `maven-publish`
    signing
    jacoco
    id("org.sonarqube") version "3.3"
}

group = "io.justdevit.libs"
version = "0.1.0"

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_16

dependencies {
    implementation(kotlin("stdlib"))

    testCompileOnly("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")

    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("io.mockk:mockk:1.12.1")

}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
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
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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
    repositories {
        maven {
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = properties["ossrhUsername"] as String?
                password = properties["ossrhPassword"] as String?
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

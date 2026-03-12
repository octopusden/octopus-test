import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("com.bmuschko:gradle-docker-plugin:6.7.0")
    }
}

plugins {
    `maven-publish`
    `java-library`
    `signing`
    `jacoco`
    id("io.github.gradle-nexus.publish-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.bmuschko.docker-spring-boot-application") version "9.4.0"
}

group = "org.octopusden"

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
    withJavadocJar()
    withSourcesJar()
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(System.getenv("MAVEN_USERNAME"))
            password.set(System.getenv("MAVEN_PASSWORD"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named("bootJar"))
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set(project.name)
                description.set("Octopus module for testing Maven release workflow")
                url.set("https://github.com/octopusden/octopus-test.git")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    url.set("https://github.com/octopusden/octopus-test.git")
                    connection.set("scm:git://github.com/octopusden/octopus-test.git")
                }
                developers {
                    developer {
                        id.set("octopus")
                        name.set("octopus")
                    }
                }
            }
        }
    }
}

if (!project.version.toString().endsWith("SNAPSHOT", true)) {
    signing {
        sign(publishing.publications["mavenJava"])
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(
            signingKey,
            signingPassword
        )
    }
}

repositories {
    mavenCentral()
}

val dockerRegistry = System.getenv().getOrDefault("DOCKER_REGISTRY", project.properties["docker.registry"]) as? String
val octopusGithubDockerRegistry = System.getenv().getOrDefault("OCTOPUS_GITHUB_DOCKER_REGISTRY", project.properties["octopus.github.docker.registry"]) as? String
val authServerUrl = System.getenv().getOrDefault("AUTH_SERVER_URL", project.properties["auth-server.url"]) as? String
val authServerRealm = System.getenv().getOrDefault("AUTH_SERVER_REALM", project.properties["auth-server.realm"]) as? String

docker {
    springBootApplication {
        baseImage.set("$dockerRegistry/eclipse-temurin:21")
        ports.set(listOf(8080, 8080))
        images.set(setOf("$octopusGithubDockerRegistry/octopusden/${project.name}:${project.version}"))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation(kotlin("stdlib"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test-junit5"))
}

tasks {
    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport) // report is always generated after tests run
    }
    jacocoTestReport {
        reports {
            xml.required.set(true)
            xml.outputLocation.set(file("${buildDir}/reports/jacoco/report.xml"))
            html.required.set(true)
            csv.required.set(true)
        }
        dependsOn(test) // tests are required to run before generating the report
    }
    jacocoTestCoverageVerification {
        dependsOn(test)
        violationRules {
            rule {
                element = "BUNDLE"
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.75".toBigDecimal()
                }
            }
        }
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        exclude("org/octopus/app/OctopusApplication*")
                    }
                }
            )
        )
    }

    register("qualityStatic") {
        group = "verification"
        description = "Runs compile-time quality checks for CI quality gate"
        dependsOn("classes", "testClasses")
    }

    register("qualityCoverage") {
        group = "verification"
        description = "Runs unit tests and coverage report generation for CI quality gate"
        dependsOn("test", "jacocoTestReport", "jacocoTestCoverageVerification")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "21"
}

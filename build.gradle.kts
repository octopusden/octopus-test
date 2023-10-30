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
    id("org.springframework.boot") version "2.7.0"
    id("com.bmuschko.docker-spring-boot-application") version "9.3.6"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "org.octopusden"

java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
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
        baseImage.set("$dockerRegistry/openjdk:11")
        ports.set(listOf(8080, 8080))
        images.set(setOf("$octopusGithubDockerRegistry/octopusden/${project.name}:${project.version}"))
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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
}

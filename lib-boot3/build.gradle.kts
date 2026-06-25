plugins {
    `java-library`
    `maven-publish`
    signing
}

version = "1.0.0"

val springVersion by extra("6.2.0")
val mongoClientVersion by extra("5.5.2")
val isReleaseVersion by extra(!version.toString().endsWith("SNAPSHOT"))

sourceSets {
    named("main") { java.setSrcDirs(listOf(rootProject.file("shared/src/main/java"))) }
    named("test") { java.setSrcDirs(listOf(rootProject.file("shared/src/test/java"))) }
}

dependencies {
    compileOnly("org.springframework:spring-test:${springVersion}")
    compileOnly("org.springframework:spring-context:${springVersion}")

    compileOnly("org.mongodb:bson:${mongoClientVersion}")
    compileOnly("org.mongodb:mongodb-driver-core:${mongoClientVersion}")
    compileOnly("org.mongodb:mongodb-driver-sync:${mongoClientVersion}")

    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.3")
}

configurations {
    testImplementation {
        extendsFrom(compileOnly.get())
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    isFailOnError = false
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Implementation-Title" to "${rootProject.name}-boot3",
            "Implementation-Version" to project.version
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("library") {
            groupId = rootProject.group.toString()
            artifactId = "${rootProject.name}-boot3"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set("Spring MongoDB Test (Spring Boot 3)")
                description.set("A TestExecutionListener for simplifying the database seeding in integration tests for Spring and MongoDB")
                url.set("https://github.com/Wholegrain-Software/spring-mongodb-test")
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("DevAtHeart")
                        name.set("Jimi Steidl")
                        email.set("jimi.steidl@wholegrain-software.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Wholegrain-Software/spring-mongodb-test.git")
                    developerConnection.set("scm:git:ssh://github.com/Wholegrain-Software/spring-mongodb-test.git")
                    url.set("https://github.com/Wholegrain-Software/spring-mongodb-test")
                }
            }
        }
    }

    repositories {
        maven {
            name = "local"
            url = uri("$buildDir/repos/release")
        }

        maven {
            name = "gitlab"
            url = uri("https://gitlab.com/api/v4/projects/25683146/packages/maven")
            credentials(HttpHeaderCredentials::class) {
                name = properties["gitlabRepositoryUser"].toString()
                value = properties["gitlabRepositoryPassword"].toString()
            }
            authentication { create<HttpHeaderAuthentication>("header") }
        }

        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"

            name = "mavenCentral"
            url = uri(if (isReleaseVersion) releasesRepoUrl else snapshotsRepoUrl)
            credentials {
                username = properties["mavenCentralUser"].toString()
                password = properties["mavenCentralPassword"].toString()
            }
        }
    }
}

signing {
    setRequired { isReleaseVersion }
    sign(publishing.publications["library"])
}

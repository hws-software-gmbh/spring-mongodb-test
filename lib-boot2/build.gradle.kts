plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.37.0"
}

version = "1.0.0"

val springVersion by extra("5.3.4")
val mongoClientVersion by extra("4.1.1")

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

tasks.withType<Javadoc> {
    isFailOnError = false
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Implementation-Title" to "${rootProject.name}-boot2",
            "Implementation-Version" to project.version
        )
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    signAllPublications()

    coordinates(rootProject.group.toString(), "${rootProject.name}-boot2", version.toString())

    pom {
        name.set("Spring MongoDB Test (Spring Boot 2)")
        description.set("A TestExecutionListener for simplifying the database seeding in integration tests for Spring and MongoDB")
        url.set("https://github.com/hws-software-gmbh/spring-mongodb-test")
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
                email.set("jimi.steidl@hws-software.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/hws-software-gmbh/spring-mongodb-test.git")
            developerConnection.set("scm:git:ssh://github.com/hws-software-gmbh/spring-mongodb-test.git")
            url.set("https://github.com/hws-software-gmbh/spring-mongodb-test")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repos/release"))
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
    }
}

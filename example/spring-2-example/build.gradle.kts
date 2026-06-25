plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation(project(":lib-boot2"))
    testImplementation("org.testcontainers:mongodb:1.21.4")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage")
        exclude(group = "org.junit", module = "junit")
    }
}

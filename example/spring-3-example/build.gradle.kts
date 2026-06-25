plugins {
    id("org.springframework.boot") version "3.5.15"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation(project(":lib-boot3"))
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage")
        exclude(group = "org.junit", module = "junit")
    }
}

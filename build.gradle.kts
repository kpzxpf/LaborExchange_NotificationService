plugins {
    java
    id("org.springframework.boot") version "3.3.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.vlz"
version = "0.0.1-SNAPSHOT"
description = "LaborExchange_NotificationService"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
    implementation("com.github.loki4j:loki-logback-appender:1.5.0")
    implementation("org.springframework.boot:spring-boot-starter-mail")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

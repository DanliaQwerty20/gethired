plugins {
    java
    id("org.springframework.boot") version "3.3.9"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "by.system"
version = "0.0.1-SNAPSHOT"
description = "gethired"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:0.8.1")
    }
}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring AI
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter")

    // OpenAPI documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Telegram Bot
    implementation("org.telegram:telegrambots:6.8.0")

    // Document processing
    implementation("org.apache.pdfbox:pdfbox:3.0.3")
    implementation("org.apache.poi:poi-ooxml:5.3.0")

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    // Utilities
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.liquibase:liquibase-core")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
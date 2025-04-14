plugins {
    java
    antlr
    id("maven-publish")
}

group = "dev.gruen"
version = System.getenv("VERSION") ?: "0.0.1-SNAPSHOT"
description = "Simple QueryCompiler for Java"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.2")
    implementation("org.antlr:antlr4-runtime:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}


tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
}

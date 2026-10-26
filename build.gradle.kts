plugins {
    `java-library`
    antlr
    jacoco
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "dev.gruen"
version = System.getenv("VERSION") ?: "0.0.1-SNAPSHOT"
description = "Query compiler: compiles filter query strings into any target object"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.2")
    api("org.antlr:antlr4-runtime:4.13.2")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

configurations.api {
    setExtendsFrom(extendsFrom.filter { it.name != "antlr" })
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
}

tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) { exclude("dev/gruen/query/generated/**") }
    }))
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-long-messages")
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)

    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }

    coordinates(project.group.toString(), "query-compiler", project.version.toString())

    pom {
        name = "query-compiler"
        description = project.description
        url = "https://github.com/nRo/query-compiler"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "alexgruen"
                name = "Alexander Grün"
                email = "alex@gruen.dev"
            }
        }
        scm {
            url = "https://github.com/nRo/query-compiler"
            connection = "scm:git:https://github.com/nRo/query-compiler.git"
            developerConnection = "scm:git:ssh://git@github.com/nRo/query-compiler.git"
        }
    }
}

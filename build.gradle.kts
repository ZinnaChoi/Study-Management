import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.plugins.BasePlugin
import org.gradle.kotlin.dsl.register
import java.util.*

plugins {
    kotlin("jvm") version "1.5.21"
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "antonic"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val frontendDir = "$projectDir/src/main/frontend"

sourceSets {
    main {
        resources {
            srcDirs = setOf("$projectDir/src/main/resources")
        }
    }
}

tasks {
    "processResources" {
        dependsOn("copyReactBuildFiles")
    }

    register<Exec>("installReact") {
        workingDir(frontendDir)
        inputs.dir(frontendDir)
        group = BasePlugin.BUILD_GROUP
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
            commandLine("npm.cmd", "audit", "fix")
            commandLine("npm.cmd", "install")
        } else {
            commandLine("npm", "audit", "fix")
            commandLine("npm", "install")
        }
    }

    register<Exec>("buildReact") {
        dependsOn("installReact")
        workingDir(frontendDir)
        inputs.dir(frontendDir)
        group = BasePlugin.BUILD_GROUP
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
            commandLine("npm.cmd", "run-script", "build")
        } else {
            commandLine("npm", "run-script", "build")
        }
    }

    register<Copy>("copyReactBuildFiles") {
        dependsOn("buildReact")
        from("$frontendDir/build")
        into("$projectDir/src/main/resources/static")
    }
}

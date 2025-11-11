plugins {
    id("maven-publish")
    id("io.freefair.lombok")
    id("java-library")
    id("checkstyle")
}

project.version = "1.2.0"

repositories {
    mavenCentral()
}

checkstyle {
    toolVersion = "12.1.1"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.1")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    api("ch.qos.logback:logback-classic:1.3.0-alpha5")
    api("org.jetbrains:annotations:19.0.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
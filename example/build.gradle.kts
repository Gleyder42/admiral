plugins {
    id("io.freefair.lombok")
    id("java-library")
    id("checkstyle")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
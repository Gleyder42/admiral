plugins {
    id("io.freefair.lombok")
    `java-library`
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
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
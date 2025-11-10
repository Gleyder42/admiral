plugins {
    id("maven-publish")
    id("io.freefair.lombok")
    id("java-library")
    id("checkstyle")
}

project.version = "1.3.0"

repositories {
    mavenCentral()
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

group = "de.gleyder.admiral"

dependencies {
    implementation(project(":core"))
    implementation("net.bytebuddy:byte-buddy:1.10.20")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.gleyder.admiral"
            artifactId = project.name
            version = "2.0.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
plugins {
    `maven-publish`
    id("io.freefair.lombok")
    `java-library`
    checkstyle
}

project.version = "2.0.0"

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

group = "de.gleyder.admiral"

dependencies {
    implementation(project(":core"))
    implementation("net.bytebuddy:byte-buddy:1.17.8")
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
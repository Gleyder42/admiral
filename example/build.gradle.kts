plugins {
    id("io.freefair.lombok")
    `java-library`
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
}

application {
    mainClass = "main"
    applicationDefaultJvmArgs = listOf("--enable-preview")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")

}
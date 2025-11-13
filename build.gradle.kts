plugins {
    id("io.freefair.lombok") version "9.1.0" apply false
}

repositories {
    mavenCentral()
}

tasks.register<Copy>("copyHooks") {
    doLast {
        from(file("./hooks/"))
        into(file("./.git/hooks/"))
    }
}

tasks.register("compileJava") {
    dependsOn("copyHooks")
}
plugins {
    application
    kotlin("jvm") version "2.2.20"
}

group = "io.antlerscript"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
application {
    mainClass.set("io.antlerscript.MainKt")
}
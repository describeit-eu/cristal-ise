// Root project build file
plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

allprojects {
    group = "eu.describeit"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }
}

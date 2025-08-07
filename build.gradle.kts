import org.gradle.api.JavaVersion.VERSION_21

// Root project build file
plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2" apply false
  id("io.freefair.lombok") version "8.14" apply false
}

val vertxVersion   = "5.0.2"
val junitVersion   = "5.13.4"
val jacksonVersion = "2.19.2"

allprojects {
  group = "eu.describeit"
    version = "1.0.0-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "java")

  dependencies {
    add("implementation", platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    add("implementation", platform("com.fasterxml.jackson:jackson-bom:$jacksonVersion"))

    add("testImplementation", platform("org.junit:junit-bom:$junitVersion"))
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = VERSION_21.toString()
    targetCompatibility = VERSION_21.toString()
  }
}

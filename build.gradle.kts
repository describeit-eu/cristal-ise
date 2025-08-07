import org.gradle.api.JavaVersion.VERSION_21

// Root project build file
plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

val vertxVersion  = "5.0.2"
val junitVersion  = "5.13.4"

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

    add("testImplementation", platform("org.junit:junit-bom:$junitVersion"))
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = VERSION_21.toString()
    targetCompatibility = VERSION_21.toString()
  }
}

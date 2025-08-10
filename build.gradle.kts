import org.gradle.api.JavaVersion.VERSION_21

// Root project build file
allprojects {
  group = "eu.describeit"
    version = "1.0.0-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}

subprojects {
  tasks.withType<JavaCompile> {
    sourceCompatibility = VERSION_21.toString()
    targetCompatibility = VERSION_21.toString()
  }
}

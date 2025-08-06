// Root project build file
plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

val vertxVersion  = "5.0.2"
val junitVersion  = "5.13.4"
val groovyVersion = "4.0.28"

allprojects {
  group = "eu.describeit"
    version = "1.0.0-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "groovy")

  dependencies {
    add("implementation", platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    add("implementation", platform("org.apache.groovy:groovy-bom:$groovyVersion"))

    add("testImplementation", platform("org.junit:junit-bom:$junitVersion"))
  }

  tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
  }
}

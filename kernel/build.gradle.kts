//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val vertxVersion     = "5.0.6"
val slf4jVersion     = "2.0.17"
val logbackVersion   = "1.5.21"
val liquibaseVersion = "5.0.1"
val groovyVersion    = "4.0.28"
val daggerVersion    = "2.57.2"
val psqlVersion      = "42.7.8"

val junitVersion          = "5.13.4"
val testcontainersVersion = "2.0.2"
val spockVersion          = "2.3-groovy-4.0"

plugins {
  application
  java
  groovy
  id("io.freefair.lombok") version "8.14"
//  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val mainVerticleName = "eu.describeit.cristalise.kernel.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  // Groovy support
  implementation("org.apache.groovy:groovy:$groovyVersion")

  // Vert.x dependencies
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-launcher-application")
  implementation("io.vertx:vertx-lang-groovy")
  implementation("io.vertx:vertx-service-proxy")
  implementation("io.vertx:vertx-sql-client-templates")
  implementation("io.vertx:vertx-pg-client")
//  implementation("io.vertx:vertx-auth-properties")
  implementation("io.vertx:vertx-hazelcast")
  implementation("io.vertx:vertx-config")
  implementation("org.postgresql:postgresql:$psqlVersion")
  implementation("com.fasterxml.jackson.core:jackson-databind")

  // Vert.x codegen dependencies
  compileOnly("io.vertx:vertx-codegen-json")
  annotationProcessor("io.vertx:vertx-codegen:$vertxVersion:processor")
  annotationProcessor("io.vertx:vertx-sql-client-templates:$vertxVersion")
  annotationProcessor("io.vertx:vertx-service-proxy:$vertxVersion")

  implementation("com.google.dagger:dagger:${daggerVersion}")
  annotationProcessor ("com.google.dagger:dagger-compiler:${daggerVersion}")
  implementation("org.liquibase:liquibase-core:${liquibaseVersion}")

  // Logging dependencies
  implementation("org.slf4j:slf4j-api:$slf4jVersion")
  implementation("ch.qos.logback:logback-classic:$logbackVersion")

  // Test dependencies
  testImplementation(platform("org.junit:junit-bom:$junitVersion"))
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("io.vertx:vertx-json-schema")
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.spockframework:spock-core:$spockVersion")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testAnnotationProcessor ("com.google.dagger:dagger-compiler:${daggerVersion}")

  // Integration testing: Testcontainers + Liquibase + PostgreSQL JDBC
  testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
  testImplementation("org.testcontainers:testcontainers-junit-jupiter")
  testImplementation("org.testcontainers:testcontainers-postgresql")
}

//tasks.withType<ShadowJar> {
//    archiveClassifier.set("fat")
//    manifest {
//        attributes(mapOf("Main-Verticle" to mainVerticleName))
//    }
//    mergeServiceFiles()
//}

tasks {
  compileGroovy {
    groovyOptions.isJavaAnnotationProcessing = true
  }

  compileTestGroovy {
    groovyOptions.isJavaAnnotationProcessing = true
  }

  withType<Test> {
    useJUnitPlatform()
    testLogging {
      events = setOf(PASSED, SKIPPED, FAILED)
    }
    configureEach {
      maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    }
  }

  withType<JavaExec> {
    args = listOf(mainVerticleName)
  }
}


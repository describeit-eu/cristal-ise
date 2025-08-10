//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

val vertxVersion   = "5.0.2"
val junitVersion   = "5.13.4"
val slf4jVersion   = "2.0.17"
val logbackVersion = "1.5.18"

plugins {
  application
  java
  id("io.freefair.lombok") version "8.14"
//  id("com.github.johnrengelman.shadow") version "7.1.2"
}

val mainVerticleName = "eu.describeit.cristalise.kernel.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))

  // Vert.x dependencies
//  implementation("io.vertx:vertx-launcher-application")
//  implementation("io.vertx:vertx-jdbc-client")
//  implementation("io.vertx:vertx-service-proxy")
  implementation("io.vertx:vertx-sql-client-templates")
  implementation("io.vertx:vertx-pg-client")
//  implementation("io.vertx:vertx-auth-properties")
//  implementation("io.vertx:vertx-hazelcast")

  // Vert.x codegen dependencies
  compileOnly("io.vertx:vertx-codegen-json")
  annotationProcessor("io.vertx:vertx-codegen:$vertxVersion:processor")
  annotationProcessor("io.vertx:vertx-sql-client-templates:$vertxVersion")

  // Logging dependencies
  implementation("org.slf4j:slf4j-api:$slf4jVersion")
  implementation("ch.qos.logback:logback-classic:$logbackVersion")

  // Test dependencies
  platform("org.junit:junit-bom:$junitVersion")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

//tasks.withType<ShadowJar> {
//    archiveClassifier.set("fat")
//    manifest {
//        attributes(mapOf("Main-Verticle" to mainVerticleName))
//    }
//    mergeServiceFiles()
//}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf(mainVerticleName)
}

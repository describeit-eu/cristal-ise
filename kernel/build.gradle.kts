import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  application
  id("io.freefair.lombok")
//  id("com.github.johnrengelman.shadow")
}

val mainVerticleName = "eu.describeit.cristalise.kernel.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  // Vert.x dependencies
  implementation("io.vertx:vertx-launcher-application")
  implementation("io.vertx:vertx-jdbc-client")
//  implementation("io.vertx:vertx-service-proxy")
  implementation("io.vertx:vertx-sql-client-templates")
  implementation("io.vertx:vertx-pg-client")
  implementation("io.vertx:vertx-auth-properties")
  implementation("io.vertx:vertx-hazelcast")

  // Vert.x codegen dependencies
  compileOnly("io.vertx:vertx-codegen-json:5.0.2")
  compileOnly("com.fasterxml.jackson.core:jackson-databind")
  compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
  annotationProcessor("io.vertx:vertx-codegen:5.0.2:processor")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf(mainVerticleName)
}

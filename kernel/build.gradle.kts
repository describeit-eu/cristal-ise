import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

val mainVerticleName = "eu.describeit.cristalise.kernel.MainVerticle"
val launcherClassName = "io.vertx.launcher.application.VertxApplication"

application {
    mainClass.set(launcherClassName)
}

dependencies {
    implementation("io.vertx:vertx-launcher-application")
    implementation("io.vertx:vertx-jdbc-client")
    implementation("io.vertx:vertx-service-proxy")
    implementation("io.vertx:vertx-sql-client-templates")
    implementation("io.vertx:vertx-pg-client")
    implementation("io.vertx:vertx-auth-properties")
    implementation("io.vertx:vertx-hazelcast")

    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter")
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

package eu.describeit.cristalise.kernel.persistency.repository

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Vertx
import io.vertx.sqlclient.Pool
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.postgresql.PostgreSQLContainer

import static eu.describeit.cristalise.kernel.persistency.DatabaseTestUtils.*
import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Abstract superclass for repository integration tests.
 * Provides common Testcontainers Postgres + Vert.x Pool setup/teardown
 * and Liquibase initialization with test data.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
abstract class AbstractRepositoryIT {

  protected PostgreSQLContainer pgContainer
  protected Vertx vertx
  protected Pool pool

  @BeforeAll
  void setUpAll() throws Exception {
    vertx = Vertx.vertx()
    pgContainer = getPGContainer()
    pgContainer.start()

    pool = getPool(vertx, pgContainer)
    liquibaseCreateTables(pgContainer)
    liquibaseLoadTestData(pgContainer)
  }

  @AfterAll
  void tearDownAll() {
    pool?.close()

    vertx
      ?.close()
      ?.toCompletionStage()
      ?.toCompletableFuture()
      ?.orTimeout(5, SECONDS)
      ?.exceptionally(ex -> null)

    pgContainer?.stop()
  }
}

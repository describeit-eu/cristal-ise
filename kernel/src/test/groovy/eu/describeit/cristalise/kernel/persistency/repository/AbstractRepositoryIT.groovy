package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.dagger.DaggerTestKernelComponent
import eu.describeit.cristalise.kernel.dagger.TestKernelComponent
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.sqlclient.Pool
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.testcontainers.postgresql.PostgreSQLContainer

import static eu.describeit.cristalise.kernel.persistency.DatabaseTestUtils.*

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
  protected Pool pool

  @BeforeAll
  void setUpAll() throws Exception {
    System.setProperty('vertx-config-path', 'src/test/conf/config.json')
    TestKernelComponent component = DaggerTestKernelComponent.create()

    pgContainer = component.pgContainer()
    pgContainer.start()

    pool = component.dbPool()

    liquibaseCreateTables(pgContainer)
    liquibaseLoadTestData(pgContainer)
  }

  @AfterAll
  void tearDownAll() {
    /*
    vertx
      ?.close()
      ?.toCompletionStage()
      ?.toCompletableFuture()
      ?.orTimeout(5, SECONDS)
      ?.exceptionally(ex -> null)
    */
    pool?.close()
    pgContainer?.stop()
  }
}

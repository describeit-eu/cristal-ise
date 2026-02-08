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

/**
 * Abstract superclass for repository integration tests.
 * Provides common Testcontainers Postgres + Vert.x Pool setup/teardown
 * and Liquibase initialization with test data.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
abstract class AbstractRepositoryIT {

  final String dbSchemaChangelogFiles = '/liquibase/changelog/changelog-master.yaml'
  final String testDataChangelogFiles = '/liquibase/changelog/changelog-testData-master.yaml'

  protected PostgreSQLContainer pgContainer
  protected Pool pool
  protected TestKernelComponent component

  @BeforeAll
  void setUpAll() throws Exception {
    System.setProperty('vertx-config-path', 'src/test/conf/config.json')
    component = DaggerTestKernelComponent.create()

    pgContainer = component.pgContainer()
    pgContainer.start()

    pool = component.dbPool()

    component.liquibaseCommand()
      .executeUpdate(pgContainer.jdbcUrl, pgContainer.username, pgContainer.password, dbSchemaChangelogFiles)
      .executeUpdate(pgContainer.jdbcUrl, pgContainer.username, pgContainer.password, testDataChangelogFiles)
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

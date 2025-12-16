package eu.describeit.cristalise.kernel.persistency

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.pgclient.PgBuilder
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.SqlClient
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.resource.ClassLoaderResourceAccessor
import org.testcontainers.postgresql.PostgreSQLContainer

import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
@CompileStatic
class DatabaseTestUtils {

  static void liquibaseLoadTestData(PostgreSQLContainer pgContainer) throws Exception {
    liquibaseLoadTestData(pgContainer, null)
  }

  static void liquibaseLoadTestData(PostgreSQLContainer pgContainer, String context) throws Exception {
    def logFile = "/liquibase/changelog/changelog-testData-master.yaml"
    liquibaseUpdate(pgContainer, logFile, context)
  }

  static void liquibaseCreateTables(PostgreSQLContainer pgContainer) throws Exception {
    def logFile = "/liquibase/changelog/changelog-master.yaml"
    liquibaseUpdate(pgContainer, logFile, null)
  }

  private static void liquibaseUpdate(PostgreSQLContainer pgContainer, String logFile, String context) throws Exception {
    Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
      def update = new CommandScope("update")

      update.addArgumentValue("changelogFile", logFile)
      update.addArgumentValue("url", pgContainer.getJdbcUrl())
      update.addArgumentValue("username", pgContainer.getUsername())
      update.addArgumentValue("password", pgContainer.getPassword())
      if (context != null) update.addArgumentValue("contextFilter", context)

      update.execute()

      log.info("liquibaseUpdate() - DONE url: {}", pgContainer.getJdbcUrl())
    })
  }

  static <T> T await(Future<T> future) {
    try {
      return future.toCompletionStage().toCompletableFuture().get(5, SECONDS)
    } catch (Exception e) {
      throw new RuntimeException(e)
    }
  }

/*
  static <T> T awaitDebug(Future<T> future) {
    if (Context.isOnVertxThread()) {
      log.info("await() - Vert.x event loop thread")
      return Future.await(future)
    } else {
      log.info("await() - Platform thread")
      def futureResult = new CompletableFuture<T>()

      future.onComplete(ar -> {
        if (ar.succeeded()) {
          log.debug("await(onComplete) - SUCCEEDED result:{}", ar.result())
          futureResult.complete(ar.result())
        } else if (ar.failed()) {
          log.debug("await(onComplete) - FAILED", ar.cause())
          futureResult.completeExceptionally(ar.cause())
        } else {
          log.warn("await(onComplete) - ??????? result:{}", ar)
          futureResult.completeExceptionally(new RuntimeException("Unexpected result: " + ar))
        }
      })

      try {
        def result = futureResult.get(5, SECONDS)
        log.info("await(get) - futureResult:{} result:{}", futureResult, result)
        return result
      } catch (Exception e) {
        log.error("await(get) - FAILED", e)
        throw new RuntimeException(e)
      }
    }
  }
*/
}

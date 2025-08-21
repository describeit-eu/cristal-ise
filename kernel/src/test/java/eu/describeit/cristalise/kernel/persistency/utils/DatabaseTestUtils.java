package eu.describeit.cristalise.kernel.persistency.utils;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class DatabaseTestUtils {

  public static PostgreSQLContainer<?> getPGContainer() {
    var container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17"));
    container.withDatabaseName("cristalise-test");

    log.info("getPGContainer() - {}", container);

    return container;
  }

  public static void liquibaseLoadTestData(PostgreSQLContainer<?> pgContainer) throws Exception {
    liquibaseCreateTables(pgContainer, "/liquibase/changelog/changelog-testData-master.yaml");
  }

  public static void liquibaseCreateTables(PostgreSQLContainer<?> pgContainer) throws Exception {
    liquibaseCreateTables(pgContainer, "/liquibase/changelog/changelog-master.yaml");
  }

  public static Pool getPool(Vertx vertx, PostgreSQLContainer<?> pgContainer) {
    PgConnectOptions connectOptions = getPgConnectOptions(pgContainer);

    return PgBuilder.pool()
      .with(new PoolOptions().setMaxSize(5))
      .connectingTo(connectOptions)
      .using(vertx)
      .build();
  }

  public static SqlClient getSqlClient(Vertx vertx, PostgreSQLContainer<?> pgContainer) {
    PgConnectOptions connectOptions = getPgConnectOptions(pgContainer);

    return PgBuilder.client()
      .with(new PoolOptions().setMaxSize(5))
      .connectingTo(connectOptions)
      .using(vertx)
      .build();
  }

  private static void liquibaseCreateTables(PostgreSQLContainer<?> pgContainer, String logFile) throws Exception {
    Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
      CommandScope update = new CommandScope("update");

      update.addArgumentValue("changelogFile", logFile);
      update.addArgumentValue("url", pgContainer.getJdbcUrl());
      update.addArgumentValue("username", pgContainer.getUsername());
      update.addArgumentValue("password", pgContainer.getPassword());

      update.execute();

      log.info("liquibaseUpdate() - DONE url: {}", pgContainer.getJdbcUrl());
    });
  }

  private static PgConnectOptions getPgConnectOptions(PostgreSQLContainer<?> pgContainer) {
    return new PgConnectOptions()
      .setPort(pgContainer.getMappedPort(5432))
      .setHost(pgContainer.getHost())
      .setDatabase(pgContainer.getDatabaseName())
      .setUser(pgContainer.getUsername())
      .setPassword(pgContainer.getPassword());
  }

  public static <T> T awaitSimple(Future<T> future) {
    try {
      return future.toCompletionStage().toCompletableFuture().get(5, SECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T await(Future<T> future) {
    if (Context.isOnVertxThread()) {
      log.info("await() - Vert.x event loop thread");
      return Future.await(future);
    } else {
      log.info("await() - Platform thread");
      CompletableFuture<T> futureResult = new CompletableFuture<>();

      future.onComplete(ar -> {
        if (ar.succeeded()) {
          log.info("await(onComplete) - SUCCEEDED result:{}", ar.result());
          futureResult.complete(ar.result());
        } else if (ar.failed()) {
          log.info("await(onComplete) - FAILED", ar.cause());
          futureResult.completeExceptionally(ar.cause());
        } else {
          log.warn("await(onComplete) - ??????? result:{}", ar);
          futureResult.completeExceptionally(new RuntimeException("Unexpected result: " + ar));
        }
      });

      try {
        var result = futureResult.get(5, SECONDS);
        log.info("await(get) - futureResult:{} result:{}", futureResult, result);
        return result;
      } catch (Exception e) {
        log.error("await(get) - FAILED", e);
        throw new RuntimeException(e);
      }
    }
  }
}

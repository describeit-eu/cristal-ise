package eu.describeit.cristalise.kernel.persistency.utils;

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
}

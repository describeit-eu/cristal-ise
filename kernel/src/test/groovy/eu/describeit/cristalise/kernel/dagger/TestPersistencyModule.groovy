package eu.describeit.cristalise.kernel.dagger

import dagger.Module
import dagger.Provides
import groovy.transform.CompileStatic
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.pgclient.PgBuilder
import io.vertx.pgclient.PgConnectOptions
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.SqlClient
import org.testcontainers.postgresql.PostgreSQLContainer

import javax.inject.Singleton

@CompileStatic
@Module
class TestPersistencyModule {

  @Provides
  @Singleton
  static PostgreSQLContainer providePGContainer(ConfigRetriever configRetriever) {
    JsonObject config = configRetriever.getCachedConfig()
    JsonObject db = config.getJsonObject("database", config.getJsonObject("db", new JsonObject()))

    String image = db.getString("image", "postgres:17-ltree")
    String dbName = db.getString("image", "cristalise-test")

    PostgreSQLContainer container = new PostgreSQLContainer(image)
    container.withDatabaseName(dbName)

    return container
  }

  @Provides
  @Singleton
  static PgConnectOptions providePgConnectOptions(PostgreSQLContainer pgContainer) {

    String host     = pgContainer.getHost()
    int port        = pgContainer.getMappedPort(5432)
    String database = pgContainer.getDatabaseName()
    String user     = pgContainer.getUsername()
    String password = pgContainer.getPassword()

    return new PgConnectOptions()
      .setHost(host)
      .setPort(port)
      .setDatabase(database)
      .setUser(user)
      .setPassword(password)
  }

  /**
   * Provides PoolOptions from configuration. It checks db.pool first, then root pool.
   */
  @Provides
  @Singleton
  static PoolOptions providePoolOptions(ConfigRetriever configRetriever) {
    JsonObject config = configRetriever.getCachedConfig()

    JsonObject db = config.getJsonObject("database", config.getJsonObject("db", new JsonObject()))
    JsonObject poolCfg = db.getJsonObject("pool", config.getJsonObject("pool", new JsonObject()))

    int maxSize          = poolCfg.getInteger("maxSize", 10)
    int maxWaitQueueSize = poolCfg.getInteger("maxWaitQueueSize", -1)

    PoolOptions opts = new PoolOptions().setMaxSize(maxSize)
    if (maxWaitQueueSize >= 0) opts.setMaxWaitQueueSize(maxWaitQueueSize)

    return opts
  }

  /**
   * Provides a Vert.x database Pool built with PgBuilder and configured from Vert.x config.
   */
  @Provides
  @Singleton
  static Pool provideDatabasePool(Vertx vertx, PgConnectOptions connectOptions, PoolOptions poolOptions) {
    return PgBuilder.pool()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build()
  }

  /**
   * Expose SqlClient via the same Pool instance to allow repositories to depend on SqlClient.
   */
  @Provides
  @Singleton
  static SqlClient provideSqlClient(Pool pool) { return pool }

}

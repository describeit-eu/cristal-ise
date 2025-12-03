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

import javax.inject.Singleton

/**
 * Dagger module that provides persistency-related bindings (database connections, pools, etc.).
 */
@CompileStatic
@Module
class PersistencyModule {

  /**
   * Provides PgConnectOptions built from Vert.x configuration.
   * Expected config structure:
   * {
   *   "db": {
   *     "host": "localhost",
   *     "port": 5432,
   *     "database": "cristalise",
   *     "user": "postgres",
   *     "password": "secret"
   *   }
   * }
   * Also supports "database" root key as alias of "db".
   */
  @Provides
  @Singleton
  static PgConnectOptions providePgConnectOptions(ConfigRetriever configRetriever) {
    JsonObject config = configRetriever.getCachedConfig()
    JsonObject db = config.getJsonObject("db", config.getJsonObject("database", new JsonObject()))

    String host     = db.getString("host", "localhost")
    int port        = db.getInteger("port", 5432)
    String database = db.getString("database", "cristalise")
    String user     = db.getString("user", "postgres")
    String password = db.getString("password", "postgres")

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

    JsonObject db = config.getJsonObject("db", config.getJsonObject("database", new JsonObject()))
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

package eu.describeit.cristalise.kernel.dagger

import dagger.Module
import dagger.Provides
import eu.describeit.cristalise.kernel.item.Item
import eu.describeit.cristalise.kernel.item.ItemService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.ThreadingModel
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.Pool

import javax.inject.Singleton

import static java.util.concurrent.TimeUnit.SECONDS

/**
 * Dagger module that provides bindings for kernel-related services.
 */
@Slf4j
@CompileStatic
@Module
class KernelModule {

  /**
   * Provides the Item service implementation.
   */
  @Provides
  @Singleton
  static Item provideItemService(Pool pool) {
    return new ItemService(pool)
  }

  /**
   * Provides ConfigStoreOptions for file-based configuration.
   */
  @Provides
  @Singleton
  static ConfigStoreOptions provideConfigStoreOptions() {
    return new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path", "config.json"))
  }

  /**
   * Provides the Vertx instance.
   */
  @Provides
  @Singleton
  static Vertx provideVertx() {
    // Fallback to creating a Vertx instance when not running on a Vert.x context (e.g., in tests)
    return Vertx.currentContext()?.owner() ?: Vertx.vertx()
  }

  /**
   * Provides ConfigRetriever for accessing Vert.x configuration.
   */
  @Provides
  @Singleton
  static ConfigRetriever provideConfigRetriever(Vertx vertx, ConfigStoreOptions configStore) {
    ConfigRetriever retriever = ConfigRetriever.create(vertx)
    // Eagerly load config so cached config is available to other providers during component creation
    try {
      retriever
        .getConfig()
        .toCompletionStage()
        .toCompletableFuture()
        .get(5, SECONDS)
    } catch (Exception ignored) {
      log.debug("", ignored)
    }

    log.trace('provideConfigRetriever() - {}', retriever.cachedConfig)
    return retriever
  }

  /**
   * Provides DeploymentOptions configured from the config file.
   */
  @Provides
  @Singleton
  static DeploymentOptions provideDeploymentOptions(ConfigRetriever configRetriever) {
    JsonObject config = configRetriever.getCachedConfig()
    JsonObject deploymentConfig = config.getJsonObject("deployment", new JsonObject())

    String threadingModel = deploymentConfig.getString("threadingModel", "VIRTUAL_THREAD")
    Integer instances = deploymentConfig.getInteger("instances", 1)

    return new DeploymentOptions()
      .setThreadingModel(ThreadingModel.valueOf(threadingModel))
      .setInstances(instances)
  }

}

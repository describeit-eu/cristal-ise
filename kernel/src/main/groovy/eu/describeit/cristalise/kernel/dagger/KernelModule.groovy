package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import dagger.Module
import dagger.Provides
import eu.describeit.cristalise.kernel.item.Item
import eu.describeit.cristalise.kernel.item.ItemService
import eu.describeit.cristalise.kernel.item.ItemVerticle
import groovy.transform.CompileStatic
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.DeploymentOptions
import io.vertx.core.ThreadingModel
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

import javax.inject.Singleton

/**
 * Dagger module that provides bindings for Item-related services.
 */
@CompileStatic
@Module
class KernelModule {

  /**
   * Provides the Item service implementation.
   */
  @Provides
  @Singleton
  static Item provideItemService() {
    return new ItemService()
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
    return Vertx.currentContext()?.owner()
  }

  /**
   * Provides ConfigRetriever for accessing Vert.x configuration.
   */
  @Provides
  @Singleton
  static ConfigRetriever provideConfigRetriever(Vertx vertx, ConfigStoreOptions configStore) {
    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(configStore)
    return ConfigRetriever.create(vertx, options)
  }

  /**
   * Provides DeploymentOptions configured from the config file.
   */
  @Provides
  @Singleton
  static DeploymentOptions provideDeploymentOptions(ConfigRetriever configRetriever) {
    JsonObject config = configRetriever.getCachedConfig()
    JsonObject deploymentConfig = config.getJsonObject("deployment", new JsonObject())
    ThreadingModel threadingModel = ThreadingModel.valueOf(
      deploymentConfig.getString("threadingModel", "VIRTUAL_THREAD"))

    return new DeploymentOptions()
      .setThreadingModel(threadingModel)
      .setInstances(deploymentConfig.getInteger("instances", 1))
  }

}

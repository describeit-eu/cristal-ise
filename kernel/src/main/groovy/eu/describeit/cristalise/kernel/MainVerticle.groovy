package eu.describeit.cristalise.kernel

import eu.describeit.cristalise.kernel.item.ItemVerticle
import io.vertx.core.*
import groovy.util.logging.Slf4j

@Slf4j
class MainVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    DeploymentOptions options = new DeploymentOptions()
      .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
      .setInstances(4)

    Promise<?> promise = Promise.promise()

    vertx.deployVerticle(ItemVerticle.class, options).onComplete { ar ->
      if (ar.succeeded()) {
        log.info("ItemVerticle deployed successfully")
        promise.succeed()
      } else {
        log.error("Error deploying ItemVerticle", ar.cause())
        promise.fail(ar.cause())
      }
    }

    return promise.future()
  }
}

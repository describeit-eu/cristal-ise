package eu.describeit.cristalise.kernel

import eu.describeit.cristalise.kernel.item.ItemVerticle
import groovy.transform.CompileStatic
import io.vertx.core.*
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class MainVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    DeploymentOptions options = new DeploymentOptions()
      .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
      .setInstances(4)

    Promise<?> promise = Promise.promise()

    vertx.deployVerticle(ItemVerticle.class, options)
      .onSuccess((String result) -> {
        log.info("ItemVerticle deployed successfully");
        promise.complete(result);
      })
      .onFailure((Throwable failure) -> {
        log.error("Error deploying ItemVerticle", failure);
        promise.fail(failure);
      });

    return promise.future()
  }
}

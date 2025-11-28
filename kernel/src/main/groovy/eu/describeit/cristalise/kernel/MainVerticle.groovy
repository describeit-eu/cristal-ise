package eu.describeit.cristalise.kernel

import eu.describeit.cristalise.kernel.item.ItemVerticle
import eu.describeit.cristalise.kernel.item.DaggerItemComponent
import eu.describeit.cristalise.kernel.item.ItemComponent
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
      .setInstances(1)

    Promise<?> promise = Promise.promise()

    // Build DI component and inject dependencies into verticles
    ItemComponent component = DaggerItemComponent.create()

    ItemVerticle v = new ItemVerticle()
    component.inject(v)

    vertx.deployVerticle(v, options)
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

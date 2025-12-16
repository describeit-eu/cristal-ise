package eu.describeit.cristalise.kernel

import eu.describeit.cristalise.kernel.dagger.DaggerKernelComponent
import eu.describeit.cristalise.kernel.dagger.KernelComponent
import groovy.transform.CompileStatic
import io.vertx.core.*
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class MainVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    Promise<?> promise = Promise.promise()

    KernelComponent component = DaggerKernelComponent.create()

    vertx.deployVerticle(component.itemVerticle(), component.deploymentOptions())
      .onSuccess((String result) -> {
        log.info("ItemVerticle deployed successfully")
        promise.complete(result)
      })
      .onFailure((Throwable failure) -> {
        log.error("Error deploying ItemVerticle", failure)
        promise.fail(failure)
      })

    return promise.future()
  }
}

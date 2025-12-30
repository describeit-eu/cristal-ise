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

    vertx.deployVerticle(component.itemServiceVerticle(), component.deploymentOptions())
      .onSuccess { String result ->
        log.info("start() - ItemServiceVerticle deployed successfully")
        promise.complete(result)
      }
      .onFailure { Throwable failure ->
        log.error("Error deploying ItemServiceVerticle", failure)
        promise.fail(failure)
      }

    log.info("start() - DONE")
    return promise.future()
  }

  @Override
  public Future<?> stop() throws Exception {
    log.info("stop() - DONE")
    return super.stop()
  }
}

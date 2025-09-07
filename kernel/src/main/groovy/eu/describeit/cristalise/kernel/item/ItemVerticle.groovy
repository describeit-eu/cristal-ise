package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.service.ItemService
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.serviceproxy.ServiceBinder
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class ItemVerticle extends VerticleBase {

    @Override
    public Future<?> start() throws Exception {
        new ServiceBinder(vertx)
            .setAddress(Item.ADDRESS)
            .register(Item.class, new ItemService())

        log.info("ItemVerticle started")
        return super.start()
    }

    @Override
    public Future<?> stop() throws Exception {
        log.info("ItemVerticle stopped")
        return super.stop()
    }
}

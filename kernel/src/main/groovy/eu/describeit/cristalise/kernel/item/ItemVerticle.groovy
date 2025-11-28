package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.service.ItemService
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.serviceproxy.ServiceBinder
import groovy.util.logging.Slf4j
import javax.inject.Inject

@Slf4j
@CompileStatic
class ItemVerticle extends VerticleBase {

    /**
     * Item service to be registered. If not injected (e.g., in tests),
     * a default ItemService instance will be used as a fallback.
     */
    @Inject
    Item itemService

    ItemVerticle() {}

    ItemVerticle(Item itemService) {
        this.itemService = itemService
    }

    @Override
    public Future<?> start() throws Exception {
        Item service = (itemService != null) ? itemService : new ItemService()

        new ServiceBinder(vertx)
            .setAddress(Item.ADDRESS)
            .register(Item.class, service)

        log.info("ItemVerticle started")
        return super.start()
    }

    @Override
    public Future<?> stop() throws Exception {
        log.info("ItemVerticle stopped")
        return super.stop()
    }
}

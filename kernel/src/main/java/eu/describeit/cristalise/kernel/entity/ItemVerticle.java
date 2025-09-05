package eu.describeit.cristalise.kernel.entity;

import eu.describeit.cristalise.kernel.service.ItemService;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemVerticle extends VerticleBase {

    @Override
    public Future<?> start() throws Exception {
        new ServiceBinder(vertx)
            .setAddress(Item.ADDRESS)
            .register(Item.class, new ItemService());

        log.info("ItemVerticle started");
        return super.start();
    }

    @Override
    public Future<?> stop() throws Exception {
        log.info("ItemVerticle stopped");
        return super.stop();
    }
}

package eu.describeit.cristalise.kernel.item

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.core.json.JsonObject
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection

import javax.inject.Inject
import javax.inject.Singleton

import static eu.describeit.cristalise.kernel.item.ItemProxy.create

@Slf4j
@CompileStatic
@Singleton
class ItemServiceVerticle extends VerticleBase implements Item {

  Pool dbPool

  @Inject
  ItemServiceVerticle(Pool pool) {
    dbPool = pool
  }

  @Override
  public Future<String> requestAction(
    String itemUuid,
    String actorUuid,
    String actionPath,
    String transitionID,
    String outcome,
    String fileName,
    List<Byte> attachment)
  {
    log.info('requestAction() - {}', itemUuid)
    def outcomeJson = new JsonObject(outcome)

    dbPool.withTransaction() { SqlConnection connection ->
      return create(connection, itemUuid).compose { ItemProxy item ->
        log.info('requestAction() - {}', item)
        outcomeJson.put('name', item.name)
        return Future.succeededFuture(outcomeJson.encode())
      }
    }.onFailure() { Throwable t ->
      return Future.failedFuture(t)
    }
  }

  @Override
  public Future<?> start() throws Exception {
    new ServiceBinder(vertx)
      .setAddress(Item.ADDRESS)
      .setIncludeDebugInfo(true)
      .register(Item.class, this)

    log.info("ItemServiceVerticle started")
    return super.start()
  }

  @Override
  public Future<?> stop() throws Exception {
    log.info("ItemServiceVerticle stopped")
    return super.stop()
  }
}

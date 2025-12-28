package eu.describeit.cristalise.kernel.item

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection

import javax.inject.Inject
import javax.inject.Singleton

import static eu.describeit.cristalise.kernel.item.ItemProxy.create

@Slf4j
@CompileStatic
@Singleton
class ItemService implements Item {

  Pool dbPool

  @Inject
  ItemService(Pool pool) {
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
}

package eu.describeit.cristalise.kernel.item

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.Promise
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
    Promise<String> promise = Promise.promise()

    dbPool.withTransaction() { SqlConnection connection ->
      log.info('requestAction() - {} connection:{}', itemUuid, connection)
      create(connection, itemUuid).compose {
        ItemProxy proxy ->
      }
    }.onSuccess() {
      promise.succeed(outcome) // this is not correct, but works for the time being
    }.onFailure() { Throwable t ->
      promise.fail(t)
    }

    return promise.future()
  }
}

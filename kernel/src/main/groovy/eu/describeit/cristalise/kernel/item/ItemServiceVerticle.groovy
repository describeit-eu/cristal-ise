package eu.describeit.cristalise.kernel.item

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.VerticleBase
import io.vertx.core.json.JsonObject
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Transaction

import javax.inject.Inject
import javax.inject.Singleton


@Slf4j
@CompileStatic
@Singleton
class ItemServiceVerticle extends VerticleBase implements ItemService {

  Pool dbPool

  @Inject
  ItemServiceVerticle(Pool pool) {
    dbPool = pool
  }

  @Override
  Future<String> requestAction(
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

    SqlConnection conn = null
    Transaction tx = null

    try {
      conn = dbPool.getConnection().await()
      tx = conn.begin().await()

      ItemProxy item = ItemProxy.create(conn, itemUuid).await()
      outcomeJson.put('name', item.name)

      tx.commit().await()
      conn.close().await()
      return Future.succeededFuture(outcomeJson.encode())
    }
    catch (Throwable t) {
      if (tx) tx.rollback().await()
      if (conn) conn.close().await()

      log.info('requestAction() - FAILED item:{}', itemUuid, t)
      return Future.failedFuture(t)
    }
  }

  Future<String> requestActionAsync(
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
      return ItemProxy.create(connection, itemUuid)
        .compose { ItemProxy item ->
          log.info('requestAction() - {}', item)
          outcomeJson.put('name', item.name)
          return Future.succeededFuture(outcomeJson.encode())
        }
    }.onFailure() { Throwable t ->
      return Future.failedFuture(t)
    }
  }

  @Override
  Future<?> start() throws Exception {
    new ServiceBinder(vertx)
      .setAddress(ItemService.ADDRESS)
      .setIncludeDebugInfo(true)
      .register(ItemService.class, this)

    log.info("ItemServiceVerticle started")
    return super.start()
  }

  @Override
  Future<?> stop() throws Exception {
    log.info("ItemServiceVerticle stopped")
    return super.stop()
  }
}

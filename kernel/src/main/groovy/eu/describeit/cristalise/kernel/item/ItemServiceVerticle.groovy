package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.lifecycle.CompositeAction
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
    // TODO: authn/authz should be based on Interceptors attached to ServiceBinder
    // TODO: use services like keycloak / Authentik / casdoor
    final JsonObject inputOutcome = new JsonObject(outcome)

    final SqlConnection conn
    final Transaction tx

    try {
      conn = dbPool.getConnection().await()
      tx = conn.begin().await()

      // checks if actor and item exists
      final ItemProxy actor =  null // ItemProxy.create(conn, actorUuid).await()
      final ItemProxy item = ItemProxy.create(conn, itemUuid).await()

      final JsonObject outputOutcome = handleRequest(item, actor, actionPath, transitionID, inputOutcome)

      tx.commit().await()

      return Future.succeededFuture(outputOutcome.encode())
    }
    catch (Throwable t) {
      log.error('requestAction() - FAILED item:{}', itemUuid, t)

      if (tx) tx.rollback().await()

      return Future.failedFuture(t)
    }
    finally {
      if (conn) conn.close().await()
    }
  }

  private static JsonObject handleRequest(final ItemProxy item, final ItemProxy actor, final String actionPath, final String transitionId, final JsonObject inputOutcome) {
    CompositeAction lifecycle = item.lifeCycle.await()

    final JsonObject outputOutcome = lifecycle.request(item, actor, actionPath, transitionId, inputOutcome).await()

//    sendEvents(changes) how????
//    def jobs = lifecycle.calculateNextJobs().await()
//    sendEvents(jobs)

    return outputOutcome
  }

  @Override
  Future<?> start() throws Exception {
    new ServiceBinder(vertx)
      .setAddress(ItemService.ADDRESS)
      .setIncludeDebugInfo(true)
//      .addInterceptor {JWTAuth.create(it, new JWTAuthOptions())))
//      .addInterceptor {AuthorizationInterceptor.create(JWTAuthorization.create("permissions")) {...} }
      .register(ItemService.class, this)

    log.info("start() - DONE")
    return super.start()
  }

  @Override
  Future<?> stop() throws Exception {
    log.info("stop() - DONE")
    return super.stop()
  }
}

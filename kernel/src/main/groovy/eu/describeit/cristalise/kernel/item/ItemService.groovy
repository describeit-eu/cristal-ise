package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO
import eu.describeit.cristalise.kernel.persistency.repository.ItemRepository
import eu.describeit.cristalise.kernel.persistency.repository.ItemRepositoryImpl
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.sqlclient.Pool

import javax.inject.Inject
import javax.inject.Singleton

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
    Promise<String> promise = Promise.promise()

    dbPool.withTransaction() {connection ->
      def item = new ItemProxy(connection, itemUuid)

    }.onFailure { Throwable t ->
      log.error("Error processing action request", t)
      promise.fail(t)
    }

    return promise.future()
  }
}

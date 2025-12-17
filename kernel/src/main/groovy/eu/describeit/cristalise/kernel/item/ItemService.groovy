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
      ItemRepository itemRepo = new ItemRepositoryImpl(connection)

      Optional<ItemDO> itemOptional = itemRepo.findById(UUID.fromString(itemUuid)).await()

      if (itemOptional.isPresent()) {
        String result = String.format("Action '%s' requested for Item %s by Actor %s", actionPath, itemUuid, actorUuid)
        promise.complete(result)
      } else {
        String error = String.format("Item %s does not exists", itemUuid)
        promise.fail(error)
      }
    }.onFailure { Throwable t ->
      log.error("Error processing action request", t)
      promise.fail(t)
    }

    return promise.future()
  }
}

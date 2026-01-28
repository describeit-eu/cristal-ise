package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.lifecycle.*
import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.*
import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.SqlClient

import static eu.describeit.cristalise.kernel.persistency.domain.ActionDO.ActionType.*

@Slf4j
@ToString(includePackage=false,  includeNames=true, excludes = 'storage, lifeCycle')
@CompileStatic
class ItemProxy {
  private final RepositoryWrapper storage
  private final UUID itemId
  private ItemDO itemDO

  private Future<CompositeAction> lifeCycle

  private final Vertx vertx

  static Future<ItemProxy> create(SqlClient client, String itemId) {
    return create(client, UUID.fromString(itemId))
  }

  static Future<ItemProxy> create(SqlClient client, UUID uuid) {
    ItemProxy proxy = new ItemProxy(client, uuid)
    return proxy.initialise()
  }

  private Future<ItemProxy> initialise() {
    return storage.getItemDO(itemId).compose { ItemDO itemDO ->
      this.itemDO = itemDO
      return Future.succeededFuture(this)
    } as Future<ItemProxy>
  }

  /**
   * @return the storage
   */
  RepositoryWrapper getStorage() {
    return storage
  }

  /**
   * @return the lifecycle of this item
   */
  Future<CompositeAction> getLifeCycle() {
    if (lifeCycle) return lifeCycle

    Long actionId = itemDO?.actionId
    if (actionId == null) return Future.succeededFuture(null)

    return (Future<CompositeAction>) storage.getActionDO(actionId)
      .compose { ActionDO actionDO ->
        log.info('getLifeCycle() - creating {}', actionDO)
        Action newAction = AbstractCompositeAction.createAction(actionDO)

        if (newAction.type == ELEMENTARY) {
          return Future.failedFuture(new IllegalArgumentException("ELEMENTARY Action cannot be used for lifeCycle $this"))
        } else {
          CompositeAction compositeAction = (CompositeAction) newAction
          return compositeAction.initialise(storage)
            .compose {
              lifeCycle = Future.succeededFuture(compositeAction)
              return lifeCycle
            }
        }
      }
  }

  /**
   * Use this constructor to create ItemProxies without connection to the database.
   * It should only be used for testing purposes.
   *
   * @param v the fully configure Vertx instance
   * @param uuid the itemId
   */
  ItemProxy(Vertx v, UUID uuid) {
    vertx = v
    itemId = uuid

    itemDO = null
    storage = null
  }

  /**
   *
   * @param client
   * @param uuid
   */
  public ItemProxy(SqlClient client, UUID uuid) {
    vertx = null
    itemId = uuid
    storage = new RepositoryWrapper(client)
  }

  /**
   * @return
   */
  UUID getItemId() {
    return itemId
  }

  /**
   * @return
   */
  String getName() {
    return itemDO?.name
  }

  /**
   * @return
   */
  String getType() {
    return itemDO?.type
  }

  /**
   * @return
   */
  String getVersion() {
    return itemDO?.version
  }

  /**
   *
   * @param actorId
   * @param actionPath
   * @param transitionID
   * @param outcome
   * @return
   */
  Future<JsonObject> requestAction(
    UUID       actorId,
    String     actionPath,
    String     transitionID,
    JsonObject outcome
  ) {
    String fileName = null
    List<Byte> attachment = Collections.emptyList()

    callItemService(
      actorId, actionPath, transitionID, outcome, fileName, attachment
    ).compose { String result ->
      return Future.succeededFuture(new JsonObject(result))
    }.onFailure { Throwable t ->
      log.debug("requestAction() - could not process request for:{}", this, t)
      return Future.failedFuture(t)
    }
  }

  /**
   *
   * @param actorId
   * @param actionPath
   * @param transitionID
   * @param outcome
   * @param fileName
   * @param attachment
   * @return
   */
  private Future<String> callItemService(
    UUID       actorId,
    String     actionPath,
    String     transitionID,
    JsonObject outcome,
    String     fileName,
    List<Byte> attachment
  ) {
    ItemService itemService = new ItemServiceVertxEBProxy(vertx, ItemService.ADDRESS);

    return itemService.requestAction(
      itemId.toString(),
      actorId.toString(),
      actionPath,
      transitionID,
      outcome.encode(),
      fileName,
      attachment
    )
  }

}

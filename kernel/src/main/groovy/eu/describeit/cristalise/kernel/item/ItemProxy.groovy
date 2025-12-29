package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.persistency.repository.*

import groovy.transform.CompileStatic
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.SqlClient

@Slf4j
@ToString(includePackage=false)
@CompileStatic
class ItemProxy {

  private final ItemRepository itemRepository

  private final DomainPathRepository domainPathRepository
  private final ItemPropertyRepository itemPropertyRepository

  private final ActionRepository actionRepository
  private final CollectionRepository collectionRepository
  private final CollectionMemberRepository collectionMemberRepository

  private final OutcomeRepository outcomeRepository
  private final AttachmentRepository attachmentRepository
  private final ViewPointRepository viewPointRepository

  private final JobRepository jobRepository

  private final UUID itemId
  private ItemDO itemDO

  private final Vertx vertx

  static Future<ItemProxy> create(SqlClient client, String itemId) {
    return create(client, UUID.fromString(itemId))
  }

  static Future<ItemProxy> create(SqlClient client, UUID uuid) {
    ItemProxy proxy = new ItemProxy(client, uuid)
    return proxy.initialise()
  }

  private Future<ItemProxy> initialise() {
    return itemRepository.findById(itemId).compose { Optional<ItemDO> itemOptional ->
      if (itemOptional.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("Item ${itemId} does not exists"))
      } else {
        this.itemDO = itemOptional.get()
        return Future.succeededFuture(this)
      }
    } as Future<ItemProxy>
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

    itemRepository = null
    domainPathRepository = null
    itemPropertyRepository = null
    actionRepository = null
    collectionRepository = null
    collectionMemberRepository = null
    outcomeRepository = null
    attachmentRepository = null
    viewPointRepository =null
    jobRepository = null
  }

  /**
   *
   * @param client
   * @param uuid
   */
  private ItemProxy(SqlClient client, UUID uuid) {
    vertx = null
    itemRepository = new ItemRepositoryImpl(client)

    itemId = uuid

    domainPathRepository = new DomainPathRepositoryImpl(client)
    itemPropertyRepository = new ItemPropertyRepositoryImpl(client)

    actionRepository = new ActionRepositoryImpl(client)
    collectionRepository = new CollectionRepositoryImpl(client)
    collectionMemberRepository = new CollectionMemberRepositoryImpl(client)

    outcomeRepository = new OutcomeRepositoryImpl(client)
    attachmentRepository = new AttachmentRepositoryImpl(client)
    viewPointRepository = new ViewPointRepositoryImpl(client)

    jobRepository = new JobRepositoryImpl(client)
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

    requestAction(
      actorId, actionPath, transitionID, outcome, fileName, attachment
    ).compose { String result ->
      return Future.succeededFuture(new JsonObject(result))
    }.onFailure { Throwable t ->
      log.debug("requestAction() - could not process request for item: {}", itemId, t)
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
  Future<String> requestAction(
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

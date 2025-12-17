package eu.describeit.cristalise.kernel.item

import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.persistency.repository.*

import groovy.transform.CompileStatic
import io.vertx.sqlclient.SqlClient

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

  private final ItemDO item

  ItemProxy(SqlClient client, String uuid) {
    this(client, UUID.fromString(uuid))
  }

  ItemProxy(SqlClient client, UUID itemId) {
    itemRepository = new ItemRepositoryImpl(client)

    item = checkItem(itemId)

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

  private ItemDO checkItem(UUID itemId) {
    Optional<ItemDO> itemOptional = itemRepository.findById(itemId).await()

    if (itemOptional.isEmpty()) {
      throw new IllegalArgumentException("Item ${itemId} does not exists")
    }

    return itemOptional.get()
  }

}

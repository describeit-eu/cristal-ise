package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.persistency.repository.*
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient

@CompileStatic
class Storage {
  final ItemRepository itemRepository
  final DomainPathRepository domainPathRepository
  final ItemPropertyRepository itemPropertyRepository
  final ActionRepository actionRepository
  final CollectionRepository collectionRepository
  final CollectionMemberRepository collectionMemberRepository
  final OutcomeRepository outcomeRepository
  final AttachmentRepository attachmentRepository
  final ViewPointRepository viewPointRepository
  final JobRepository jobRepository
  final EventRepository eventRepository

  Storage(SqlClient client) {
    itemRepository = new ItemRepositoryImpl(client)
    domainPathRepository = new DomainPathRepositoryImpl(client)
    itemPropertyRepository = new ItemPropertyRepositoryImpl(client)
    actionRepository = new ActionRepositoryImpl(client)
    collectionRepository = new CollectionRepositoryImpl(client)
    collectionMemberRepository = new CollectionMemberRepositoryImpl(client)
    outcomeRepository = new OutcomeRepositoryImpl(client)
    attachmentRepository = new AttachmentRepositoryImpl(client)
    viewPointRepository = new ViewPointRepositoryImpl(client)
    jobRepository = new JobRepositoryImpl(client)
    eventRepository = new EventRepositoryImpl(client)
  }

  Future<ItemDO> getItemDO(UUID itemId) {
    return itemRepository.findById(itemId).compose { Optional<ItemDO> itemOptional ->
      if (itemOptional.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("Item ${itemId} does not exists"))
      } else {
        return Future.succeededFuture(itemOptional.get())
      }
    } as Future<ItemDO>
  }

  Future<ActionDO> getActionDO(Long actionId) {
    return actionRepository.findById(actionId).compose { Optional<ActionDO> actionOptional ->
      if (actionOptional.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("Action id:${actionId} does not exists"))
      } else {
        return Future.succeededFuture(actionOptional.get())
      }
    } as Future<ActionDO>
  }
}

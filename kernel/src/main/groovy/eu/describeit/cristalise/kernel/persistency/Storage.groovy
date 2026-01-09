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

  // --- Item ---

  Future<ItemDO> addItemDO(ItemDO item) {
    return itemRepository.insert(item)
  }

  // --- Action ---

  Future<List<ActionDO>> getChildActionDOs(Long parentId) {
    return actionRepository.findByParentId(parentId)
  }

  // --- DomainPath ---

  Future<DomainPathDO> insertDomainPath(DomainPathDO domainPath) {
    return domainPathRepository.insert(domainPath)
  }

  Future<Optional<DomainPathDO>> findDomainPathByPath(String path) {
    return domainPathRepository.findByPath(path)
  }

  // --- ItemProperty ---

  Future<ItemPropertyDO> insertItemProperty(ItemPropertyDO itemProperty) {
    return itemPropertyRepository.insert(itemProperty)
  }

  Future<List<ItemPropertyDO>> insertItemProperties(List<ItemPropertyDO> itemProperties) {
    return itemPropertyRepository.insertMany(itemProperties)
  }

  Future<List<ItemPropertyDO>> findItemPropertiesByItemId(UUID itemId) {
    return itemPropertyRepository.findByItemId(itemId)
  }

  // --- Event ---

  Future<EventDO> insertEvent(EventDO event) {
    return eventRepository.insert(event)
  }

  Future<List<EventDO>> findAllEvents() {
    return eventRepository.findAll()
  }

  // --- Outcome ---

  Future<OutcomeDO> insertOutcome(OutcomeDO outcome) {
    return outcomeRepository.insert(outcome)
  }

  Future<List<OutcomeDO>> findOutcomesByItemId(UUID itemId) {
    return outcomeRepository.findByItemId(itemId)
  }

  // --- Attachment ---

  Future<AttachmentDO> insertAttachment(AttachmentDO attachment) {
    return attachmentRepository.insert(attachment)
  }

  // --- ViewPoint ---

  Future<ViewPointDO> insertViewPoint(ViewPointDO viewPoint) {
    return viewPointRepository.insert(viewPoint)
  }

  Future<List<ViewPointDO>> insertViewPoints(List<ViewPointDO> viewPoints) {
    return viewPointRepository.insertMany(viewPoints)
  }

  Future<List<ViewPointDO>> findAllViewPoints() {
    return viewPointRepository.findAll()
  }

  // --- Collection ---

  Future<CollectionDO> insertCollection(CollectionDO collection) {
    return collectionRepository.insert(collection)
  }

  // --- CollectionMember ---

  Future<CollectionMemberDO> insertCollectionMember(CollectionMemberDO member) {
    return collectionMemberRepository.insert(member)
  }

  // --- Job ---

  Future<JobDO> insertJob(JobDO job) {
    return jobRepository.insert(job)
  }
}

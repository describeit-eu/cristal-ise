package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.persistency.repository.*
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient

@CompileStatic
class ItemStorage {
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

  ItemStorage(SqlClient client) {
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

  Future<ItemDO> putItemDO(ItemDO item) {
    if (item.id != null) {
      return itemRepository.update(item).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : itemRepository.insert(item)
      }
    }
    return itemRepository.insert(item)
  }

  Future<List<ActionDO>> getChildActionDOs(Long parentId) {
    return actionRepository.findByParentId(parentId).compose { List<ActionDO> list ->
      if (list.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("Child Action for parent id:${parentId} does not exists"))
      } else {
        return Future.succeededFuture(list)
      }
    } as Future<List<ActionDO>>
  }

  Future<DomainPathDO> putDomainPath(DomainPathDO domainPath) {
    if (domainPath.id != null) {
      return domainPathRepository.update(domainPath).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : domainPathRepository.insert(domainPath)
      }
    }
    return domainPathRepository.insert(domainPath)
  }

  Future<DomainPathDO> getDomainPathByPath(String path) {
    return domainPathRepository.findByPath(path).compose { Optional<DomainPathDO> domainPathOptional ->
      if (domainPathOptional.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("DomainPath ${path} does not exists"))
      } else {
        return Future.succeededFuture(domainPathOptional.get())
      }
    } as Future<DomainPathDO>
  }

  Future<ItemPropertyDO> putItemProperty(ItemPropertyDO itemProperty) {
    if (itemProperty.id != null) {
      return itemPropertyRepository.update(itemProperty).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : itemPropertyRepository.insert(itemProperty)
      }
    }
    return itemPropertyRepository.insert(itemProperty)
  }

  Future<List<ItemPropertyDO>> putItemProperties(List<ItemPropertyDO> itemProperties) {
    List<Future<ItemPropertyDO>> futures = itemProperties.collect { putItemProperty(it) }
    return Future.all(futures).map { it.list() as List<ItemPropertyDO> }
  }

  Future<List<ItemPropertyDO>> getItemPropertiesByItemId(UUID itemId) {
    return itemPropertyRepository.findByItemId(itemId).compose { List<ItemPropertyDO> list ->
      if (list.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("ItemProperty for item ${itemId} does not exists"))
      } else {
        return Future.succeededFuture(list)
      }
    } as Future<List<ItemPropertyDO>>
  }

  Future<EventDO> putEvent(EventDO event) {
    if (event.id != null) {
      return eventRepository.update(event).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : eventRepository.insert(event)
      }
    }
    return eventRepository.insert(event)
  }

  Future<List<EventDO>> getAllEvents() {
    return eventRepository.findAll().compose { List<EventDO> list ->
      if (list.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("Events do not exists"))
      } else {
        return Future.succeededFuture(list)
      }
    } as Future<List<EventDO>>
  }

  Future<OutcomeDO> putOutcome(OutcomeDO outcome) {
    if (outcome.id != null) {
      return outcomeRepository.update(outcome).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : outcomeRepository.insert(outcome)
      }
    }
    return outcomeRepository.insert(outcome)
  }

  Future<List<OutcomeDO>> getOutcomesByItemId(UUID itemId) {
    return outcomeRepository.findByItemId(itemId).compose { List<OutcomeDO> list ->
      if (list.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("Outcome for item ${itemId} does not exists"))
      } else {
        return Future.succeededFuture(list)
      }
    } as Future<List<OutcomeDO>>
  }

  Future<AttachmentDO> putAttachment(AttachmentDO attachment) {
    if (attachment.id != null) {
      return attachmentRepository.update(attachment).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : attachmentRepository.insert(attachment)
      }
    }
    return attachmentRepository.insert(attachment)
  }

  Future<ViewPointDO> putViewPoint(ViewPointDO viewPoint) {
    if (viewPoint.id != null) {
      return viewPointRepository.update(viewPoint).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : viewPointRepository.insert(viewPoint)
      }
    }
    return viewPointRepository.insert(viewPoint)
  }

  Future<List<ViewPointDO>> putViewPoints(List<ViewPointDO> viewPoints) {
    List<Future<ViewPointDO>> futures = viewPoints.collect { putViewPoint(it) }
    return Future.all(futures).map { it.list() as List<ViewPointDO> }
  }

  Future<List<ViewPointDO>> getAllViewPoints() {
    return viewPointRepository.findAll().compose { List<ViewPointDO> list ->
      if (list.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("ViewPoints do not exists"))
      } else {
        return Future.succeededFuture(list)
      }
    } as Future<List<ViewPointDO>>
  }

  Future<CollectionDO> putCollection(CollectionDO collection) {
    if (collection.id != null) {
      return collectionRepository.update(collection).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : collectionRepository.insert(collection)
      }
    }
    return collectionRepository.insert(collection)
  }

  Future<CollectionMemberDO> putCollectionMember(CollectionMemberDO member) {
    if (member.id != null) {
      return collectionMemberRepository.update(member).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : collectionMemberRepository.insert(member)
      }
    }
    return collectionMemberRepository.insert(member)
  }

  Future<JobDO> putJob(JobDO job) {
    if (job.id != null) {
      return jobRepository.update(job).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : jobRepository.insert(job)
      }
    }
    return jobRepository.insert(job)
  }
}

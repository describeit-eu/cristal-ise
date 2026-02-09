package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.persistency.repository.*
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient

@CompileStatic
class RepositoryWrapper {
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

  RepositoryWrapper(SqlClient client) {
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

  Future<UUID> getItemId(DomainPathDO dpDO) {
    String path = dpDO.path

    return domainPathRepository.findByPath(path).compose { Optional<DomainPathDO> domainPathOptional ->
      if (domainPathOptional.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("DomainPath '${path}' does not exists"))
      }

      UUID itemId = domainPathOptional.get().itemId

      if (itemId) {
        return Future.succeededFuture(itemId)
      } else {
        return Future.failedFuture(new IllegalArgumentException("DomainPath '${path}' does not reference an Item"))
      }
    } as Future<UUID>
  }

  Future<List<UUID>> getItemIdsOfSameType(String type) {
    return itemPropertyRepository.findItemIdsByItemProperty(new ItemPropertyDO(name: 'Type', value: type))
  }

  Future<List<ViewPointDO>> getViewPointsByItemId(UUID itemId) {
    Future<List<ViewPointDO>> vpListFuture = viewPointRepository.findByItemId(itemId)

    return vpListFuture.compose { List<ViewPointDO> vpList ->
      if (vpListFuture) {
        return Future.succeededFuture(vpList)
      } else {
        return Future.failedFuture(new IllegalArgumentException("NO ViewPoint exists for itemId:$itemId"))
      }
    } as Future<List<ViewPointDO>>
  }

  Future<ViewPointDO> getViewPointDO(UUID itemId, String schemaName, String vpName) {
    Future<Optional<ViewPointDO>> vpFuture = viewPointRepository.findByItemIdAndSchemaNameAndName(itemId, schemaName, vpName)

    return vpFuture.compose { Optional<ViewPointDO> vpOpt ->
      if (vpOpt.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("ViewPoint itemId:$itemId schemaName:$schemaName vpName:$vpName does not exists"))
      } else {
        return Future.succeededFuture(vpOpt.get())
      }
    } as Future<ViewPointDO>
  }

  Future<OutcomeDO> getOutcomeDO(ViewPointDO vp) {
    return getOutcomeDO(vp.outcomeId)
  }

  Future<OutcomeDO> getOutcomeDO(Long id) {
    Future<Optional<OutcomeDO>> outcomeFuture = outcomeRepository.findById(id)

    return outcomeFuture.compose { Optional<OutcomeDO> outcomeOpt ->
      if (outcomeOpt.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("OutcomeId:$id NOT FOUND"))
      } else {
        return Future.succeededFuture(outcomeOpt.get())
      }
    } as Future<OutcomeDO>
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

  Future<ActionDO> putAction(ActionDO action) {
    if (action.id != null) {
      return actionRepository.update(action).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : actionRepository.insert(action)
      }
    }
    return actionRepository.insert(action)
  }

  Future<List<ActionDO>> putActions(List<ActionDO> actions) {
    if (actions.isEmpty()) return Future.succeededFuture([])
    if (actions.every { it.id == null }) {
      return actionRepository.insertMany(actions)
    }
    List<Future<ActionDO>> futures = actions.collect { putAction(it) }
    return Future.all(futures).map { it.list() as List<ActionDO> }
  }

  Future<DomainPathDO> putDomainPath(DomainPathDO domainPath) {
    if (domainPath.id != null) {
      return domainPathRepository.update(domainPath).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : domainPathRepository.insert(domainPath)
      }
    }
    return domainPathRepository.insert(domainPath)
  }

  Future<Boolean> exists(DomainPathDO dp) {
    domainPathRepository.findByPath(dp.path).compose { Optional<DomainPathDO> domainPathOptional ->
      return Future.succeededFuture(domainPathOptional.present)
    }
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

  Future<List<DomainPathDO>> getDomainPathListByItemId(UUID itemId) {
    return domainPathRepository.findByItemId(itemId).compose { List<DomainPathDO> domainPathList ->
      if (domainPathList) {
        return Future.succeededFuture(domainPathList)
      } else {
        return Future.failedFuture(new IllegalArgumentException("NO DomainPath exists for itemId:$itemId"))
      }
    } as Future<List<DomainPathDO>>
  }

  Future<ItemPropertyDO> putItemProperty(ItemPropertyDO itemProperty) {
    if (itemProperty.id != null) {
      return itemPropertyRepository.update(itemProperty).compose { opt ->
        opt.isPresent() ? Future.succeededFuture(opt.get()) : itemPropertyRepository.insert(itemProperty)
      }
    }
    return itemPropertyRepository.insert(itemProperty)
  }

  Future<List<ItemPropertyDO>> putItemProperties(Map<String, String> itemPropsMap, UUID itemId) {
    List<ItemPropertyDO> itemProps = new ArrayList<>()

    for (propEntry in itemPropsMap) {
      boolean mutable = propEntry.key != 'Type'
      itemProps << new ItemPropertyDO(propEntry.key, propEntry.value, mutable, itemId)
    }

    return putItemProperties(itemProps)
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

  Future<List<EventDO>> getEventsByItemId(UUID itemId) {
    return eventRepository.findByItemId(itemId).compose { List<EventDO> list ->
      if (list.isEmpty()) {
        return Future.failedFuture(new IllegalArgumentException("No Event exists for itemId:$itemId"))
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
        return Future.failedFuture(new IllegalArgumentException("NO Outcome exists for itemId:${itemId}"))
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

package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.DescriptionObject
import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future

import java.time.LocalDateTime

@Slf4j
@CompileStatic
class ImportDescriptionObjectAction implements BuiltInAction {
  protected ItemProxy item
  protected ItemProxy actor
  protected RepositoryWrapper storage

  @Override
  Future<UUID> request(ItemProxy item, ItemProxy actor, Object input) {
    log.info('request({}) - inputType:{}', item, input.class)
    this.item = item
    this.actor = actor
    this.storage = item.getStorage()

    if (input instanceof DescriptionObject) {
      return importDescriptionObject(input)
    } else {
      return Future.failedFuture(new IllegalArgumentException("Cannot handle input of class:${input.class.simpleName}"))
    }
  }

  private Future<UUID> importDescriptionObject(DescriptionObject descObject) {
    UUID newItemId = UUID.randomUUID()
    String parentPath = "kernel.description.${descObject.resourceType.typeCode}"

    log.info('importDescriptionObject({}) - parentPath:{}', item, parentPath)

    return ensurePathExists(parentPath)
      .compose { createItem(newItemId, descObject) }
      .compose { createItemDomainPath(newItemId, descObject, parentPath) }
      .compose { createItemProperties(newItemId, descObject) }
//      .compose { createLifeCycle(newItemId, descObject) }
      .compose { createImportEvent(newItemId, descObject) }
      .compose { createdEvent -> createStateMachineOutcome(newItemId, createdEvent, descObject) }
      .compose { createdOutcome -> createViewPoints(newItemId, createdOutcome, descObject) }
      .map { newItemId }
  }

  private Future<ItemDO> createItem(UUID newItemId, DescriptionObject descObject) {
    ItemDO newItem = new ItemDO(newItemId, descObject.name, descObject.type, descObject.version, null)
    return storage.putItemDO(newItem)
  }

  private Future<ActionDO> createLifeCycle(UUID newItemId, DescriptionObject descObject) {
    descObject.resourceType.workflowDef
    return Future.failedFuture('Unimplemented')
  }

  private Future<DomainPathDO> createItemDomainPath(UUID newItemId, DescriptionObject descObject, String parentPath) {
    String fullPath = "${parentPath}.${descObject.name}"
    return storage.putDomainPath(new DomainPathDO(fullPath, newItemId))
  }

  private Future<List<ItemPropertyDO>> createItemProperties(UUID newItemId, DescriptionObject descObject) {
    List<ItemPropertyDO> props = []
    props << new ItemPropertyDO('Name', descObject.name, false, newItemId)
    props << new ItemPropertyDO('Type', descObject.type, false, newItemId)
    props << new ItemPropertyDO('Module', descObject.namespace ?: "", false, newItemId)
    props << new ItemPropertyDO('Version', descObject.version, false, newItemId)
    return storage.putItemProperties(props)
  }

  private Future<EventDO> createImportEvent(UUID newItemId, DescriptionObject descObject) {
    EventDO event = new EventDO()
    event.itemId = newItemId
    event.itemVersion = descObject.version
    event.userLogin = actor?.getName() ?: "system"
    event.timestamp = LocalDateTime.now()
    event.actionPath = "import"
    event.stateMachineVersion = descObject.version

    return storage.putEvent(event)
  }

  private Future<OutcomeDO> createStateMachineOutcome(UUID newItemId, EventDO createdEvent, DescriptionObject descObject) {
    OutcomeDO outcome = new OutcomeDO()
    outcome.itemId = newItemId
    outcome.data = descObject.toJson()
    outcome.eventId = createdEvent.id
    // TODO: fix this to store valid reference to the Schema Item
    outcome.schema = UUID.fromString('00000000-0000-0000-0000-000000000000')
    outcome.schemaVersion = descObject.version

    return storage.putOutcome(outcome)
  }

  private Future<List<ViewPointDO>> createViewPoints(UUID newItemId, OutcomeDO createdOutcome, DescriptionObject descObject) {
    List<ViewPointDO> viewPoints = []
    viewPoints << new ViewPointDO('v0', createdOutcome.schema, descObject.version, descObject.type, createdOutcome.id, newItemId)
    viewPoints << new ViewPointDO('last', createdOutcome.schema, descObject.version, descObject.type, createdOutcome.id, newItemId)
    return storage.putViewPoints(viewPoints)
  }

  private Future<DomainPathDO> ensurePathExists(String path) {
    return (Future<DomainPathDO>) storage.getDomainPathByPath(path).recover { Throwable err ->
      int lastDot = path.lastIndexOf('.')
      if (lastDot > 0) {
        String parent = path.substring(0, lastDot)
        return (Future<DomainPathDO>) ensurePathExists(parent).compose {
          storage.putDomainPath(new DomainPathDO(path, (UUID)null))
        }
      } else {
        return storage.putDomainPath(new DomainPathDO(path, (UUID)null))
      }
    }
  }
}

package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.DescriptionObject
import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.migration.ImportScript
import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

import javax.inject.Inject

import java.time.LocalDateTime
import java.util.stream.Collectors
import java.util.stream.IntStream

@Slf4j
@CompileStatic
class ImportDescriptionObjectAction implements BuiltInAction {
  protected ImportScript.Factory importScriptFactory

  protected ItemProxy item
  protected ItemProxy actor
  protected RepositoryWrapper storage

  @Inject
  ImportDescriptionObjectAction(ImportScript.Factory factory) {
    importScriptFactory = factory
  }

  @Override
  Future<JsonObject> request(ItemProxy item, ItemProxy actor, Object input) {
    log.info('request() - item:{} inputType:{}', item.name, input.class.simpleName)
    this.item = item
    this.actor = actor
    this.storage = item.getStorage()

    if (input instanceof DescriptionObject) {
      return importDescriptionObject(input)
    } else if (input instanceof String) {
      return importString(input)
    } else {
      return Future.failedFuture(new IllegalArgumentException("Cannot handle input of class:${input.class.simpleName}"))
    }
  }

  private Future<JsonObject> importString(String inputString) {
    if      (inputString.endsWith('.groovy')) return runImportScript(inputString)
    else if (inputString.endsWith('.json'))   return importJson(inputString)
    else
      return Future.failedFuture(new IllegalArgumentException("Cannot handle string input:$inputString"))
  }

  private Future<JsonObject> importJson(String inputFile) {
    JsonObject json = new JsonObject(new File(inputFile).text)
    return Future.succeededFuture(json)
  }

  private Future<JsonObject> runImportScript(String scriptName) {
    try {
      ImportScript script = importScriptFactory.create(scriptName, new Binding())

      List<DescriptionObject> descObjList = script.run() as List<DescriptionObject>

      List<Future<JsonObject>> futures = []
      for (descObj in descObjList) {
        def futureJson = importDescriptionObject(descObj)
        futures.add(futureJson)
      }

      Future.join(futures)
        .map { CompositeFuture cf ->
          List<JsonObject> resultList = cf.list() as List<JsonObject>

          def descObjsStatusJson = new JsonObject()
          descObjsStatusJson.put('uuids', new JsonArray())
          descObjsStatusJson.put('names', new JsonArray())
          descObjsStatusJson.put('types', new JsonArray())

          for (aJson in resultList) {
            descObjsStatusJson.getJsonArray('uuids').addAll(aJson.getJsonArray('uuids'))
            descObjsStatusJson.getJsonArray('names').addAll(aJson.getJsonArray('names'))
            descObjsStatusJson.getJsonArray('types').addAll(aJson.getJsonArray('types'))
          }
          return descObjsStatusJson
        }
    } catch (Throwable t) {
      return Future.failedFuture(t)
    }
  }

  private Future<JsonObject> importDescriptionObject(DescriptionObject descObject) {
    UUID newItemId = UUID.randomUUID()
    log.debug('importDescriptionObject() - name:{} parentPath:{}', descObject.name, descObject.resourceType.typeRoot)

    //TODO check if Item exist and if so check if Item needs to be updated (use checksum of the JSON Outcome)

    return ensurePathExists(descObject.resourceType.typeRoot)
      .compose { createItem(newItemId, descObject) }
      .compose { createItemDomainPath(newItemId, descObject, descObject.resourceType.typeRoot) }
      .compose { createItemProperties(newItemId, descObject) }
//      .compose { createLifeCycle(newItemId, descObject) }
      .compose { createImportEvent(newItemId, descObject) }
      .compose { createdEvent -> createOutcome(newItemId, createdEvent, descObject) }
      .compose { createdOutcome -> createViewPoints(newItemId, createdOutcome, descObject) }
      .map {
        def descObjImportedJson = new JsonObject()
        descObjImportedJson.put('uuids', new JsonArray().add(newItemId.toString()))
        descObjImportedJson.put('names', new JsonArray().add(descObject.name))
        descObjImportedJson.put('types', new JsonArray().add(descObject.resourceType.name()))

        log.info('importDescriptionObject() - DONE {}', descObjImportedJson)

        return descObjImportedJson
      } as Future<JsonObject>
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
    event.actionPath = getPath()
    event.stateMachineVersion = descObject.version

    return storage.putEvent(event)
  }

  private Future<OutcomeDO> createOutcome(UUID newItemId, EventDO createdEvent, DescriptionObject descObject) {
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
          storage.putDomainPath(new DomainPathDO(path: path))
        }
      } else {
        return storage.putDomainPath(new DomainPathDO(path: path))
      }
    }
  }
}

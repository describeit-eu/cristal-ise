package eu.describeit.cristalise.kernel.persistency.domain

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.vertx.codegen.annotations.DataObject
import io.vertx.codegen.format.SnakeCase
import io.vertx.codegen.json.annotations.JsonGen
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.templates.annotations.ParametersMapped
import io.vertx.sqlclient.templates.annotations.RowMapped

import java.beans.ConstructorProperties
import java.time.LocalDateTime

@CompileStatic
@Canonical
@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase)
@ParametersMapped(formatter = SnakeCase)
class EventDO {
  EventDO(JsonObject json) { EventDOConverter.fromJson(json, this) }

  EventDO() {}

  @ConstructorProperties(["id", "actionDesc", "actionVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId", "itemVersion", "actionPath", "transitionName"])
  EventDO(Long id, UUID actionDesc, String actionVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId, String itemVersion, String actionPath, String transitionName) {
    this.id = id
    this.actionDesc = actionDesc
    this.actionVersion = actionVersion
    this.script = script
    this.scriptVersion = scriptVersion
    this.stateMachineDesc = stateMachineDesc
    this.stateMachineVersion = stateMachineVersion
    this.userLogin = userLogin
    this.timestamp = timestamp
    this.actionProperties = actionProperties
    this.itemId = itemId
    this.itemVersion = itemVersion
    this.actionPath = actionPath
    this.transitionName = transitionName
  }

  @ConstructorProperties(["actionDesc", "actionVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId", "itemVersion", "actionPath", "transitionName"])
  EventDO(UUID actionDesc, String actionVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId, String itemVersion, String actionPath, String transitionName) {
    this(null, actionDesc, actionVersion, script, scriptVersion, stateMachineDesc, stateMachineVersion, userLogin, timestamp, actionProperties, itemId, itemVersion, actionPath, transitionName)
  }

  EventDO(EventDO other) {
    this(null, other.actionDesc, other.actionVersion, other.script, other.scriptVersion, other.stateMachineDesc, other.stateMachineVersion, other.userLogin, other.timestamp, other.actionProperties, other.itemId, other.itemVersion, other.actionPath, other.transitionName)
  }

  Long id
  UUID actionDesc
  String actionVersion
  UUID script
  String scriptVersion
  UUID stateMachineDesc
  String stateMachineVersion
  String userLogin
  LocalDateTime timestamp
  String actionProperties
  UUID itemId
  String itemVersion
  String actionPath
  String transitionName
}

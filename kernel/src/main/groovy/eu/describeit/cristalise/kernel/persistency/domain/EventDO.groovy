package eu.describeit.cristalise.kernel.persistency.domain

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.vertx.codegen.annotations.DataObject
import io.vertx.codegen.format.SnakeCase
import io.vertx.codegen.json.annotations.JsonGen
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.templates.annotations.ParametersMapped
import io.vertx.sqlclient.templates.annotations.RowMapped

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

  @java.beans.ConstructorProperties(["id", "actionDesc", "actionVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId", "itemVersion", "actionPath", "transitionName"])
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

  @java.beans.ConstructorProperties(["actionDesc", "actionVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId", "itemVersion", "actionPath", "transitionName"])
  EventDO(UUID actionDesc, String actionVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId, String itemVersion, String actionPath, String transitionName) {
    this(null, actionDesc, actionVersion, script, scriptVersion, stateMachineDesc, stateMachineVersion, userLogin, timestamp, actionProperties, itemId, itemVersion, actionPath, transitionName)
  }

  EventDO(EventDO other) {
    this(null, other.actionDesc, other.actionVersion, other.script, other.scriptVersion, other.stateMachineDesc, other.stateMachineVersion, other.userLogin, other.timestamp, other.actionProperties, other.itemId, other.itemVersion, other.actionPath, other.transitionName)
  }

  EventDO setId(Long id) { this.id = id; return this }
  EventDO setActionDesc(UUID actionDesc) { this.actionDesc = actionDesc; return this }
  EventDO setActionVersion(String actionVersion) { this.actionVersion = actionVersion; return this }
  EventDO setScript(UUID script) { this.script = script; return this }
  EventDO setScriptVersion(String scriptVersion) { this.scriptVersion = scriptVersion; return this }
  EventDO setStateMachineDesc(UUID stateMachineDesc) { this.stateMachineDesc = stateMachineDesc; return this }
  EventDO setStateMachineVersion(String stateMachineVersion) { this.stateMachineVersion = stateMachineVersion; return this }
  EventDO setUserLogin(String userLogin) { this.userLogin = userLogin; return this }
  EventDO setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this }
  EventDO setActionProperties(String actionProperties) { this.actionProperties = actionProperties; return this }
  EventDO setItemId(UUID itemId) { this.itemId = itemId; return this }
  EventDO setItemVersion(String itemVersion) { this.itemVersion = itemVersion; return this }
  EventDO setActionPath(String actionPath) { this.actionPath = actionPath; return this }
  EventDO setTransitionName(String transitionName) { this.transitionName = transitionName; return this }

  Long getId() { return id }
  UUID getActionDesc() { return actionDesc }
  String getActionVersion() { return actionVersion }
  UUID getScript() { return script }
  String getScriptVersion() { return scriptVersion }
  UUID getStateMachineDesc() { return stateMachineDesc }
  String getStateMachineVersion() { return stateMachineVersion }
  String getUserLogin() { return userLogin }
  LocalDateTime getTimestamp() { return timestamp }
  String getActionProperties() { return actionProperties }
  UUID getItemId() { return itemId }
  String getItemVersion() { return itemVersion }
  String getActionPath() { return actionPath }
  String getTransitionName() { return transitionName }

  private Long id
  private UUID actionDesc
  private String actionVersion
  private UUID script
  private String scriptVersion
  private UUID stateMachineDesc
  private String stateMachineVersion
  private String userLogin
  private LocalDateTime timestamp
  private String actionProperties
  private UUID itemId
  private String itemVersion
  private String actionPath
  private String transitionName
}

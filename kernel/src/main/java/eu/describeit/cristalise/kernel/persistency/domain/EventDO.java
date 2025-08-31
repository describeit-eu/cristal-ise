package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
public final class EventDO {
  public EventDO(JsonObject json) { EventDOConverter.fromJson(json, this); }

  public EventDO() {}

  @java.beans.ConstructorProperties({"id", "actionDesc", "actionVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId", "itemVersion", "actionPath", "transitionName"})
  public EventDO(Long id, UUID actionDesc, String actionVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId, String itemVersion, String actionPath, String transitionName) {
    this.id = id;
    this.actionDesc = actionDesc;
    this.actionVersion = actionVersion;
    this.script = script;
    this.scriptVersion = scriptVersion;
    this.stateMachineDesc = stateMachineDesc;
    this.stateMachineVersion = stateMachineVersion;
    this.userLogin = userLogin;
    this.timestamp = timestamp;
    this.actionProperties = actionProperties;
    this.itemId = itemId;
    this.itemVersion = itemVersion;
    this.actionPath = actionPath;
    this.transitionName = transitionName;
  }

  @java.beans.ConstructorProperties({"actionDesc", "actionVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId", "itemVersion", "actionPath", "transitionName"})
  public EventDO(UUID actionDesc, String actionVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId, String itemVersion, String actionPath, String transitionName) {
    this(null, actionDesc, actionVersion, script, scriptVersion, stateMachineDesc, stateMachineVersion, userLogin, timestamp, actionProperties, itemId, itemVersion, actionPath, transitionName);
  }

  public EventDO(EventDO other) {
    this(null, other.actionDesc, other.actionVersion, other.script, other.scriptVersion, other.stateMachineDesc, other.stateMachineVersion, other.userLogin, other.timestamp, other.actionProperties, other.itemId, other.itemVersion, other.actionPath, other.transitionName);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EventDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getActionDesc(), other.getActionDesc()) &&
           Objects.equals(this.getActionVersion(), other.getActionVersion()) &&
           Objects.equals(this.getScript(), other.getScript()) &&
           Objects.equals(this.getScriptVersion(), other.getScriptVersion()) &&
           Objects.equals(this.getStateMachineDesc(), other.getStateMachineDesc()) &&
           Objects.equals(this.getStateMachineVersion(), other.getStateMachineVersion()) &&
           Objects.equals(this.getUserLogin(), other.getUserLogin()) &&
           Objects.equals(this.getTimestamp(), other.getTimestamp()) &&
           Objects.equals(this.getActionProperties(), other.getActionProperties()) &&
           Objects.equals(this.getItemId(), other.getItemId()) &&
           Objects.equals(this.getItemVersion(), other.getItemVersion()) &&
           Objects.equals(this.getActionPath(), other.getActionPath()) &&
           Objects.equals(this.getTransitionName(), other.getTransitionName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getActionDesc(), getActionVersion(), getScript(), getScriptVersion(), getStateMachineDesc(), getStateMachineVersion(), getUserLogin(), getTimestamp(), getActionProperties(), getItemId(), getItemVersion(), getActionPath(), getTransitionName());
  }

  @Override
  public String toString() {
    return "EventDO[id=%s, actionDesc=%s, actionVersion='%s', script=%s, scriptVersion='%s', stateMachineDesc=%s, stateMachineVersion='%s', userLogin='%s', timestamp=%s, actionProperties='%s', itemId=%s, itemVersion='%s', actionPath='%s', transitionName='%s']"
      .formatted(this.id, this.actionDesc, this.actionVersion, this.script, this.scriptVersion, this.stateMachineDesc, this.stateMachineVersion, this.userLogin, this.timestamp, this.actionProperties, this.itemId, this.itemVersion, this.actionPath, this.transitionName);
  }

  public EventDO setId(Long id) { this.id = id; return this; }
  public EventDO setActionDesc(UUID actionDesc) { this.actionDesc = actionDesc; return this; }
  public EventDO setActionVersion(String actionVersion) { this.actionVersion = actionVersion; return this; }
  public EventDO setScript(UUID script) { this.script = script; return this; }
  public EventDO setScriptVersion(String scriptVersion) { this.scriptVersion = scriptVersion; return this; }
  public EventDO setStateMachineDesc(UUID stateMachineDesc) { this.stateMachineDesc = stateMachineDesc; return this; }
  public EventDO setStateMachineVersion(String stateMachineVersion) { this.stateMachineVersion = stateMachineVersion; return this; }
  public EventDO setUserLogin(String userLogin) { this.userLogin = userLogin; return this; }
  public EventDO setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
  public EventDO setActionProperties(String actionProperties) { this.actionProperties = actionProperties; return this; }
  public EventDO setItemId(UUID itemId) { this.itemId = itemId; return this; }
  public EventDO setItemVersion(String itemVersion) { this.itemVersion = itemVersion; return this; }
  public EventDO setActionPath(String actionPath) { this.actionPath = actionPath; return this; }
  public EventDO setTransitionName(String transitionName) { this.transitionName = transitionName; return this; }

  public Long getId() { return id; }
  public UUID getActionDesc() { return actionDesc; }
  public String getActionVersion() { return actionVersion; }
  public UUID getScript() { return script; }
  public String getScriptVersion() { return scriptVersion; }
  public UUID getStateMachineDesc() { return stateMachineDesc; }
  public String getStateMachineVersion() { return stateMachineVersion; }
  public String getUserLogin() { return userLogin; }
  public LocalDateTime getTimestamp() { return timestamp; }
  public String getActionProperties() { return actionProperties; }
  public UUID getItemId() { return itemId; }
  public String getItemVersion() { return itemVersion; }
  public String getActionPath() { return actionPath; }
  public String getTransitionName() { return transitionName; }

  private Long id;
  private UUID actionDesc;
  private String actionVersion;
  private UUID script;
  private String scriptVersion;
  private UUID stateMachineDesc;
  private String stateMachineVersion;
  private String userLogin;
  private LocalDateTime timestamp;
  private String actionProperties;
  private UUID itemId;
  private String itemVersion;
  private String actionPath;
  private String transitionName;
}

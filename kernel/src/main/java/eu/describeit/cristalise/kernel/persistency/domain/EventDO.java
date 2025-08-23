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

  @java.beans.ConstructorProperties({"id", "itemVersion", "actionDesc", "actionDescVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId"})
  public EventDO(Long id, String itemVersion, UUID actionDesc, String actionDescVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId) {
    this.id = id;
    this.itemVersion = itemVersion;
    this.actionDesc = actionDesc;
    this.actionDescVersion = actionDescVersion;
    this.script = script;
    this.scriptVersion = scriptVersion;
    this.stateMachineDesc = stateMachineDesc;
    this.stateMachineVersion = stateMachineVersion;
    this.userLogin = userLogin;
    this.timestamp = timestamp;
    this.actionProperties = actionProperties;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"itemVersion", "actionDesc", "actionDescVersion", "script", "scriptVersion", "stateMachineDesc", "stateMachineVersion", "userLogin", "timestamp", "actionProperties", "itemId"})
  public EventDO(String itemVersion, UUID actionDesc, String actionDescVersion, UUID script, String scriptVersion, UUID stateMachineDesc, String stateMachineVersion, String userLogin, LocalDateTime timestamp, String actionProperties, UUID itemId) {
    this(null, itemVersion, actionDesc, actionDescVersion, script, scriptVersion, stateMachineDesc, stateMachineVersion, userLogin, timestamp, actionProperties, itemId);
  }

  public EventDO(EventDO other) {
    this(null, other.itemVersion, other.actionDesc, other.actionDescVersion, other.script, other.scriptVersion, other.stateMachineDesc, other.stateMachineVersion, other.userLogin, other.timestamp, other.actionProperties, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EventDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getItemVersion(), other.getItemVersion()) &&
           Objects.equals(this.getActionDesc(), other.getActionDesc()) &&
           Objects.equals(this.getActionDescVersion(), other.getActionDescVersion()) &&
           Objects.equals(this.getScript(), other.getScript()) &&
           Objects.equals(this.getScriptVersion(), other.getScriptVersion()) &&
           Objects.equals(this.getStateMachineDesc(), other.getStateMachineDesc()) &&
           Objects.equals(this.getStateMachineVersion(), other.getStateMachineVersion()) &&
           Objects.equals(this.getUserLogin(), other.getUserLogin()) &&
           Objects.equals(this.getTimestamp(), other.getTimestamp()) &&
           Objects.equals(this.getActionProperties(), other.getActionProperties()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getItemVersion(), getActionDesc(), getActionDescVersion(), getScript(), getScriptVersion(), getStateMachineDesc(), getStateMachineVersion(), getUserLogin(), getTimestamp(), getActionProperties(), getItemId());
  }

  @Override
  public String toString() {
    return "EventDO[id=%s, itemVersion='%s', actionDesc=%s, actionDescVersion='%s', script=%s, scriptVersion='%s', stateMachineDesc=%s, stateMachineVersion='%s', userLogin='%s', timestamp=%s, actionProperties='%s', itemId=%s]"
      .formatted(this.id, this.itemVersion, this.actionDesc, this.actionDescVersion, this.script, this.scriptVersion, this.stateMachineDesc, this.stateMachineVersion, this.userLogin, this.timestamp, this.actionProperties, this.itemId);
  }

  public EventDO setId(Long id) { this.id = id; return this; }
  public EventDO setItemVersion(String itemVersion) { this.itemVersion = itemVersion; return this; }
  public EventDO setActionDesc(UUID actionDesc) { this.actionDesc = actionDesc; return this; }
  public EventDO setActionDescVersion(String actionDescVersion) { this.actionDescVersion = actionDescVersion; return this; }
  public EventDO setScript(UUID script) { this.script = script; return this; }
  public EventDO setScriptVersion(String scriptVersion) { this.scriptVersion = scriptVersion; return this; }
  public EventDO setStateMachineDesc(UUID stateMachineDesc) { this.stateMachineDesc = stateMachineDesc; return this; }
  public EventDO setStateMachineVersion(String stateMachineVersion) { this.stateMachineVersion = stateMachineVersion; return this; }
  public EventDO setUserLogin(String userLogin) { this.userLogin = userLogin; return this; }
  public EventDO setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
  public EventDO setActionProperties(String actionProperties) { this.actionProperties = actionProperties; return this; }
  public EventDO setItemId(UUID itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public String getItemVersion() { return itemVersion; }
  public UUID getActionDesc() { return actionDesc; }
  public String getActionDescVersion() { return actionDescVersion; }
  public UUID getScript() { return script; }
  public String getScriptVersion() { return scriptVersion; }
  public UUID getStateMachineDesc() { return stateMachineDesc; }
  public String getStateMachineVersion() { return stateMachineVersion; }
  public String getUserLogin() { return userLogin; }
  public LocalDateTime getTimestamp() { return timestamp; }
  public String getActionProperties() { return actionProperties; }
  public UUID getItemId() { return itemId; }

  private Long id;
  private String itemVersion;
  private UUID actionDesc;
  private String actionDescVersion;
  private UUID script;
  private String scriptVersion;
  private UUID stateMachineDesc;
  private String stateMachineVersion;
  private String userLogin;
  private LocalDateTime timestamp;
  private String actionProperties;
  private UUID itemId;
}

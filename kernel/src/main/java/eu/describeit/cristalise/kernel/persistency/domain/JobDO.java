package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.util.Objects;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
public final class JobDO {
  public JobDO(JsonObject json) { JobDOConverter.fromJson(json, this); }

  public JobDO() {}

  @java.beans.ConstructorProperties({"id", "actionName", "transition", "itemId"})
  public JobDO(Long id, String actionName, String transition, Long itemId) {
    this.id = id;
    this.actionName = actionName;
    this.transition = transition;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"actionName", "transition", "itemId"})
  public JobDO(String actionName, String transition, Long itemId) {
    this(null, actionName, transition, itemId);
  }

  public JobDO(JobDO other) {
    this(null, other.actionName, other.transition, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof JobDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getActionName(), other.getActionName()) &&
           Objects.equals(this.getTransition(), other.getTransition()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getActionName(), getTransition(), getItemId());
  }

  @Override
  public String toString() {
    return "JobDO[id=%s, actionName='%s', transition='%s', itemId=%s]"
      .formatted(this.id, this.actionName, this.transition, this.itemId);
  }

  public JobDO setId(Long id) { this.id = id; return this; }
  public JobDO setActionName(String actionName) { this.actionName = actionName; return this; }
  public JobDO setTransition(String transition) { this.transition = transition; return this; }
  public JobDO setItemId(Long itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public String getActionName() { return actionName; }
  public String getTransition() { return transition; }
  public Long getItemId() { return itemId; }

  private Long id;
  private String actionName;
  private String transition;
  private Long itemId;
}

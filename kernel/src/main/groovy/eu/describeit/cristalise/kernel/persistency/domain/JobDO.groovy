package eu.describeit.cristalise.kernel.persistency.domain

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.vertx.codegen.annotations.DataObject
import io.vertx.codegen.format.SnakeCase
import io.vertx.codegen.json.annotations.JsonGen
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.templates.annotations.ParametersMapped
import io.vertx.sqlclient.templates.annotations.RowMapped

@CompileStatic
@Canonical
@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase)
@ParametersMapped(formatter = SnakeCase)
class JobDO {
  JobDO(JsonObject json) { JobDOConverter.fromJson(json, this) }

  JobDO() {}

  @java.beans.ConstructorProperties(["id", "actionName", "transition", "itemId"])
  JobDO(Long id, String actionName, String transition, UUID itemId) {
    this.id = id
    this.actionName = actionName
    this.transition = transition
    this.itemId = itemId
  }

  @java.beans.ConstructorProperties(["actionName", "transition", "itemId"])
  JobDO(String actionName, String transition, UUID itemId) {
    this(null, actionName, transition, itemId)
  }

  JobDO(JobDO other) {
    this(null, other.actionName, other.transition, other.itemId)
  }

  JobDO setId(Long id) { this.id = id; return this }
  JobDO setActionName(String actionName) { this.actionName = actionName; return this }
  JobDO setTransition(String transition) { this.transition = transition; return this }
  JobDO setItemId(UUID itemId) { this.itemId = itemId; return this }

  Long getId() { return id }
  String getActionName() { return actionName }
  String getTransition() { return transition }
  UUID getItemId() { return itemId }

  private Long id
  private String actionName
  private String transition
  private UUID itemId
}

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

@CompileStatic
@Canonical
@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase)
@ParametersMapped(formatter = SnakeCase)
class JobDO {
  JobDO(JsonObject json) { JobDOConverter.fromJson(json, this) }

  JobDO() {}

  @ConstructorProperties(["id", "actionName", "transition", "itemId"])
  JobDO(Long id, String actionName, String transition, UUID itemId) {
    this.id = id
    this.actionName = actionName
    this.transition = transition
    this.itemId = itemId
  }

  @ConstructorProperties(["actionName", "transition", "itemId"])
  JobDO(String actionName, String transition, UUID itemId) {
    this(null, actionName, transition, itemId)
  }

  JobDO(JobDO other) {
    this(null, other.actionName, other.transition, other.itemId)
  }

  Long id
  String actionName
  String transition
  UUID itemId
}

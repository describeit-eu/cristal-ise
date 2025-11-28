package eu.describeit.cristalise.kernel.persistency.domain

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.vertx.codegen.annotations.DataObject
import io.vertx.codegen.format.SnakeCase
import io.vertx.codegen.json.annotations.JsonGen
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.templates.annotations.ParametersMapped
import io.vertx.sqlclient.templates.annotations.RowMapped

/**
 * Item data object representing the item table in the database.
 */
@CompileStatic
@Canonical
@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase)
@ParametersMapped(formatter = SnakeCase)
class ItemDO {
  ItemDO(JsonObject json) { ItemDOConverter.fromJson(json, this) }

  ItemDO() {}

  @java.beans.ConstructorProperties(["id", "name", "type", "version", "actionId"])
  ItemDO(UUID id, String name, String type, String version, Long actionId) {
    this.id = id
    this.name = name
    this.type = type
    this.version = version
    this.actionId = actionId
  }

  ItemDO(ItemDO other) {
    this(other.id, other.name, other.type, other.version, other.actionId)
  }

  ItemDO setId(UUID id) { this.id = id; return this }
  ItemDO setName(String name) { this.name = name; return this }
  ItemDO setType(String type) { this.type = type; return this }
  ItemDO setVersion(String version) { this.version = version; return this }
  ItemDO setActionId(Long actionId) { this.actionId = actionId; return this }

  UUID getId() { return id }
  String getName() { return name }
  String getType() { return type }
  String getVersion() { return version }
  Long getActionId() { return actionId }

  private UUID id
  private String name
  private String type
  private String version
  private Long actionId
}

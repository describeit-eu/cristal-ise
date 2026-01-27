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

  @ConstructorProperties(["id", "name", "type", "version", "actionId"])
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

  UUID id
  String name
  String type
  String version
  Long actionId
}

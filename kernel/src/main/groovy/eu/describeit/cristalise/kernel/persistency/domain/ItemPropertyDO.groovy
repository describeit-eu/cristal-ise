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
class ItemPropertyDO {
  ItemPropertyDO(JsonObject json) { ItemPropertyDOConverter.fromJson(json, this) }

  ItemPropertyDO() {}

  @ConstructorProperties(["id", "name", "value", "isMutable", "itemId"])
  ItemPropertyDO(Long id, String name, String value, Boolean isMutable, UUID itemId) {
    this.id = id
    this.name = name
    this.value = value
    this.isMutable = isMutable
    this.itemId = itemId
  }

  @ConstructorProperties(["name", "value", "isMutable", "itemId"])
  ItemPropertyDO(String name, String value, Boolean isMutable, UUID itemId) {
    this(null, name, value, isMutable, itemId)
  }

  ItemPropertyDO(ItemPropertyDO other) {
    this(null, other.name, other.value, other.isMutable, other.itemId)
  }

  Long id
  String name
  String value
  Boolean isMutable
  UUID itemId
}

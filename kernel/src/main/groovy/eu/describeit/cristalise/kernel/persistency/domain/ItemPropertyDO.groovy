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
class ItemPropertyDO {
  ItemPropertyDO(JsonObject json) { ItemPropertyDOConverter.fromJson(json, this) }

  ItemPropertyDO() {}

  @java.beans.ConstructorProperties(["id", "name", "value", "isMutable", "itemId"])
  ItemPropertyDO(Long id, String name, String value, Boolean isMutable, UUID itemId) {
    this.id = id
    this.name = name
    this.value = value
    this.isMutable = isMutable
    this.itemId = itemId
  }

  @java.beans.ConstructorProperties(["name", "value", "isMutable", "itemId"])
  ItemPropertyDO(String name, String value, Boolean isMutable, UUID itemId) {
    this(null, name, value, isMutable, itemId)
  }

  ItemPropertyDO(ItemPropertyDO other) {
    this(null, other.name, other.value, other.isMutable, other.itemId)
  }

  ItemPropertyDO setId(Long id) { this.id = id; return this }
  ItemPropertyDO setName(String name) { this.name = name; return this }
  ItemPropertyDO setValue(String value) { this.value = value; return this }
  ItemPropertyDO setIsMutable(Boolean isMutable) { this.isMutable = isMutable; return this }
  ItemPropertyDO setItemId(UUID itemId) { this.itemId = itemId; return this }

  Long getId() { return id }
  String getName() { return name }
  String getValue() { return value }
  Boolean getIsMutable() { return isMutable }
  UUID getItemId() { return itemId }

  private Long id
  private String name
  private String value
  private Boolean isMutable
  private UUID itemId
}

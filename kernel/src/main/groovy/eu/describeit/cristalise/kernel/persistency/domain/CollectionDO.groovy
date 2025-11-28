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
class CollectionDO {
  CollectionDO(JsonObject json) { CollectionDOConverter.fromJson(json, this) }

  CollectionDO() {}

  @java.beans.ConstructorProperties(["id", "name", "version", "properties", "itemId"])
  CollectionDO(Long id, String name, String version, String properties, UUID itemId) {
    this.id = id
    this.name = name
    this.version = version
    this.properties = properties
    this.itemId = itemId
  }

  @java.beans.ConstructorProperties(["name", "version", "properties", "itemId"])
  CollectionDO(String name, String version, String properties, UUID itemId) {
    this(null, name, version, properties, itemId)
  }

  CollectionDO(CollectionDO other) {
    this(null, other.name, other.version, other.properties, other.itemId)
  }

  CollectionDO setId(Long id) { this.id = id; return this }
  CollectionDO setName(String name) { this.name = name; return this }
  CollectionDO setVersion(String version) { this.version = version; return this }
  CollectionDO setProperties(String properties) { this.properties = properties; return this }
  CollectionDO setItemId(UUID itemId) { this.itemId = itemId; return this }

  Long getId() { return id }
  String getName() { return name }
  String getVersion() { return version }
  String getProperties() { return properties }
  UUID getItemId() { return itemId }

  private Long id
  private String name
  private String version
  private String properties
  private UUID itemId
}

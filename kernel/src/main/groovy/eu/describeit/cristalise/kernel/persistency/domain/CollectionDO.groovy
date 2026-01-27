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
class CollectionDO {
  CollectionDO(JsonObject json) { CollectionDOConverter.fromJson(json, this) }

  CollectionDO() {}

  @ConstructorProperties(["id", "name", "version", "properties", "itemId"])
  CollectionDO(Long id, String name, String version, String properties, UUID itemId) {
    this.id = id
    this.name = name
    this.version = version
    this.properties = properties
    this.itemId = itemId
  }

  @ConstructorProperties(["name", "version", "properties", "itemId"])
  CollectionDO(String name, String version, String properties, UUID itemId) {
    this(null, name, version, properties, itemId)
  }

  CollectionDO(CollectionDO other) {
    this(null, other.name, other.version, other.properties as String, other.itemId)
  }

  Long id
  String name
  String version
  String properties
  UUID itemId
}

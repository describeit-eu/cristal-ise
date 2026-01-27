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
class CollectionMemberDO {
  CollectionMemberDO(JsonObject json) { CollectionMemberDOConverter.fromJson(json, this) }

  CollectionMemberDO() {}

  @ConstructorProperties(["id", "childItem", "properties", "collectionId"])
  CollectionMemberDO(Long id, UUID childItem, String properties, Long collectionId) {
    this.id = id
    this.childItem = childItem
    this.properties = properties
    this.collectionId = collectionId
  }

  @ConstructorProperties(["childItem", "properties", "collectionId"])
  CollectionMemberDO(UUID childItem, String properties, Long collectionId) {
    this(null, childItem, properties, collectionId)
  }

  CollectionMemberDO(CollectionMemberDO other) {
    this(null, other.childItem, other.properties as String, other.collectionId)
  }

  Long id
  UUID childItem
  String properties
  Long collectionId
}

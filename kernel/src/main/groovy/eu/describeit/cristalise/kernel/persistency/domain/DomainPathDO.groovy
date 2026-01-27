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
class DomainPathDO {
  DomainPathDO(JsonObject json) { DomainPathDOConverter.fromJson(json, this) }

  DomainPathDO() {}

  @ConstructorProperties(["id", "path", "itemId"])
  DomainPathDO(Long id, String path, UUID itemId) {
    this.id = id
    this.path = path
    this.itemId = itemId
  }

  @ConstructorProperties(["path", "itemId"])
  DomainPathDO(String path, UUID itemId) {
    this(null, path, itemId)
  }

  DomainPathDO(DomainPathDO other) {
    this(null, other.path, other.itemId)
  }

  Long id
  String path
  UUID itemId
}

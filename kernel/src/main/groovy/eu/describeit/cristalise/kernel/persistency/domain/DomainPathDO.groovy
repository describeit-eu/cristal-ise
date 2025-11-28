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
class DomainPathDO {
  DomainPathDO(JsonObject json) { DomainPathDOConverter.fromJson(json, this) }

  DomainPathDO() {}

  @java.beans.ConstructorProperties(["id", "path", "itemId"])
  DomainPathDO(Long id, String path, UUID itemId) {
    this.id = id
    this.path = path
    this.itemId = itemId
  }

  @java.beans.ConstructorProperties(["path", "itemId"])
  DomainPathDO(String path, UUID itemId) {
    this(null, path, itemId)
  }

  DomainPathDO(DomainPathDO other) {
    this(null, other.path, other.itemId)
  }

  DomainPathDO setId(Long id) { this.id = id; return this }
  DomainPathDO setPath(String path) { this.path = path; return this }
  DomainPathDO setItemId(UUID itemId) { this.itemId = itemId; return this }

  Long getId() { return id }
  String getPath() { return path }
  UUID getItemId() { return itemId }

  private Long id
  private String path
  private UUID itemId
}

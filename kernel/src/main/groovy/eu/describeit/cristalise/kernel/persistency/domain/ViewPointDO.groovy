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
class ViewPointDO {
  ViewPointDO(JsonObject json) { ViewPointDOConverter.fromJson(json, this) }

  ViewPointDO() {}

  @ConstructorProperties(["id", "name", "schema", "schemaVersion", "schemaName", "outcomeId", "itemId"])
  ViewPointDO(Long id, String name, UUID schema, String schemaVersion, String schemaName, Long outcomeId, UUID itemId) {
    this.id = id
    this.name = name
    this.schema = schema
    this.schemaVersion = schemaVersion
    this.schemaName = schemaName
    this.outcomeId = outcomeId
    this.itemId = itemId
  }

  @ConstructorProperties(["name", "schema", "schemaVersion", "schemaName", "outcomeId", "itemId"])
  ViewPointDO(String name, UUID schema, String schemaVersion, String schemaName, Long outcomeId, UUID itemId) {
    this(null, name, schema, schemaVersion, schemaName, outcomeId, itemId)
  }

  ViewPointDO(ViewPointDO other) {
    this(null, other.name, other.schema, other.schemaVersion, other.schemaName, other.outcomeId, other.itemId)
  }

  Long id
  String name
  UUID schema
  String schemaVersion
  String schemaName
  Long outcomeId
  UUID itemId
}

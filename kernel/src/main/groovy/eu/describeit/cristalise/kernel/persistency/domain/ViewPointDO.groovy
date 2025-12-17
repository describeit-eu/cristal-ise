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

  ViewPointDO setId(Long id) { this.id = id; return this }
  ViewPointDO setName(String name) { this.name = name; return this }
  ViewPointDO setSchema(UUID schema) { this.schema = schema; return this }
  ViewPointDO setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; return this }
  ViewPointDO setSchemaName(String schemaName) { this.schemaName = schemaName; return this }
  ViewPointDO setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; return this }
  ViewPointDO setItemId(UUID itemId) { this.itemId = itemId; return this }

  Long getId() { return id }
  String getName() { return name }
  UUID getSchema() { return schema }
  String getSchemaVersion() { return schemaVersion }
  String getSchemaName() { return schemaName }
  Long getOutcomeId() { return outcomeId }
  UUID getItemId() { return itemId }

  private Long id
  private String name
  private UUID schema
  private String schemaVersion
  private String schemaName
  private Long outcomeId
  private UUID itemId
}

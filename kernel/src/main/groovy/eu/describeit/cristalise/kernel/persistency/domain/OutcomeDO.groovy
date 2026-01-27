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
class OutcomeDO {
  OutcomeDO(JsonObject json) { OutcomeDOConverter.fromJson(json, this) }

  OutcomeDO() {}

  @ConstructorProperties(["id", "schema", "schemaVersion", "data", "eventId", "itemId"])
  OutcomeDO(Long id, UUID schema, String schemaVersion, JsonObject data, Long eventId, UUID itemId) {
    this.id = id
    this.schema = schema
    this.schemaVersion = schemaVersion
    this.data = data
    this.eventId = eventId
    this.itemId = itemId
  }

  @ConstructorProperties(["schema", "schemaVersion", "data", "eventId", "itemId"])
  OutcomeDO(UUID schema, String schemaVersion, String data, Long eventId, UUID itemId) {
    this(null, schema, schemaVersion, new JsonObject(data), eventId, itemId)
  }

  @ConstructorProperties(["schema", "schemaVersion", "data", "eventId", "itemId"])
  OutcomeDO(UUID schema, String schemaVersion, JsonObject data, Long eventId, UUID itemId) {
    this(null, schema, schemaVersion, data, eventId, itemId)
  }

  OutcomeDO(OutcomeDO other) {
    this(null, other.schema, other.schemaVersion, other.data, other.eventId, other.itemId)
  }

  Long id
  UUID schema
  String schemaVersion
  JsonObject data
  Long eventId
  UUID itemId
}

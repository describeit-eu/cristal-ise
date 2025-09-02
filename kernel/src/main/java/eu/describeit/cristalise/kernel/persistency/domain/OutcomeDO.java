package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.util.Objects;
import java.util.UUID;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
public final class OutcomeDO {
  public OutcomeDO(JsonObject json) { OutcomeDOConverter.fromJson(json, this); }

  public OutcomeDO() {}

  @java.beans.ConstructorProperties({"id", "schema", "schemaVersion", "data", "eventId", "itemId"})
  public OutcomeDO(Long id, UUID schema, String schemaVersion, String data, UUID eventId, UUID itemId) {
    this.id = id;
    this.schema = schema;
    this.schemaVersion = schemaVersion;
    this.data = data;
    this.eventId = eventId;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"schema", "schemaVersion", "data", "eventId", "itemId"})
  public OutcomeDO(UUID schema, String schemaVersion, String data, UUID eventId, UUID itemId) {
    this(null, schema, schemaVersion, data, eventId, itemId);
  }

  public OutcomeDO(OutcomeDO other) {
    this(null, other.schema, other.schemaVersion, other.data, other.eventId, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof OutcomeDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getSchema(), other.getSchema()) &&
           Objects.equals(this.getSchemaVersion(), other.getSchemaVersion()) &&
           Objects.equals(this.getData(), other.getData()) &&
           Objects.equals(this.getEventId(), other.getEventId()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getSchema(), getSchemaVersion(), getData(), getEventId(), getItemId());
  }

  @Override
  public String toString() {
    return "OutcomeDO[id=%s, schema=%s, schemaVersion='%s', data='%s', eventId=%s, itemId=%s]"
      .formatted(this.id, this.schema, this.schemaVersion, this.data, this.eventId, this.itemId);
  }

  public OutcomeDO setId(Long id) { this.id = id; return this; }
  public OutcomeDO setSchema(UUID schema) { this.schema = schema; return this; }
  public OutcomeDO setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; return this; }
  public OutcomeDO setData(String data) { this.data = data; return this; }
  public OutcomeDO setEventId(UUID eventId) { this.eventId = eventId; return this; }
  public OutcomeDO setItemId(UUID itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public UUID getSchema() { return schema; }
  public String getSchemaVersion() { return schemaVersion; }
  public String getData() { return data; }
  public UUID getEventId() { return eventId; }
  public UUID getItemId() { return itemId; }

  private Long id;
  private UUID schema;
  private String schemaVersion;
  private String data;
  private UUID eventId;
  private UUID itemId;
}

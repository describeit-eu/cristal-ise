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
public final class ViewPointDO {
  public ViewPointDO(JsonObject json) { ViewPointDOConverter.fromJson(json, this); }

  public ViewPointDO() {}

  @java.beans.ConstructorProperties({"id", "name", "schema", "schemaVersion", "schemaName", "outcomeId", "itemId"})
  public ViewPointDO(Long id, String name, UUID schema, String schemaVersion, String schemaName, Long outcomeId, UUID itemId) {
    this.id = id;
    this.name = name;
    this.schema = schema;
    this.schemaVersion = schemaVersion;
    this.schemaName = schemaName;
    this.outcomeId = outcomeId;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"name", "schema", "schemaVersion", "schemaName", "outcomeId", "itemId"})
  public ViewPointDO(String name, UUID schema, String schemaVersion, String schemaName, Long outcomeId, UUID itemId) {
    this(null, name, schema, schemaVersion, schemaName, outcomeId, itemId);
  }

  public ViewPointDO(ViewPointDO other) {
    this(null, other.name, other.schema, other.schemaVersion, other.schemaName, other.outcomeId, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ViewPointDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getName(), other.getName()) &&
           Objects.equals(this.getSchema(), other.getSchema()) &&
           Objects.equals(this.getSchemaVersion(), other.getSchemaVersion()) &&
           Objects.equals(this.getSchemaName(), other.getSchemaName()) &&
           Objects.equals(this.getOutcomeId(), other.getOutcomeId()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getSchema(), getSchemaVersion(), getSchemaName(), getOutcomeId(), getItemId());
  }

  @Override
  public String toString() {
    return "ViewPointDO[id=%s, name='%s', schema=%s, schemaVersion='%s', schemaName='%s', outcomeId=%s, itemId=%s]"
      .formatted(this.id, this.name, this.schema, this.schemaVersion, this.schemaName, this.outcomeId, this.itemId);
  }

  public ViewPointDO setId(Long id) { this.id = id; return this; }
  public ViewPointDO setName(String name) { this.name = name; return this; }
  public ViewPointDO setSchema(UUID schema) { this.schema = schema; return this; }
  public ViewPointDO setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; return this; }
  public ViewPointDO setSchemaName(String schemaName) { this.schemaName = schemaName; return this; }
  public ViewPointDO setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; return this; }
  public ViewPointDO setItemId(UUID itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public String getName() { return name; }
  public UUID getSchema() { return schema; }
  public String getSchemaVersion() { return schemaVersion; }
  public String getSchemaName() { return schemaName; }
  public Long getOutcomeId() { return outcomeId; }
  public UUID getItemId() { return itemId; }

  private Long id;
  private String name;
  private UUID schema;
  private String schemaVersion;
  private String schemaName;
  private Long outcomeId;
  private UUID itemId;
}

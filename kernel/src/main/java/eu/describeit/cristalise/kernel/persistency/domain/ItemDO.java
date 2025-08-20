package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.util.Objects;
import java.util.UUID;

/**
 * Item data object representing the item table in the database.
 */
@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
final public class ItemDO {
  public ItemDO(JsonObject json) { ItemDOConverter.fromJson(json, this); }

  public ItemDO() {}

  @java.beans.ConstructorProperties({"id", "uuid", "name", "type", "version"})
  public ItemDO(Long id, UUID uuid, String name, String type, String version) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.type = type;
    this.version = version;
  }

  @java.beans.ConstructorProperties({"uuid", "name", "type", "version"})
  public ItemDO(UUID uuid, String name, String type, String version) {
    this(null, uuid, name, type, version);
  }

  public ItemDO(ItemDO other) {
    this(null, other.uuid, other.name, other.type, other.version);
  }

    @Override
  public boolean equals(Object o) {
    if (!(o instanceof ItemDO other)) return false;

    return
      Objects.equals(this.getId(),      other.getId()) &&
      Objects.equals(this.getUuid(),    other.getUuid()) &&
      Objects.equals(this.getName(),    other.getName()) &&
      Objects.equals(this.getType(),    other.getType()) &&
      Objects.equals(this.getVersion(), other.getVersion());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUuid(), getName(), getType(), getVersion());
  }

  @Override
  public String toString() {
    return "ItemDO[id=%s, uuid=%s, name='%s', type='%s', version='%s']"
      .formatted(this.id, this.uuid, this.name, this.type, this.version);
  }

  public ItemDO setId(Long id) { this.id = id; return this; }
  public ItemDO setUuid(UUID uuid) { this.uuid = uuid; return this; }
  public ItemDO setName(String name) { this.name = name; return this; }
  public ItemDO setType(String type) { this.type = type; return this; }
  public ItemDO setVersion(String version) { this.version = version; return this; }

  public Long getId() { return id; }
  public UUID getUuid() { return uuid; }
  public String getName() { return name; }
  public String getType() { return type; }
  public String getVersion() { return version; }

  private Long id;
  private UUID uuid;
  private String name;
  private String type;
  private String version;
}

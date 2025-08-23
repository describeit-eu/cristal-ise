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

  @java.beans.ConstructorProperties({"id", "name", "type", "version"})
  public ItemDO(UUID id, String name, String type, String version) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.version = version;
  }

  public ItemDO(ItemDO other) {
    this(other.id, other.name, other.type, other.version);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ItemDO other)) return false;

    return
      Objects.equals(this.getId(),      other.getId()) &&
      Objects.equals(this.getName(),    other.getName()) &&
      Objects.equals(this.getType(),    other.getType()) &&
      Objects.equals(this.getVersion(), other.getVersion());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getType(), getVersion());
  }

  @Override
  public String toString() {
    return "ItemDO[id=%s,name='%s', type='%s', version='%s']"
      .formatted(this.id, this.name, this.type, this.version);
  }

  public ItemDO setId(UUID id) { this.id = id; return this; }
  public ItemDO setName(String name) { this.name = name; return this; }
  public ItemDO setType(String type) { this.type = type; return this; }
  public ItemDO setVersion(String version) { this.version = version; return this; }

  public UUID getId() { return id; }
  public String getName() { return name; }
  public String getType() { return type; }
  public String getVersion() { return version; }

  private UUID id;
  private String name;
  private String type;
  private String version;
}

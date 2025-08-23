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
public final class CollectionDO {
  public CollectionDO(JsonObject json) { CollectionDOConverter.fromJson(json, this); }

  public CollectionDO() {}

  @java.beans.ConstructorProperties({"id", "name", "version", "properties", "itemId"})
  public CollectionDO(Long id, String name, String version, String properties, UUID itemId) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.properties = properties;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"name", "version", "properties", "itemId"})
  public CollectionDO(String name, String version, String properties, UUID itemId) {
    this(null, name, version, properties, itemId);
  }

  public CollectionDO(CollectionDO other) {
    this(null, other.name, other.version, other.properties, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CollectionDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getName(), other.getName()) &&
           Objects.equals(this.getVersion(), other.getVersion()) &&
           Objects.equals(this.getProperties(), other.getProperties()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getVersion(), getProperties(), getItemId());
  }

  @Override
  public String toString() {
    return "CollectionDO[id=%s, name='%s', version='%s', properties='%s', itemId=%s]"
      .formatted(this.id, this.name, this.version, this.properties, this.itemId);
  }

  public CollectionDO setId(Long id) { this.id = id; return this; }
  public CollectionDO setName(String name) { this.name = name; return this; }
  public CollectionDO setVersion(String version) { this.version = version; return this; }
  public CollectionDO setProperties(String properties) { this.properties = properties; return this; }
  public CollectionDO setItemId(UUID itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getVersion() { return version; }
  public String getProperties() { return properties; }
  public UUID getItemId() { return itemId; }

  private Long id;
  private String name;
  private String version;
  private String properties;
  private UUID itemId;
}

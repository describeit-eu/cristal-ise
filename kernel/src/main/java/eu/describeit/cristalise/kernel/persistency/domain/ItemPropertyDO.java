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
public final class ItemPropertyDO {
  public ItemPropertyDO(JsonObject json) { ItemPropertyDOConverter.fromJson(json, this); }

  public ItemPropertyDO() {}

  @java.beans.ConstructorProperties({"id", "name", "value", "isMutable", "itemId"})
  public ItemPropertyDO(Long id, String name, String value, Boolean isMutable, UUID itemId) {
    this.id = id;
    this.name = name;
    this.value = value;
    this.isMutable = isMutable;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"name", "value", "isMutable", "itemId"})
  public ItemPropertyDO(String name, String value, Boolean isMutable, UUID itemId) {
    this(null, name, value, isMutable, itemId);
  }

  public ItemPropertyDO(ItemPropertyDO other) {
    this(null, other.name, other.value, other.isMutable, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ItemPropertyDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getName(), other.getName()) &&
           Objects.equals(this.getValue(), other.getValue()) &&
           Objects.equals(this.getIsMutable(), other.getIsMutable()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getValue(), getIsMutable(), getItemId());
  }

  @Override
  public String toString() {
    return "ItemPropertyDO[id=%s, name='%s', value='%s', isMutable=%s, itemId=%s]"
      .formatted(this.id, this.name, this.value, this.isMutable, this.itemId);
  }

  public ItemPropertyDO setId(Long id) { this.id = id; return this; }
  public ItemPropertyDO setName(String name) { this.name = name; return this; }
  public ItemPropertyDO setValue(String value) { this.value = value; return this; }
  public ItemPropertyDO setIsMutable(Boolean isMutable) { this.isMutable = isMutable; return this; }
  public ItemPropertyDO setItemId(UUID itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getValue() { return value; }
  public Boolean getIsMutable() { return isMutable; }
  public UUID getItemId() { return itemId; }

  private Long id;
  private String name;
  private String value;
  private Boolean isMutable;
  private UUID itemId;
}

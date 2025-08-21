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
public final class CollectionMemberDO {
  public CollectionMemberDO(JsonObject json) { CollectionMemberDOConverter.fromJson(json, this); }

  public CollectionMemberDO() {}

  @java.beans.ConstructorProperties({"id", "childItem", "properties", "collectionId"})
  public CollectionMemberDO(Long id, UUID childItem, String properties, Long collectionId) {
    this.id = id;
    this.childItem = childItem;
    this.properties = properties;
    this.collectionId = collectionId;
  }

  @java.beans.ConstructorProperties({"childItem", "properties", "collectionId"})
  public CollectionMemberDO(UUID childItem, String properties, Long collectionId) {
    this(null, childItem, properties, collectionId);
  }

  public CollectionMemberDO(CollectionMemberDO other) {
    this(null, other.childItem, other.properties, other.collectionId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof CollectionMemberDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getChildItem(), other.getChildItem()) &&
           Objects.equals(this.getProperties(), other.getProperties()) &&
           Objects.equals(this.getCollectionId(), other.getCollectionId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getChildItem(), getProperties(), getCollectionId());
  }

  @Override
  public String toString() {
    return "CollectionMemberDO[id=%s, childItem=%s, properties='%s', collectionId=%s]"
      .formatted(this.id, this.childItem, this.properties, this.collectionId);
  }

  public CollectionMemberDO setId(Long id) { this.id = id; return this; }
  public CollectionMemberDO setChildItem(UUID childItem) { this.childItem = childItem; return this; }
  public CollectionMemberDO setProperties(String properties) { this.properties = properties; return this; }
  public CollectionMemberDO setCollectionId(Long collectionId) { this.collectionId = collectionId; return this; }

  public Long getId() { return id; }
  public UUID getChildItem() { return childItem; }
  public String getProperties() { return properties; }
  public Long getCollectionId() { return collectionId; }

  private Long id;
  private UUID childItem;
  private String properties;
  private Long collectionId;
}

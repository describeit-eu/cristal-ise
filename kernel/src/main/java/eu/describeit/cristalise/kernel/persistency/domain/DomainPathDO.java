package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.util.Objects;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
public final class DomainPathDO {
  public DomainPathDO(JsonObject json) { DomainPathDOConverter.fromJson(json, this); }

  public DomainPathDO() {}

  @java.beans.ConstructorProperties({"id", "path", "itemId"})
  public DomainPathDO(Long id, String path, Long itemId) {
    this.id = id;
    this.path = path;
    this.itemId = itemId;
  }

  @java.beans.ConstructorProperties({"path", "itemId"})
  public DomainPathDO(String path, Long itemId) {
    this(null, path, itemId);
  }

  public DomainPathDO(DomainPathDO other) {
    this(null, other.path, other.itemId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DomainPathDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getPath(), other.getPath()) &&
           Objects.equals(this.getItemId(), other.getItemId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getPath(), getItemId());
  }

  @Override
  public String toString() {
    return "DomainPathDO[id=%s, path='%s', itemId=%s]".formatted(this.id, this.path, this.itemId);
  }

  public DomainPathDO setId(Long id) { this.id = id; return this; }
  public DomainPathDO setPath(String path) { this.path = path; return this; }
  public DomainPathDO setItemId(Long itemId) { this.itemId = itemId; return this; }

  public Long getId() { return id; }
  public String getPath() { return path; }
  public Long getItemId() { return itemId; }

  private Long id;
  private String path;
  private Long itemId;
}

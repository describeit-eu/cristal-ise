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
public final class ActionDO {
  public enum ActionType { ELEMENTARY, SEQUENCE, LOOP, SPLIT }
  public ActionDO(JsonObject json) { ActionDOConverter.fromJson(json, this); }

  public ActionDO() {}

  @java.beans.ConstructorProperties({"id", "name", "path", "version", "properties", "type", "layout", "parentId"})
  public ActionDO(Long id, String name, String path, String version, String properties, ActionType type, String layout, Long parentId) {
    this.id = id;
    this.name = name;
    this.path = path;
    this.version = version;
    this.properties = properties;
    this.type = type;
    this.layout = layout;
    this.parentId = parentId;
  }

  @java.beans.ConstructorProperties({"name", "path", "version", "properties", "type", "layout", "parentId"})
  public ActionDO(String name, String path, String version, String properties, ActionType type, String layout, Long parentId) {
    this(null, name, path, version, properties, type, layout, parentId);
  }

  public ActionDO(ActionDO other) {
    this(null, other.name, other.path, other.version, other.properties, other.type, other.layout, other.parentId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ActionDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getName(), other.getName()) &&
           Objects.equals(this.getPath(), other.getPath()) &&
           Objects.equals(this.getVersion(), other.getVersion()) &&
           Objects.equals(this.getProperties(), other.getProperties()) &&
           Objects.equals(this.getType(), other.getType()) &&
           Objects.equals(this.getLayout(), other.getLayout()) &&
           Objects.equals(this.getParentId(), other.getParentId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getPath(), getVersion(), getProperties(), getType(), getLayout(), getParentId());
  }

  @Override
  public String toString() {
    return "ActionDO[id=%s, name='%s', path='%s', version='%s', properties='%s', type=%s, layout='%s', parentId=%s]"
      .formatted(this.id, this.name, this.path, this.version, this.properties, this.type, this.layout, this.parentId);
  }

  public ActionDO setId(Long id) { this.id = id; return this; }
  public ActionDO setName(String name) { this.name = name; return this; }
  public ActionDO setPath(String path) { this.path = path; return this; }
  public ActionDO setVersion(String version) { this.version = version; return this; }
  public ActionDO setProperties(String properties) { this.properties = properties; return this; }
  public ActionDO setType(ActionType type) { this.type = type; return this; }
  public ActionDO setLayout(String layout) { this.layout = layout; return this; }
  public ActionDO setParentId(Long parentId) { this.parentId = parentId; return this; }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getPath() { return path; }
  public String getVersion() { return version; }
  public String getProperties() { return properties; }
  public ActionType getType() { return type; }
  public String getLayout() { return layout; }
  public Long getParentId() { return parentId; }

  private Long id;
  private String name;
  private String path;
  private String version;
  private String properties;
  private ActionType type;
  private String layout;
  private Long parentId;
}

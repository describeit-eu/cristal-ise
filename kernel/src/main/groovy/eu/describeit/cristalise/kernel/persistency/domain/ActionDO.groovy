package eu.describeit.cristalise.kernel.persistency.domain

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.ToString
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
@ToString(includePackage = false, includeNames = true, excludes = 'properties, layout' )
class ActionDO {
  enum ActionType { ELEMENTARY, SEQUENCE, LOOP, SPLIT, SCRIPTED }

  ActionDO() {}

  ActionDO(JsonObject json) { ActionDOConverter.fromJson(json, this) }

  @ConstructorProperties(["id", "name", "path", "version", "properties", "type", "layout", "parentId"])
  ActionDO(Long id, String name, String path, String version, String properties, ActionType type, String layout, Long parentId) {
    this.id = id
    this.name = name
    this.path = path
    this.version = version
    this.properties = properties
    this.type = type
    this.layout = layout
    this.parentId = parentId
  }

  @ConstructorProperties(["name", "path", "version", "properties", "type", "layout", "parentId"])
  ActionDO(String name, String path, String version, String properties, ActionType type, String layout, Long parentId) {
    this(null, name, path, version, properties, type, layout, parentId)
  }

  ActionDO(ActionDO other) {
    this(null, other.name, other.path, other.version, other.properties, other.type, other.layout, other.parentId)
  }
/*
  ActionDO setId(Long id) { this.id = id; return this }
  ActionDO setName(String name) { this.name = name; return this }
  ActionDO setPath(String path) { this.path = path; return this }
  ActionDO setVersion(String version) { this.version = version; return this }
  ActionDO setProperties(String properties) { this.properties = properties; return this }
  ActionDO setType(ActionType type) { this.type = type; return this }
  ActionDO setLayout(String layout) { this.layout = layout; return this }
  ActionDO setParentId(Long parentId) { this.parentId = parentId; return this }

  Long getId() { return id }
  String getName() { return name }
  String getPath() { return path }
  String getVersion() { return version }
  String getProperties() { return properties }
  ActionType getType() { return type }
  String getLayout() { return layout }
  Long getParentId() { return parentId }
*/
  Long id
  String name
  String path
  String version
  String properties
  ActionType type
  String layout
  Long parentId
}

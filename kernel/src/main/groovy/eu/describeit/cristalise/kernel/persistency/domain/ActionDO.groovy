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

  Long id
  String name
  String path
  String version
  String properties
  ActionType type
  String layout
  Long parentId
  UUID stateMachine
  String stateMachineVersion
}

package eu.describeit.cristalise.kernel.persistency.domain

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.vertx.codegen.annotations.DataObject
import io.vertx.codegen.format.SnakeCase
import io.vertx.codegen.json.annotations.JsonGen
import io.vertx.core.buffer.Buffer
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
class AttachmentDO {
  AttachmentDO(JsonObject json) { AttachmentDOConverter.fromJson(json, this) }

  AttachmentDO() {}

  @ConstructorProperties(["id", "name", "type", "fileName", "data", "outcomeId"])
  AttachmentDO(Long id, String name, String type, String fileName, Buffer data, Long outcomeId) {
    this.id = id
    this.name = name
    this.type = type
    this.fileName = fileName
    this.data = data
    this.outcomeId = outcomeId
  }

  @ConstructorProperties(["name", "type", "fileName", "data", "outcomeId"])
  AttachmentDO(String name, String type, String fileName, Buffer data, Long outcomeId) {
    this(null, name, type, fileName, data, outcomeId)
  }

  AttachmentDO(AttachmentDO other) {
    this(null, other.name, other.type, other.fileName, other.data, other.outcomeId)
  }

  Long id
  String name
  String type
  String fileName
  Buffer data
  Long outcomeId
}

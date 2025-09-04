package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;

import java.util.Objects;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
public final class AttachmentDO {
  public AttachmentDO(JsonObject json) { AttachmentDOConverter.fromJson(json, this); }

  public AttachmentDO() {}

  @java.beans.ConstructorProperties({"id", "name", "type", "fileName", "data", "outcomeId"})
  public AttachmentDO(Long id, String name, String type, String fileName, Buffer data, Long outcomeId) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.fileName = fileName;
    this.data = data;
    this.outcomeId = outcomeId;
  }

  @java.beans.ConstructorProperties({"name", "type", "fileName", "data", "outcomeId"})
  public AttachmentDO(String name, String type, String fileName, Buffer data, Long outcomeId) {
    this(null, name, type, fileName, data, outcomeId);
  }

  public AttachmentDO(AttachmentDO other) {
    this(null, other.name, other.type, other.fileName, other.data, other.outcomeId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AttachmentDO other)) return false;
    return Objects.equals(this.getId(), other.getId()) &&
           Objects.equals(this.getName(), other.getName()) &&
           Objects.equals(this.getType(), other.getType()) &&
           Objects.equals(this.getFileName(), other.getFileName()) &&
           Objects.equals(this.getData(), other.getData()) &&
           Objects.equals(this.getOutcomeId(), other.getOutcomeId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName(), getType(), getFileName(), getData(), getOutcomeId());
  }

  @Override
  public String toString() {
    return "AttachmentDO[id=%s, name='%s', type='%s', fileName='%s', data=%s, outcomeId=%s]"
      .formatted(this.id, this.name, this.type, this.fileName, this.data, this.outcomeId);
  }

  public AttachmentDO setId(Long id) { this.id = id; return this; }
  public AttachmentDO setName(String name) { this.name = name; return this; }
  public AttachmentDO setType(String type) { this.type = type; return this; }
  public AttachmentDO setFileName(String fileName) { this.fileName = fileName; return this; }
  public AttachmentDO setData(Buffer data) { this.data = data; return this; }
  public AttachmentDO setOutcomeId(Long outcomeId) { this.outcomeId = outcomeId; return this; }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getType() { return type; }
  public String getFileName() { return fileName; }
  public Buffer getData() { return data; }
  public Long getOutcomeId() { return outcomeId; }

  private Long id;
  private String name;
  private String type;
  private String fileName;
  private Buffer data;
  private Long outcomeId;
}

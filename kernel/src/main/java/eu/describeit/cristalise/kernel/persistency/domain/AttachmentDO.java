package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class AttachmentDO {
  public AttachmentDO(JsonObject json) { AttachmentDOConverter.fromJson(json, this); }

  private Long id;
  private String name;
  private String type;
  private String fileName;
  private Buffer data;
  private Long outcomeId;
}

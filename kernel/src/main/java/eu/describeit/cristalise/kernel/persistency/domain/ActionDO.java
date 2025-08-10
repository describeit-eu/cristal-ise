package eu.describeit.cristalise.kernel.persistency.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
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
public class ActionDO {
  public ActionDO(JsonObject json) { ActionDOConverter.fromJson(json, this); }

  private Long id;
  private String name;
  private String path;
  private String version;
  private String properties;
  private Boolean isComposite;
  private String layout;
  private Long parentId;
  private Long itemId;
}

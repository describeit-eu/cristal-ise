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

import java.time.LocalDateTime;
import java.util.UUID;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class EventDO {
  public EventDO(JsonObject json) { EventDOConverter.fromJson(json, this); }

  private Long id;
  private String itemVersion;
  private UUID actionDesc;
  private String actionDescVersion;
  private UUID script;
  private String scriptVersion;
  private UUID stateMachineDesc;
  private String stateMachineVersion;
  private String userLogin;
  private LocalDateTime timestamp;
  private String actionProperties;
  private Long itemId;
}

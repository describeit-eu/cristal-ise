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

import java.util.UUID;

@DataObject
@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class CollectionMemberDO {
  public CollectionMemberDO(JsonObject json) { CollectionMemberDOConverter.fromJson(json, this); }

  private Long id;
  private UUID childItem;
  private String properties;
  private Long collectionId;
}

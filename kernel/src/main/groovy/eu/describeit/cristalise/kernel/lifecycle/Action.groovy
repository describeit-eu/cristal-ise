package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.persistency.domain.ActionDO.ActionType
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

@CompileStatic
interface Action {
  Long getId()
  String getName()
  String getPath()
  ActionType getType()

  Future<JsonObject> request(
    final ItemProxy item,
    final ItemProxy actor,
    final String actionPath,
    final String transitionID,
    final JsonObject inputOutcome
  )
}

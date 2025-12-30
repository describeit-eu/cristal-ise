package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.item.ItemProxy
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

@CompileStatic
interface Action {
  Long getId()
  String getName()
  String getPath()

  Future<JsonObject> request(final ItemProxy item, final ItemProxy actor, final JsonObject inputOutcome, final  String transitionID)
}

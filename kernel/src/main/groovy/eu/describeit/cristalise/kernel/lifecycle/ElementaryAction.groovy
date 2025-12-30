package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.item.ItemProxy
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

@Slf4j
@CompileStatic
class ElementaryAction extends AbstractAction {

  @Override
  Future<JsonObject> request(ItemProxy item, ItemProxy actor, JsonObject inputOutcome, String transitionID) {
    return null
  }
}

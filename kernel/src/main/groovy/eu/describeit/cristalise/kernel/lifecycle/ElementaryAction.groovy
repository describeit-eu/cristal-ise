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
  Future<JsonObject> request(
    final ItemProxy item,
    final ItemProxy actor,
    final String actionPath,
    final String transitionID,
    final JsonObject inputOutcome)
  {
    log.warn('request() - DUMB IMPLEMENTATION item:{}/{} action({}):{} ', item.type, item.name, dataObject.type, actionPath)

    def outputOutcome = inputOutcome.copy().put('name', item.name)

    return Future.succeededFuture(outputOutcome)
  }
}

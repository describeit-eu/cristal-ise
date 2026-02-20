package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.lifecycle.builtin.BuiltInActionContainer
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

import javax.inject.Inject

@Slf4j
@CompileStatic
class LifeCycle {

  @Inject
  BuiltInActionContainer builtInActions

  CompositeAction domainActions

  Future<JsonObject> request(
    final ItemProxy item,
    final ItemProxy actor,
    final String actionPath,
    final String transitionID,
    final JsonObject inputOutcome)
  {
//    log.info('request() - item:{}/{} action({}):{} ', item.type, item.name, dataObject.type, actionPath)

    if (actionPath.startsWith('builtIn/')) {
      def actionName = actionPath.substring('builtIn/'.length())
      return builtInActions.request(item, actor, actionName, inputOutcome)
    }
    else {
      return domainActions.request(item, actor, actionPath, transitionID, inputOutcome)
    }
  }
}

package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import eu.describeit.cristalise.kernel.persistency.repository.ActionRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

import static eu.describeit.cristalise.kernel.persistency.domain.ActionDO.ActionType.*

@Slf4j
@CompileStatic
abstract class AbstractCompositeAction extends AbstractAction implements CompositeAction {
  List<Action> actions = []

  static Action createAction(ActionDO actionDO) {
    switch (actionDO.type) {
      case ELEMENTARY: return new ElementaryAction(dataObject: actionDO)
      case SEQUENCE:   return new SequencingCompositeAction(dataObject: actionDO)
      case LOOP:       return new LoopingCompositeAction(dataObject: actionDO)
      case SPLIT:      return new SplittingCompositeAction(dataObject: actionDO)
      case SCRIPTED:   return new ScriptedCompositeAction(dataObject: actionDO)
      default:
        throw new IllegalArgumentException("Unimplemented lifeCycle type:$actionDO")
    }
  }

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

  @Override
  Future<Void> initialise(ActionRepository repo) {
    return repo.findByParentId(dataObject.id).compose { actionDOList ->
      for (def actionDO: actionDOList) {
        log.debug('initialise() - adding:{}', actionDO)
        actions.add(createAction(actionDO))
      }
      return Future.succeededFuture()
    } as Future<Void>
  }

  @Override
  Future<Action> findAction(String actionPath) {
    return Future.failedFuture('Unimplemented feature: findAction()')
  }
}

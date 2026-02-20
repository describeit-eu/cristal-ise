package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.lifecycle.builtin.BuiltInActionContainer
import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import eu.describeit.cristalise.kernel.persistency.domain.JobDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

import javax.inject.Inject

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
  Future<JobDO> calculateNextJobs() {
    return Future.failedFuture('Unimplemented')
  }

  @Override
  Future<JsonObject> request(
    final ItemProxy item,
    final ItemProxy actor,
    final String actionPath,
    final String transitionID,
    final JsonObject inputOutcome)
  {
    log.info('request() - item:{}/{} action({}):{} ', item.type, item.name, dataObject.type, actionPath)

    return findAction(actionPath).compose { Action action ->
      return action.request(item, actor, actionPath, transitionID, inputOutcome)
    }
//    .onFailure { Throwable ex ->
//      return Future.failedFuture(ex)
//    }
  }

  @Override
  Future<Void> initialise(RepositoryWrapper storage) {
    return storage.getChildActionDOs(dataObject.id).compose { actionDOList ->
      List<Future<Void>> initFutures = []

      for (def actionDO: actionDOList) {
        Action childAction = createAction(actionDO)
        actions.add(childAction)

        if (childAction.type != ELEMENTARY) {
          def ca = (CompositeAction) childAction
          initFutures.add(ca.initialise(storage))
        }
      }

      log.info('initialise() - actions:{}', actions.collect {it.name})

      return Future.all(initFutures).mapEmpty()
    } as Future<Void>
  }

  @Override
  Future<Action> findAction(String actionPath) {
    if (actionPath.startsWith('/')) actionPath = actionPath.substring(1)

    return handleFindAction(actionPath.split('/'))
      .compose { Action action ->
        if (action) return Future.succeededFuture(action)
        else        return Future.failedFuture("Action:$actionPath not found")
      } as Future<Action>
  }

  private Future<Action> handleFindAction(String ... actionPath) {
    log.debug('findAction() - name:{} actionPath:{}', name, actionPath)

    if (actionPath[0] == this.name) actionPath = actionPath.drop(1)

    if (actionPath.size() == 0) {
      return Future.succeededFuture((Action)this)
    } else if (actionPath.size() == 1) {
      Action foundAction = actions.find {it.name == actionPath[0] }

      if (foundAction) {
        log.debug('findAction() - FOUND action:{}', foundAction.name)
        return Future.succeededFuture(foundAction)
      } else {
        return Future.succeededFuture(null)
      }
    } else {
      return handleFindAction(actionPath)
    }
  }
}

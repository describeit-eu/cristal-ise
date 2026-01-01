package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import eu.describeit.cristalise.kernel.persistency.repository.ActionRepository
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface CompositeAction extends Action {
  Future<Void> initialise(ActionRepository repo)

  List<Action> getActions()
  Future<Action> findAction(String actionPath)
}

package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.Storage
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface CompositeAction extends Action {
  Future<Void> initialise(Storage storage)

  List<Action> getActions()
  Future<Action> findAction(String actionPath)
}

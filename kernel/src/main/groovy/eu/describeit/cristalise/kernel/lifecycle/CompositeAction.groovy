package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.ItemStorage
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface CompositeAction extends Action {
  Future<Void> initialise(ItemStorage storage)

  List<Action> getActions()
  Future<Action> findAction(String actionPath)
}

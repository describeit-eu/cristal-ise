package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface CompositeAction extends Action {
  Future<Void> initialise()

  Future<Action> findAction(String actionPath)
}

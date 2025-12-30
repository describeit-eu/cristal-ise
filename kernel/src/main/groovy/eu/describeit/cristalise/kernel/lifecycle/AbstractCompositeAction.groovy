package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future

@Slf4j
@CompileStatic
abstract class AbstractCompositeAction extends AbstractAction implements CompositeAction {
  List<Action> actions = []

  @Override
  Future<Void> initialise() {
  }

  @Override
  Future<Action> findAction(String actionPath) {
    return null
  }
}

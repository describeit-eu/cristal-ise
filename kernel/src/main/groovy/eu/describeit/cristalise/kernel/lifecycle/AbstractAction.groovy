package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future

@Slf4j
@CompileStatic
abstract class AbstractAction implements Action {
  ActionDO actionDO

  @Override
  Long getId() {
    return actionDO?.id
  }

  @Override
  String getName() {
    return actionDO?.name
  }

  @Override
  String getPath() {
    return actionDO?.path
  }
}

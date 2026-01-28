package eu.describeit.cristalise.kernel.lifecycle

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import eu.describeit.cristalise.kernel.statemachine.State
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
abstract class AbstractAction implements Action {
  Integer currentStateID
  ActionDO dataObject

  @Override
  Long getId() {
    return dataObject?.id
  }

  @Override
  String getName() {
    return dataObject?.name
  }

  @Override
  String getPath() {
    return dataObject?.path
  }

  @Override
  ActionDO.ActionType getType() {
    return dataObject?.type
  }
}

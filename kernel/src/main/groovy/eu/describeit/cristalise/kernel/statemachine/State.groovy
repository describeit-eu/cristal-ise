package eu.describeit.cristalise.kernel.statemachine

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
@Canonical
class State {

  int id
  String name

  /**
   * If true, this state deactivates the current Action and the lifecycle/workflow proceeds
   */
  boolean finishing = false
  /**
   * If true, this state activates the current Action and the lifecycle/workflow proceeds
   */
  boolean initial = false

  Set<Integer> transitionIds = new TreeSet<>()

  protected void addTransition(Transition transition) {
    transitionIds.add(transition.id)
    log.info("Added transition {} to state {}", transition.name, name)
  }

  @JsonIgnore
  boolean isBlocking() {
    transitionIds.isEmpty() && !finishing
  }
}

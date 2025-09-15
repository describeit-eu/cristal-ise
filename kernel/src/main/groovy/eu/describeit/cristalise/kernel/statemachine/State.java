package eu.describeit.cristalise.kernel.statemachine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.TreeSet;

@Data @Slf4j
public class State {

  int id;
  String name;

  /**
   * If true, this state deactivates the current Action and the lifecycle/workflow proceeds
   */
  boolean finishing = false;
  /**
   * If true, this state activates the current Action and the lifecycle/workflow proceeds
   */
  boolean initial = false;

  Set<Integer> transitionIds;

  public State() {
    transitionIds = new TreeSet<>();
  }

  public State(int id, String name) {
    this();
    this.id = id;
    this.name = name;
  }

  protected void addTransition(Transition transition) {
    transitionIds.add(transition.getId());
    log.info("Added transition {} to state {}", transition.getName(), name);
  }

  @JsonIgnore
  public boolean isBlocking() {
    return transitionIds.isEmpty() && !finishing;
  }
}

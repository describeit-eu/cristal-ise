package eu.describeit.cristalise.kernel.statemachine;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
public class Transition {

  int id;
  String name;

  int originStateId = -1;
  int targetStateId = -1;

  String reservation;

  boolean errorHandler = false;

  /**
   * The name of the Boolean property that enables/disables this transition e.g.'Skippable'.
   * If no property name is specified, the Transition is enabled
   */
  String enablerProp;
  /**
   * Whether the Action must be active for this transition to be available (activation property)
   */
  boolean requiresActive = true;
  /**
   * Whether the target state is a finishing state (activation property)
   */
  boolean finishing;
  /**
   * Whether the transition reInitializes the Action
   */
  boolean reInitializes = false;

  public Transition() {
  }

  public Transition(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public Transition(int id, String name, int originStateId, int targetStateId) {
    this(id, name);
    this.originStateId = originStateId;
    this.targetStateId = targetStateId;
  }

  public void setReInitializes(boolean reinit) {
    if (finishing && reinit) throw new RuntimeException("Transition '" + name + "' cannot be both reinitializing and finishing");
    reInitializes = reinit;
  }

  protected boolean resolveStates(List<State> states) {
    boolean allFound = true;

    var originState = states.stream().filter(s -> s.getId() == originStateId).findFirst().orElse(null);
    if (originState != null) {
      originState.addTransition(this);
    } else {
      log.warn("Transition '{}' has invalid originStateId:{}", name, originStateId);
      allFound = false;
    }

    var targetState = states.stream().filter(s -> s.getId() == targetStateId).findFirst().orElse(null);
    if (targetState != null) {
      targetState.addTransition(this);
    } else {
      log.warn("Transition '{}' has invalid targetStateId:{}", name, targetStateId);
      allFound = false;
    }

    return allFound;
  }
}

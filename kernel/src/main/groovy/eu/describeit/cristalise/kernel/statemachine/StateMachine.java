package eu.describeit.cristalise.kernel.statemachine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.describeit.cristalise.kernel.BuiltInResources;
import eu.describeit.cristalise.kernel.DescriptionObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static eu.describeit.cristalise.kernel.BuiltInResources.STATE_MACHINE_RESOURCE;

@Slf4j @Data
public class StateMachine implements DescriptionObject {
  private String namespace;
  private String name;
  private Integer version;

  private UUID itemID;

  private List<State> states;
  private List<Transition> transitions;

  Integer initialStateId;
  List<Integer> finalStateIds;

  public StateMachine() {
    states = new ArrayList<State>();
    transitions = new ArrayList<Transition>();
  }

  public StateMachine(String name, Integer version) {
    this();
    this.name = name;
    this.version = version;
  }

  /**
   * Stores the next State id. -1 means that the value was not initialized yet
   * (e.g. after unmarshall from xml)
   */
  private int nextStateId = -1;

  /**
   * Stores the next Transition id. -1 means that the value was not
   * initialized yet (e.g. after unmarshall from xml)
   */
  private int nextTransId = -1;

  /**
   * Computes the next State id. When loaded from XML, the next id calculated
   * from the existing States
   *
   * @return the next state id
   */
  private int getNextStateId() {
    if (nextStateId == -1) {
      for (State s : states) {
        if (s.id > nextStateId) nextStateId = s.id;
      }
      nextStateId++;
    }
    return nextStateId++;
  }

  /**
   * Computes the next Transition id. When loaded from XML, the next id
   * calculated from the existing Transitions
   *
   * @return the next state id
   */
  private int getNextTransId() {
    if (nextTransId == -1) {
      for (Transition t : transitions) {
        if (t.id > nextTransId) nextTransId = t.id;
      }
      nextTransId++;
    }
    return nextTransId++;
  }

  /**
   * Factory method to create a new State for the given name.
   * It does NOT check whether the name exists or not
   *
   * @param name the name of the State
   * @return the new State
   */
  public State createState(String name) {
    State newState = new State(getNextStateId(), name);
    states.add(newState);
    log.debug("createState() - created:{} id:{}", name, newState.id);
    return newState;
  }

  /**
   * Factory method to create a new Transition for the given name.
   * It does NOT check whether the name exists or not
   *
   * @param name the name of the Transition
   * @return the new Transition
   */
  public Transition createTransition(String name) {
    Transition newTrans = new Transition(getNextTransId(), name);
    transitions.add(newTrans);
    log.debug("createTransition() - created:{} id:{}", name, newTrans.id);
    return newTrans;
  }

  public void setStates(ArrayList<State> newStates) {
    this.states = newStates;
    validate();
  }

  public void setTransitions(ArrayList<Transition> newTransitions) {
    this.transitions = newTransitions;
    validate();
  }

  public boolean validate() {
    boolean isCoherent = initialStateId != null && getState(initialStateId) != null;

    for (Transition trans : transitions) {
      isCoherent = isCoherent && trans.resolveStates(states);
    }

    log.debug("validate() - name:{} isCoherent:{}", name, isCoherent);
    return isCoherent;
  }

  public void setInitialState(State initialState) {
    setInitialStateId(initialState.getId());
  }

  public void setInitialStateId(int initialStateId) {
    var s = getState(initialStateId);
    Objects.requireNonNull(s);
    s.setInitial(true);
    this.initialStateId = initialStateId;
  }

  public Transition getTransition(int transitionID) {
    return transitions.stream().filter(t -> t.getId() == transitionID).findFirst().orElse(null);
  }

  public Transition getTransition(String name) {
    return transitions.stream().filter(t -> t.getName().equals(name)).findFirst().orElse(null);
  }

  public State getState(int stateID) {
    return states.stream().filter(s -> s.getId() == stateID).findFirst().orElse(null);
  }

  public State getState(String name) {
    return states.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
  }

  @JsonIgnore
  @Override
  public BuiltInResources getResourceType() {
    return STATE_MACHINE_RESOURCE;
  }
}

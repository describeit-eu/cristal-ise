package eu.describeit.cristalise.kernel.statemachine

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.describeit.cristalise.kernel.BuiltInResources
import eu.describeit.cristalise.kernel.DescriptionObject
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import static eu.describeit.cristalise.kernel.BuiltInResources.STATE_MACHINE_RESOURCE

@Slf4j
@CompileStatic
@Canonical
class StateMachine implements DescriptionObject {
    String namespace
    String name
    String version

    UUID itemID

    List<State> states = []
    List<Transition> transitions = []

    Integer initialStateId
    List<Integer> finalStateIds

    StateMachine() {
    }

    StateMachine(String name, String version) {
        this.name = name
        this.version = version
    }

    /**
     * Stores the next State id. -1 means that the value was not initialized yet
     * (e.g. after unmarshall from xml)
     */
    private int nextStateId = -1

    /**
     * Stores the next Transition id. -1 means that the value was not
     * initialized yet (e.g. after unmarshall from xml)
     */
    private int nextTransId = -1

    /**
     * Computes the next State id. When loaded from XML, the next id calculated
     * from the existing States
     *
     * @return the next state id
     */
    private int getNextStateId() {
        if (nextStateId == -1) {
            for (State s : states) {
                if (s.id > nextStateId) nextStateId = s.id
            }
            nextStateId++
        }
        return nextStateId++
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
                if (t.id > nextTransId) nextTransId = t.id
            }
            nextTransId++
        }
        return nextTransId++
    }

    /**
     * Factory method to create a new State for the given name.
     * It does NOT check whether the name exists or not
     *
     * @param name the name of the State
     * @return the new State
     */
    State createState(String name) {
        State newState = new State(getNextStateId(), name)
        states.add(newState)
        log.debug "createState() - created:{} id:{}", name, newState.id
        return newState
    }

    /**
     * Factory method to create a new Transition for the given name.
     * It does NOT check whether the name exists or not
     *
     * @param name the name of the Transition
     * @return the new Transition
     */
    Transition createTransition(String name) {
        Transition newTrans = new Transition(getNextTransId(), name)
        transitions.add(newTrans)
        log.debug "createTransition() - created:{} id:{}", name, newTrans.id
        return newTrans
    }

    void setStates(List<State> newStates) {
        this.states = newStates
        validate()
    }

    void setTransitions(List<Transition> newTransitions) {
        this.transitions = newTransitions
        validate()
    }

    boolean validate() {
        boolean isCoherent = initialStateId != null && getState(initialStateId) != null

        for (Transition trans : transitions) {
            isCoherent = isCoherent && trans.resolveStates(states)
        }

        log.debug "validate() - name:{} isCoherent:{}", name, isCoherent
        return isCoherent
    }

    void setInitialState(State initialState) {
        setInitialStateId(initialState.id)
    }

    void setInitialStateId(int initialStateId) {
        State s = getState(initialStateId)
        Objects.requireNonNull(s)
        s.initial = true
        this.initialStateId = initialStateId
    }

    Transition getTransition(int transitionID) {
        transitions.find { it.id == transitionID }
    }

    Transition getTransition(String name) {
        transitions.find { it.name == name }
    }

    State getState(int stateID) {
        states.find { it.id == stateID }
    }

    State getState(String name) {
        states.find { it.name == name }
    }

    @JsonIgnore
    @Override
    BuiltInResources getResourceType() {
        return STATE_MACHINE_RESOURCE
    }
}

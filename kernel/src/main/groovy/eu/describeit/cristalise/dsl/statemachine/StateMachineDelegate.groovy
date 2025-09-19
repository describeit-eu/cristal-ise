package eu.describeit.cristalise.dsl.statemachine

import eu.describeit.cristalise.kernel.statemachine.State
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import eu.describeit.cristalise.kernel.statemachine.Transition
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 *
 */
@CompileStatic
@Slf4j
class StateMachineDelegate {
  String ns = ""
  String name = ""
  int version = -1

  StateMachine sm = null

  Map<String, State> stateCache = [:]
  Map<String, Transition> transCache = [:]

  public StateMachineDelegate(String ns, String n, int v) {
    ns = ns
    name = n
    version = v

    sm = new StateMachine(n, v)
    sm.namespace = ns
  }

  public void processClosure(@DelegatesTo(StateMachineDelegate) Closure cl) {
    assert cl, "StateMachineDelegate only works with a valid Closure"

    cl.delegate = this
    cl.resolveStrategy = Closure.DELEGATE_FIRST
    cl()
  }

  public void state(String stateName) {
    log.debug "state() - stateName:{}", stateName
    assert stateName
    def state = stateCache[stateName]
    if (!state) {
      state = sm.createState(stateName)
      stateCache[stateName] = state
    }
  }

  public void transition(String transName, Map<String, String> states = null, @DelegatesTo(TransitionDelegate) Closure cl = null) {
    log.debug "transition() - transName:{}, states:{}", transName, states
    assert transName

    def trans = transCache[transName]
    if (!trans) {
      trans = sm.createTransition(transName)
      transCache[transName] = trans
    }

    if (states) {
      assert states.origin && states.target

      def origin = stateCache[states.origin]
      if (!origin) {
        origin = sm.createState(states.origin)
        stateCache[states.origin] = origin
      }

      def target = stateCache[states.target]
      if (!target) {
        target = sm.createState(states.target)
        stateCache[states.target] = target
      }

      trans.originStateId = origin.id
      trans.targetStateId = target.id
    }

    if (cl) new TransitionDelegate(trans).processClosure(cl)
  }

  public void initialState(String stateName) {
    log.debug "initialState() - stateName:{}", stateName
    assert stateCache && stateCache[stateName]

    sm.initialState = stateCache[stateName]
  }

  public void finishingState(String... stateNames) {
    log.debug "finishingState() - states:{}", stateNames
    List<Integer> ids = []

    for (s in stateNames) {
      assert stateCache && stateCache[s]
      stateCache[s].finishing = true
      ids.add stateCache[s].id
    }
    sm.finalStateIds = ids
  }
}

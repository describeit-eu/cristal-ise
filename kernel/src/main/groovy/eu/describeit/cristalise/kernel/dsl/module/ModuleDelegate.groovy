package eu.describeit.cristalise.kernel.dsl.module

import eu.describeit.cristalise.kernel.dsl.statemachine.StateMachineBuilder
import eu.describeit.cristalise.kernel.dsl.statemachine.StateMachineDelegate
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class ModuleDelegate {

  StateMachine StateMachine(Map args, @DelegatesTo(StateMachineDelegate) Closure cl) {
    log.info('StateMachine() - args:{}', args)
    return StateMachineBuilder.build((String)args.ns, (String)args.name, (String)args.version, cl).sm
  }
}

package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future

/**
 * <pre>
 * 1. new DomainPath of /kernel/statemachine if not exists
 * 2. new Item of type StateMachine, with no LifeCycle (for the time being)
 * 3. new ItemProperties: Name, Type, Module, Version
 * 4. new Outcome with the JSON of the StateMachine
 * 5. new Collection: ....
 * 6. two new ViewPoints:  v0,  last
 * 7. new Event for the Import
 */
@Slf4j
@CompileStatic
class ImportDescriptionObjectAction implements BuiltInAction {
  @Override
  Future<UUID> request(ItemProxy item, ItemProxy actor, Object input) {
    log.info('request({}) - inputType:{}', item, input.class)

    switch (input.class) {
      case StateMachine:
        return importStateMachine(item, actor, (StateMachine)input)
      default:
        return Future.failedFuture(new IllegalArgumentException("Cannot handle class:${input.class.simpleName}"))
    }
  }

  private static Future<UUID> importStateMachine(ItemProxy item, ItemProxy actor, StateMachine sm) {
    log.info('importStateMachine({}) - {}', item, sm)


    return Future.failedFuture('Unimplemented')
  }

}

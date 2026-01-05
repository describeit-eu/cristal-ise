package eu.describeit.cristalise.dsl.statemachine

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.json.JsonObject


/**
 *
 */
@CompileStatic
@Slf4j
class StateMachineBuilder {
  String module = ""
  String name = ""
  String version = -1

  StateMachine sm

  DomainPathDO domainPath = null

  public StateMachineBuilder() {}

  /**
   *
   * @param module
   * @param name
   * @param version
   */
  public StateMachineBuilder(String module, String name, String version) {
    this.module = module
    this.name = name
    this.version = version
  }

  /**
   *
   * @param module
   * @param delegate
   */
  public StateMachineBuilder(String module, StateMachineDelegate delegate) {
    this(module, delegate.name, delegate.version)
    this.sm = delegate.sm
  }

  public static StateMachine create(String module, String name, String version, @DelegatesTo(StateMachineDelegate) Closure cl) {
    def builder = build(module, name, version, cl)
    return builder.sm
  }

  public static StateMachineBuilder build(String module, String name, String version, @DelegatesTo(StateMachineDelegate) Closure cl) {
    def delegate = new StateMachineDelegate(module, name, version)

    delegate.processClosure(cl)

    def builder = new StateMachineBuilder(module, delegate)
    builder.sm.validate()

    log.info('build() - json:\n{}', JsonObject.mapFrom(builder.sm).encodePrettily())

    return builder
  }

  static StateMachine StateMachine(Map<String, Object> args, @DelegatesTo(StateMachineDelegate) Closure cl) {
    log.info('StateMachine() - name:{} version:{}', args.name, args.version)

    return build((String)args.ns, (String)args.name, (String)args.version, cl).sm
  }
}

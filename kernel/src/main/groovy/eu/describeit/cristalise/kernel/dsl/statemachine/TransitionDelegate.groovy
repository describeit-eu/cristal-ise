package eu.describeit.cristalise.kernel.dsl.statemachine

import eu.describeit.cristalise.kernel.statemachine.Transition
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 *
 */
@Slf4j
@CompileStatic
class TransitionDelegate {
  Transition trans

  public TransitionDelegate(Transition t) {
    trans = t
  }

  public void processClosure(@DelegatesTo(TransitionDelegate) Closure cl) {
    assert cl, "TransitionDelegate only works with a valid Closure"

    cl.delegate = this
    cl.resolveStrategy = Closure.DELEGATE_FIRST
    cl()
  }

  @CompileDynamic
  public void property(Map<String, Object> attrs) {
    log.debug "property() - attrs:{}", attrs
    assert attrs, ""

    //FIXME: dynamic groovy is needed for this line only
    attrs.each { String k, v -> trans."$k" = v }
  }

  public void schema(Map attrs) {
    log.debug "outcome() - attrs:{}", attrs
    assert attrs && attrs.name && attrs.version, 'Schema name and version shall not be null'
    trans.schema = attrs
  }

  public void script(Map attrs) {
    log.debug "script() - attrs:{}", attrs
    assert attrs && attrs.name && attrs.version, 'Script name and version shall not be null'
    trans.script = attrs
  }

  public void query(Map attrs) {
    log.debug "query() - attrs:{}", attrs
    assert attrs && attrs.name && attrs.version, 'Query name and version shall not be null'
    trans.query = attrs
  }
}

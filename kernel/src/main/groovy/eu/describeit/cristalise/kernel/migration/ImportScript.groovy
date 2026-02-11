package eu.describeit.cristalise.kernel.migration

import eu.describeit.cristalise.kernel.dsl.module.ModuleDelegate
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
abstract class ImportScript extends DelegatingScript {

  interface Factory {
    ImportScript create(String scriptName, Binding scriptBinding) throws ResourceException, ScriptException
  }

  @Delegate
  ModuleDelegate moduleDelegate = new ModuleDelegate()

  abstract Object scriptBody()

  @Override
  Object run() {
    def result = scriptBody()
    log.info('run() - result:{}', result)
    return result
  }
}

package eu.describeit.cristalise.kernel.migration

import eu.describeit.cristalise.kernel.dsl.module.ModuleDelegate
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

@Slf4j
@CompileStatic
abstract class ImportScript extends DelegatingScript {

  // TODO read script directories from configs
  static final String[] classLoaderRoots = [
    'eu/describeit/cristalise/kernel/module/',
    'src/main/groovy/eu/describeit/cristalise/kernel/module/',
    'kernel/src/main/groovy/eu/describeit/cristalise/kernel/module/',
  ]

  @Delegate
  ModuleDelegate moduleDelegate = new ModuleDelegate()

  abstract Object scriptBody()

  @Override
  Object run() {
    def result = scriptBody()
    log.info('run() - result:{}', result)
    return result
  }

  static ImportScript initScript(String scriptName, Binding scriptBinding) {
    CompilerConfiguration cc = new CompilerConfiguration()
    cc.setScriptBaseClass(ImportScript.class.getName())

    GroovyScriptEngine engine = new GroovyScriptEngine(classLoaderRoots)
    engine.setConfig(cc)

    scriptName = scriptName.endsWith('.groovy') ?: scriptName+'.groovy'

    ImportScript script = (ImportScript) engine.createScript(scriptName, scriptBinding)
    script.setDelegate(script)

    return script
  }
}

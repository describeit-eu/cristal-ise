package eu.describeit.cristalise.kernel.dsl

import eu.describeit.cristalise.kernel.DescriptionObject
import eu.describeit.cristalise.kernel.dsl.statemachine.StateMachineBuilder
import eu.describeit.cristalise.kernel.dsl.statemachine.StateMachineDelegate
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

@Slf4j
@CompileStatic
abstract class ImportScript extends DelegatingScript {

  static final String[] classLoaderRoots = ['eu/describeit/cristalise/kernel/module']

  abstract Object scriptBody()

  @Override
  Object run() {
    DescriptionObject result = (DescriptionObject) scriptBody()

    log.info('run() - result:{}', result)

    return result
  }

  static ImportScript initScript(String scriptName, Binding scriptBinding) {
    CompilerConfiguration cc = new CompilerConfiguration()
    cc.setScriptBaseClass(ImportScript.class.getName())

    cc.addCompilationCustomizers(new ASTTransformationCustomizer(CompileStatic))

    GroovyScriptEngine engine = new GroovyScriptEngine(classLoaderRoots)
    engine.setConfig(cc)

    ImportScript script = (ImportScript) engine.createScript(scriptName+'.groovy', scriptBinding)
    script.setDelegate(script)

    return script
  }

  StateMachine StateMachine(Map<String, Object> args, @DelegatesTo(StateMachineDelegate) Closure cl) {
    log.info('StateMachine() - name:{} version:{}', args.name, args.version)

    return StateMachineBuilder.build((String)args.ns, (String)args.name, (String)args.version, cl).sm
  }

}

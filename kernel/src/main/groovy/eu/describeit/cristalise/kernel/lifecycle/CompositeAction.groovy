package eu.describeit.cristalise.kernel.lifecycle

import groovy.transform.CompileStatic

@CompileStatic
interface CompositeAction extends Action {
  Action findAction(String actionPath)
}

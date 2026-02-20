package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.item.ItemProxy
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

@CompileStatic
interface BuiltInAction {

  default String getName() {
    final String action = 'Action'
    String className = this.class.simpleName

    if (className.endsWith(action)) return className.substring(0, className.length() - action.length())
    else                            return className
  }

  default String getPath() {
    return 'builtin/' + name
  }

  Future<JsonObject> request(
    final ItemProxy item,
    final ItemProxy actor,
    final Object input
  )
}

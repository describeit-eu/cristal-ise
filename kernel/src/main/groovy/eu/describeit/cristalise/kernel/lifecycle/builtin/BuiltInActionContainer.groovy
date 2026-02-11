package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.migration.ImportScript
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

import javax.inject.Inject

@CompileStatic
class BuiltInActionContainer {

  List<BuiltInAction> actions = []

  @Inject
  BuiltInActionContainer(ImportScript.Factory importScriptFactory) {
    actions.add(new ImportDescriptionObjectAction(importScriptFactory))
  }

  Future<JsonObject> request(final ItemProxy item, final ItemProxy actor, final String actionName, final Object input) {
    BuiltInAction action = actions.find { it.name == actionName }

    if (action == null) {
      return Future.failedFuture(new IllegalArgumentException("Built-in action not found: ${actionName}"))
    }

    return action.request(item, actor, input)
  }

}

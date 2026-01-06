package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.DescriptionObject
import eu.describeit.cristalise.kernel.item.ItemProxy
import io.vertx.core.Future
import io.vertx.core.json.JsonObject

interface BuiltInAction {
  Future<UUID> request(
    final ItemProxy item,
    final ItemProxy actor,
    final Object input
  )

}

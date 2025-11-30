package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import dagger.Module
import dagger.Provides
import eu.describeit.cristalise.kernel.item.Item
import eu.describeit.cristalise.kernel.item.ItemService
import eu.describeit.cristalise.kernel.item.ItemVerticle

import javax.inject.Singleton

/**
 * Dagger module that provides bindings for Item-related services.
 */
@Module
class KernelModule {

    /**
     * Provides the Item service implementation.
     */
    @Provides
    @Singleton
    static Item provideItemService() {
        return new ItemService()
    }

  @Singleton
  @Component(modules = [KernelModule])
  static interface ItemFactory {

    ItemVerticle itemVerticle()
  }
}

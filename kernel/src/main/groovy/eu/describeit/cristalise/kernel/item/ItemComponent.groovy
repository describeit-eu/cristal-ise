package eu.describeit.cristalise.kernel.item

import dagger.Component
import eu.describeit.cristalise.kernel.dagger.KernelModule

import javax.inject.Singleton

@Singleton
@Component(modules = [KernelModule])
interface ItemComponent {

    // Expose Item binding if needed elsewhere
    Item item()

    // Allow field injection for verticles
    void inject(ItemVerticle verticle)
}

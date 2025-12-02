package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import eu.describeit.cristalise.kernel.item.ItemVerticle
import groovy.transform.CompileStatic
import io.vertx.core.DeploymentOptions

import javax.inject.Singleton

@CompileStatic
@Singleton
@Component(modules = [KernelModule])
 interface KernelComponent {

  ItemVerticle itemVerticle()

  DeploymentOptions deploymentOptions()
}

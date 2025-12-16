package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import eu.describeit.cristalise.kernel.item.ItemVerticle
import groovy.transform.CompileStatic
import io.vertx.core.DeploymentOptions
import io.vertx.sqlclient.Pool

import javax.inject.Singleton

@CompileStatic
@Singleton
@Component(modules = [KernelModule, PersistencyModule])
interface KernelComponent {

  ItemVerticle itemVerticle()

  DeploymentOptions deploymentOptions()

  Pool dbPool()
}

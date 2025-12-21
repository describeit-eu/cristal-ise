package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import eu.describeit.cristalise.kernel.item.ItemVerticle
import eu.describeit.cristalise.kernel.migration.LiquibaseCommand
import groovy.transform.CompileStatic
import io.vertx.config.ConfigRetriever
import io.vertx.core.DeploymentOptions
import io.vertx.sqlclient.Pool

import javax.inject.Singleton

@CompileStatic
@Singleton
@Component(modules = [KernelModule, PersistencyModule])
interface KernelComponent {

  ConfigRetriever configRetriever()

  LiquibaseCommand liquibaseCommand()

  ItemVerticle itemVerticle()

  DeploymentOptions deploymentOptions()

  Pool dbPool()
}

package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import groovy.transform.CompileStatic
import io.vertx.sqlclient.Pool
import org.testcontainers.postgresql.PostgreSQLContainer

import javax.inject.Singleton

@CompileStatic
@Singleton
@Component(modules = [KernelModule, TestPersistencyModule])
interface TestKernelComponent {
  PostgreSQLContainer pgContainer()
  Pool dbPool()
}

package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import groovy.transform.CompileStatic
import org.testcontainers.postgresql.PostgreSQLContainer

import javax.inject.Singleton

@CompileStatic
@Singleton
@Component(modules = [KernelModule, TestPersistencyModule])
interface TestKernelComponent extends KernelComponent {
  PostgreSQLContainer pgContainer()
}

package eu.describeit.cristalise.kernel.dagger

import dagger.Component
import eu.describeit.cristalise.kernel.lifecycle.builtin.ImportDescriptionObjectAction
import groovy.transform.CompileStatic
import org.testcontainers.postgresql.PostgreSQLContainer

import javax.inject.Singleton

@CompileStatic
@Singleton
@Component(modules = [KernelModule, TestPersistencyModule])
interface TestKernelComponent extends KernelComponent {
  ImportDescriptionObjectAction importDescriptionObjectAction()
  PostgreSQLContainer pgContainer()
}

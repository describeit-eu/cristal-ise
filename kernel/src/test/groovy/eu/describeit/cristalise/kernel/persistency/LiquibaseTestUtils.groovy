package eu.describeit.cristalise.kernel.persistency

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.resource.ClassLoaderResourceAccessor
import org.testcontainers.postgresql.PostgreSQLContainer

@Slf4j
@CompileStatic
class LiquibaseTestUtils {

  static void liquibaseLoadTestData(PostgreSQLContainer pgContainer) throws Exception {
    liquibaseLoadTestData(pgContainer, null)
  }

  static void liquibaseLoadTestData(PostgreSQLContainer pgContainer, String context) throws Exception {
    def logFile = "/liquibase/changelog/changelog-testData-master.yaml"
    liquibaseUpdate(pgContainer, logFile, context)
  }

  static void liquibaseCreateTables(PostgreSQLContainer pgContainer) throws Exception {
    def logFile = "/liquibase/changelog/changelog-master.yaml"
    liquibaseUpdate(pgContainer, logFile, null)
  }

  private static void liquibaseUpdate(PostgreSQLContainer pgContainer, String logFile, String context) throws Exception {
    Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
      def update = new CommandScope("update")

      update.addArgumentValue("changelogFile", logFile)
      update.addArgumentValue("url", pgContainer.getJdbcUrl())
      update.addArgumentValue("username", pgContainer.getUsername())
      update.addArgumentValue("password", pgContainer.getPassword())
      if (context != null) update.addArgumentValue("contextFilter", context)

      update.execute()

      log.info("liquibaseUpdate() - DONE url: {}", pgContainer.getJdbcUrl())
    })
  }
}

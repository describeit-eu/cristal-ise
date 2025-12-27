package eu.describeit.cristalise.kernel.migration

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.config.ConfigRetriever
import io.vertx.core.json.JsonObject
import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.resource.ClassLoaderResourceAccessor

import javax.inject.Inject

@Slf4j
@CompileStatic
class LiquibaseCommand {
  ConfigRetriever configRetriever

  @Inject
  LiquibaseCommand(ConfigRetriever config) {
    configRetriever = config
  }

  LiquibaseCommand executeUpdate(String jdbcUrl, String userName, String pwd, String logFile, String context = null) {
    execute(jdbcUrl, userName, pwd, logFile, context)
    return this
  }

  LiquibaseCommand executeUpdate(String logFile, String context = null) throws Exception {
    JsonObject dbConfig = configRetriever.getCachedConfig().getJsonObject('database')

    String userName = dbConfig.getString('user')
    String pwd = dbConfig.getString('password')
    String host = dbConfig.getString('host')
    Integer port = dbConfig.getInteger('port')
    String dbName = dbConfig.getString('name')

    String jdbcUrl = "jdbc:postgresql://$host:$port/$dbName"

    execute(jdbcUrl, userName, pwd, logFile, context)

    return this
  }

  private static void execute(String jdbcUrl, String userName, String pwd, String logFile, String context) {
    Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
      CommandScope command = new CommandScope('update')

      command.addArgumentValue("changelogFile", logFile)
      command.addArgumentValue("url", jdbcUrl)
      command.addArgumentValue("username", userName)
      command.addArgumentValue("password", pwd)
      if (context) command.addArgumentValue("contextFilter", context)

      def result = command.execute()

      log.trace("execute() - update DONE url:{} result:{}", jdbcUrl, result.results)
    })
  }
}

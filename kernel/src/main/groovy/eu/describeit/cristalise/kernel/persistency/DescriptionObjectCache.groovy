package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient

import javax.inject.Inject

import static eu.describeit.cristalise.kernel.BuiltInResources.STATE_MACHINE_RESOURCE

@CompileStatic
class DescriptionObjectCache {

  // TODO add cache
  RepositoryWrapper repository

  @Inject
  DescriptionObjectCache(SqlClient sqlClient) {
    repository = new RepositoryWrapper(sqlClient)
  }

  Future<StateMachine> getStateMachine(String name, String version) {
    String path = STATE_MACHINE_RESOURCE.typeRoot + '/' + name
    DomainPathDO dp = new DomainPathDO(path: path)

    return repository.getItemId(dp).compose { UUID itemId ->
      return getStateMachine(itemId, version)
    }
  }

  Future<StateMachine> getStateMachine(UUID itemId, String version) {
    String schemaName = STATE_MACHINE_RESOURCE.schemaName
    Future<ViewPointDO> vpFuture = repository.getViewPointDO(itemId, schemaName, version)

    return vpFuture.compose { ViewPointDO vp ->
      repository.getOutcomeDO(vp).compose { OutcomeDO outcomeDO ->
        StateMachine sm = outcomeDO.data.mapTo(StateMachine.class)
        return Future.succeededFuture(sm)
      }
    } as Future<StateMachine>
  }
}

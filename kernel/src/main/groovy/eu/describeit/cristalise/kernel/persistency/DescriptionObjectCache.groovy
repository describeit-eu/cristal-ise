package eu.describeit.cristalise.kernel.persistency

import com.github.benmanes.caffeine.cache.AsyncCacheLoader
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import eu.describeit.cristalise.kernel.BuiltInResources
import eu.describeit.cristalise.kernel.DescriptionObject
import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.SqlClient

import javax.inject.Inject
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

import static eu.describeit.cristalise.kernel.BuiltInResources.STATE_MACHINE_RESOURCE

@CompileStatic
class DescriptionObjectCache {

  private final RepositoryWrapper repository
  private final AsyncLoadingCache<DescriptionObjectKey, DescriptionObject> cache

  @Inject
  DescriptionObjectCache(SqlClient sqlClient) {
    this(new RepositoryWrapper(sqlClient))
  }

  DescriptionObjectCache(RepositoryWrapper repository) {
    this.repository = repository
    cache = (AsyncLoadingCache<DescriptionObjectKey, DescriptionObject>) Caffeine.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .recordStats()
      .executor { Runnable cmd ->
        Context context = Vertx.currentContext()
        if (context) context.runOnContext { cmd.run() }
        else         cmd.run()
      }
      .buildAsync({ DescriptionObjectKey key, Executor exec ->
        getFromRepository(key.itemId, key.version).toCompletionStage().toCompletableFuture()
      } as AsyncCacheLoader<DescriptionObjectKey, DescriptionObject>)
  }

  Future<StateMachine> getStateMachine(String name, String version) {
    String path = STATE_MACHINE_RESOURCE.typeRoot + '.' + name
    DomainPathDO dp = new DomainPathDO(path: path)

    return repository.getItemId(dp).compose { UUID itemId ->
      return getStateMachine(itemId, version)
    }
  }

  Future<StateMachine> getStateMachine(UUID itemId, String version) {
    return get(itemId, version).map { DescriptionObject desc -> desc as StateMachine }
  }

  private Future<DescriptionObject> get(UUID itemId, String version) {
    return Future.fromCompletionStage(cache.get(new DescriptionObjectKey(itemId, version)))
  }

  private Future<DescriptionObject> getFromRepository(UUID itemId, String version) {
    repository.getItemProperty(itemId, 'Type').compose { ItemPropertyDO type ->
      repository.getViewPointDO(itemId, type.value, version).compose { ViewPointDO vp ->
        repository.getOutcomeDO(vp).compose { OutcomeDO outcomeDO ->
          def descObj = BuiltInResources.toDescriptionObject(type.value, outcomeDO.data)
          return Future.succeededFuture(descObj)
        }
      }
    }
  }

  @EqualsAndHashCode
  @TupleConstructor
  private static class DescriptionObjectKey {
    final UUID itemId
    final String version
  }
}

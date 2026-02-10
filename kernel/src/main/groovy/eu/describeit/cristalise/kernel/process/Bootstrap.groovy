package eu.describeit.cristalise.kernel.process

import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection

import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalDateTime

@Slf4j
@CompileStatic
@Singleton
class Bootstrap {
  static final UUID   rootItemId   = UUID.fromString('00000000-0000-0000-0000-000000000001')
  static final String rootItemName = 'CristaliseRoot'
  static final String rootItemType = 'RootItem'
  static final String rootItemPath = "kernel.${rootItemType}.${rootItemName}"
  static final String rootItemVersion = 'v0'

  private final Pool pool
  private RepositoryWrapper storage = null

  @Inject
  Bootstrap(Pool pool) {
    this.pool = pool
  }

  Future<Void> execute() {
    pool.withTransaction { SqlConnection conn ->
      storage = new RepositoryWrapper(conn)
      return createCristaliseRoot()
        .compose { return importKernelResources() }
    }
  }

  private Future<Void> createCristaliseRoot() {
    return storage.exists(new DomainPathDO(path:  rootItemPath))
      .compose { Boolean exists ->
        if (exists) return Future.succeededFuture()
        else        return createCristaliseRootHandler()
      }
      .mapEmpty()
  }

  private Future<Void> createCristaliseRootHandler() {
    log.info('createCristaliseRootHandler() - creating with itemId:{}', rootItemId)

    ItemDO rootDO = new ItemDO(rootItemId, rootItemName, rootItemType, rootItemVersion, null)

    return storage.putItemDO(rootDO)
      .compose {
        log.debug('createCristaliseRootHandler() - ItemDO created with id:{}', it.id)
        //assert it.id == rootId
        storage.putDomainPath(new DomainPathDO("kernel.${rootItemType}.${rootItemName}", rootItemId))
      }
      .compose {
        return storage.putItemProperties([Name: rootItemName, Type: rootItemType, Version: rootItemVersion], rootItemId)
      }
      .compose {
        EventDO event = new EventDO(
          itemId: rootItemId,
          itemVersion: rootItemVersion,
          userLogin: 'system',
          timestamp: LocalDateTime.now(),
          actionPath: 'bootstrap'
        )
        return storage.putEvent(event)
      }
      .map {
        log.info('createCristaliseRootHandler() - DONE itemId:{}', rootItemId)
      }
  }

  private Future<Void> importKernelResources() {
    return Future.succeededFuture()
  }
}

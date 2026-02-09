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
  static String rootItemName = "CristaliseRoot"
  static String rootItemType = "RootItem"
  static String rootItemPath = "kernel.${rootItemType}.${rootItemName}"
  static String rootItemVersion = "v0"

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
    }.mapEmpty()
  }

  private Future<UUID> createCristaliseRoot() {
    storage.getItemId(new DomainPathDO(path:  rootItemPath))
      .compose({ UUID itemId ->
          log.info("createCristaliseRoot() - CristaliseRoot already exists ${itemId}")
          return Future.succeededFuture(itemId)
        }, { Throwable t ->
          return createCristaliseRootHandler()
      })
  }

  private Future<UUID> createCristaliseRootHandler() {
    UUID rootId = UUID.randomUUID() //UUID.nameUUIDFromBytes("CristaliseRoot".getBytes())

    log.info("createCristaliseRootHandler() - creating with itemId:{}", rootId)

    ItemDO rootDO = new ItemDO(rootId, rootItemName, rootItemType, rootItemVersion, null)

    return storage.putItemDO(rootDO)
      .compose {
        log.debug('createCristaliseRootHandler() - ItemDO created with id:{}', it.id)
        assert it.id == rootId
        storage.putDomainPath(new DomainPathDO("kernel.${rootItemType}.${rootItemName}", rootId))
      }
      .compose {
        return storage.putItemProperties([Name: rootItemName, Type: rootItemType, Version: rootItemVersion], rootId)
      }
      .compose {
        EventDO event = new EventDO(itemId: rootId, itemVersion: rootItemVersion, userLogin: 'system', timestamp: LocalDateTime.now(), actionPath: "bootstrap")
        return storage.putEvent(event)
      }
      .map {
        log.info('createCristaliseRootHandler() - DONE itemId:{}', rootId)
        return rootId
      }
  }
}

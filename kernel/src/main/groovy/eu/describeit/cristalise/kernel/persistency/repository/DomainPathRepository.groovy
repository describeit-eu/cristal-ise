package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface DomainPathRepository {

  Future<Optional<DomainPathDO>> findById(Long id)

  Future<List<DomainPathDO>> findAll()

  Future<DomainPathDO> insert(DomainPathDO domainPath)

  Future<Optional<DomainPathDO>> update(DomainPathDO domainPath)

  Future<Integer> deleteById(Long id)

  Future<List<DomainPathDO>> findByItemId(UUID itemId)

  Future<Optional<DomainPathDO>> findByPath(String path)

  /**
   * @param path
   * @return direct children one level below the given path
   */
  Future<List<DomainPathDO>> getChildren(String path)

  /**
   * @param path
   * @return the subtree, i.e. all descendants, including the given path if present
   */
  Future<List<DomainPathDO>> getTree(String path)
}

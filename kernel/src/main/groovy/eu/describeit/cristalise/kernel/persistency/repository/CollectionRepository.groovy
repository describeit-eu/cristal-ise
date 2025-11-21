package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.CollectionDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
public interface CollectionRepository {

  Future<Optional<CollectionDO>> findById(Long id)

  Future<List<CollectionDO>> findAll()

  Future<CollectionDO> insert(CollectionDO collection)

  Future<Optional<CollectionDO>> update(CollectionDO collection)

  Future<Integer> deleteById(Long id)
}

package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.CollectionDO
import io.vertx.core.Future

import java.util.List
import java.util.Optional

public interface CollectionRepository {

  Future<Optional<CollectionDO>> findById(Long id)

  Future<List<CollectionDO>> findAll()

  Future<CollectionDO> insert(CollectionDO collection)

  Future<Optional<CollectionDO>> update(CollectionDO collection)

  Future<Integer> deleteById(Long id)
}

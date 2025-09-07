package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import io.vertx.core.Future

import java.util.List
import java.util.Optional
import java.util.UUID

public interface ViewPointRepository {

  Future<Optional<ViewPointDO>> findById(Long id)

  Future<List<ViewPointDO>> findAll()

  Future<ViewPointDO> insert(ViewPointDO viewPoint)

  Future<Optional<ViewPointDO>> update(ViewPointDO viewPoint)

  Future<Integer> deleteById(Long id)

  Future<List<ViewPointDO>> findByItemId(UUID itemId)

  Future<Integer> deleteByItemId(UUID itemId)
}

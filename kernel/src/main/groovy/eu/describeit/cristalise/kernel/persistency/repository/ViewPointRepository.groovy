package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
public interface ViewPointRepository {

  Future<Optional<ViewPointDO>> findById(Long id)

  Future<List<ViewPointDO>> findAll()

  Future<ViewPointDO> insert(ViewPointDO viewPoint)

  Future<List<ViewPointDO>> insertMany(List<ViewPointDO> viewPoints)

  Future<Optional<ViewPointDO>> update(ViewPointDO viewPoint)

  Future<Integer> deleteById(Long id)

  Future<List<ViewPointDO>> findByItemId(UUID itemId)

  Future<Integer> deleteByItemId(UUID itemId)
}

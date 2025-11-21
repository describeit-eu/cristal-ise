package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
public interface OutcomeRepository {

  Future<Optional<OutcomeDO>> findById(Long id)

  Future<List<OutcomeDO>> findAll()

  Future<OutcomeDO> insert(OutcomeDO outcome)

  Future<Optional<OutcomeDO>> update(OutcomeDO outcome)

  Future<Integer> deleteById(Long id)

  Future<List<OutcomeDO>> findByItemId(UUID itemId)

  Future<Integer> deleteByItemId(UUID itemId)
}

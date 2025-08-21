package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface ActionRepository {

  Future<Optional<ActionDO>> findById(Long id);

  Future<List<ActionDO>> findAll();

  /**
   * Insert a new Action. Returns the inserted row (including generated id).
   */
  Future<ActionDO> insert(ActionDO action);

  /**
   * Update an existing Action by id. Returns the updated row.
   */
  Future<Optional<ActionDO>> update(ActionDO action);

  /**
   * Delete by id. Returns the number of affected rows (0 or 1).
   */
  Future<Integer> deleteById(Long id);
}

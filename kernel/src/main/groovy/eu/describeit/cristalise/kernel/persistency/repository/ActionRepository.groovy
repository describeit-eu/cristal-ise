package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
interface ActionRepository {

  Future<Optional<ActionDO>> findById(Long id)

  Future<List<ActionDO>> findByParentId(Long parentId)

  Future<List<ActionDO>> findAll()

  /**
   * Insert a new Action. Returns the inserted row (including generated id).
   */
  Future<ActionDO> insert(ActionDO action)

  /**
   * Insert multiple Actions. Returns the inserted rows (including generated ids).
   */
  Future<List<ActionDO>> insertMany(List<ActionDO> actions)

  /**
   * Update an existing Action by id. Returns the updated row.
   */
  Future<Optional<ActionDO>> update(ActionDO action)

  /**
   * Delete by id. Returns the number of affected rows (0 or 1).
   */
  Future<Integer> deleteById(Long id)
}

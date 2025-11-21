package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
public interface ItemRepository {

  Future<Optional<ItemDO>> findById(UUID id)

  Future<List<ItemDO>> findAll()

  /**
   * Insert a new item. Returns the inserted row (including generated id).
   */
  Future<ItemDO> insert(ItemDO item)

  /**
   * Update an existing item by id. Returns the updated row.
   */
  Future<Optional<ItemDO>> update(ItemDO item)

  /**
   * Delete by id. Returns the number of affected rows (0 or 1).
   */
  Future<Integer> deleteById(UUID id)
}

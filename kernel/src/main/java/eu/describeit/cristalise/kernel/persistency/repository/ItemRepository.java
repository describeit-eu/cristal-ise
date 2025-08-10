package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {

  Future<Optional<ItemDO>> findById(Long id);

  Future<Optional<ItemDO>> findByUuid(UUID uuid);

  Future<List<ItemDO>> findAll();

  /**
   * Insert a new item. Returns the inserted row (including generated id).
   */
  Future<ItemDO> insert(ItemDO item);

  /**
   * Update an existing item by id. Returns the updated row.
   */
  Future<Optional<ItemDO>> update(ItemDO item);

  /**
   * Delete by id. Returns the number of affected rows (0 or 1).
   */
  Future<Integer> deleteById(Long id);

  /**
   * Delete by id. Returns the number of affected rows (0 or 1).
   */
  Future<Integer> deleteByUuid(UUID uuid);
}

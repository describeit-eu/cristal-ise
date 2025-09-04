package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemPropertyRepository {

  Future<Optional<ItemPropertyDO>> findById(Long id);

  Future<List<ItemPropertyDO>> findByItemId(UUID item_id);

  Future<List<ItemPropertyDO>> findAll();

  Future<ItemPropertyDO> insert(ItemPropertyDO itemProperty);

  Future<Optional<ItemPropertyDO>> update(ItemPropertyDO itemProperty);

  Future<Integer> deleteById(Long id);

  Future<Integer> deleteByItemId(UUID item_id);
}

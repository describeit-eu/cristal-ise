package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface ItemPropertyRepository {

  Future<Optional<ItemPropertyDO>> findById(Long id);

  Future<List<ItemPropertyDO>> findAll();

  Future<ItemPropertyDO> insert(ItemPropertyDO itemProperty);

  Future<Optional<ItemPropertyDO>> update(ItemPropertyDO itemProperty);

  Future<Integer> deleteById(Long id);
}

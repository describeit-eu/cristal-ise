package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.EventDO
import io.vertx.core.Future

import java.util.List
import java.util.Optional
import java.util.UUID

public interface EventRepository {

  Future<Optional<EventDO>> findById(Long id)

  Future<List<EventDO>> findAll()

  Future<List<EventDO>> findByItemId(UUID item_id)

  Future<EventDO> insert(EventDO event)

  Future<Optional<EventDO>> update(EventDO event)

  Future<Integer> deleteById(Long id)

  Future<Integer> deleteByItemId(UUID item_id)
}

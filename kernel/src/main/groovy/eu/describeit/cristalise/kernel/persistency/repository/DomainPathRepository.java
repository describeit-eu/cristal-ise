package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainPathRepository {

  Future<Optional<DomainPathDO>> findById(Long id);

  Future<List<DomainPathDO>> findAll();

  Future<DomainPathDO> insert(DomainPathDO domainPath);

  Future<Optional<DomainPathDO>> update(DomainPathDO domainPath);

  Future<Integer> deleteById(Long id);

  Future<List<DomainPathDO>> findByItemId(UUID itemId);

  // Returns direct children one level below the given path
  Future<List<DomainPathDO>> getChildren(String path);

  // Returns the subtree (all descendants including the given path if present)
  Future<List<DomainPathDO>> getTree(String path);
}

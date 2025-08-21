package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface DomainPathRepository {

  Future<Optional<DomainPathDO>> findById(Long id);

  Future<List<DomainPathDO>> findAll();

  Future<DomainPathDO> insert(DomainPathDO domainPath);

  Future<Optional<DomainPathDO>> update(DomainPathDO domainPath);

  Future<Integer> deleteById(Long id);
}

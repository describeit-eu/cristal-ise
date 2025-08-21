package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.JobDO;
import io.vertx.core.Future;

import java.util.List;
import java.util.Optional;

public interface JobRepository {

  Future<Optional<JobDO>> findById(Long id);

  Future<List<JobDO>> findAll();

  Future<JobDO> insert(JobDO job);

  Future<Optional<JobDO>> update(JobDO job);

  Future<Integer> deleteById(Long id);
}

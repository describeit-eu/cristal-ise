package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.CollectionMemberDO
import groovy.transform.CompileStatic
import io.vertx.core.Future

@CompileStatic
public interface CollectionMemberRepository {

  Future<Optional<CollectionMemberDO>> findById(Long id)

  Future<List<CollectionMemberDO>> findAll()

  Future<CollectionMemberDO> insert(CollectionMemberDO member)

  Future<Optional<CollectionMemberDO>> update(CollectionMemberDO member)

  Future<Integer> deleteById(Long id)
}

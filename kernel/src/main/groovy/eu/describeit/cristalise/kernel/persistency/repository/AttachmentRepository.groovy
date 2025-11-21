package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.AttachmentDO
import groovy.transform.CompileStatic
import io.vertx.core.Future


@CompileStatic
interface AttachmentRepository {

  Future<Optional<AttachmentDO>> findById(Long id)

  Future<List<AttachmentDO>> findAll()

  Future<AttachmentDO> insert(AttachmentDO attachment)

  Future<Optional<AttachmentDO>> update(AttachmentDO attachment)

  Future<Integer> deleteById(Long id)
}

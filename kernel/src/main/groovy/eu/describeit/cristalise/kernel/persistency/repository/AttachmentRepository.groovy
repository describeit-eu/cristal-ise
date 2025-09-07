package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.AttachmentDO
import io.vertx.core.Future

import java.util.List
import java.util.Optional

interface AttachmentRepository {

  Future<Optional<AttachmentDO>> findById(Long id)

  Future<List<AttachmentDO>> findAll()

  Future<AttachmentDO> insert(AttachmentDO attachment)

  Future<Optional<AttachmentDO>> update(AttachmentDO attachment)

  Future<Integer> deleteById(Long id)
}

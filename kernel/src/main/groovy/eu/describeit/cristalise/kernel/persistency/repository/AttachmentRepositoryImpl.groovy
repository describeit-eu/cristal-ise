package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.AttachmentDO
import eu.describeit.cristalise.kernel.persistency.domain.AttachmentDOParametersMapper
import eu.describeit.cristalise.kernel.persistency.domain.AttachmentDORowMapper
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.SqlResult
import io.vertx.sqlclient.templates.SqlTemplate

import java.util.*

public class AttachmentRepositoryImpl implements AttachmentRepository {

  private final SqlClient client

  private static final String TABLE = "attachment"
  private static final String COLUMNS = "id,name,type,file_name,data,outcome_id"
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (name, type, file_name, data, outcome_id) VALUES (#{name}, #{type}, #{file_name}, #{data}, #{outcome_id}) RETURNING " + COLUMNS
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET name=#{name}, type=#{type}, file_name=#{file_name}, data=#{data}, outcome_id=#{outcome_id} WHERE id=#{id} RETURNING " + COLUMNS
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}"

  public AttachmentRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<AttachmentDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(AttachmentDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(RepositoryUtils::firstOptional)
  }

  @Override
  public Future<List<AttachmentDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(AttachmentDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(RepositoryUtils::toList)
  }

  @Override
  public Future<AttachmentDO> insert(AttachmentDO attachment) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(AttachmentDOParametersMapper.INSTANCE)
      .mapTo(AttachmentDORowMapper.INSTANCE)
      .execute(attachment)
      .compose(rowSet -> RepositoryUtils.firstOrFail(rowSet, "Insert did not return a row for attachment name:"+attachment.getName()))
  }

  @Override
  public Future<Optional<AttachmentDO>> update(AttachmentDO attachment) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(AttachmentDOParametersMapper.INSTANCE)
      .mapTo(AttachmentDORowMapper.INSTANCE)
      .execute(attachment)
      .map(RepositoryUtils::firstOptional)
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount)
  }
}

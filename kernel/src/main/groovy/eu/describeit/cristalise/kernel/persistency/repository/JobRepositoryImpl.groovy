package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.JobDO
import eu.describeit.cristalise.kernel.persistency.domain.JobDOParametersMapper
import eu.describeit.cristalise.kernel.persistency.domain.JobDORowMapper
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.SqlResult
import io.vertx.sqlclient.templates.SqlTemplate

import java.util.*

public class JobRepositoryImpl implements JobRepository {

  private final SqlClient client

  private static final String TABLE = "job"
  // Note: alias action_path -> action_name for row mapping consistency
  private static final String COLUMNS = "id,action_path as action_name,transition,item_id"
  private static final String RETURNING_COLUMNS = "id,action_path,transition,item_id"
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (action_path, transition, item_id) VALUES (#{action_name}, #{transition}, #{item_id}) RETURNING " + RETURNING_COLUMNS
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET action_path=#{action_name}, transition=#{transition}, item_id=#{item_id} WHERE id=#{id} RETURNING " + RETURNING_COLUMNS
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}"

  public JobRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<JobDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(JobDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(RepositoryUtils::firstOptional)
  }

  @Override
  public Future<List<JobDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(JobDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(RepositoryUtils::toList)
  }

  @Override
  public Future<JobDO> insert(JobDO job) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(JobDOParametersMapper.INSTANCE)
      .mapTo(JobDORowMapper.INSTANCE)
      .execute(job)
      .compose(rowSet -> RepositoryUtils.firstOrFail(rowSet, "Insert did not return a row for job item:"+job.getItemId()))
  }

  @Override
  public Future<Optional<JobDO>> update(JobDO job) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(JobDOParametersMapper.INSTANCE)
      .mapTo(JobDORowMapper.INSTANCE)
      .execute(job)
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

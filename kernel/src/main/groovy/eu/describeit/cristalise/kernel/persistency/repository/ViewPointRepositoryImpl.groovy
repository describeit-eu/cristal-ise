package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDOParametersMapper
import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDORowMapper
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.SqlResult
import io.vertx.sqlclient.templates.SqlTemplate

import java.util.*

public class ViewPointRepositoryImpl implements ViewPointRepository {

  private final SqlClient client

  private static final String TABLE = "view_point"
  private static final String COLUMNS = "id,name,schema,schema_version,schema_name,outcome_id,item_id"
  private static final String SQL_FIND_BY_ID      = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_BY_ITEM_ID = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE item_id=#{itemId}"
  private static final String SQL_FIND_ALL        = "SELECT " + COLUMNS + " FROM " + TABLE
  private static final String SQL_INSERT          = "INSERT INTO " + TABLE + " (name, schema, schema_version, schema_name, outcome_id, item_id) VALUES (#{name}, #{schema}, #{schema_version}, #{schema_name}, #{outcome_id}, #{item_id}) RETURNING " + COLUMNS
  private static final String SQL_UPDATE          = "UPDATE "      + TABLE + " SET name=#{name}, schema=#{schema}, schema_version=#{schema_version}, schema_name=#{schema_name}, outcome_id=#{outcome_id}, item_id=#{item_id} WHERE id=#{id} RETURNING " + COLUMNS
  private static final String SQL_DELETE_BY_ID      = "DELETE FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_DELETE_BY_ITEM_ID = "DELETE FROM " + TABLE + " WHERE item_id=#{itemId}"

  public ViewPointRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<ViewPointDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(ViewPointDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(RepositoryUtils::firstOptional)
  }

  @Override
  public Future<List<ViewPointDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(ViewPointDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(RepositoryUtils::toList)
  }

  @Override
  public Future<ViewPointDO> insert(ViewPointDO viewPoint) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(ViewPointDOParametersMapper.INSTANCE)
      .mapTo(ViewPointDORowMapper.INSTANCE)
      .execute(viewPoint)
      .compose(rowSet -> RepositoryUtils.firstOrFail(rowSet, "Insert did not return a row for viewPoint id:"+viewPoint.getId()))
  }

  @Override
  public Future<Optional<ViewPointDO>> update(ViewPointDO viewPoint) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(ViewPointDOParametersMapper.INSTANCE)
      .mapTo(ViewPointDORowMapper.INSTANCE)
      .execute(viewPoint)
      .map(RepositoryUtils::firstOptional)
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount)
  }

  @Override
  public Future<List<ViewPointDO>> findByItemId(UUID itemId) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ITEM_ID)
      .mapTo(ViewPointDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("itemId", itemId))
      .map(RepositoryUtils::toList)
  }

  @Override
  public Future<Integer> deleteByItemId(UUID itemId) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ITEM_ID)
      .execute(Collections.singletonMap("itemId", itemId))
      .map(SqlResult::rowCount)
  }
}

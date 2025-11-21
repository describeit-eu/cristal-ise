package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDOParametersMapper
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDORowMapper
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.templates.SqlTemplate

import static eu.describeit.cristalise.kernel.persistency.repository.RepositoryUtils.firstOptional
import static eu.describeit.cristalise.kernel.persistency.repository.RepositoryUtils.firstOrFail
import static eu.describeit.cristalise.kernel.persistency.repository.RepositoryUtils.toList

@CompileStatic
public class OutcomeRepositoryImpl implements OutcomeRepository {

  private final SqlClient client

  private static final String TABLE = "outcome"
  private static final String COLUMNS = "id,schema,schema_version,data,event_id,item_id"
  private static final String SQL_FIND_BY_ID      = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_BY_ITEM_ID = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE item_id=#{itemId}"
  private static final String SQL_FIND_ALL        = "SELECT " + COLUMNS + " FROM " + TABLE
  private static final String SQL_INSERT          = "INSERT INTO " + TABLE + " (schema, schema_version, data, event_id, item_id) VALUES (#{schema}, #{schema_version}, #{data}, #{event_id}, #{item_id}) RETURNING " + COLUMNS
  private static final String SQL_UPDATE          = "UPDATE "      + TABLE + " SET schema=#{schema}, schema_version=#{schema_version}, data=#{data}, event_id=#{event_id}, item_id=#{item_id} WHERE id=#{id} RETURNING " + COLUMNS
  private static final String SQL_DELETE_BY_ID      = "DELETE FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_DELETE_BY_ITEM_ID = "DELETE FROM " + TABLE + " WHERE item_id=#{itemId}"

  public OutcomeRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<OutcomeDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", (Object)id))
      .map(rs -> firstOptional(rs))
  }

  @Override
  public Future<List<OutcomeDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rs -> toList(rs))
  }

  @Override
  public Future<OutcomeDO> insert(OutcomeDO outcome) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(OutcomeDOParametersMapper.INSTANCE)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(outcome)
      .compose(rowSet -> firstOrFail(rowSet, "Insert did not return a row for outcome id:"+outcome.getId()))
  }

  @Override
  public Future<Optional<OutcomeDO>> update(OutcomeDO outcome) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(OutcomeDOParametersMapper.INSTANCE)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(outcome)
      .map(rs -> firstOptional(rs))
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", (Object)id))
      .map(rs -> rs.rowCount())
  }

  @Override
  public Future<List<OutcomeDO>> findByItemId(UUID itemId) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ITEM_ID)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("itemId", (Object)itemId))
      .map(rs -> toList(rs))
  }

  @Override
  public Future<Integer> deleteByItemId(UUID itemId) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ITEM_ID)
      .execute(Collections.singletonMap("itemId", (Object)itemId))
      .map(rs -> rs.rowCount())
  }
}

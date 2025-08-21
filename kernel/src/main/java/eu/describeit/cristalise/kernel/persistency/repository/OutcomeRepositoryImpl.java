package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO;
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.*;

public class OutcomeRepositoryImpl implements OutcomeRepository {

  private final SqlClient client;

  private static final String TABLE = "outcome";
  private static final String COLUMNS = "id,schema,schema_version,data,event_id";
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (schema, schema_version, data, event_id) VALUES (#{schema}, #{schema_version}, #{data}, #{event_id}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET schema=#{schema}, schema_version=#{schema_version}, data=#{data}, event_id=#{event_id} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}";

  public OutcomeRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<OutcomeDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(this::firstOptional);
  }

  @Override
  public Future<List<OutcomeDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rowSet -> {
        List<OutcomeDO> list = new ArrayList<>();
        for (OutcomeDO row : rowSet) list.add(row);
        return list;
      });
  }

  @Override
  public Future<OutcomeDO> insert(OutcomeDO outcome) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(OutcomeDOParametersMapper.INSTANCE)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(outcome)
      .compose(rowSet -> {
        Iterator<OutcomeDO> it = rowSet.iterator();
        if (it.hasNext()) return Future.succeededFuture(it.next());
        else              return Future.failedFuture("Insert did not return a row for outcome id:"+outcome.getId());
      });
  }

  @Override
  public Future<Optional<OutcomeDO>> update(OutcomeDO outcome) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(OutcomeDOParametersMapper.INSTANCE)
      .mapTo(OutcomeDORowMapper.INSTANCE)
      .execute(outcome)
      .map(this::firstOptional);
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount);
  }

  private Optional<OutcomeDO> firstOptional(Iterable<OutcomeDO> rs) {
    Iterator<OutcomeDO> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }
}

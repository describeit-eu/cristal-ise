package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.EventDO;
import eu.describeit.cristalise.kernel.persistency.domain.EventDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.EventDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.*;

public class EventRepositoryImpl implements EventRepository {

  private final SqlClient client;

  private static final String TABLE = "event";
  private static final String COLUMNS = "id,item_version,action_desc,action_version,script,script_version,state_machine_desc,state_machine_version,user_login,timestamp,action_properties,item_id,action_path,transition_name";
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (item_version, action_desc, action_version, script, script_version, state_machine_desc, state_machine_version, user_login, timestamp, action_properties, item_id, action_path, transition_name) VALUES (#{item_version}, #{action_desc}, #{action_version}, #{script}, #{script_version}, #{state_machine_desc}, #{state_machine_version}, #{user_login}, #{timestamp}, #{action_properties}, #{item_id}, #{action_path}, #{transition_name}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET item_version=#{item_version}, action_desc=#{action_desc}, action_version=#{action_version}, script=#{script}, script_version=#{script_version}, state_machine_desc=#{state_machine_desc}, state_machine_version=#{state_machine_version}, user_login=#{user_login}, timestamp=#{timestamp}, action_properties=#{action_properties}, item_id=#{item_id}, action_path=#{action_path}, transition_name=#{transition_name} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}";

  public EventRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<EventDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(EventDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(RepositoryUtils::firstOptional);
  }

  @Override
  public Future<List<EventDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(EventDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(RepositoryUtils::toList);
  }

  @Override
  public Future<EventDO> insert(EventDO event) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(EventDOParametersMapper.INSTANCE)
      .mapTo(EventDORowMapper.INSTANCE)
      .execute(event)
      .compose(rowSet -> RepositoryUtils.firstOrFail(rowSet, "Insert did not return a row for event id:"+event.getId()));
  }

  @Override
  public Future<Optional<EventDO>> update(EventDO event) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(EventDOParametersMapper.INSTANCE)
      .mapTo(EventDORowMapper.INSTANCE)
      .execute(event)
      .map(RepositoryUtils::firstOptional);
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount);
  }
}

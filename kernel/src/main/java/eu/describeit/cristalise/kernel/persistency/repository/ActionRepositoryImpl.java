package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO;
import eu.describeit.cristalise.kernel.persistency.domain.ActionDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.ActionDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.*;

public class ActionRepositoryImpl implements ActionRepository {

  private final SqlClient client;

  private static final String TABLE = "action";
  private static final String COLUMNS = "id,name,path,version,properties,is_composite,layout,parent_id,item_id";
  private static final String SELECT_COLUMNS = "id,name,path,version,properties,is_composite,layout,parent_id,item_id";
  private static final String SQL_FIND_BY_ID   = "SELECT " + SELECT_COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_ALL     = "SELECT " + SELECT_COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (name, path, version, properties, is_composite, layout, parent_id, item_id) VALUES (#{name}, #{path}, #{version}, #{properties}, #{is_composite}, #{layout}, #{parent_id}, #{item_id}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET name=#{name}, path=#{path}, version=#{version}, properties=#{properties}, is_composite=#{is_composite}, layout=#{layout}, parent_id=#{parent_id}, item_id=#{item_id} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}";

  public ActionRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<ActionDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(ActionDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(this::firstOptional);
  }

  @Override
  public Future<List<ActionDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(ActionDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rowSet -> {
        List<ActionDO> list = new ArrayList<>();
        for (ActionDO row : rowSet) list.add(row);
        return list;
      });
  }

  @Override
  public Future<ActionDO> insert(ActionDO action) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(ActionDOParametersMapper.INSTANCE)
      .mapTo(ActionDORowMapper.INSTANCE)
      .execute(action)
      .compose(rowSet -> {
        Iterator<ActionDO> it = rowSet.iterator();
        if (it.hasNext()) return Future.succeededFuture(it.next());
        else              return Future.failedFuture("Insert did not return a row for action name:"+action.getName());
      });
  }

  @Override
  public Future<Optional<ActionDO>> update(ActionDO action) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(ActionDOParametersMapper.INSTANCE)
      .mapTo(ActionDORowMapper.INSTANCE)
      .execute(action)
      .map(this::firstOptional);
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount);
  }

  private Optional<ActionDO> firstOptional(Iterable<ActionDO> rs) {
    Iterator<ActionDO> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }
}

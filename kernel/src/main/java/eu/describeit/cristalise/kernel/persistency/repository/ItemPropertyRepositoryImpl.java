package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO;
import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.*;

public class ItemPropertyRepositoryImpl implements ItemPropertyRepository {

  private final SqlClient client;

  private static final String TABLE = "item_property";
  private static final String COLUMNS = "id,name,value,is_mutable,item_id";
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (name, value, is_mutable, item_id) VALUES (#{name}, #{value}, #{is_mutable}, #{item_id}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET name=#{name}, value=#{value}, is_mutable=#{is_mutable}, item_id=#{item_id} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}";

  public ItemPropertyRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<ItemPropertyDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(ItemPropertyDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(RepositoryUtils::firstOptional);
  }

  @Override
  public Future<List<ItemPropertyDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(ItemPropertyDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(RepositoryUtils::toList);
  }

  @Override
  public Future<ItemPropertyDO> insert(ItemPropertyDO itemProperty) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(ItemPropertyDOParametersMapper.INSTANCE)
      .mapTo(ItemPropertyDORowMapper.INSTANCE)
      .execute(itemProperty)
      .compose(rowSet -> RepositoryUtils.firstOrFail(rowSet, "Insert did not return a row for item_property name:"+itemProperty.getName()));
  }

  @Override
  public Future<Optional<ItemPropertyDO>> update(ItemPropertyDO itemProperty) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(ItemPropertyDOParametersMapper.INSTANCE)
      .mapTo(ItemPropertyDORowMapper.INSTANCE)
      .execute(itemProperty)
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

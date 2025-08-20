package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO;
import eu.describeit.cristalise.kernel.persistency.domain.ItemDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.ItemDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.SqlClient;

import java.util.*;

public class ItemRepositoryImpl implements ItemRepository {

  private final SqlClient client;

  private static final String TABLE = "item";
  private static final String COLUMNS = "id,uuid,name,type,version";
  private static final String SQL_FIND_BY_ID     = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_BY_UUID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE uuid=#{uuid}";
  private static final String SQL_FIND_ALL       = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT         = "INSERT INTO " + TABLE + " (uuid, name, type, version) VALUES (#{uuid}, #{name}, #{type}, #{version}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE         = "UPDATE "      + TABLE + " SET uuid=#{uuid}, name=#{name}, type=#{type}, version=#{version} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID   = "DELETE FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_DELETE_BY_UUID = "DELETE FROM " + TABLE + " WHERE uuid=#{uuid}";

  public ItemRepositoryImpl(SqlClient client) {
    this.client = client;
  }

  @Override
  public Future<Optional<ItemDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(this::firstOptional);
  }

  @Override
  public Future<Optional<ItemDO>> findByUuid(UUID uuid) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_UUID)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("uuid", uuid))
      .map(this::firstOptional);
  }

  @Override
  public Future<List<ItemDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rowSet -> {
        List<ItemDO> list = new ArrayList<>();
        for (ItemDO item : rowSet) list.add(item);
        return list;
      });
  }

  @Override
  public Future<ItemDO> insert(ItemDO item) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(ItemDOParametersMapper.INSTANCE)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(item)
      .compose(rowSet -> {
        Iterator<ItemDO> it = rowSet.iterator();

        if (it.hasNext()) return Future.succeededFuture(it.next());
        else              return Future.failedFuture("Insert did not return a row uuid:"+item.getUuid());
      });
  }

  @Override
  public Future<Optional<ItemDO>> update(ItemDO item) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(ItemDOParametersMapper.INSTANCE)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(item)
      .map(this::firstOptional);
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount);
  }

  @Override
  public Future<Integer> deleteByUuid(UUID uuid) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_UUID)
      .execute( Collections.singletonMap("uuid", uuid))
      .map(SqlResult::rowCount);
  }

  private Optional<ItemDO> firstOptional(Iterable<ItemDO> rs) {
    Iterator<ItemDO> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }
}

package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO;
import eu.describeit.cristalise.kernel.persistency.domain.ItemDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.ItemDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.SqlClient;

import java.util.*;

public class ItemRepositoryImpl implements ItemRepository {

  private final SqlClient client;

  private static final String TABLE = "item";
  private static final String COLUMNS = "id, uuid, name, type, version";
  private static final String SQL_FIND_BY_ID     = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_BY_UUID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE uuid=#{uuid}";
  private static final String SQL_FIND_ALL       = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT         = "INSERT INTO " + TABLE + " (uuid, name, type, version) VALUES (#{uuid}, #{name}, #{type}, #{version}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE         = "UPDATE " + TABLE + " SET uuid=#{uuid}, name=#{name}, type=#{type}, version=#{version} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID   = "DELETE FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_DELETE_BY_UUID = "DELETE FROM " + TABLE + " WHERE uuid=#{uuid}";

  public ItemRepositoryImpl(SqlClient client) {
    this.client = client;
  }

  @Override
  public Future<Optional<ItemDO>> findById(Long id) {
    Map<String, Object> params = Collections.singletonMap("id", id);
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(params)
      .map(this::firstOptional);
  }

  @Override
  public Future<Optional<ItemDO>> findByUuid(UUID uuid) {
    Map<String, Object> params = Collections.singletonMap("uuid", uuid);
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_UUID)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(params)
      .map(this::firstOptional);
  }

  @Override
  public Future<List<ItemDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rs -> {
        List<ItemDO> list = new ArrayList<>();
        for (ItemDO item : rs) { // RowSet<ItemDO> is iterable when mapTo is set
          list.add(item);
        }
        return list;
      });
  }

  @Override
  public Future<ItemDO> insert(ItemDO item) {
    return SqlTemplate
      .forQuery(client, SQL_INSERT)
      .mapFrom(ItemDOParametersMapper.INSTANCE)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(item)
      .compose(rs -> {
        Iterator<ItemDO> it = rs.iterator();
        if (it.hasNext()) return Future.succeededFuture(it.next());
        return Future.failedFuture("Insert did not return a row");
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
    Map<String, Object> params = Collections.singletonMap("id", id);
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(params)
      .map(rs -> rs.rowCount());
  }

  @Override
  public Future<Integer> deleteByUuid(UUID uuid) {
    Map<String, Object> params = Collections.singletonMap("uuid", uuid);
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_UUID)
      .execute(params)
      .map(rs -> rs.rowCount());
  }

  private Optional<ItemDO> firstOptional(Iterable<ItemDO> rs) {
    Iterator<ItemDO> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }
}

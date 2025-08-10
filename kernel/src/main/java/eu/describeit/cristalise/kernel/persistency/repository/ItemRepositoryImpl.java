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

  public ItemRepositoryImpl(SqlClient client) {
    this.client = client;
  }

  @Override
  public Future<Optional<ItemDO>> findById(Long id) {
    Map<String, Object> params = Collections.singletonMap("id", id);
    return SqlTemplate
      .forQuery(client, "SELECT id, uuid, name, type, version FROM item WHERE id=#{id}")
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(params)
      .map(this::firstOptional);
  }

  @Override
  public Future<Optional<ItemDO>> findByUuid(UUID uuid) {
    Map<String, Object> params = Collections.singletonMap("uuid", uuid);
    return SqlTemplate
      .forQuery(client, "SELECT id, uuid, name, type, version FROM item WHERE uuid=#{uuid}")
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(params)
      .map(this::firstOptional);
  }

  @Override
  public Future<List<ItemDO>> findAll() {
    return SqlTemplate
      .forQuery(client, "SELECT id, uuid, name, type, version FROM item ORDER BY id")
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
    // Use forQuery with RETURNING to map back to ItemDO
    String sql = "INSERT INTO item (uuid, name, type, version) VALUES (#{uuid}, #{name}, #{type}, #{version}) " +
                 "RETURNING id, uuid, name, type, version";
    return SqlTemplate
      .forQuery(client, sql)
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
    String sql = "UPDATE item SET uuid=#{uuid}, name=#{name}, type=#{type}, version=#{version} WHERE id=#{id} " +
                 "RETURNING id, uuid, name, type, version";
    return SqlTemplate
      .forQuery(client, sql)
      .mapFrom(ItemDOParametersMapper.INSTANCE)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(item)
      .map(this::firstOptional);
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    Map<String, Object> params = Collections.singletonMap("id", id);
    return SqlTemplate
      .forUpdate(client, "DELETE FROM item WHERE id=#{id}")
      .execute(params)
      .map(rs -> rs.rowCount());
  }

  @Override
  public Future<Integer> deleteByUuid(UUID uuid) {
    Map<String, Object> params = Collections.singletonMap("uuid", uuid);
    return SqlTemplate
      .forUpdate(client, "DELETE FROM item WHERE uuid=#{uuid}")
      .execute(params)
      .map(rs -> rs.rowCount());
  }

  private Optional<ItemDO> firstOptional(Iterable<ItemDO> rs) {
    Iterator<ItemDO> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }
}

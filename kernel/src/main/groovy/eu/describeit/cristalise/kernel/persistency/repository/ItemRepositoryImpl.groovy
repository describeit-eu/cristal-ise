package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO
import eu.describeit.cristalise.kernel.persistency.domain.ItemDOParametersMapper
import eu.describeit.cristalise.kernel.persistency.domain.ItemDORowMapper
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.templates.SqlTemplate
import io.vertx.sqlclient.SqlClient

import static eu.describeit.cristalise.kernel.persistency.PersistencyUtils.firstOptional
import static eu.describeit.cristalise.kernel.persistency.PersistencyUtils.firstOrFail
import static eu.describeit.cristalise.kernel.persistency.PersistencyUtils.toList

@CompileStatic
public class ItemRepositoryImpl implements ItemRepository {

  private final SqlClient client

  private static final String TABLE = "item"
  private static final String COLUMNS = "id,name,type,version"
  private static final String SQL_FIND_BY_ID     = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_ALL       = "SELECT " + COLUMNS + " FROM " + TABLE
  private static final String SQL_INSERT         = "INSERT INTO " + TABLE + " (id, name, type, version) VALUES (#{id}, #{name}, #{type}, #{version}) RETURNING " + COLUMNS
  private static final String SQL_UPDATE         = "UPDATE "      + TABLE + " SET id=#{id}, name=#{name}, type=#{type}, version=#{version} WHERE id=#{id} RETURNING " + COLUMNS
  private static final String SQL_DELETE_BY_ID   = "DELETE FROM " + TABLE + " WHERE id=#{id}"

  public ItemRepositoryImpl(SqlClient client) {
    this.client = client
  }

  @Override
  public Future<Optional<ItemDO>> findById(UUID id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", (Object)id))
      .map(rs -> firstOptional(rs))
  }

  @Override
  public Future<List<ItemDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rs -> toList(rs))
  }

  @Override
  public Future<ItemDO> insert(ItemDO item) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(ItemDOParametersMapper.INSTANCE)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(item)
      .compose(rowSet -> firstOrFail(rowSet, "Insert did not return a row id:"+item.getId()))
  }

  @Override
  public Future<Optional<ItemDO>> update(ItemDO item) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(ItemDOParametersMapper.INSTANCE)
      .mapTo(ItemDORowMapper.INSTANCE)
      .execute(item)
      .map(rs -> firstOptional(rs))
  }

  @Override
  public Future<Integer> deleteById(UUID id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", (Object)id))
      .map(rs -> rs.rowCount())
  }
}

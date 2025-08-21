package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.CollectionDO;
import eu.describeit.cristalise.kernel.persistency.domain.CollectionDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.CollectionDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.*;

public class CollectionRepositoryImpl implements CollectionRepository {

  private final SqlClient client;

  private static final String TABLE = "collection";
  private static final String COLUMNS = "id,name,version,properties,item_id";
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (name, version, properties, item_id) VALUES (#{name}, #{version}, #{properties}, #{item_id}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET name=#{name}, version=#{version}, properties=#{properties}, item_id=#{item_id} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}";

  public CollectionRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<CollectionDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(CollectionDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(RepositoryUtils::firstOptional);
  }

  @Override
  public Future<List<CollectionDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(CollectionDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(RepositoryUtils::toList);
  }

  @Override
  public Future<CollectionDO> insert(CollectionDO collection) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(CollectionDOParametersMapper.INSTANCE)
      .mapTo(CollectionDORowMapper.INSTANCE)
      .execute(collection)
      .compose(rowSet -> RepositoryUtils.firstOrFail(rowSet, "Insert did not return a row for collection name:"+collection.getName()));
  }

  @Override
  public Future<Optional<CollectionDO>> update(CollectionDO collection) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(CollectionDOParametersMapper.INSTANCE)
      .mapTo(CollectionDORowMapper.INSTANCE)
      .execute(collection)
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

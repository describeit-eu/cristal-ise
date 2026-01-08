package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDOParametersMapper
import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDORowMapper
import groovy.transform.CompileStatic
import io.vertx.core.Future
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.templates.SqlTemplate

import static eu.describeit.cristalise.kernel.persistency.PersistencyUtils.firstOptional
import static eu.describeit.cristalise.kernel.persistency.PersistencyUtils.firstOrFail
import static eu.describeit.cristalise.kernel.persistency.PersistencyUtils.toList

@CompileStatic
public class DomainPathRepositoryImpl implements DomainPathRepository {

  private final SqlClient client

  private static final String TABLE = "domain_path"
  private static final String COLUMNS = "id,path,item_id"
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (path, item_id) VALUES (#{path}, #{item_id}) RETURNING " + COLUMNS
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET path=#{path}, item_id=#{item_id} WHERE id=#{id} RETURNING " + COLUMNS
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}"
  private static final String SQL_FIND_BY_ITEM_ID = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE item_id=#{item_id}"
  private static final String SQL_FIND_BY_PATH = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE path = text2ltree(#{path})"
  private static final String SQL_GET_CHILDREN = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE path <@ text2ltree(#{path}) AND nlevel(path) = nlevel(text2ltree(#{path})) + 1"
  private static final String SQL_GET_TREE     = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE path <@ text2ltree(#{path})"

  public DomainPathRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<DomainPathDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", (Object)id))
      .map(rs -> firstOptional(rs))
  }

  @Override
  public Future<List<DomainPathDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rs -> toList(rs))
  }

  @Override
  public Future<DomainPathDO> insert(DomainPathDO domainPath) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(DomainPathDOParametersMapper.INSTANCE)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(domainPath)
      .compose(rowSet -> firstOrFail(rowSet, "Insert did not return a row for domain path:"+domainPath.getPath()))
  }

  @Override
  public Future<Optional<DomainPathDO>> update(DomainPathDO domainPath) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(DomainPathDOParametersMapper.INSTANCE)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(domainPath)
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
  public Future<List<DomainPathDO>> findByItemId(UUID itemId) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ITEM_ID)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("item_id", (Object)itemId))
      .map(rs -> toList(rs))
  }

  @Override
  public Future<Optional<DomainPathDO>> findByPath(String path) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_PATH)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("path", (Object)path))
      .map(rs -> firstOptional(rs))
  }

  @Override
  public Future<List<DomainPathDO>> getChildren(String path) {
    Map<String, Object> params = Map.of("path", (Object)path)
    return SqlTemplate
      .forQuery(client, SQL_GET_CHILDREN)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(params)
      .map(rs -> toList(rs))
  }

  @Override
  public Future<List<DomainPathDO>> getTree(String path) {
    Map<String, Object> params = Map.of("path", (Object)path)
    return SqlTemplate
      .forQuery(client, SQL_GET_TREE)
      .mapTo(DomainPathDORowMapper.INSTANCE)
      .execute(params)
      .map(rs -> toList(rs))
  }
}

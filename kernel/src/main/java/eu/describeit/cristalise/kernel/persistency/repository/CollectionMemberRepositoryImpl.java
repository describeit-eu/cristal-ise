package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.CollectionMemberDO;
import eu.describeit.cristalise.kernel.persistency.domain.CollectionMemberDOParametersMapper;
import eu.describeit.cristalise.kernel.persistency.domain.CollectionMemberDORowMapper;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.*;

public class CollectionMemberRepositoryImpl implements CollectionMemberRepository {

  private final SqlClient client;

  private static final String TABLE = "collection_member";
  private static final String COLUMNS = "id,child_item,properties,collection_id";
  private static final String SQL_FIND_BY_ID   = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id=#{id}";
  private static final String SQL_FIND_ALL     = "SELECT " + COLUMNS + " FROM " + TABLE;
  private static final String SQL_INSERT       = "INSERT INTO " + TABLE + " (child_item, properties, collection_id) VALUES (#{child_item}, #{properties}, #{collection_id}) RETURNING " + COLUMNS;
  private static final String SQL_UPDATE       = "UPDATE "      + TABLE + " SET child_item=#{child_item}, properties=#{properties}, collection_id=#{collection_id} WHERE id=#{id} RETURNING " + COLUMNS;
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE + " WHERE id=#{id}";

  public CollectionMemberRepositoryImpl(SqlClient client) { this.client = client; }

  @Override
  public Future<Optional<CollectionMemberDO>> findById(Long id) {
    return SqlTemplate
      .forQuery(client, SQL_FIND_BY_ID)
      .mapTo(CollectionMemberDORowMapper.INSTANCE)
      .execute(Collections.singletonMap("id", id))
      .map(this::firstOptional);
  }

  @Override
  public Future<List<CollectionMemberDO>> findAll() {
    return SqlTemplate
      .forQuery(client, SQL_FIND_ALL)
      .mapTo(CollectionMemberDORowMapper.INSTANCE)
      .execute(Collections.emptyMap())
      .map(rowSet -> {
        List<CollectionMemberDO> list = new ArrayList<>();
        for (CollectionMemberDO row : rowSet) list.add(row);
        return list;
      });
  }

  @Override
  public Future<CollectionMemberDO> insert(CollectionMemberDO member) {
    return SqlTemplate
      .forUpdate(client, SQL_INSERT)
      .mapFrom(CollectionMemberDOParametersMapper.INSTANCE)
      .mapTo(CollectionMemberDORowMapper.INSTANCE)
      .execute(member)
      .compose(rowSet -> {
        Iterator<CollectionMemberDO> it = rowSet.iterator();
        if (it.hasNext()) return Future.succeededFuture(it.next());
        else              return Future.failedFuture("Insert did not return a row for collection_member id:"+member.getCollectionId());
      });
  }

  @Override
  public Future<Optional<CollectionMemberDO>> update(CollectionMemberDO member) {
    return SqlTemplate
      .forQuery(client, SQL_UPDATE)
      .mapFrom(CollectionMemberDOParametersMapper.INSTANCE)
      .mapTo(CollectionMemberDORowMapper.INSTANCE)
      .execute(member)
      .map(this::firstOptional);
  }

  @Override
  public Future<Integer> deleteById(Long id) {
    return SqlTemplate
      .forUpdate(client, SQL_DELETE_BY_ID)
      .execute(Collections.singletonMap("id", id))
      .map(SqlResult::rowCount);
  }

  private Optional<CollectionMemberDO> firstOptional(Iterable<CollectionMemberDO> rs) {
    Iterator<CollectionMemberDO> it = rs.iterator();
    return it.hasNext() ? Optional.ofNullable(it.next()) : Optional.empty();
  }
}

package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ItemRepositoryIT {

  @Container
  private PostgreSQLContainer<?> pgContainer;

  private Vertx vertx;
  private Pool pool;
  private ItemRepository repository;

  @BeforeAll
  void setUpAll() throws Exception {
    // Init Vert.x and PgPool client
    vertx = Vertx.vertx();
    pgContainer = getPGContainer();
    pgContainer.start();

    pool = getPool(vertx, pgContainer);
    liquibaseCreateTables(pgContainer);
    liquibaseLoadTestData(pgContainer);
    repository = new ItemRepositoryImpl(pool);
  }

  @AfterAll
  void tearDownAll() {
    if (pool != null) {
      pool.close();
    }
    if (vertx != null) {
      vertx.close().toCompletionStage().toCompletableFuture().orTimeout(5, SECONDS).exceptionally(ex -> null);
    }
    pgContainer.stop();
  }

  private static <T> T awaitOrig(Future<T> future) {
    try {
      return future.toCompletionStage().toCompletableFuture().get(5, SECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> T await(Future<T> future) {
    if (Context.isOnVertxThread()) {
      // On the Vert.x event loop thread using virtual threads, block
      log.info("await() - Vert.x event loop thread");
      return Future.await(future);
    }
    else {
      // On a platform thread, wait asynchronously
      log.info("await() - Platform thread");
      CompletableFuture<T> futureResult = new CompletableFuture<>();

      future
        .onComplete(ar -> {
          if (ar.succeeded()) {
            log.info("await(onComplete) - SUCCEEDED result:{}", ar.result());
            futureResult.complete(ar.result());
          } else if (ar.failed()) {
            log.info("await(onComplete) - FAILED", ar.cause());
            futureResult.completeExceptionally(ar.cause());
          } else {
            log.warn("await(onComplete) - ??????? result:{}", ar);
            futureResult.completeExceptionally(new RuntimeException("Unexpected result: " + ar));
          }
        });

      // blocks until the future is complete or the timeout is reached
      try {
        var result = futureResult.get(5, SECONDS);
        log.info("await(get) - futureResult:{} result:{}", futureResult, result);
        return result;
      } catch (Exception e) {
        log.error("await(get) - FAILED", e);
        throw new RuntimeException(e);
      }
    }
  }

  @Test
  void testFindByUuid() {
    var uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    //var uuid = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");
    Optional<ItemDO> foundByUuid = await(repository.findByUuid(uuid));

    assertTrue(foundByUuid.isPresent());
    var item = foundByUuid.get();

    assertEquals(uuid, item.getUuid());
    assertEquals(1, item.getId());
    assertEquals("Budapest", item.getName());
    assertEquals("Country", item.getType());
    assertEquals("v1", item.getVersion());
  }

  @Test
  void testInsert() {
    ItemDO toInsert = new ItemDO(100L, UUID.randomUUID(), "name-1", "type-A", "v1");

    ItemDO inserted = await(repository.insert(toInsert));

    assertNotNull(inserted);
    assertNotNull(inserted.getId(), "Inserted item should have generated id");
    assertEquals(toInsert.getUuid(), inserted.getUuid());
    assertEquals("name-1", inserted.getName());
  }

  /*
  @Test
  void testCrudOperations() {
    // Initially empty
    List<ItemDO> allBefore = await(repository.findAll());
    assertNotNull(allBefore);

    // Insert
    ItemDO toInsert = new ItemDO()
      .uuid(UUID.randomUUID())
      .name("name-1")
      .type("type-A")
      .version("v1");

    ItemDO inserted = await(repository.insert(toInsert));
    assertNotNull(inserted);
    assertNotNull(inserted.id(), "Inserted item should have generated id");
    assertEquals(toInsert.uuid(), inserted.uuid());
    assertEquals("name-1", inserted.name());

    // Find by id
    Optional<ItemDO> foundById = await(repository.findById(inserted.id()));
    assertTrue(foundById.isPresent());
    assertEquals(inserted.uuid(), foundById.get().uuid());

    // Find by uuid
    Optional<ItemDO> foundByUuid = await(repository.findByUuid(inserted.uuid()));
    assertTrue(foundByUuid.isPresent());
    assertEquals(inserted.id(), item.id());

    // Find all should contain at least one
    List<ItemDO> all = await(repository.findAll());
    assertTrue(all.size() >= 1);

    // Update
    ItemDO toUpdate = new ItemDO()
      .id(inserted.id())
      .uuid(inserted.uuid())
      .name("name-2")
      .type("type-A")
      .version("v2");

    Optional<ItemDO> updatedOpt = await(repository.update(toUpdate));
    assertTrue(updatedOpt.isPresent());
    ItemDO updated = updatedOpt.get();
    assertEquals(inserted.id(), updated.id());
    assertEquals("name-2", updated.name());
    assertEquals("v2", updated.version());

    // Delete by id
    Integer rows = await(repository.deleteById(updated.id()));
    assertEquals(1, rows);

    Optional<ItemDO> afterDelete = await(repository.findById(updated.id()));
    assertTrue(afterDelete.isEmpty());

    // Insert again and delete by uuid
    ItemDO again = await(repository.insert(new ItemDO()
      .uuid(UUID.randomUUID()).name("again").type("type-B").version("v1")));

    Integer rowsByUuid = await(repository.deleteByUuid(again.uuid()));
    assertEquals(1, rowsByUuid);

    Optional<ItemDO> afterDeleteUuid = await(repository.findByUuid(again.uuid()));
    assertTrue(afterDeleteUuid.isEmpty());
  }
*/
}

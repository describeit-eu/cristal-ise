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
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ItemRepositoryIT {

  private PostgreSQLContainer<?> pgContainer;

  private Vertx vertx;
  private Pool pool;
  private ItemRepository repository;

  final UUID uuidZero = UUID.fromString("00000000-0000-0000-0000-000000000000");

  // to be found
  final long idBudapest = 1L;
  final UUID uuidBudapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");

  // to be updated
  final UUID uuidDelhi = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d");

  // to be deleted
  final UUID uuidLondon = UUID.fromString("04a71ecd-7cda-439f-bf6e-6517a824f753");
  final long idParis = 2L;

  @BeforeAll
  void setUpAll() throws Exception {
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
  void testFindBy() {
    var nameBudapest = "Budapest";
    var typeBudapest = "City";
    var versionBudapest = "v1";

    // use findByUuid
    Optional<ItemDO> foundByUuid = await(repository.findByUuid(uuidBudapest));

    assertTrue(foundByUuid.isPresent());
    var itemByUuid = foundByUuid.get();

    assertEquals(uuidBudapest,    itemByUuid.getUuid());
    assertEquals(idBudapest,      itemByUuid.getId());
    assertEquals(nameBudapest,    itemByUuid.getName());
    assertEquals(typeBudapest,    itemByUuid.getType());
    assertEquals(versionBudapest, itemByUuid.getVersion());

    // use findById and compare it with findByUuid
    Optional<ItemDO> foundById = await(repository.findById(idBudapest));

    assertTrue(foundById.isPresent());
    var itemById = foundById.get();

    assertEquals(itemByUuid, itemById);

    // test non-existent item
    Optional<ItemDO> noneExistent = await(repository.findByUuid(uuidZero));
    assertTrue(noneExistent.isEmpty());
  }

  @Test
  void testFindAll() {
    List<ItemDO> foundItems = await(repository.findAll());
    assertTrue(foundItems.size() >= 8, "There should be at least 8 cities in the database");
  }

  @Test
  void testInsert() {
    ItemDO toInsert = new ItemDO(UUID.randomUUID(), "Tokyo", "megaCity", "v1.1");

    ItemDO cityTokyo = await(repository.insert(toInsert));

    assertNotNull(cityTokyo);
    assertNotNull(cityTokyo.getId(), "Tokyo item should have generated id");

    assertEquals(toInsert.getUuid(),    cityTokyo.getUuid());
    assertEquals(toInsert.getName(),    cityTokyo.getName());
    assertEquals(toInsert.getType(),    cityTokyo.getType());
    assertEquals(toInsert.getVersion(), cityTokyo.getVersion());
  }

  @Test
  void testUpdate() {
    ItemDO cityDelhi = await(repository.findByUuid(uuidDelhi)).get();
    cityDelhi.setName("Mumbai");

    ItemDO cityMumbai= await(repository.update(cityDelhi)).get();

    assertEquals("Mumbai", cityMumbai.getName());
    assertEquals(cityDelhi, cityMumbai  );
  }

  @Test
  void testDeleteById() {
    var rowsById = await(repository.deleteById(idParis));
    assertEquals(1, rowsById);

    Optional<ItemDO> afterDelete = await(repository.findById(idParis));
    assertTrue(afterDelete.isEmpty());
  }

  @Test
  void testDeleteByUuid() {
    var rowsById = await(repository.deleteByUuid(uuidLondon));
    assertEquals(1, rowsById);

    Optional<ItemDO> afterDelete = await(repository.findByUuid(uuidLondon));
    assertTrue(afterDelete.isEmpty());
  }
}

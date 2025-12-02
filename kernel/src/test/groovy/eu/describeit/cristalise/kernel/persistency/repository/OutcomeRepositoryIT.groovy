package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import io.vertx.core.json.JsonObject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.kernel.persistency.DatabaseTestUtils.await
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@CompileStatic
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class OutcomeRepositoryIT extends AbstractRepositoryIT {

  private OutcomeRepository repository

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new OutcomeRepositoryImpl(pool)
  }

  @Test
  void testFindAll() {
    List<OutcomeDO> outcomes = await(repository.findAll())
    // From 08-outcome.csv there are 9 non-header lines
    assertTrue(outcomes.size() >= 9, "There should be at least 9 outcomes loaded from CSV")
  }

  @Test
  void testInsertAndFindById() {
    OutcomeDO toInsert = new OutcomeDO(
      UUID.fromString("33333333-3333-3333-3333-333333333333"), // schema
      "v2", // schemaVersion
      "{\"action\":\"TestAction\",\"status\":\"pending\",\"data\":{\"testField\":\"testValue\"}}", // data
      1L, // eventId
      UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd") // itemId
    )

    OutcomeDO inserted = await(repository.insert(toInsert))
    assertNotNull(inserted.getId())
    assertEquals(toInsert.getSchema(), inserted.getSchema())
    assertEquals(toInsert.getSchemaVersion(), inserted.getSchemaVersion())
    assertEquals(toInsert.getData(), inserted.getData())
    assertEquals(toInsert.getEventId(), inserted.getEventId())
    assertEquals(toInsert.getItemId(), inserted.getItemId())

    Optional<OutcomeDO> fetched = await(repository.findById(inserted.getId()))
    assertTrue(fetched.isPresent())
    assertEquals(inserted, fetched.get())
  }

  @Test
  void testUpdate() {
    // insert a row then update it
    OutcomeDO base = new OutcomeDO(
      UUID.fromString("44444444-4444-4444-4444-444444444444"), // schema
      "v1", // schemaVersion
      "{\"action\":\"OriginalAction\",\"status\":\"pending\"}", // data
      1L, // eventId
      UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd") // itemId
    )
    OutcomeDO inserted = await(repository.insert(base))

    inserted.setSchemaVersion("v3")
    inserted.setData(new JsonObject("{\"action\":\"UpdatedAction\",\"status\":\"completed\"}"))

    Optional<OutcomeDO> updatedOpt = await(repository.update(inserted))
    assertTrue(updatedOpt.isPresent())
    OutcomeDO updated = updatedOpt.get()

    assertEquals(inserted.getId(), updated.getId())
    assertEquals("v3", updated.getSchemaVersion())
    assertEquals("{\"action\":\"UpdatedAction\",\"status\":\"completed\"}", updated.getData().toString())
    assertEquals(inserted.getSchema(), updated.getSchema())
    assertEquals(inserted.getEventId(), updated.getEventId())
    assertEquals(inserted.getItemId(), updated.getItemId())
  }

  @Test
  void testDeleteByIdAndFindNoneExistent() {
    // insert one to delete
    OutcomeDO base = new OutcomeDO(
      UUID.fromString("55555555-5555-5555-5555-555555555555"), // schema
      "v1", // schemaVersion
      "{\"action\":\"ToDelete\",\"status\":\"pending\"}", // data
      1L, // eventId
      UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd") // itemId
    )
    OutcomeDO inserted = await(repository.insert(base))

    int rows = await(repository.deleteById(inserted.getId()))
    assertEquals(1, rows)

    Optional<OutcomeDO> afterDelete = await(repository.findById(inserted.getId()))
    assertTrue(afterDelete.isEmpty())

    // also check non-existent id returns empty
    Optional<OutcomeDO> none = await(repository.findById(-1L))
    assertTrue(none.isEmpty())
  }

  @Test
  void testFindByItemId() {
    UUID testItemId = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd")

    // Insert two outcomes for the same item
    OutcomeDO outcome1 = new OutcomeDO(
      UUID.fromString("66666666-6666-6666-6666-666666666666"), // schema
      "v1", // schemaVersion
      "{\"action\":\"FirstAction\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )
    OutcomeDO outcome2 = new OutcomeDO(
      UUID.fromString("77777777-7777-7777-7777-777777777777"), // schema
      "v1", // schemaVersion
      "{\"action\":\"SecondAction\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )

    OutcomeDO inserted1 = await(repository.insert(outcome1))
    OutcomeDO inserted2 = await(repository.insert(outcome2))

    List<OutcomeDO> foundOutcomes = await(repository.findByItemId(testItemId))
    assertEquals(5, foundOutcomes.size())

    // Verify both outcomes are present (order may vary)
    assertTrue(foundOutcomes.stream().anyMatch(o -> o.getId().equals(inserted1.getId())))
    assertTrue(foundOutcomes.stream().anyMatch(o -> o.getId().equals(inserted2.getId())))
  }

  @Test
  void testDeleteByItemId() {
    UUID testItemId = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d")

    // Insert two outcomes for the same item
    OutcomeDO outcome1 = new OutcomeDO(
      UUID.fromString("88888888-8888-8888-8888-888888888888"), // schema
      "v1", // schemaVersion
      "{\"action\":\"ToDeleteAction1\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )
    OutcomeDO outcome2 = new OutcomeDO(
      UUID.fromString("99999999-9999-9999-9999-999999999999"), // schema
      "v1", // schemaVersion
      "{\"action\":\"ToDeleteAction2\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )

    OutcomeDO inserted1 = await(repository.insert(outcome1))
    OutcomeDO inserted2 = await(repository.insert(outcome2))

    // Verify both outcomes exist
    List<OutcomeDO> beforeDelete = await(repository.findByItemId(testItemId))
    assertEquals(2, beforeDelete.size())

    // Delete by item ID
    int deletedRows = await(repository.deleteByItemId(testItemId))
    assertEquals(2, deletedRows)

    // Verify outcomes are gone
    List<OutcomeDO> afterDelete = await(repository.findByItemId(testItemId))
    assertTrue(afterDelete.isEmpty())

    // Verify individual lookups also return empty
    Optional<OutcomeDO> outcome1AfterDelete = await(repository.findById(inserted1.getId()))
    Optional<OutcomeDO> outcome2AfterDelete = await(repository.findById(inserted2.getId()))
    assertTrue(outcome1AfterDelete.isEmpty())
    assertTrue(outcome2AfterDelete.isEmpty())
  }

  @Test
  void testFindByItemIdWithNonExistentItem() {
    UUID nonExistentItemId = UUID.fromString("00000000-0000-0000-0000-000000000000")
    List<OutcomeDO> outcomes = await(repository.findByItemId(nonExistentItemId))
    assertTrue(outcomes.isEmpty())
  }

  @Test
  void testDeleteByItemIdWithNonExistentItem() {
    UUID nonExistentItemId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    int deletedRows = await(repository.deleteByItemId(nonExistentItemId))
    assertEquals(0, deletedRows)
  }
}

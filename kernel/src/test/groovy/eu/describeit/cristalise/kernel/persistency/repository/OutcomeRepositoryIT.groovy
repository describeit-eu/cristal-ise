package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import io.vertx.core.json.JsonObject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestIds.*
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
    List<OutcomeDO> outcomes = repository.findAll().await()
    // From 08-outcome.csv there are 9 non-header lines
    assertTrue(outcomes.size() >= 9, "There should be at least 9 outcomes loaded from CSV")
  }

  @Test
  void testInsertAndFindById() {
    OutcomeDO toInsert = new OutcomeDO(
      SCHEMA_1.uuid, // schema
      "v2", // schemaVersion
      "{\"action\":\"TestAction\",\"status\":\"pending\",\"data\":{\"testField\":\"testValue\"}}", // data
      1L, // eventId
      BUDAPEST.uuid // itemId
    )

    OutcomeDO inserted = repository.insert(toInsert).await()
    assertNotNull(inserted.getId())
    assertEquals(toInsert.getSchema(), inserted.getSchema())
    assertEquals(toInsert.getSchemaVersion(), inserted.getSchemaVersion())
    assertEquals(toInsert.getData(), inserted.getData())
    assertEquals(toInsert.getEventId(), inserted.getEventId())
    assertEquals(toInsert.getItemId(), inserted.getItemId())

    Optional<OutcomeDO> fetched = repository.findById(inserted.getId()).await()
    assertTrue(fetched.isPresent())
    assertEquals(inserted, fetched.get())
  }

  @Test
  void testUpdate() {
    // insert a row then update it
    OutcomeDO base = new OutcomeDO(
      SCHEMA_2.uuid, // schema
      "v1", // schemaVersion
      "{\"action\":\"OriginalAction\",\"status\":\"pending\"}", // data
      1L, // eventId
      BUDAPEST.uuid // itemId
    )
    OutcomeDO inserted = repository.insert(base).await()

    inserted.setSchemaVersion("v3")
    inserted.setData(new JsonObject("{\"action\":\"UpdatedAction\",\"status\":\"completed\"}"))

    Optional<OutcomeDO> updatedOpt = repository.update(inserted).await()
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
      SCHEMA_3.uuid, // schema
      "v1", // schemaVersion
      "{\"action\":\"ToDelete\",\"status\":\"pending\"}", // data
      1L, // eventId
      BUDAPEST.uuid // itemId
    )
    OutcomeDO inserted = repository.insert(base).await()

    int rows = repository.deleteById(inserted.getId()).await()
    assertEquals(1, rows)

    Optional<OutcomeDO> afterDelete = repository.findById(inserted.getId()).await()
    assertTrue(afterDelete.isEmpty())

    // also check non-existent id returns empty
    Optional<OutcomeDO> none = repository.findById(-1L).await()
    assertTrue(none.isEmpty())
  }

  @Test
  void testFindByItemId() {
    UUID testItemId = BUDAPEST.uuid

    // Insert two outcomes for the same item
    OutcomeDO outcome1 = new OutcomeDO(
      SCHEMA_4.uuid, // schema
      "v1", // schemaVersion
      "{\"action\":\"FirstAction\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )
    OutcomeDO outcome2 = new OutcomeDO(
      SCHEMA_5.uuid, // schema
      "v1", // schemaVersion
      "{\"action\":\"SecondAction\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )

    OutcomeDO inserted1 = repository.insert(outcome1).await()
    OutcomeDO inserted2 = repository.insert(outcome2).await()

    List<OutcomeDO> foundOutcomes = repository.findByItemId(testItemId).await()
    assertEquals(5, foundOutcomes.size())

    // Verify both outcomes are present (order may vary)
    assertTrue(foundOutcomes.stream().anyMatch(o -> o.getId().equals(inserted1.getId())))
    assertTrue(foundOutcomes.stream().anyMatch(o -> o.getId().equals(inserted2.getId())))
  }

  @Test
  void testDeleteByItemId() {
    UUID testItemId = DELHI.uuid

    // Insert two outcomes for the same item
    OutcomeDO outcome1 = new OutcomeDO(
      SCHEMA_6.uuid, // schema
      "v1", // schemaVersion
      "{\"action\":\"ToDeleteAction1\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )
    OutcomeDO outcome2 = new OutcomeDO(
      SCHEMA_7.uuid, // schema
      "v1", // schemaVersion
      "{\"action\":\"ToDeleteAction2\",\"status\":\"completed\"}", // data
      1L, // eventId
      testItemId // itemId
    )

    OutcomeDO inserted1 = repository.insert(outcome1).await()
    OutcomeDO inserted2 = repository.insert(outcome2).await()

    // Verify both outcomes exist
    List<OutcomeDO> beforeDelete = repository.findByItemId(testItemId).await()
    assertEquals(2, beforeDelete.size())

    // Delete by item ID
    int deletedRows = repository.deleteByItemId(testItemId).await()
    assertEquals(2, deletedRows)

    // Verify outcomes are gone
    List<OutcomeDO> afterDelete = repository.findByItemId(testItemId).await()
    assertTrue(afterDelete.isEmpty())

    // Verify individual lookups also return empty
    Optional<OutcomeDO> outcome1AfterDelete = repository.findById(inserted1.getId()).await()
    Optional<OutcomeDO> outcome2AfterDelete = repository.findById(inserted2.getId()).await()
    assertTrue(outcome1AfterDelete.isEmpty())
    assertTrue(outcome2AfterDelete.isEmpty())
  }

  @Test
  void testFindByItemIdWithNonExistentItem() {
    UUID nonExistentItemId = NON_EXISTENT.uuid
    List<OutcomeDO> outcomes = repository.findByItemId(nonExistentItemId).await()
    assertTrue(outcomes.isEmpty())
  }

  @Test
  void testDeleteByItemIdWithNonExistentItem() {
    UUID nonExistentItemId = NON_EXISTENT_2.uuid
    int deletedRows = repository.deleteByItemId(nonExistentItemId).await()
    assertEquals(0, deletedRows)
  }
}

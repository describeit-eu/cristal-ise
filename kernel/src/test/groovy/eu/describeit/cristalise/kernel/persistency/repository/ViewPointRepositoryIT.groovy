package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static org.junit.jupiter.api.Assertions.*

@Slf4j
@CompileStatic
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ViewPointRepositoryIT extends AbstractRepositoryIT {

  private ViewPointRepository repository

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new ViewPointRepositoryImpl(pool)
  }

  @Test
  void testFindAll() {
    List<ViewPointDO> viewPoints = repository.findAll().await()
    // From 11-viewPoint.csv there are 9 non-header lines
    assertTrue(viewPoints.size() >= 9, "There should be at least 9 viewPoints loaded from CSV")
  }

  @Test
  void testInsertAndFindById() {
    ViewPointDO toInsert = new ViewPointDO(
      "TestViewPoint", // name
      UUID.fromString("33333333-3333-3333-3333-333333333333"), // schema
      "v2", // schemaVersion
      "TestSchema", // schemaName
      1L, // outcomeId
      UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd") // itemId
    )

    ViewPointDO inserted = repository.insert(toInsert).await()
    assertNotNull(inserted.getId())
    assertEquals(toInsert.getName(), inserted.getName())
    assertEquals(toInsert.getSchema(), inserted.getSchema())
    assertEquals(toInsert.getSchemaVersion(), inserted.getSchemaVersion())
    assertEquals(toInsert.getSchemaName(), inserted.getSchemaName())
    assertEquals(toInsert.getOutcomeId(), inserted.getOutcomeId())
    assertEquals(toInsert.getItemId(), inserted.getItemId())

    Optional<ViewPointDO> fetched = repository.findById(inserted.getId()).await()
    assertTrue(fetched.isPresent())
    assertEquals(inserted, fetched.get())
  }

  @Test
  void testUpdate() {
    // insert a row then update it
    ViewPointDO base = new ViewPointDO(
      "OriginalViewPoint", // name
      UUID.fromString("44444444-4444-4444-4444-444444444444"), // schema
      "v1", // schemaVersion
      "OriginalSchema", // schemaName
      1L, // outcomeId
      UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd") // itemId
    )
    ViewPointDO inserted = repository.insert(base).await()

    inserted.setName("UpdatedViewPoint")
    inserted.setSchemaVersion("v3")
    inserted.setSchemaName("UpdatedSchema")

    Optional<ViewPointDO> updatedOpt = repository.update(inserted).await()
    assertTrue(updatedOpt.isPresent())
    ViewPointDO updated = updatedOpt.get()

    assertEquals(inserted.getId(), updated.getId())
    assertEquals("UpdatedViewPoint", updated.getName())
    assertEquals("v3", updated.getSchemaVersion())
    assertEquals("UpdatedSchema", updated.getSchemaName())
    assertEquals(inserted.getSchema(), updated.getSchema())
    assertEquals(inserted.getOutcomeId(), updated.getOutcomeId())
    assertEquals(inserted.getItemId(), updated.getItemId())
  }

  @Test
  void testDeleteByIdAndFindNoneExistent() {
    // insert one to delete
    ViewPointDO base = new ViewPointDO(
      "ToDeleteViewPoint", // name
      UUID.fromString("55555555-5555-5555-5555-555555555555"), // schema
      "v1", // schemaVersion
      "ToDeleteSchema", // schemaName
      1L, // outcomeId
      UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd") // itemId
    )
    ViewPointDO inserted = repository.insert(base).await()

    int rows = repository.deleteById(inserted.getId()).await()
    assertEquals(1, rows)

    Optional<ViewPointDO> afterDelete = repository.findById(inserted.getId()).await()
    assertTrue(afterDelete.isEmpty())

    // also check non-existent id returns empty
    Optional<ViewPointDO> none = repository.findById(-1L).await()
    assertTrue(none.isEmpty())
  }

  @Test
  void testFindByItemId() {
    UUID testItemId = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd")

    // Insert two viewPoints for the same item
    ViewPointDO viewPoint1 = new ViewPointDO(
      "FirstViewPoint", // name
      UUID.fromString("66666666-6666-6666-6666-666666666666"), // schema
      "v1", // schemaVersion
      "FirstSchema", // schemaName
      1L, // outcomeId
      testItemId // itemId
    )
    ViewPointDO viewPoint2 = new ViewPointDO(
      "SecondViewPoint", // name
      UUID.fromString("77777777-7777-7777-7777-777777777777"), // schema
      "v1", // schemaVersion
      "SecondSchema", // schemaName
      1L, // outcomeId
      testItemId // itemId
    )

    ViewPointDO inserted1 = repository.insert(viewPoint1).await()
    ViewPointDO inserted2 = repository.insert(viewPoint2).await()

    List<ViewPointDO> foundViewPoints = repository.findByItemId(testItemId).await()
    assertEquals(5, foundViewPoints.size())

    // Verify both viewPoints are present (order may vary)
    assertTrue(foundViewPoints.stream().anyMatch(vp -> vp.getId().equals(inserted1.getId())))
    assertTrue(foundViewPoints.stream().anyMatch(vp -> vp.getId().equals(inserted2.getId())))
  }

  @Test
  void testDeleteByItemId() {
    UUID testItemId = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d")

    // Insert two viewPoints for the same item
    ViewPointDO viewPoint1 = new ViewPointDO(
      "ToDeleteViewPoint1", // name
      UUID.fromString("88888888-8888-8888-8888-888888888888"), // schema
      "v1", // schemaVersion
      "ToDeleteSchema1", // schemaName
      1L, // outcomeId
      testItemId // itemId
    )
    ViewPointDO viewPoint2 = new ViewPointDO(
      "ToDeleteViewPoint2", // name
      UUID.fromString("99999999-9999-9999-9999-999999999999"), // schema
      "v1", // schemaVersion
      "ToDeleteSchema2", // schemaName
      1L, // outcomeId
      testItemId // itemId
    )

    ViewPointDO inserted1 = repository.insert(viewPoint1).await()
    ViewPointDO inserted2 = repository.insert(viewPoint2).await()

    // Verify both viewPoints exist
    List<ViewPointDO> beforeDelete = repository.findByItemId(testItemId).await()
    assertEquals(2, beforeDelete.size())

    // Delete by item ID
    int deletedRows = repository.deleteByItemId(testItemId).await()
    assertEquals(2, deletedRows)

    // Verify viewPoints are gone
    List<ViewPointDO> afterDelete = repository.findByItemId(testItemId).await()
    assertTrue(afterDelete.isEmpty())

    // Verify individual lookups also return empty
    Optional<ViewPointDO> viewPoint1AfterDelete = repository.findById(inserted1.getId()).await()
    Optional<ViewPointDO> viewPoint2AfterDelete = repository.findById(inserted2.getId()).await()
    assertTrue(viewPoint1AfterDelete.isEmpty())
    assertTrue(viewPoint2AfterDelete.isEmpty())
  }

  @Test
  void testFindByItemIdWithNonExistentItem() {
    UUID nonExistentItemId = UUID.fromString("00000000-0000-0000-0000-000000000000")
    List<ViewPointDO> viewPoints = repository.findByItemId(nonExistentItemId).await()
    assertTrue(viewPoints.isEmpty())
  }

  @Test
  void testDeleteByItemIdWithNonExistentItem() {
    UUID nonExistentItemId = UUID.fromString("00000000-0000-0000-0000-000000000001")
    int deletedRows = repository.deleteByItemId(nonExistentItemId).await()
    assertEquals(0, deletedRows)
  }
}

package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestItemIds.*
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@CompileStatic
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ItemPropertyRepositoryIT extends AbstractRepositoryIT {

  private ItemPropertyRepository repository

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new ItemPropertyRepositoryImpl(pool)
  }

  @Test
  void testFindAll() {
    List<ItemPropertyDO> found = repository.findAll().await()
    assertTrue(found.size() >= 36, "There should be at least 36 ItemProperties loaded from CSV")
  }

  @Test
  void testInsertAndFindById() {
    ItemPropertyDO toInsert = new ItemPropertyDO("Population", "1750000", true, BUDAPEST.uuid)

    ItemPropertyDO inserted = repository.insert(toInsert).await()
    assertNotNull(inserted.getId())
    assertEquals(toInsert.getName(), inserted.getName())
    assertEquals(toInsert.getValue(), inserted.getValue())
    assertEquals(toInsert.getIsMutable(), inserted.getIsMutable())
    assertEquals(toInsert.getItemId(), inserted.getItemId())

    Optional<ItemPropertyDO> fetched = repository.findById(inserted.getId()).await()
    assertTrue(fetched.isPresent())
    assertEquals(inserted, fetched.get())
  }

  @Test
  void testInsertMany() {
    List<ItemPropertyDO> props = [
      new ItemPropertyDO("Prop1", "Val1", false, PARIS.uuid),
      new ItemPropertyDO("Prop2", "Val2", true, PARIS.uuid)
    ]

    List<ItemPropertyDO> before = repository.findByItemId(PARIS.uuid).await()
    List<ItemPropertyDO> inserted = repository.insertMany(props).await()
    log.info('{}', inserted)
    assertEquals(2, inserted.size())

    inserted.eachWithIndex { ItemPropertyDO item, int i ->
      assertNotNull(item.getId())
      assertEquals(props[i].name, item.name)
      assertEquals(props[i].value, item.value)
      assertEquals(props[i].itemId, item.itemId)
    }

    List<ItemPropertyDO> after = repository.findByItemId(PARIS.uuid).await()
    assertEquals(before.size() + 2, after.size())
    assertTrue(after.any { it.name == "Prop1" })
    assertTrue(after.any { it.name == "Prop2" })
  }

  @Test
  void testFindByItemId() {
    List<ItemPropertyDO> fetchedByItemId = repository.findByItemId(BUDAPEST.uuid).await()
    assertEquals(4, fetchedByItemId.size())
  }

  @Test
  void testFindNoneExistent() {
    Optional<ItemPropertyDO> none = repository.findById(1000L).await()
    assertTrue(none.isEmpty())
  }

  @Test
  void testUpdate() {
    // Insert first to obtain a known id
    ItemPropertyDO toInsert = new ItemPropertyDO("Nickname", "Buda", false, BUDAPEST.uuid)
    ItemPropertyDO inserted = repository.insert(toInsert).await()

    inserted.setValue("Pest")
    inserted.setIsMutable(true)

    Optional<ItemPropertyDO> updatedOpt = repository.update(inserted).await()
    assertTrue(updatedOpt.isPresent())
    ItemPropertyDO updated = updatedOpt.get()

    assertEquals(inserted.getId(), updated.getId())
    assertEquals("Pest", updated.getValue())
    assertTrue(updated.getIsMutable())
    assertEquals(inserted, updated)
  }

  @Test
  void testDeleteById() {
    // Insert one to delete
    ItemPropertyDO toInsert = new ItemPropertyDO("Temp", "X", false, BUDAPEST.uuid)
    ItemPropertyDO inserted = repository.insert(toInsert).await()

    int rows = repository.deleteById(inserted.getId()).await()
    assertEquals(1, rows)

    Optional<ItemPropertyDO> afterDelete = repository.findById(inserted.getId()).await()
    assertTrue(afterDelete.isEmpty())
  }

  @Test
  void testDeleteByItemId() {
    int rowsDeleted = repository.deleteByItemId(PARIS.uuid).await()
    assertEquals(4, rowsDeleted)

    List<ItemPropertyDO> afterDelete = repository.findByItemId(PARIS.uuid).await()
    assertEquals(0, afterDelete.size())
  }

  @Test
  void testFindItemIdsByItemProperty() {
    ItemPropertyDO search = new ItemPropertyDO("Type", "City", false, null)
    List<UUID> itemIds = repository.findItemIdsByItemProperty(search).await()

    assertNotNull(itemIds)
    assertTrue(itemIds.contains(BUDAPEST.uuid))
    assertTrue(itemIds.contains(DELHI.uuid))
  }
}

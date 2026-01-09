package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO
import eu.describeit.cristalise.kernel.persistency.repository.AbstractRepositoryIT
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestItemIds.*
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
@CompileStatic
class ItemStorageIT extends AbstractRepositoryIT {

  private ItemStorage storage

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    storage = new ItemStorage(pool)
  }

  @Test
  void testPutItemDO_Insert() {
    ItemDO toInsert = new ItemDO(UUID.randomUUID(), "Berlin", "City", "v1", null)
    ItemDO inserted = storage.putItemDO(toInsert).await()

    assertNotNull(inserted)
    assertEquals(toInsert.name, inserted.name)
    assertNotNull(inserted.id)
  }

  @Test
  void testPutItemDO_Update() {
    ItemDO original = storage.getItemDO(BUDAPEST.uuid).await()
    original.setName("Budapest Updated")

    ItemDO updated = storage.putItemDO(original).await()

    assertEquals(original.name, updated.name)
    assertEquals(BUDAPEST.uuid, updated.id)

    ItemDO reFetched = storage.getItemDO(BUDAPEST.uuid).await()
    assertEquals(original.name, reFetched.name)
  }

  @Test
  void testPutItemDO_UpsertWithId() {
    UUID newId = UUID.randomUUID()
    ItemDO toUpsert = new ItemDO(newId, "New City", "City", "v1", null)

    // First call should insert
    ItemDO upserted1 = storage.putItemDO(toUpsert).await()
    assertEquals(newId, upserted1.id)
    assertEquals(toUpsert.name, upserted1.name)

    // Second call with same ID should update
    toUpsert.setName("New City Updated")
    ItemDO upserted2 = storage.putItemDO(toUpsert).await()
    assertEquals(newId, upserted2.id)
    assertEquals(toUpsert.name, upserted2.name)
  }
}

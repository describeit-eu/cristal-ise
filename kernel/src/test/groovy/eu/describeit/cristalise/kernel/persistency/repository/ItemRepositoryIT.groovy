package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO
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
class ItemRepositoryIT extends AbstractRepositoryIT {

  private ItemRepository repository

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new ItemRepositoryImpl(pool)
  }

  @Test
  void testFindById() {
    def nameBudapest = "Budapest"
    def typeBudapest = "Capital"
    def versionBudapest = "v1"

    // use findByUuid
    Optional<ItemDO> foundByUuid = repository.findById(BUDAPEST.uuid).await()

    assertTrue(foundByUuid.isPresent())
    def itemByUuid = foundByUuid.get()

    assertEquals(BUDAPEST.uuid, itemByUuid.getId())
    assertEquals(nameBudapest, itemByUuid.getName())
    assertEquals(typeBudapest, itemByUuid.getType())
    assertEquals(versionBudapest, itemByUuid.getVersion())
  }

  @Test
  void testFindNoneExistent() {
    Optional<ItemDO> noneExistent = repository.findById(NON_EXISTENT.uuid).await()
    assertTrue(noneExistent.isEmpty())
  }

  @Test
  void testFindAll() {
    List<ItemDO> foundItems = repository.findAll().await()
    assertTrue(foundItems.size() >= 9, "There should be at least 9 cities in the database")
  }

  @Test
  void testInsert() {
    ItemDO toInsert = new ItemDO(UUID.randomUUID(), "Tokyo", "megaCity", "v1.1", null)

    ItemDO cityTokyo = repository.insert(toInsert).await()

    assertNotNull(cityTokyo)
    assertEquals(toInsert, cityTokyo)
  }

  @Test
  void testUpdate() {
    ItemDO cityDelhi = repository.findById(DELHI.uuid).await().get()
    cityDelhi.setName("Mumbai")

    ItemDO cityMumbai = repository.update(cityDelhi).await().get()

    assertEquals("Mumbai", cityMumbai.getName())
    assertEquals(cityDelhi, cityMumbai)
  }

  @Test
  void testDeleteById() {
    // choose an item without events to avoid FK violations
    def rowsById = repository.deleteById(LONDON.uuid).await()
    assertEquals(1, rowsById)

    Optional<ItemDO> afterDelete = repository.findById(LONDON.uuid).await()
    assertTrue(afterDelete.isEmpty())
  }

  @Test
  void testDeleteNoneExistent() {
    def rowsById = repository.deleteById(NON_EXISTENT.uuid).await()
    assertEquals(0, rowsById)
  }
}

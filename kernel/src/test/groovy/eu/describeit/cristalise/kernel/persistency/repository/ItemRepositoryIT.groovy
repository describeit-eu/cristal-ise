package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static org.junit.jupiter.api.Assertions.*

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
@CompileStatic
class ItemRepositoryIT extends AbstractRepositoryIT {

  private ItemRepository repository

  final UUID idZero = UUID.fromString("00000000-0000-0000-0000-000000000000")

  // to be found
  final UUID idBudapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd")

  // to be updated
  final UUID idDelhi = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d")

  // to be deleted (choose an item without events to avoid FK violations)
  final UUID idLondon = UUID.fromString("04a71ecd-7cda-439f-bf6e-6517a824f753")

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
    Optional<ItemDO> foundByUuid = repository.findById(idBudapest).await()

    assertTrue(foundByUuid.isPresent())
    def itemByUuid = foundByUuid.get()

    assertEquals(idBudapest, itemByUuid.getId())
    assertEquals(nameBudapest, itemByUuid.getName())
    assertEquals(typeBudapest, itemByUuid.getType())
    assertEquals(versionBudapest, itemByUuid.getVersion())
  }

  @Test
  void testFindNoneExistent() {
    Optional<ItemDO> noneExistent = repository.findById(idZero).await()
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
    ItemDO cityDelhi = repository.findById(idDelhi).await().get()
    cityDelhi.setName("Mumbai")

    ItemDO cityMumbai = repository.update(cityDelhi).await().get()

    assertEquals("Mumbai", cityMumbai.getName())
    assertEquals(cityDelhi, cityMumbai)
  }

  @Test
  void testDeleteById() {
    def rowsById = repository.deleteById(idLondon).await()
    assertEquals(1, rowsById)

    Optional<ItemDO> afterDelete = repository.findById(idLondon).await()
    assertTrue(afterDelete.isEmpty())
  }

  @Test
  void testDeleteNoneExistent() {
    def rowsById = repository.deleteById(idZero).await()
    assertEquals(0, rowsById)
  }
}

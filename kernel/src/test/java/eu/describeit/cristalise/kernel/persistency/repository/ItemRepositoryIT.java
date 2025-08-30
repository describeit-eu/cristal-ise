package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ItemRepositoryIT extends AbstractRepositoryIT {

  private ItemRepository repository;

  final UUID idZero = UUID.fromString("00000000-0000-0000-0000-000000000000");

  // to be found
  final UUID idBudapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");

  // to be updated
  final UUID idDelhi = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d");

  // to be deleted
  final UUID idParis = UUID.fromString("b42800c5-463f-4a9a-be7d-11c792856ced");

  @BeforeAll
  @Override
  void setUpAll()throws Exception  {
    super.setUpAll();
    repository = new ItemRepositoryImpl(pool);
  }

  @Test
  void testFindById() {
    var nameBudapest = "Budapest";
    var typeBudapest = "City";
    var versionBudapest = "v1";

    // use findByUuid
    Optional<ItemDO> foundByUuid = await(repository.findById(idBudapest));

    assertTrue(foundByUuid.isPresent());
    var itemByUuid = foundByUuid.get();

    assertEquals(idBudapest,      itemByUuid.getId());
    assertEquals(nameBudapest,    itemByUuid.getName());
    assertEquals(typeBudapest,    itemByUuid.getType());
    assertEquals(versionBudapest, itemByUuid.getVersion());
  }

  @Test
  void testFindNoneExistent() {
    Optional<ItemDO> noneExistent = await(repository.findById(idZero));
    assertTrue(noneExistent.isEmpty());
  }

  @Test
  void testFindAll() {
    List<ItemDO> foundItems = await(repository.findAll());
    assertTrue(foundItems.size() >= 9, "There should be at least 8 cities in the database");
  }

  @Test
  void testInsert() {
    ItemDO toInsert = new ItemDO(UUID.randomUUID(), "Tokyo", "megaCity", "v1.1", null);

    ItemDO cityTokyo = await(repository.insert(toInsert));

    assertNotNull(cityTokyo);
    assertEquals(toInsert, cityTokyo);
  }

  @Test
  void testUpdate() {
    ItemDO cityDelhi = await(repository.findById(idDelhi)).get();
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
  void testDeleteNoneExistent() {
    var rowsById = await(repository.deleteById(idZero));
    assertEquals(0, rowsById);
  }
}

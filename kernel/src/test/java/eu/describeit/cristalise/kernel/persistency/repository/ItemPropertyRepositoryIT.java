package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ItemPropertyRepositoryIT extends AbstractRepositoryIT {

  private ItemPropertyRepository repository;

  // Known item UUIDs from test data (item.csv)
  final UUID idBudapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");
  final UUID idParis = UUID.fromString("b42800c5-463f-4a9a-be7d-11c792856ced");

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll();
    repository = new ItemPropertyRepositoryImpl(pool);
  }

  @Test
  void testFindAll() {
    List<ItemPropertyDO> found = await(repository.findAll());
    assertTrue(found.size() >= 36, "There should be at least 36 ItemProperties loaded from CSV");
  }

  @Test
  void testInsertAndFindById() {
    ItemPropertyDO toInsert = new ItemPropertyDO("Population", "1750000", true, idBudapest);

    ItemPropertyDO inserted = await(repository.insert(toInsert));
    assertNotNull(inserted.getId());
    assertEquals(toInsert.getName(), inserted.getName());
    assertEquals(toInsert.getValue(), inserted.getValue());
    assertEquals(toInsert.getIsMutable(), inserted.getIsMutable());
    assertEquals(toInsert.getItemId(), inserted.getItemId());

    Optional<ItemPropertyDO> fetched = await(repository.findById(inserted.getId()));
    assertTrue(fetched.isPresent());
    assertEquals(inserted, fetched.get());
  }

  @Test
  void testFindByItemId() {
    List<ItemPropertyDO> fetchedByItemId = await(repository.findByItemId(idBudapest));
    assertEquals(4, fetchedByItemId.size());
  }

  @Test
  void testFindNoneExistent() {
    Optional<ItemPropertyDO> none = await(repository.findById(0L));
    assertTrue(none.isEmpty());
  }

  @Test
  void testUpdate() {
    // Insert first to obtain a known id
    ItemPropertyDO toInsert = new ItemPropertyDO("Nickname", "Buda", false, idBudapest);
    ItemPropertyDO inserted = await(repository.insert(toInsert));

    inserted.setValue("Pest");
    inserted.setIsMutable(true);

    Optional<ItemPropertyDO> updatedOpt = await(repository.update(inserted));
    assertTrue(updatedOpt.isPresent());
    ItemPropertyDO updated = updatedOpt.get();

    assertEquals(inserted.getId(), updated.getId());
    assertEquals("Pest", updated.getValue());
    assertTrue(updated.getIsMutable());
    assertEquals(inserted, updated);
  }

  @Test
  void testDeleteById() {
    // Insert one to delete
    ItemPropertyDO toInsert = new ItemPropertyDO("Temp", "X", false, idBudapest);
    ItemPropertyDO inserted = await(repository.insert(toInsert));

    int rows = await(repository.deleteById(inserted.getId()));
    assertEquals(1, rows);

    Optional<ItemPropertyDO> afterDelete = await(repository.findById(inserted.getId()));
    assertTrue(afterDelete.isEmpty());
  }

  @Test
  void testDeleteByItemId() {
    int rowsDeleted = await(repository.deleteByItemId(idParis));
    assertEquals(4, rowsDeleted);

    List<ItemPropertyDO> afterDelete = await(repository.findByItemId(idParis));
    assertEquals(0, afterDelete.size());
  }
}

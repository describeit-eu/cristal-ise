package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class DomainPathRepositoryIT extends AbstractRepositoryIT {

  private DomainPathRepository repository;

  // Known item UUIDs from test data
  final UUID idBudapest = UUID.fromString("63f5033b-f427-4c4a-9ab4-2e4ba80589dd");
  final UUID idParis    = UUID.fromString("b42800c5-463f-4a9a-be7d-11c792856ced");
  final UUID idDelhi    = UUID.fromString("bbcb31f8-7f4c-47fb-8876-864a61e48d5d");

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll();
    repository = new DomainPathRepositoryImpl(pool);
  }

  @Test
  void testFindAll() {
    List<DomainPathDO> found = await(repository.findAll());
    // There are 15 non-header, non-empty lines in 02-domainPath.csv
    assertTrue(found.size() >= 15, "There should be at least 15 DomainPath rows loaded from CSV");
  }

  @Test
  void testInsertAndFindById() {
    DomainPathDO toInsert = new DomainPathDO("Test.City.Capital.Budapest-Alt", idBudapest);

    DomainPathDO inserted = await(repository.insert(toInsert));
    assertNotNull(inserted.getId());
    assertEquals(toInsert.getPath(), inserted.getPath());
    assertEquals(toInsert.getItemId(), inserted.getItemId());

    Optional<DomainPathDO> fetched = await(repository.findById(inserted.getId()));
    assertTrue(fetched.isPresent());
    assertEquals(inserted, fetched.get());
  }

  @Test
  void testUpdate() {
    // insert one to update
    DomainPathDO dp = await(repository.insert(new DomainPathDO("Test.City.Michelin.Paris", idParis)));

    dp.setPath("Michelin.History.Paris");

    Optional<DomainPathDO> updatedOpt = await(repository.update(dp));
    assertTrue(updatedOpt.isPresent());
    DomainPathDO updated = updatedOpt.get();

    assertEquals(dp.getId(), updated.getId());
    assertEquals("Michelin.History.Paris", updated.getPath());
    assertEquals(dp.getItemId(), updated.getItemId());
    assertEquals(dp, updated);
  }

  @Test
  void testDeleteById() {
    // insert one to delete
    DomainPathDO dp = await(repository.insert(new DomainPathDO("Michelin.History.Delhi", idDelhi)));

    int rows = await(repository.deleteById(dp.getId()));
    assertEquals(1, rows);

    Optional<DomainPathDO> afterDelete = await(repository.findById(dp.getId()));
    assertTrue(afterDelete.isEmpty());
  }

  @Test
  void testFindNoneExistent() {
    Optional<DomainPathDO> none = await(repository.findById(-1L));
    assertTrue(none.isEmpty());
  }
}

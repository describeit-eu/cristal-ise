package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
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
  final List<String> childrenOfCity = Arrays.asList(new String[]{"Capital", "Budapest", "Paris", "London", "Barcelona", "Munich", "Bristol", "Bern", "Geneva", "Washington", "Delhi"});

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

  @Test
  void testGetChildren() {
    List<DomainPathDO> actualChildren = await(repository.getChildren("Test.City"));

    assertNotNull(actualChildren);
    assertEquals(childrenOfCity.size(), actualChildren.size());

    // All children must be present in the expected list.
    for (var expectedChild : childrenOfCity) {
      assertTrue(
        actualChildren.stream().anyMatch(child -> child.getPath().endsWith(expectedChild)),
        "Expected child '" + expectedChild + "' not found"
      );

      if ("Budapest".equals(expectedChild)) {
        assertEquals(
          idBudapest,
          actualChildren.stream()
            .filter(child -> child.getPath().endsWith("Budapest"))
            .findFirst()
            .map(DomainPathDO::getItemId)
            .orElse(null),
          "Budapest should have correct itemId"
        );
      }

      if ("Capital".equals(expectedChild)) {
        assertNull(
          actualChildren.stream()
            .filter(child -> child.getPath().endsWith("Capital"))
            .findFirst()
            .map(DomainPathDO::getItemId)
            .orElse(null),
          "Capital should NOT have itemId"
        );
      }
    }

    // All children must start with Test.City.
    assertTrue(actualChildren.stream().allMatch(dp -> dp.getPath().startsWith("Test.City.")));

    // None of them should have more than one extra level (i.e., no Test.City.Capital.X)
    assertTrue(actualChildren.stream().noneMatch(dp -> dp.getPath().startsWith("Test.City.Capital.")));
  }

  @Test
  void testGetTree() {
    List<DomainPathDO> treeCity = await(repository.getTree("Test.City"));
    // The tree for Test.City should include Test.City itself and all descendants. From CSV these are lines 3-19 => 17 entries
    assertEquals(17, treeCity.size(), "Test.City subtree size should be 17");
    assertTrue(treeCity.stream().anyMatch(dp -> dp.getPath().equals("Test.City")));
    assertTrue(treeCity.stream().allMatch(dp -> dp.getPath().startsWith("Test.City")));

    List<DomainPathDO> treeCapital = await(repository.getTree("Test.City.Capital"));
    // For Test.City.Capital: include itself and its 6 descendants (Budapest, Paris, London, Bern, Washington) total lines relating: Capital itself + 5 capitals? CSV shows Capitals for Budapest, Paris, London, Bern, Washington => 6 including Capital node
    assertEquals(6, treeCapital.size(), "Test.City.Capital subtree size should be 6");
    assertTrue(treeCapital.stream().allMatch(dp -> dp.getPath().startsWith("Test.City.Capital")));
  }

  @Test
  void testFindByItemId() {
    // Budapest appears twice in CSV: Test.City.Budapest and Test.City.Capital.Budapest
    List<DomainPathDO> budapestPaths = await(repository.findByItemId(idBudapest));
    assertEquals(2, budapestPaths.size());
    assertTrue(budapestPaths.stream().anyMatch(dp -> dp.getPath().equals("Test.City.Budapest")));
    assertTrue(budapestPaths.stream().anyMatch(dp -> dp.getPath().equals("Test.City.Capital.Budapest")));

    // Barcelona appears once
    java.util.UUID idBarcelona = java.util.UUID.fromString("1224b816-102a-45da-ab3f-864d991c7f5b");
    List<DomainPathDO> barcelonaPaths = await(repository.findByItemId(idBarcelona));
    assertEquals(1, barcelonaPaths.size());
    assertEquals("Test.City.Barcelona", barcelonaPaths.get(0).getPath());

    // Non-existent UUID should return an empty list
    java.util.UUID none = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
    List<DomainPathDO> nonePaths = await(repository.findByItemId(none));
    assertTrue(nonePaths.isEmpty());
  }
}

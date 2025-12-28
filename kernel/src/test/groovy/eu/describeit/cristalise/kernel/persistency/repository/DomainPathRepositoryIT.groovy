package eu.describeit.cristalise.kernel.persistency.repository

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestIds.*
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
@CompileStatic
class DomainPathRepositoryIT extends AbstractRepositoryIT {

  private DomainPathRepository repository

  // Known item UUIDs from test data
  final List<String> childrenOfCity = Arrays.asList("Capital", "Budapest", "Paris", "London", "Barcelona", "Munich", "Bristol", "Bern", "Geneva", "Washington", "Delhi")

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new DomainPathRepositoryImpl(pool)
  }

  @Test
  void testFindAll() {
    def found = repository.findAll().await()
    // There are 15 non-header, non-empty lines in 02-domainPath.csv
    assertTrue(found.size() >= 15, "There should be at least 15 DomainPath rows loaded from CSV")
  }

  @Test
  void testInsertAndFindById() {
    def toInsert = new DomainPathDO("Test.City.Capital.Budapest-Alt", BUDAPEST.uuid)

    def inserted = repository.insert(toInsert).await()
    assertNotNull(inserted.getId())
    assertEquals(toInsert.getPath(), inserted.getPath())
    assertEquals(toInsert.getItemId(), inserted.getItemId())

    def fetched = repository.findById(inserted.getId()).await()
    assertTrue(fetched.isPresent())
    assertEquals(inserted, fetched.get())
  }

  @Test
  void testUpdate() {
    // insert one to update
    def dp = repository.insert(new DomainPathDO("Test.City.Michelin.Paris", PARIS.uuid)).await()

    dp.setPath("Michelin.History.Paris")

    def updatedOpt = repository.update(dp).await()
    assertTrue(updatedOpt.isPresent())
    def updated = updatedOpt.get()

    assertEquals(dp.getId(), updated.getId())
    assertEquals("Michelin.History.Paris", updated.getPath())
    assertEquals(dp.getItemId(), updated.getItemId())
    assertEquals(dp, updated)
  }

  @Test
  void testDeleteById() {
    // insert one to delete
    def dp = repository.insert(new DomainPathDO("Michelin.History.Delhi", DELHI.uuid)).await()

    def rows = repository.deleteById(dp.getId()).await()
    assertEquals(1, rows)

    def afterDelete = repository.findById(dp.getId()).await()
    assertTrue(afterDelete.isEmpty())
  }

  @Test
  void testFindNoneExistent() {
    def none = repository.findById(-1L).await()
    assertTrue(none.isEmpty())
  }

  @Test
  void testGetChildren() {
    def actualChildren = repository.getChildren("Test.City").await()

    assertNotNull(actualChildren)
    assertEquals(childrenOfCity.size(), actualChildren.size())

    // All children must be present in the expected list.
    for (def expectedChild : childrenOfCity) {
      assertTrue(
        actualChildren.stream().anyMatch(child -> child.getPath().endsWith(expectedChild)),
        "Expected child '" + expectedChild + "' not found"
      )

      if ("Budapest".equals(expectedChild)) {
        assertEquals(
          BUDAPEST.uuid,
          actualChildren.stream()
            .filter(child -> child.getPath().endsWith("Budapest"))
            .findFirst()
            .map(DomainPathDO::getItemId)
            .orElse(null),
          "Budapest should have correct itemId"
        )
      }

      if ("Capital".equals(expectedChild)) {
        assertNull(
          actualChildren.stream()
            .filter(child -> child.getPath().endsWith("Capital"))
            .findFirst()
            .map(DomainPathDO::getItemId)
            .orElse(null),
          "Capital should NOT have itemId"
        )
      }
    }

    // All children must start with Test.City.
    assertTrue(actualChildren.stream().allMatch(dp -> dp.getPath().startsWith("Test.City.")))

    // None of them should have more than one extra level (i.e., no Test.City.Capital.X)
    assertTrue(actualChildren.stream().noneMatch(dp -> dp.getPath().startsWith("Test.City.Capital.")))
  }

  @Test
  void testGetTree() {
    def treeCity = repository.getTree("Test.City").await()
    // The tree for Test.City should include Test.City itself and all descendants. From CSV these are lines 3-19 => 17 entries
    assertEquals(17, treeCity.size(), "Test.City subtree size should be 17")
    assertTrue(treeCity.stream().anyMatch(dp -> dp.getPath().equals("Test.City")))
    assertTrue(treeCity.stream().allMatch(dp -> dp.getPath().startsWith("Test.City")))

    def treeCapital = repository.getTree("Test.City.Capital").await()
    // For Test.City.Capital: include itself and its 6 descendants (Budapest, Paris, London, Bern, Washington) total lines relating: Capital itself + 5 capitals? CSV shows Capitals for Budapest, Paris, London, Bern, Washington => 6 including Capital node
    assertEquals(6, treeCapital.size(), "Test.City.Capital subtree size should be 6")
    assertTrue(treeCapital.stream().allMatch(dp -> dp.getPath().startsWith("Test.City.Capital")))
  }

  @Test
  void testFindByItemId() {
    // Budapest appears twice in CSV: Test.City.Budapest and Test.City.Capital.Budapest
    def budapestPaths = repository.findByItemId(BUDAPEST.uuid).await()
    assertEquals(2, budapestPaths.size())
    assertTrue(budapestPaths.stream().anyMatch(dp -> dp.getPath().equals("Test.City.Budapest")))
    assertTrue(budapestPaths.stream().anyMatch(dp -> dp.getPath().equals("Test.City.Capital.Budapest")))

    // Barcelona appears once
    def idBarcelona = BARCELONA.uuid
    def barcelonaPaths = repository.findByItemId(idBarcelona).await()
    assertEquals(1, barcelonaPaths.size())
    assertEquals("Test.City.Barcelona", barcelonaPaths.get(0).getPath())

    // Non-existent UUID should return an empty list
    def none = NON_EXISTENT_2.uuid
    def nonePaths = repository.findByItemId(none).await()
    assertTrue(nonePaths.isEmpty())
  }
}

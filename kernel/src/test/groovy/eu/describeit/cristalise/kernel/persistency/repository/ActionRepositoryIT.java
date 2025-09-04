package eu.describeit.cristalise.kernel.persistency.repository;

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static eu.describeit.cristalise.kernel.persistency.domain.ActionDO.ActionType.*;
import static eu.describeit.cristalise.kernel.persistency.utils.DatabaseTestUtils.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
class ActionRepositoryIT extends AbstractRepositoryIT {

  private ActionRepository repository;

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll();
    repository = new ActionRepositoryImpl(pool);
  }

  @Test
  void testFindAll() {
    List<ActionDO> actions = await(repository.findAll());
    // From 01-action.csv there are 10 non-header lines
    assertTrue(actions.size() >= 10, "There should be at least 10 actions loaded from CSV");
  }

  @Test
  void testInsertAndFindById() {
    ActionDO toInsert = new ActionDO(
      "TestAction",
      "/CityWf/TestAction",
      "v9",
      null,
      ELEMENTARY,
      null,
      1L // parent CityWf assumed to have id=1 as first inserted
    );

    ActionDO inserted = await(repository.insert(toInsert));
    assertNotNull(inserted.getId());
    assertEquals(toInsert.getName(), inserted.getName());
    assertEquals(toInsert.getPath(), inserted.getPath());
    assertEquals(toInsert.getVersion(), inserted.getVersion());
    assertEquals(toInsert.getProperties(), inserted.getProperties());
    assertEquals(toInsert.getType(), inserted.getType());
    assertEquals(toInsert.getLayout(), inserted.getLayout());
    assertEquals(toInsert.getParentId(), inserted.getParentId());

    Optional<ActionDO> fetched = await(repository.findById(inserted.getId()));
    assertTrue(fetched.isPresent());
    assertEquals(inserted, fetched.get());
  }

  @Test
  void testUpdate() {
    // insert a row then update it
    ActionDO base = new ActionDO(
      "TempAction",
      "/CapitalWf/TempAction",
      "v1",
      null,
      ELEMENTARY,
      null,
      6L // parent CapitalWf assumed id
    );
    ActionDO inserted = await(repository.insert(base));

    inserted.setName("TempActionUpdated");
    inserted.setVersion("v2");

    Optional<ActionDO> updatedOpt = await(repository.update(inserted));
    assertTrue(updatedOpt.isPresent());
    ActionDO updated = updatedOpt.get();

    assertEquals(inserted.getId(), updated.getId());
    assertEquals("TempActionUpdated", updated.getName());
    assertEquals("v2", updated.getVersion());
    assertEquals(inserted.getPath(), updated.getPath());
    assertEquals(inserted.getType(), updated.getType());
    assertEquals(inserted.getParentId(), updated.getParentId());
  }

  @Test
  void testDeleteByIdAndFindNoneExistent() {
    // insert one to delete
    ActionDO base = new ActionDO(
      "ToDelete",
      "/CityWf/ToDelete",
      "v1",
      null,
      ELEMENTARY,
      null,
      1L
    );
    ActionDO inserted = await(repository.insert(base));

    int rows = await(repository.deleteById(inserted.getId()));
    assertEquals(1, rows);

    Optional<ActionDO> afterDelete = await(repository.findById(inserted.getId()));
    assertTrue(afterDelete.isEmpty());

    // also check non-existent id returns empty
    Optional<ActionDO> none = await(repository.findById(-1L));
    assertTrue(none.isEmpty());
  }
}

package eu.describeit.cristalise.kernel.persistency.repository


import eu.describeit.cristalise.kernel.lifecycle.LoopingCompositeAction
import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
import eu.describeit.cristalise.kernel.lifecycle.SplittingCompositeAction
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.TestDataIdUtils.getActionId
import static eu.describeit.cristalise.kernel.persistency.domain.ActionDO.ActionType.*
import static org.junit.jupiter.api.Assertions.*

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(Lifecycle.PER_CLASS)
@CompileStatic
class ActionRepositoryIT extends AbstractRepositoryIT {

  private ActionRepository repository

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    repository = new ActionRepositoryImpl(pool)
  }

  @Test
  void testFindAll() {
    def actions = repository.findAll().await()
    // From 01-action.csv there are 10 non-header lines
    assertTrue(actions.size() >= 10, "There should be at least 10 actions loaded from CSV")
  }

  @Test
  void testInsertAndFindById() {
    def toInsert = new ActionDO(
      "TestAction",
      "/CityWf/TestAction",
      "v9",
      null,
      ELEMENTARY,
      null,
      1L // parent CityWf assumed to have id=1 as first inserted
    )

    def inserted = repository.insert(toInsert).await()
    assertNotNull(inserted.getId())
    assertEquals(toInsert.getName(), inserted.getName())
    assertEquals(toInsert.getPath(), inserted.getPath())
    assertEquals(toInsert.getVersion(), inserted.getVersion())
    assertEquals(toInsert.getProperties(), inserted.getProperties())
    assertEquals(toInsert.getType(), inserted.getType())
    assertEquals(toInsert.getLayout(), inserted.getLayout())
    assertEquals(toInsert.getParentId(), inserted.getParentId())

    def fetched = repository.findById(inserted.getId()).await()
    assertTrue(fetched.isPresent())
    assertEquals(inserted, fetched.get())
  }

  @Test
  void testFindByParentId() {
    def parentId = getActionId('CityWf')
    def actionsBefore = repository.findByParentId(parentId).await()

    // TODO: Action with parentId=1 is inserted during previous test method
    assertEquals(3, actionsBefore.size())

    def toInsert = new ActionDO(
      "ChildAction",
      "/CityWf/ChildAction",
      "v1",
      null,
      ELEMENTARY,
      null,
      parentId
    )
    def inserted = repository.insert(toInsert).await()

    def actionsAfter = repository.findByParentId(parentId).await()
    assertEquals(actionsBefore.size() + 1, actionsAfter.size())
    assertTrue(actionsAfter.any { it.id == inserted.id })
  }

  @Test
  void testUpdate() {
    // insert a row then update it
    def base = new ActionDO(
      "TempAction",
      "/CapitalWf/TempAction",
      "v1",
      null,
      ELEMENTARY,
      null,
      6L // parent CapitalWf assumed id
    )
    def inserted = repository.insert(base).await()

    log.info('base:{} inserted:{}', base, inserted)

    inserted.setName("TempActionUpdated")
    inserted.setVersion("v2")

    def updatedOpt = repository.update(inserted).await()
    assertTrue(updatedOpt.isPresent())
    def updated = updatedOpt.get()

    assertEquals(inserted.getId(), updated.getId())
    assertEquals("TempActionUpdated", updated.getName())
    assertEquals("v2", updated.getVersion())
    assertEquals(inserted.getPath(), updated.getPath())
    assertEquals(inserted.getType(), updated.getType())
    assertEquals(inserted.getParentId(), updated.getParentId())
  }

  @Test
  void testDeleteByIdAndFindNoneExistent() {
    // insert one to delete
    def base = new ActionDO(
      "ToDelete",
      "/CityWf/ToDelete",
      "v1",
      null,
      ELEMENTARY,
      null,
      1L
    )
    def inserted = repository.insert(base).await()

    def rows = repository.deleteById(inserted.getId()).await()
    assertEquals(1, rows)

    def afterDelete = repository.findById(inserted.getId()).await()
    assertTrue(afterDelete.isEmpty())

    // also check non-existent id returns empty
    def none = repository.findById(-1L).await()
    assertTrue(none.isEmpty())
  }

  @Test
  void testCompositeActionInitialise() {
    // 1. Fetch 'CapitalWf' which is the root Action with ID=6 in CSV/Liquibase test data
    def capitalWfDO = repository.findById(getActionId('CapitalWf')).await().orElseThrow()
    assertEquals("CapitalWf", capitalWfDO.name)

    // 2. Instantiate SplittingCompositeAction (as it is the type for CapitalWf in CSV)
    def compositeAction = new SplittingCompositeAction(dataObject: capitalWfDO)

    // 3. Initialise the composite action and its children recursively
    compositeAction.initialise(repository).await()

    // 4. Verify children loading (UpdateCapital, ChangeState)
    def children = compositeAction.getActions()
    assertNotNull(children)
    // From CSV: CapitalWf(6) has children UpdateCapital(7) and ChangeState(8)
    assertEquals(2, children.size(), "CapitalWf should have 2 direct children")

    def updateCapital = children.find { it.name == "UpdateCapital" }
    assertNotNull(updateCapital)
    assertEquals(ELEMENTARY, updateCapital.type)

    def changeState = children.find { it.name == "ChangeState" }
    assertNotNull(changeState)
    assertEquals(LOOP, changeState.type)

    // 5. Verify grandchildren for ChangeState(8) (Activate, DeActivate)
    // ChangeState is a CompositeAction, so its actions should also be loaded
    def grandchildren = ((LoopingCompositeAction) changeState).getActions()
    assertEquals(2, grandchildren.size(), "ChangeState should have 2 children")
    assertTrue(grandchildren.any { it.name == "Activate" })
    assertTrue(grandchildren.any { it.name == "DeActivate" })
  }
}

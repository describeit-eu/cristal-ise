package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.ActionDO
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
class RepositoryWrapperIT extends AbstractRepositoryIT {

  private RepositoryWrapper storage

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    storage = new RepositoryWrapper(pool)
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
  void testPutAction_Insert() {
    ActionDO toInsert = new ActionDO("TestAction", "test/path", "v1", "{}", ActionDO.ActionType.ELEMENTARY, "{}", null)
    ActionDO inserted = storage.putAction(toInsert).await()

    assertNotNull(inserted)
    assertEquals(toInsert.name, inserted.name)
    assertNotNull(inserted.id)
  }

  @Test
  void testPutAction_Update() {
    ActionDO toInsert = new ActionDO("TestActionUpdate", "test/path/update", "v1", "{}", ActionDO.ActionType.ELEMENTARY, "{}", null)
    ActionDO inserted = storage.putAction(toInsert).await()

    inserted.setName("UpdatedName")
    ActionDO updated = storage.putAction(inserted).await()

    assertEquals("UpdatedName", updated.name)
    assertEquals(inserted.id, updated.id)

    ActionDO reFetched = storage.getActionDO(inserted.id).await()
    assertEquals("UpdatedName", reFetched.name)
  }

  @Test
  void testPutActions() {
    ActionDO a1 = new ActionDO("Action1", "p1", "v1", "{}", ActionDO.ActionType.ELEMENTARY, "{}", null)
    ActionDO a2 = new ActionDO("Action2", "p2", "v1", "{}", ActionDO.ActionType.ELEMENTARY, "{}", null)

    List<ActionDO> inserted = storage.putActions([a1, a2]).await()

    assertEquals(2, inserted.size())
    assertNotNull(inserted[0].id)
    assertNotNull(inserted[1].id)
  }

  @Test
  void testPutActions_Mixed() {
    ActionDO a1 = new ActionDO("ActionMixed1", "pm1", "v1", "{}", ActionDO.ActionType.ELEMENTARY, "{}", null)
    ActionDO inserted1 = storage.putAction(a1).await()

    inserted1.setName("ActionMixed1Updated")
    ActionDO a2 = new ActionDO("ActionMixed2", "pm2", "v1", "{}", ActionDO.ActionType.ELEMENTARY, "{}", null)

    List<ActionDO> results = storage.putActions([inserted1, a2]).await()

    assertEquals(2, results.size())
    ActionDO r1 = results.find { it.id == inserted1.id }
    assertNotNull(r1)
    assertEquals("ActionMixed1Updated", r1.name)

    ActionDO r2 = results.find { it.name == "ActionMixed2" }
    assertNotNull(r2)
    assertNotNull(r2.id)
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

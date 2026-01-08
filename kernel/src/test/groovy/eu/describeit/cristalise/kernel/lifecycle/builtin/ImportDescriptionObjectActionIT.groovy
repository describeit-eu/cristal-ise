package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.CommonTestItemIds
import eu.describeit.cristalise.TestDataIdUtils
import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.persistency.Storage
import eu.describeit.cristalise.kernel.persistency.repository.AbstractRepositoryIT
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestItemIds.*
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class ImportDescriptionObjectActionIT extends AbstractRepositoryIT {

  private ImportDescriptionObjectAction action

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    action = new ImportDescriptionObjectAction()
  }

  @Test
  void testImportStateMachine() {
    StateMachine sm = new StateMachine("TestSM", "v1.0")
    sm.namespace = "test"
    sm.createState("Start")
    sm.createState("End")

    ItemProxy item = ItemProxy.create(pool, BUDAPEST.uuid).await()
    ItemProxy actor = null

    UUID resultId = action.request(item, actor, sm).await()

    assertNotNull(resultId)

    // Verify it was created
    Storage storage = item.getStorage()
    def createdItem = storage.getItemDO(resultId).await()
    assertEquals("TestSM", createdItem.name)
    assertEquals("StateMachine", createdItem.type)
    assertEquals("v1.0", createdItem.version)

    // Verify properties
    def props = storage.findItemPropertiesByItemId(resultId).await()
    assertEquals(4, props.size())
    assertTrue(props.any { it.name == 'Name' && it.value == 'TestSM' })
    assertTrue(props.any { it.name == 'Type' && it.value == 'StateMachine' })
//    assertTrue(props.any { it.name == 'Module' && it.value == 'kernel.statemachine' })
    assertTrue(props.any { it.name == 'Version' && it.value == 'v1.0' })

    // Verify DomainPath
    def dp = storage.findDomainPathByPath("kernel.description.statemachine.TestSM").await()
    assertTrue(dp.isPresent())
    assertEquals(resultId, dp.get().itemId)

    // Verify Event
    def events = storage.findAllEvents().await()
    def event = events.find { it.itemId == resultId }
    assertNotNull(event)
    assertEquals("import", event.actionPath)
    assertEquals("v1.0", event.itemVersion)
    assertEquals("v1.0", event.stateMachineVersion)

    // Verify Outcome
    def outcomes = storage.findOutcomesByItemId(resultId).await()
    assertEquals(1, outcomes.size())
    def outcome = outcomes[0]
    assertEquals(event.id, outcome.eventId)
    assertEquals("v1.0", outcome.schemaVersion)
    assertNotNull(outcome.data)
    assertEquals("TestSM", outcome.data.getString("name"))

    // Verify ViewPoints
    def vps = storage.findAllViewPoints().await()
    def itemVps = vps.findAll { it.itemId == resultId }
    assertEquals(2, itemVps.size())
    assertTrue(itemVps.any { it.name == 'v0' && it.outcomeId == outcome.id })
    assertTrue(itemVps.any { it.name == 'last' && it.outcomeId == outcome.id })
  }
}

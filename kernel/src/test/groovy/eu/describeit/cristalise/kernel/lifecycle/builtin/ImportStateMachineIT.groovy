package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO
import eu.describeit.cristalise.kernel.persistency.repository.AbstractRepositoryIT
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Testcontainers

import static eu.describeit.cristalise.CommonTestItemIds.*
import static eu.describeit.cristalise.kernel.BuiltInResources.STATE_MACHINE_RESOURCE
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class ImportStateMachineIT extends AbstractRepositoryIT {

  private static final String parentPath = "kernel.description.$STATE_MACHINE_RESOURCE.typeCode"

  private ImportDescriptionObjectAction importAction
  private ItemProxy anItem

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    importAction = component.importDescriptionObjectAction()

    // TODO use the KernelRoot Item instead
    anItem = ItemProxy.create(pool, BUDAPEST.uuid).await()
  }

  @Test
  void testImportStateMachine() {
    StateMachine sm = new StateMachine('test', "TestSM", "v1.0")
    sm.createState("Start")
    sm.createState("End")
    sm.createTransition('Done')

    importAction.request(anItem, null, sm).await()

    // Verify it was created
    ItemProxy createdItem = ItemProxy.create(pool, new DomainPathDO(path: 'kernel.description.statemachine.TestSM')).await()
    assertEquals("TestSM", createdItem.name)
    assertEquals("StateMachine", createdItem.type)
    assertEquals("v1.0", createdItem.version)

    // Verify properties
    List<ItemPropertyDO> props = createdItem.getAllItemProperties().await()
    assertEquals(4, props.size())
    assertTrue(props.any { it.name == 'Name' && it.value == 'TestSM' })
    assertTrue(props.any { it.name == 'Type' && it.value == 'StateMachine' })
//    assertTrue(props.any { it.name == 'Module' && it.value == 'kernel.statemachine' })
    assertTrue(props.any { it.name == 'Version' && it.value == 'v1.0' })

    // Verify Event
    def events = createdItem.getAllEvents().await()
    assert events
    assertEquals("builtin/ImportDescriptionObject", events[0].actionPath)
    assertEquals("v1.0", events[0].itemVersion)
    assertEquals("v1.0", events[0].stateMachineVersion)

    // Verify Outcome
    def outcomes = createdItem.getAllOutcomes().await()
    assertEquals(1, outcomes.size())
    assertEquals(events[0].id, outcomes[0].eventId)
    assertEquals("v1.0", outcomes[0].schemaVersion)
    assertNotNull(outcomes[0].data)
    assertEquals("TestSM", outcomes[0].data.getString("name"))

    // Verify ViewPoints
    def vps = createdItem.getAllViewPoints().await()
    def itemVps = vps.findAll { it.itemId == createdItem.itemId }
    assertEquals(2, itemVps.size())
    assertTrue(itemVps.any { it.name == 'v0'   && it.outcomeId == outcomes[0].id })
    assertTrue(itemVps.any { it.name == 'last' && it.outcomeId == outcomes[0].id })
  }

  @Test
  void importDslStringThrowsException() {
    String smDsl = '''
StateMachine(name: 'TestSimple', version: 'v0') {
  transition('Done', [origin: 'Waiting', target: 'Finished'])

  initialState('Waiting')
  finishingState('Finished')
}
'''
    assertThrows(IllegalArgumentException.class) {
      importAction.request(anItem, null, smDsl).await()
    }
  }

  @Test
  void testImporting_StateMachineScript() {
    def result = importAction.request(anItem, null, 'StateMachineScript.groovy').await()

    assert result.getJsonArray('uuids').size() == 2
    assert result.getJsonArray('names').size() == 2
    assert result.getJsonArray('types').size() == 2

    assertEquals(['Default','Simple'], result.getJsonArray('names').toList())

    ItemProxy.create(pool, new DomainPathDO(path: 'kernel.description.statemachine.Default')).await()
    ItemProxy.create(pool, new DomainPathDO(path: 'kernel.description.statemachine.Simple')).await()
  }
}

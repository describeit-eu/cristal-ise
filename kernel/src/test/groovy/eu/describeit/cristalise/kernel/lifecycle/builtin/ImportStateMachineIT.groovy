package eu.describeit.cristalise.kernel.lifecycle.builtin


import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
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
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertTrue

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@CompileStatic
class ImportStateMachineIT extends AbstractRepositoryIT {

  private ImportDescriptionObjectAction importAction
  private ItemProxy anItem

  @BeforeAll
  @Override
  void setUpAll() throws Exception {
    super.setUpAll()
    importAction = new ImportDescriptionObjectAction()

    // TODO use the KernelRoot Item instead
    anItem = ItemProxy.create(pool, BUDAPEST.uuid).await()
  }

  @Test
  void testImportStateMachine() {
    StateMachine sm = new StateMachine('test', "TestSM", "v1.0")
    sm.namespace = "test"
    sm.createState("Start")
    sm.createState("End")

    UUID resultId = importAction.request(anItem, null, sm).await()

    assertNotNull(resultId)

    // Verify it was created
    ItemProxy createdItem = ItemProxy.create(pool, resultId).await()
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

    // Verify DomainPath
    def dpList = createdItem.getAllDomainPaths().await()
    def dp = dpList.find {it.path == 'kernel.description.statemachine.TestSM'}
    assertNotNull(dp)
    assertEquals(resultId, dp.itemId)

    // Verify Event
    def events = createdItem.getAllEvents().await()
    assert events
    assertEquals("import", events[0].actionPath)
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
    def itemVps = vps.findAll { it.itemId == resultId }
    assertEquals(2, itemVps.size())
    assertTrue(itemVps.any { it.name == 'v0'   && it.outcomeId == outcomes[0].id })
    assertTrue(itemVps.any { it.name == 'last' && it.outcomeId == outcomes[0].id })
  }

  @Test
  void testImportStateMachineDsl() {
    String smDsl = '''
StateMachine(name: 'TestSimple', version: 'v0') {
  transition('Activate', [origin: 'Waiting', target: 'Active'])
  transition('Done', [origin: 'Active', target: 'Finished']) {
    schema(name: '${SchemaType}', version: '${SchemaVersion}')
    script(name: '${ScriptName}', version: '${ScriptVersion}')
    query(name: '${QueryName}', version: '${QueryVersion}')
  }

  initialState('Waiting')
  finishingState('Finished')
}
'''
    UUID resultId = importAction.request(anItem, null, smDsl).await()

    assert resultId
    ItemProxy createdItem = ItemProxy.create(pool, resultId).await()

    assertEquals("TestSimple", createdItem.name)
    assertEquals("StateMachine", createdItem.type)
    assertEquals("v0", createdItem.version)
  }
}

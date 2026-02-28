package eu.describeit.cristalise.kernel.lifecycle.builtin

import eu.describeit.cristalise.kernel.DescriptionObject
import eu.describeit.cristalise.kernel.item.ItemProxy
import eu.describeit.cristalise.kernel.migration.ImportScript
import eu.describeit.cristalise.kernel.persistency.RepositoryWrapper
import eu.describeit.cristalise.kernel.persistency.domain.*
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import spock.lang.Specification

class ImportStateMachineSpecs extends Specification {

  ImportScript.Factory importScriptFactory = Mock()
  ImportDescriptionObjectAction importAction = new ImportDescriptionObjectAction(importScriptFactory)
  ItemProxy item = Mock(ItemProxy, constructorArgs: [null, UUID.randomUUID()])
  ItemProxy actor = Mock(ItemProxy, constructorArgs: [null, UUID.randomUUID()])
  RepositoryWrapper storage = Mock(RepositoryWrapper, constructorArgs: [null])

  def setup() {
    item.getStorage() >> storage
    actor.getName() >> "test-actor"
  }

  def "Import StateMachine created manually"() {
    given:
    StateMachine sm = new StateMachine('test', "TestSM", "v1.0")
    sm.createState("Start")
    sm.createState("End")
    sm.createTransition('Done')
    sm.initialState = sm.getState('Start')
    sm.finalStateIds = [sm.getState('End').id]

    and:
    storage.getDomainPathByPath(_ as String) >> Future.failedFuture("Not found")
    storage.putDomainPath(_ as DomainPathDO) >> { DomainPathDO dp -> Future.succeededFuture(dp) }
    storage.putItemDO(_ as ItemDO) >> { ItemDO ido -> Future.succeededFuture(ido) }
    storage.putItemProperties(_ as List<ItemPropertyDO>) >> { List<ItemPropertyDO> props -> Future.succeededFuture(props) }
    storage.putEvent(_ as EventDO) >> { EventDO e ->
      e.id = 1L
      Future.succeededFuture(e)
    }
    storage.putOutcome(_ as OutcomeDO) >> { OutcomeDO o ->
      o.id = 2L
      Future.succeededFuture(o)
    }
    storage.putViewPoints(_ as List<ViewPointDO>) >> { List<ViewPointDO> vps -> Future.succeededFuture(vps) }

    when:
    def result = importAction.request(item, actor, sm).await()

    then:

    result.getString('status') == 'SUCCESS'
    result.getString('action') == 'ImportDescriptionObject'
    result.getString('status') == 'SUCCESS'
    result.getString('name') == 'TestSM'
    result.getString('type') == 'STATE_MACHINE_RESOURCE'
    result.getString('uuid')

    and:
    1 * storage.putItemDO({ it.name == 'TestSM' && it.type == 'StateMachine' && it.version == 'v1.0' }) >> Future.succeededFuture()
    1 * storage.putItemProperties({ it.any { it.name == 'Name' && it.value == 'TestSM' } }) >> Future.succeededFuture()
    1 * storage.putEvent({ it.itemVersion == 'v1.0' && it.userLogin == 'test-actor' }) >> Future.succeededFuture(new EventDO(id: 123L))
    1 * storage.putOutcome({ it.data.getString('name') == 'TestSM' }) >> Future.succeededFuture(new OutcomeDO(id: 456L))
    1 * storage.putViewPoints(_) >> Future.succeededFuture()
  }

  def "Importing DSL string throws exception"() {
    given:
    String smDsl = '''
StateMachine(name: 'TestSimple', version: 'v0') {
  transition('Done', [origin: 'Waiting', target: 'Finished'])

  initialState('Waiting')
  finishingState('Finished')
}
'''
    when:
    importAction.request(item, actor, smDsl).await()

    then:
    thrown(IllegalArgumentException)
  }

  def "Import StateMachineScript dot groovy"() {
    given:
    def scriptName = 'StateMachineScript.groovy'

    ImportScript script = Mock()
    importScriptFactory.create(scriptName, _) >> script

    StateMachine sm1 = new StateMachine('test', 'Default', 'v1.0')
    StateMachine sm2 = new StateMachine('test', 'Simple', 'v1.0')

    script.run() >> [sm1, sm2]

    and:
    storage.getDomainPathByPath(_ as String) >> Future.failedFuture("Not found")
    storage.putDomainPath(_ as DomainPathDO) >> { DomainPathDO dp -> Future.succeededFuture(dp) }
    storage.putItemDO(_ as ItemDO) >> Future.succeededFuture()
    storage.putItemProperties(_ as List<ItemPropertyDO>) >> Future.succeededFuture()
    storage.putEvent(_ as EventDO) >> Future.succeededFuture(new EventDO(id: 1L))
    storage.putOutcome(_ as OutcomeDO) >> Future.succeededFuture(new OutcomeDO(id: 2L))
    storage.putViewPoints(_ as List<ViewPointDO>) >> Future.succeededFuture()

    when:
    def result = importAction.request(item, actor, scriptName).await()

    then:
    result.getJsonArray('importedObjects').size() == 2
    result.getJsonArray('importedObjects').collect() { it.getString('name') } == ['Default', 'Simple']
  }
}

package eu.describeit.cristalise.kernel.persistency

import eu.describeit.cristalise.kernel.persistency.domain.ItemPropertyDO
import eu.describeit.cristalise.kernel.persistency.domain.OutcomeDO
import eu.describeit.cristalise.kernel.persistency.domain.ViewPointDO
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import spock.lang.Specification

import static eu.describeit.cristalise.kernel.BuiltInResources.STATE_MACHINE_RESOURCE

class DescriptionObjectCacheSpecs extends Specification {

    def "should cache StateMachine"() {
        given:
        def repository = Mock(RepositoryWrapper, constructorArgs: [null])
        def cache = new DescriptionObjectCache(repository)
        def itemId = UUID.randomUUID()
        def version = "v1"
        def schemaName = STATE_MACHINE_RESOURCE.schemaName

        def typeProp = new ItemPropertyDO('Type', STATE_MACHINE_RESOURCE.schemaName, true, itemId)
        def vp = new ViewPointDO(itemId: itemId, schemaName: schemaName, name: version, outcomeId: 1L)
        def outcomeData = new JsonObject().put("name", "TestSM").put("version", version)
        def outcome = new OutcomeDO(id: 1L, data: outcomeData)

        when: "first call"
        def result1 = cache.getStateMachine(itemId, version).await()

        then: "repository is called"
        1 * repository.getItemProperty(itemId, 'Type') >> Future.succeededFuture(typeProp)
        1 * repository.getViewPointDO(itemId, schemaName, version) >> Future.succeededFuture(vp)
        1 * repository.getOutcomeDO(vp) >> Future.succeededFuture(outcome)
        result1.name == "TestSM"

        when: "second call"
        def result2 = cache.getStateMachine(itemId, version).await()

        then: "repository is NOT called again"
        0 * repository.getViewPointDO(_, _, _)
        0 * repository.getOutcomeDO(_)
        result2 == result1
    }

    def "should handle multiple versions"() {
        given:
        def repository = Mock(RepositoryWrapper, constructorArgs: [null])
        def cache = new DescriptionObjectCache(repository)
        def itemId = UUID.randomUUID()
        def schemaName = STATE_MACHINE_RESOURCE.schemaName

        def typeProp = new ItemPropertyDO('Type', STATE_MACHINE_RESOURCE.schemaName, true, itemId)
        def vp1 = new ViewPointDO(itemId: itemId, schemaName: schemaName, name: "v1", outcomeId: 1L)
        def outcome1 = new OutcomeDO(id: 1L, data: new JsonObject().put("name", "TestSM").put("version", "v1"))

        def vp2 = new ViewPointDO(itemId: itemId, schemaName: schemaName, name: "v2", outcomeId: 2L)
        def outcome2 = new OutcomeDO(id: 2L, data: new JsonObject().put("name", "TestSM").put("version", "v2"))

        when:
        def result1 = cache.getStateMachine(itemId, "v1").await()
        def result2 = cache.getStateMachine(itemId, "v2").await()

        then:
        1 * repository.getItemProperty(itemId, 'Type') >> Future.succeededFuture(typeProp)
        1 * repository.getViewPointDO(itemId, schemaName, "v1") >> Future.succeededFuture(vp1)
        1 * repository.getOutcomeDO(vp1) >> Future.succeededFuture(outcome1)
        1 * repository.getItemProperty(itemId, 'Type') >> Future.succeededFuture(typeProp)
        1 * repository.getViewPointDO(itemId, schemaName, "v2") >> Future.succeededFuture(vp2)
        1 * repository.getOutcomeDO(vp2) >> Future.succeededFuture(outcome2)

        result1.version == "v1"
        result2.version == "v2"
    }
}

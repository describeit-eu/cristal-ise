package eu.describeit.cristalise

import eu.describeit.cristalise.kernel.persistency.domain.*
import spock.lang.Specification

import static eu.describeit.cristalise.CommonTestItemIds.*

class TestDataIdUtilsSpec extends Specification {

    def "should read Long ID for ActionDO from CSV"() {
        expect:
        TestDataIdUtils.getId(ActionDO, "CapitalWf") == 6L
        TestDataIdUtils.getLongId(ActionDO, "CapitalWf") == 6L
        TestDataIdUtils.getId(ActionDO, "UpdateCity") == 2L
        TestDataIdUtils.getId(ActionDO, "CityWf") == 1L
    }

    def "should read UUID ID for ItemDO from CSV"() {
        expect:
        TestDataIdUtils.getId(ItemDO, "Budapest")   == BUDAPEST.uuid
        TestDataIdUtils.getUUID(ItemDO, "Budapest") == BUDAPEST.uuid
        TestDataIdUtils.getId(ItemDO, "Barcelona")  == BARCELONA.uuid
    }

    def "should return null if name not found"() {
        expect:
        TestDataIdUtils.getId(ActionDO, "NonExistent") == null
    }

    def "should throw exception if class not mapped"() {
        when:
        TestDataIdUtils.getId(String.class, "Something")

        then:
        thrown(IllegalArgumentException)
    }

    def "should handle DomainPathDO by path"() {
        expect:
        TestDataIdUtils.getId(DomainPathDO, "Test.City.Budapest") == 4L
    }

    def "should handle ItemPropertyDO by name"() {
        expect:
        TestDataIdUtils.getId(ItemPropertyDO, "Name") == 1L
        TestDataIdUtils.getId(ItemPropertyDO, "Type") == 2L
    }
}

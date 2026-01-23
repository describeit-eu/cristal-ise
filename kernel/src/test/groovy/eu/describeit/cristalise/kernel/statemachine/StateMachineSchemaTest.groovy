package eu.describeit.cristalise.kernel.statemachine

import eu.describeit.cristalise.kernel.dsl.statemachine.StateMachineBuilder
import io.vertx.core.json.JsonObject
import io.vertx.json.schema.JsonSchema
import io.vertx.json.schema.JsonSchemaOptions
import io.vertx.json.schema.Validator
import spock.lang.Specification

import static io.vertx.json.schema.Draft.DRAFT202012


class StateMachineSchemaTest extends Specification {

    def "should validate StateMachine JSON against schema"() {
        given:
        def schemaStream = getClass().getResourceAsStream("/schema/StateMachineSchema.json")
        assert schemaStream != null
        def schemaJson = new JsonObject(schemaStream.text)
        def smSchema = JsonSchema.of(schemaJson)

        def options = new JsonSchemaOptions()
            .setDraft(DRAFT7)
            .setBaseUri("https://describe-it.eu/schemas/statemachine")
        def validator = Validator.create(smSchema, options)

        def sm = StateMachineBuilder.create('testing', 'Default', 'v0') {
            transition('Done', [origin: 'Waiting', target: 'Finished']) {
                schema(name: 'MySchema', version: '1')
                script(name: 'MyScript', version: '1')
                query(name: 'MyQuery', version: '1')
            }
            initialState('Waiting')
            finishingState('Finished')
        }
        def smJson = JsonObject.mapFrom(sm)

        when:
        def result = validator.validate(smJson)

        then:
        result.valid
    }

    def "should NOT validate invalid StateMachine JSON"() {
        given:
        def schemaStream = getClass().getResourceAsStream("/schema/StateMachineSchema.json")
        assert schemaStream != null
        def schemaJson = new JsonObject(schemaStream.text)
        def smSchema = JsonSchema.of(schemaJson)

        def options = new JsonSchemaOptions()
            .setDraft(DRAFT202012)
            .setBaseUri("https://describe-it.eu/schemas/statemachine")
        def validator = Validator.create(smSchema, options)

        def invalidSmJson = new JsonObject([
            name: "InvalidSM",
            version: "v1",
            states: [
                [id: "NOT_AN_INT", name: "State1"]
            ],
            transitions: []
        ])

        when:
        def result = validator.validate(invalidSmJson)

        then:
        !result.valid
    }
}

/**
 * This file is part of the CRISTAL-iSE kernel.
 * Copyright (c) 2001-2015 The CRISTAL Consortium. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * http://www.fsf.org/licensing/licenses/lgpl.html
 */
package eu.describeit.cristalise.kernel.dsl.statemachine

import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.util.logging.Slf4j
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Assertions
import spock.lang.Ignore
import spock.lang.Specification

/**
 *
 */
@Slf4j
class StateMachineBuilderSpecs extends Specification {

  def 'SM containing a single State is valid'() {
    when:
    def builder = StateMachineBuilder.build("testing", "dummySM", 'v0') {
      state("Idle")
      initialState("Idle")
    }
    def json = JsonObject.mapFrom(builder.sm)

    then:
    builder.sm && builder.sm.validate()
    builder.sm.getState("Idle")
    json.mapTo(StateMachine.class) == builder.sm
  }

  def 'SM containing a single State and Transition is valid'() {
    when:
    def builder = StateMachineBuilder.build("testing", "dummySM", 'v0') {
      transition("Fire", [origin: 'Idle', target: 'Idle'])
      initialState("Idle")
    }
    def json = JsonObject.mapFrom(builder.sm)

    then:
    builder.sm
    builder.sm.validate()
    builder.sm.states.find { it.name == "Idle" }
    builder.sm.getTransition("Fire").originStateId == 0
    builder.sm.getTransition("Fire").targetStateId == 0
  }

  def 'SM containing a single Transition is NOT valid'() {
    when:
    def builder = StateMachineBuilder.build("testing", "dummySM", 'v0') {
      transition("Useless")
    }

    then:
    builder.sm && !builder.sm.validate()
  }

  @Ignore("unimplemented")
  def 'Builder can edit existing StateMachine'() {
    when: "the Skip transition is added"
    def builder = StateMachineBuilder.update("", "Default", 0) {
      transition("Skip", [origin: "Waiting", target: "Finished"]) {
        property(enablerProp: "Skippable", reservation: "clear")
      }
    }

    then:
    builder.sm && builder.sm.validate()
  }

  def 'Build Default StateMachine and crosscheck it with Kernel version'() {
    when:
//  StateMachine defaultSM = (StateMachine)Gateway.getMarshaller().unmarshall(Gateway.getResource().getTextResource(null, "boot/SM/Default.xml"));

    def builder = StateMachineBuilder.build("testing", "Default", 'v0') {
      transition("Done", [origin: "Waiting", target: "Finished"]) {
        outcome(name: "\${SchemaType}", version: "\${SchemaVersion}")
        script(name: "\${ScriptName}", version: "\${ScriptVersion}")
        query(name: "\${QueryName}", version: "\${QueryVersion}")
      }
      transition("Start", [origin: "Waiting", target: "Started"]) {
        property reservation: "set"
      }
      transition("Complete", [origin: "Started", target: "Finished"]) {
        property(reservation: "clear")
        outcome(name: "\${SchemaType}", version: "\${SchemaVersion}")
        script(name: "\${ScriptName}", version: "\${ScriptVersion}")
        query(name: "\${QueryName}", version: "\${QueryVersion}")
      }
      transition("Suspend", [origin: "Started", target: "Suspended"]) {
        outcome(name: "Errors", version: "0")
      }
      transition("Resume", [origin: "Suspended", target: "Started"]) {
        property(reservation: "preserve")
      }
      transition("Proceed", [origin: "Finished", target: "Finished"])

      initialState("Waiting")
      finishingState("Finished")
    }
    def json = JsonObject.mapFrom(builder.sm)
    def smCopy = json.mapTo(StateMachine.class)

    then:
    builder.sm && builder.sm.validate()
    Assertions.assertEquals(builder.sm, smCopy)
    //KernelXMLUtility.compareXML(Gateway.getMarshaller().marshall(defaultSM), builder.smXML)
  }

  def 'Build Trigger StateMachine'() {
    when:
    def builder = StateMachineBuilder.build('testing', 'TriggerStateMachine', 'v0') {
      transition('Done', [origin: 'Waiting', target: 'Finished']) {
        outcome(name: '${SchemaType}', version: '${SchemaVersion}')
        script(name: '${ScriptName}', version: '${ScriptVersion}')
        query(name: '${QueryName}', version: '${QueryVersion}')
      }
      transition('Start', [origin: 'Waiting', target: 'Started']) {
        property reservation: 'set'
      }
      transition('Complete', [origin: 'Started', target: 'Finished']) {
        outcome(name: '${SchemaType}', version: '${SchemaVersion}')
        script(name: '${ScriptName}', version: '${ScriptVersion}')
        query(name: '${QueryName}', version: '${QueryVersion}')
        property(reservation: 'clear')
      }
      transition('Warning', [origin: 'Started', target: 'Started']) {
        outcome(name: '${WarningSchemaType}', version: '${WarningSchemaVersion}')
        script(name: '${WarningScriptName}', version: '${WarningScriptVersion}')
        query(name: '${WarningQueryName}', version: '${WarningQueryVersion}')
        property(enablerProp: 'WarningOn')
        property(reservation: 'preserve')
      }
      transition('Timeout', [origin: 'Started', target: 'Paused']) {
        outcome(name: '${TimeoutSchemaType}', version: '${TimeoutSchemaVersion}')
        script(name: '${TimeoutScriptName}', version: '${TimeoutScriptVersion}')
        query(name: '${TimeoutQueryName}', version: '${TimeoutQueryVersion}')
        property(enablerProp: 'TimeoutOn')
      }
      transition('Resolve', [origin: 'Paused', target: 'Started']) {
        property(reservation: 'clear')
      }
      transition('Interrupt', [origin: 'Paused', target: 'Finished']) {
        property(reservation: 'clear')
      }
      transition('Suspend', [origin: 'Started', target: 'Suspended']) {
        outcome(name: 'Errors', version: '0')
      }
      transition('Resume', [origin: 'Suspended', target: 'Started']) {
        property(reservation: 'preserve')
      }
      transition('Proceed', [origin: 'Finished', target: 'Finished'])

      initialState('Waiting')
      finishingState('Finished')
    }
    def json = JsonObject.mapFrom(builder.sm)

    then:
    builder.sm && builder.sm.validate()
    json.mapTo(StateMachine.class) == builder.sm
  }

  def 'Build Skippable StateMachine using builder methods'() {
    when:
    def builder = StateMachineBuilder.build("testing", "Skippable", 'v0') {
      transition("Start", [origin: "Waiting", target: "Started"]) {
        property reservation: "set"
      }
      transition("Done", [origin: "Waiting", target: "Finished"]) {
        property(reservation: "clear")
        outcome(name: "\${SchemaType}", version: "\${SchemaVersion}")
        script(name: "\${ScriptName}", version: "\${ScriptVersion}")
      }
      transition("Skip", [origin: "Waiting", target: "Skipped"]) {
        property(reservation: "clear")
        property(enablerProp: "Skippable")
        outcome(name: 'Errors', version: "0")
      }
      transition("Complete", [origin: "Started", target: "Finished"]) {
        property(reservation: "clear")
        outcome(name: "\${SchemaType}", version: "\${SchemaVersion}")
        script(name: "\${ScriptName}", version: "\${ScriptVersion}")
      }
      transition("Suspend", [origin: "Started", target: "Suspended"]) {
        property(reservation: "set")
        outcome(name: "Errors", version: "0")
      }
      transition("Resume", [origin: "Suspended", target: "Started"]) {
        property(reservation: "preserve")
      }

      initialState("Waiting")
      finishingState('Finished', 'Skipped')
    }
    def sm = builder.sm
    def json = JsonObject.mapFrom(sm)

    then:
    sm && builder.sm.validate()
    sm.getState(sm.getTransition("Start").originStateId).name == "Waiting"
    sm.getState(sm.getTransition("Start").targetStateId).name == "Started"
    sm.getState(sm.getTransition("Skip").originStateId).name == "Waiting"
    sm.getState(sm.getTransition("Skip").targetStateId).name == "Skipped"
    sm.getState("Finished").isFinishing()
    sm.getState("Skipped").isFinishing()
    json.mapTo(StateMachine.class) == builder.sm
  }
}

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
package eu.describeit.cristalise.dsl.statemachine

import eu.describeit.cristalise.kernel.persistency.domain.DomainPathDO
import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.vertx.core.json.JsonObject


/**
 *
 */
@CompileStatic
@Slf4j
class StateMachineBuilder {
  String module = ""
  String name = ""
  int version = -1

  StateMachine sm

  DomainPathDO domainPath = null

  public StateMachineBuilder() {}

  /**
   *
   * @param module
   * @param name
   * @param version
   */
  public StateMachineBuilder(String module, String name, int version) {
    this.module = module
    this.name = name
    this.version = version
  }

  /**
   *
   * @param module
   * @param delegate
   */
  public StateMachineBuilder(String module, StateMachineDelegate delegate) {
    this(module, delegate.name, delegate.version)
    this.sm = delegate.sm
  }

  public static StateMachine create(String module, String name, int version, Closure cl) {
    def builder = build(module, name, version, cl)
    return builder.sm
  }

  public static StateMachineBuilder build(String module, String name, int version, Closure cl) {
    def delegate = new StateMachineDelegate(module, name, version)

    delegate.processClosure(cl)

    def builder = new StateMachineBuilder(module, delegate)
    builder.sm.validate()

    log.info('build() - json:\n{}', JsonObject.mapFrom(builder.sm).encodePrettily())

    return builder
  }
}

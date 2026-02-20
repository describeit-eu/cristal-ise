package eu.describeit.cristalise.kernel

import eu.describeit.cristalise.kernel.statemachine.StateMachine
import groovy.transform.ToString
import io.vertx.core.Future
import io.vertx.core.json.JsonObject;

@ToString(includeNames = true, includePackage = false)
enum BuiltInResources {
  ACTIVITY_DESC_RESOURCE("activity", "ActivityDef", "description.ActivityDesc", null), //'abstract' resource - does not have an Item
  PROPERTY_DESC_RESOURCE("property", "PropertyDescription", "description.PropertyDesc", "ManagePropertyDesc"),
  MODULE_RESOURCE("module", "Module", "description.Module", "ManageModule"),
  SCHEMA_RESOURCE("schema", "Schema", "description.Schema", "ManageSchema"),
  SCRIPT_RESOURCE("script", "Script", "description.Script", "ManageScript"),
  QUERY_RESOURCE("query", "Query", "description.Query", "ManageQuery"),
  STATE_MACHINE_RESOURCE("statemachine", "StateMachine", "description.StateMachine", "ManageStateMachine"),
  COMP_ACT_DESC_RESOURCE("compactivity", "CompositeActivityDef", "description.ActivityDesc", "ManageCompositeActDef"),
  ELEM_ACT_DESC_RESOURCE("elemactivity", "ElementaryActivityDef", "description.ActivityDesc", "ManageElementaryActDef"),
  ITEM_DESC_RESOURCE("item", "ItemDesc", "description.ItemDesc", "ManageItemDesc"),
  AGENT_DESC_RESOURCE("agent", "AgentDesc", "description.AgentDesc", "ManageAgentDesc"),
  ROLE_DESC_RESOURCE("role", "RoleDesc", "description.RoleDesc", "ManageRoleDesc"),
  DOMAIN_CONTEXT_RESOURCE("context", "DomainContext", "description.DomainContext", "ManageDomainContext")

  String typeCode
  String schemaName
  String typeRoot
  String workflowDef

  private BuiltInResources(final String code, final String schema, final String root, final String wf) {
    typeCode = code
    schemaName = schema
    typeRoot = root
    workflowDef = wf
  }

  static BuiltInResources getValue(String value) {
    for (BuiltInResources res : values()) {
      if (res.typeCode == value || res.schemaName == value || res.name() == value) {
        return res
      }
    }
    return null
  }

  static BuiltInResources getValue(DescriptionObject descObject) {
    switch (descObject.class.simpleName) {
      case "PropertyDescriptionList": return PROPERTY_DESC_RESOURCE
      case "Module": return MODULE_RESOURCE
      case "Schema": return SCHEMA_RESOURCE
      case "Script": return SCRIPT_RESOURCE
      case "Query": return QUERY_RESOURCE
      case "StateMachine": return STATE_MACHINE_RESOURCE
      case "CompositeActivityDef": return COMP_ACT_DESC_RESOURCE
      case "ActivityDef": return ELEM_ACT_DESC_RESOURCE
      case "ImportItem": return ITEM_DESC_RESOURCE
      case "ImportAgent": return AGENT_DESC_RESOURCE
      case "ImportRole": return ROLE_DESC_RESOURCE
      case "DomainContext": return DOMAIN_CONTEXT_RESOURCE
      default:
        return null
    }
  }

  static DescriptionObject toDescriptionObject(String schema, JsonObject json) {
    switch (getValue(schema)) {
      case STATE_MACHINE_RESOURCE: return (DescriptionObject) json.mapTo(StateMachine.class)
      default:
        throw new ResourceException("Uncovered case of DescriptionObject of schema $schema")
    }

  }
}

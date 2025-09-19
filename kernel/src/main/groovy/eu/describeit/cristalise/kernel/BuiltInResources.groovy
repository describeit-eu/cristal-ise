package eu.describeit.cristalise.kernel

import groovy.transform.ToString;

@ToString(includeNames = true)
enum BuiltInResources {
  ACTIVITY_DESC_RESOURCE("activity", "ActivityDef", "/desc/ActivityDesc", null), //'abstract' resource - does not have an Item
  PROPERTY_DESC_RESOURCE("property", "PropertyDescription", "/desc/PropertyDesc", "ManagePropertyDesc"),
  MODULE_RESOURCE("module", "Module", "/desc/Module", "ManageModule"),
  SCHEMA_RESOURCE("schema", "Schema", "/desc/Schema", "ManageSchema"),
  SCRIPT_RESOURCE("script", "Script", "/desc/Script", "ManageScript"),
  QUERY_RESOURCE("query", "Query", "/desc/Query", "ManageQuery"),
  STATE_MACHINE_RESOURCE("statemachine", "StateMachine", "/desc/StateMachine", "ManageStateMachine"),
  COMP_ACT_DESC_RESOURCE("compactivity", "CompositeActivityDef", "/desc/ActivityDesc", "ManageCompositeActDef"),
  ELEM_ACT_DESC_RESOURCE("elemactivity", "ElementaryActivityDef", "/desc/ActivityDesc", "ManageElementaryActDef"),
  ITEM_DESC_RESOURCE("item", "ItemDesc", "/desc/ItemDesc", "ManageItemDesc"),
  AGENT_DESC_RESOURCE("agent", "AgentDesc", "/desc/AgentDesc", "ManageAgentDesc"),
  ROLE_DESC_RESOURCE("role", "RoleDesc", "/desc/RoleDesc", "ManageRoleDesc"),
  DOMAIN_CONTEXT_RESOURCE("context", "DomainContext", "/desc/DomainContext", "ManageDomainContext")

  String typeCode;
  String schemaName;
  String typeRoot;
  String workflowDef;

  private BuiltInResources(final String code, final String schema, final String root, final String wf) {
    typeCode = code;
    schemaName = schema;
    typeRoot = root;
    workflowDef = wf;
  }

  static BuiltInResources getValue(String value) {
    for (BuiltInResources res : values()) {
      if (res.typeCode == value || res.schemaName == value || res.name() == value) {
        return res;
      }
    }
    return null;
  }

  static BuiltInResources getValue(DescriptionObject descObject) {
    switch (descObject.class.simpleName) {
      case "PropertyDescriptionList": return PROPERTY_DESC_RESOURCE;
      case "Module": return MODULE_RESOURCE;
      case "Schema": return SCHEMA_RESOURCE;
      case "Script": return SCRIPT_RESOURCE;
      case "Query": return QUERY_RESOURCE;
      case "StateMachine": return STATE_MACHINE_RESOURCE;
      case "CompositeActivityDef": return COMP_ACT_DESC_RESOURCE;
      case "ActivityDef": return ELEM_ACT_DESC_RESOURCE;
      case "ImportItem": return ITEM_DESC_RESOURCE;
      case "ImportAgent": return AGENT_DESC_RESOURCE;
      case "ImportRole": return ROLE_DESC_RESOURCE;
      case "DomainContext": return DOMAIN_CONTEXT_RESOURCE;
      default:
        return null;
    }
  }
}

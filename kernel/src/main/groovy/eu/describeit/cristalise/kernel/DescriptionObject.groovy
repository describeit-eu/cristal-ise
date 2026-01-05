package eu.describeit.cristalise.kernel

import groovy.transform.CompileStatic

@CompileStatic
interface DescriptionObject {
  String getNamespace()
  String getName()
  String getVersion()
  UUID getItemID()

  void setNamespace(String ns);
  void setName(String name);
  void setVersion(String version);
  void setItemID(UUID uuid);

  BuiltInResources getResourceType();
}

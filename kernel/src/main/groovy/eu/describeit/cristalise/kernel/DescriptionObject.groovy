package eu.describeit.cristalise.kernel

import groovy.transform.CompileStatic

@CompileStatic
interface DescriptionObject {
  String getNamespace()
  String getName()
  Integer getVersion()
  UUID getItemID()

  void setNamespace(String ns);
  void setName(String name);
  void setVersion(Integer version);
  void setItemID(UUID uuid);

  public BuiltInResources getResourceType();
}

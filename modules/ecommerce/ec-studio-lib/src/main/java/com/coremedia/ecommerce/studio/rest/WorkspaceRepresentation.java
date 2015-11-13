package com.coremedia.ecommerce.studio.rest;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Workspace representation for JSON.
 */
public class WorkspaceRepresentation extends AbstractCatalogRepresentation {

  private String name;
  private String externalId;
  private String externalTechId;

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getName() {
    return name;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalId() {
    return externalId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalTechId(){
    return externalTechId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public void setExternalTechId(String externalTechId) {
    this.externalTechId = externalTechId;
  }
}

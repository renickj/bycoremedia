package com.coremedia.blueprint.studio.rest;

/**
 * Represents the values of a path configuration
 */
public class PathConfigurationRepresentation {

  private String site;
  private String globalPath;
  private String sitePath;

  public String getGlobalPath() {
    return globalPath;
  }

  public void setGlobalPath(String globalPath) {
    this.globalPath = globalPath;
  }

  public void setSitePath(String path) {
    this.sitePath = path;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getSite() {
    return site;
  }

  public String getSitePath() {
    return sitePath;
  }
}

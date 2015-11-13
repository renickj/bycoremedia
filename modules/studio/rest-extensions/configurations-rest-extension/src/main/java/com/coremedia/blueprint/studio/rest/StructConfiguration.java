package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.content.Content;

import java.util.Map;

/**
 * Represents the result of a struct configuration lookup.
 * The result contains both structs, the site specific and the global struct configuration.
 */
public class StructConfiguration {
  private Map<String,Object> localStructs;
  private Map<String,Object> globalStructs;

  private Content localSettings;
  private Content globalSettings;

  public void setGlobalSettings(Content globalSettings) {
    this.globalSettings = globalSettings;
  }

  public void setLocalSettings(Content localSettings) {
    this.localSettings = localSettings;
  }

  public Map<String, Object> getLocalStructs() {
    return localStructs;
  }

  public void setLocalStructs(Map<String, Object> localStructs) {
    this.localStructs = localStructs;
  }

  public Map<String, Object> getGlobalStructs() {
    return globalStructs;
  }

  public void setGlobalStructs(Map<String, Object> globalStructs) {
    this.globalStructs = globalStructs;
  }


  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof  StructConfiguration)) {
      return false;
    }
    StructConfiguration that = (StructConfiguration)obj;
    return (this.globalSettings != null && that.globalSettings != null && this.globalSettings.equals(that.globalSettings)) ||
            (this.localSettings != null && that.localSettings != null && this.localSettings.equals(that.localSettings));
  }

  @Override
  public int hashCode() {
    return globalSettings != null ? globalSettings.hashCode() : 0;
  }
}

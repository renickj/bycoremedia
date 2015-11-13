package com.coremedia.blueprint.studio.rest;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The cache key that contains the Upload configuration
 * The configuration is read from a struct property of a site depending content object.
 * The path of the configuration content is build via spring settings and the constant for the document name,
 * see documentation below.
 */
public class UploadConfigurationCacheKey extends CacheKey<UploadConfigurationRepresentation> {   // NOSONAR  cyclomatic complexity

  //The document name the settings struct is read from, check the spring configuration for path details
  private static final String UPLOAD_SETTINGS_DOCUMENT = "UploadSettings";
  private String preferredSiteId;
  private ConfigurationService configurationService;
  private StructConfiguration config;

  /**
   * Creates a new site depending configuration cache key.
   *
   * @param preferredSiteId The site the configuration will be cached for.
   */
  public UploadConfigurationCacheKey(ConfigurationService configurationService, String preferredSiteId) {
    this.preferredSiteId = preferredSiteId;
    this.configurationService = configurationService;
  }

  /**
   * Creates the source list representations that is cached.
   *
   * @param cache the cache managing asked to return a value for this cache key
   * @return the source list representations that is cached
   * @throws Exception if the value cannot be computed. This will prevent the value from being cached.
   */
  @Override
  public UploadConfigurationRepresentation evaluate(Cache cache) throws Exception {
    UploadConfigurationRepresentation result = new UploadConfigurationRepresentation();

    config = configurationService.getStructMaps(preferredSiteId, UPLOAD_SETTINGS_DOCUMENT, "settings");
    addConfiguration(result, config.getGlobalStructs());
    addConfiguration(result, config.getLocalStructs());
    return result;
  }

  private void addConfiguration(UploadConfigurationRepresentation config, Map<String, Object> items) {  // NOSONAR  cyclomatic complexity (switch to a switch statement once we may use Java 7 features)
    if(items != null) {
      for (Map.Entry<String, Object> entry : items.entrySet()) { //check site specific values that overwrite/add values to the global configuration
        if(entry.getKey().equalsIgnoreCase("mimeTypes")) {
          List<String> mimeTypes = (List<String>) entry.getValue();
          Collections.sort(mimeTypes);
          config.setMimeTypes(mimeTypes);
        }
        else if(entry.getKey().equalsIgnoreCase("defaultFolder")) {
          config.setDefaultFolder((String)entry.getValue());
        }
        else if(entry.getKey().equalsIgnoreCase("defaultContentType")) {
          config.setDefaultContentType((String)entry.getValue());
        }
        else if(entry.getKey().equalsIgnoreCase("mimeTypeMappings")) {
          config.setMimeTypeMappings((Map<String,String>)entry.getValue());
        }
        else if(entry.getKey().equalsIgnoreCase("defaultBlobPropertyName")) {
          config.setDefaultBlobPropertyName((String) entry.getValue());
        }
        else if(entry.getKey().equalsIgnoreCase("mimeTypeToBlobPropertyMappings")) {
          config.setMimeTypeToBlobPropertyMappings((Map<String, String>) entry.getValue());
        }
        else if(entry.getKey().equalsIgnoreCase("mimeTypeToMarkupPropertyMappings")) {
          config.setMimeTypeToMarkupPropertyMappings((Map<String, String>) entry.getValue());
        }
        else if(entry.getKey().equalsIgnoreCase("timeout")) {
          config.setTimeout((Integer)entry.getValue());
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UploadConfigurationCacheKey)) {
      return false;
    }

    UploadConfigurationCacheKey that = (UploadConfigurationCacheKey) o;

    return !(config != null ? !config.equals(that.config) : that.config != null);

  }

  @Override
  public int hashCode() {
    return preferredSiteId != null ? preferredSiteId.hashCode() : 0;
  }
}

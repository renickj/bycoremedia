package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.util.StringUtil;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Implements a configuration service for accessing site depending values.
 */
public class ConfigurationServiceImpl implements ConfigurationService {

  private String globalConfigurationPath;
  private String siteConfigurationPath;
  private ContentRepository contentRepository;
  private SitesService sitesService;

  //--- Spring configuration --
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setContentRepository(ContentRepository repo) {
    this.contentRepository = repo;
  }

  @Required
  public void setGlobalConfigurationPath(String globalConfigurationPath) {
    this.globalConfigurationPath = globalConfigurationPath;
  }

  @Required
  public void setSiteConfigurationPath(String siteSpecificConfigurationPath) {
    this.siteConfigurationPath = siteSpecificConfigurationPath;
  }

  @Override
  public String getRawSiteConfigFolder() {
    return siteConfigurationPath;
  }

  @Override
  public StructConfiguration getStructMaps(String siteId, String settingsDocumentName, String propertyName) {
    StructConfiguration configuration = new StructConfiguration();

    Content globalSettings = getGlobalSettingsContent(settingsDocumentName);
    if(globalSettings != null) {
      configuration.setGlobalSettings(globalSettings);
      Map<String, Object> result = getStructMap(globalSettings, propertyName);
      if (result != null) {
        configuration.setGlobalStructs(result);
      }
    }

    if (!StringUtil.isEmpty(siteId)) {
      Content siteConfigFolder = resolveSiteConfigurationFolder(siteId, siteConfigurationPath);
      if(siteConfigFolder != null) {
        Content localSettings = siteConfigFolder.getChild(settingsDocumentName);
        configuration.setLocalSettings(localSettings);
        Map<String, Object> result = getStructMap(localSettings, propertyName);
        if (result != null) {
          configuration.setLocalStructs(result);
        }
      }
    }

    return configuration;
  }

  @Nullable
  private Content getGlobalSettingsContent(String settingsDocumentName) {
    final Content content = contentRepository.getChild(globalConfigurationPath);
    if(content != null) {
      return content.getChild(settingsDocumentName);
    }
    return null;
  }

  @Override
  public String getGlobalConfigFolder() {
    return globalConfigurationPath;
  }

  @Override
  public String getSiteConfigFolder(String siteId, String subfolder) {
    if (!StringUtil.isEmpty(siteId)) {
      Content siteConfigFolder = resolveSiteConfigurationFolder(siteId, siteConfigurationPath);
      Content configFolder = siteConfigFolder==null ? null : siteConfigFolder.getChild(subfolder);
      if(configFolder != null) {
        return configFolder.getPath();
      }
    }
    return null;
  }

  /**
   * Returns a map of struct property values for the given content path, name, property and struct name.
   *
   * @param settings     The document to look up the settings for.
   * @param propertyName The property name the struct is stored in.
   * @return An instance of map or null.
   */
  @Nullable
  public Map<String, Object> getStructMap(@Nullable Content settings, String propertyName) {
    if (settings != null) {
      Struct struct = settings.getStruct(propertyName);
      if (struct != null) {
        return struct.toNestedMaps();
      }
    }
    return null;
  }

  /**
   * Resolves the site specific configuration folder.
   * @param siteId id of the site to get the configuration folder for
   * @param siteConfigurationPath the relative path to the site configuration
   * @return the site configuration folder or {@code null} if it does not exist
   * @throws java.lang.NullPointerException if no site with siteId exists
   */
  @Nullable
  private Content resolveSiteConfigurationFolder(String siteId, String siteConfigurationPath) {
    Site site = requireNonNull(sitesService.getSite(siteId), format("Site with id %s does not exist.", siteId));
    Content siteRoot = site.getSiteRootFolder();
    return siteRoot.getChild(siteConfigurationPath);
  }
}

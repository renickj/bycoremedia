package com.coremedia.blueprint.studio.util {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.sites.SitesService;

/**
 * Utility class providing a centralized configuration for studio, which are sites aware.
 */
public class StudioConfigurationUtil {

  private static const CONFIGURATION_FOLDER:String = "Options/Settings";
  private static const GLOBAL_CONFIGURATION_FOLDER_PREFIX:String = "/Settings";

  private static const STRUCT_PROPERTY_NAME:String = "settings";
  private static const CONFIGURATION_PATH_DELIMITER:String = ".";

  /**
   * Provides a studio configuration from a site specific or global configuration.
   * This method is dependency tracked and can be used within FunctionValueExpressions.
   * @param bundle the name of the settings document relativ to CONFIGURATION_FOLDER
   * @param configuration the configuration name. Subpathes are divided by dots(.).
   * @param context the context to retrieve the site for configuration. This may either be a Site or a Content.
   *        If a Content is provided, the site containing the Content is used.
   *        If null is provided, the preferred site is used.
   * @return the requested configuration, or null, if not configured. Undefined, if currently not ready.
   */
  public static function getConfiguration(bundle:String, configuration:String, context:* = null):* {
    var sitesService:SitesService = editorContext.getSitesService();

    var contentRepository:ContentRepository = editorContext.getSession().getConnection().getContentRepository();

    var site:Site;
    if (context is Content) {
      site = sitesService.getSiteFor(Content(context));
    } else {
      site = Site(context);
    }

    if (!site) {
      site = sitesService.getPreferredSite();
    }

    var configurationResult:* = null;
    if (site) {
      configurationResult = getConfigurationInFolder(configuration, site.getSiteRootFolder(),
              CONFIGURATION_FOLDER + "/" + bundle);
      if (configurationResult === undefined) {
        return undefined;
      }
    }

    if (configurationResult === null) {
      var globalRootFolder:Content = contentRepository.getChild(GLOBAL_CONFIGURATION_FOLDER_PREFIX);
      configurationResult = getConfigurationInFolder(configuration, globalRootFolder,
              CONFIGURATION_FOLDER + "/" + bundle);
      if (configurationResult === undefined) {
        return undefined;
      }
    }

    return configurationResult;
  }

  private static function getConfigurationInFolder(configuration:String, folder:Content, pathOfSetting:String):* {
    if(!folder) {
      return undefined;
    }
    
    var settingsContent:Content = folder.getChild(pathOfSetting);
    if (settingsContent === undefined) {
      return undefined;
    }
    if (settingsContent === null) {
      return null;
    }

    var properties:ContentProperties = settingsContent.getProperties();
    if (!properties) {
      return undefined;
    }

    var configurationStruct:Struct = properties.get(STRUCT_PROPERTY_NAME);
    if (configurationStruct === undefined) {
      return undefined;
    }

    if (configurationStruct === null) {
      return null;
    }

    var configurationPaths:Array = configuration.split(CONFIGURATION_PATH_DELIMITER);
    var actualPathElement:* = configurationStruct;
    for each (var path:String in configurationPaths) {
      actualPathElement = actualPathElement.get(path);
      if (actualPathElement === undefined) {
        return undefined;
      }
      if (actualPathElement === null) {
        return null;
      }
    }
    return actualPathElement;
  }

  /**
   * constructor private
   */
  public function StudioConfigurationUtil() {
    throw new Error("not implemented!")
  }
}
}

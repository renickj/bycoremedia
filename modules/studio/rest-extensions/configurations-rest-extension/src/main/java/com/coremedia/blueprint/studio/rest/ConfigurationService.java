package com.coremedia.blueprint.studio.rest;

/**
 * Defines the access method for Studio and CAE configurations.
 */
public interface ConfigurationService {

  /**
   * Returns the property values of site specific and global configuration structs.
   * @param siteId The id of the site the configuration should be searched for.
   * @param settingsDocumentName The name of the document to look up.
   * @param propertyName The property name the struct is stored in.
   * @return An instance of list, not null.
   */
  StructConfiguration getStructMaps(String siteId, String settingsDocumentName, String propertyName);

  /**
   * Returns the raw site specific configuration path, including the placeholder.
   * This is used for the studio that is replacing the value on client side.
   * @return The raw site condifiguration path.
   */
  String getRawSiteConfigFolder();

  /**
   * Returns the path that is configured for the global settings
   * lookup path.
   * @return The global configuration folder
   */
  String getGlobalConfigFolder();

  /**
   * Returns the site depending config folder for the given content.
   * @param siteId The site placeholder.
   * @param subfolder A subfolder of the config folder, concatenated with the site config path.
   * @return The full path of the site configuration folder + subfolder (including document name) or null if that folder does not exist.
   */
  String getSiteConfigFolder(String siteId, String subfolder);
}

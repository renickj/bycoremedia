package com.coremedia.blueprint.studio.rest.osm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import javax.ws.rs.Path;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * The server side of the OSM plugin.
 * Actually it not a service. It works as a startup hook to discover if the the OSM plugin should be enabled.
 */
@Path("osm")
public class OSMResource implements InitializingBean, ApplicationContextAware {
  private static final Logger LOGGER = LoggerFactory.getLogger(OSMResource.class);

  private static final String OPEN_LAYERS_URL = "http://www.openlayers.org/api/OpenLayers.js";

  private String jangarooApplicationJs;
  private ApplicationContext applicationContext;

  @Required
  public void setJangarooApplicationJs(String joo) {
    this.jangarooApplicationJs = joo;
  }

  @Override
  public void afterPropertiesSet() throws Exception { // NOSONAR
    if (!isOSMAvailable()) {
      disableOSMStudioPlugin();
    }
  }

  /**
   * Removes the OSM module from the Studio application.
   */
  private void disableOSMStudioPlugin() {
    try {
      Resource joo = applicationContext.getResource(jangarooApplicationJs);
      File jooFile = joo.getFile();
      if(LOGGER.isInfoEnabled()) {
        LOGGER.info("Rewriting studio config file '" + jooFile.getAbsolutePath() + "'");
      }
      ModuleSelector m = new ModuleSelector(jooFile.getAbsolutePath());
      m.disable("osm-studio");
    } catch (Exception e) {
      LOGGER.error("Error disabling OSM module: " + e.getMessage(), e);
    }
  }

  /**
   * Checks if the open street map js can be retrieved
   * by executing a HTTP request.
   *
   * @return True, if OSM is available and plugin can remain enabled.
   */
  public boolean isOSMAvailable() {
    try {
      URL url = new URL(OPEN_LAYERS_URL);
      HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
      httpCon.setRequestMethod("GET");
      httpCon.connect();
      if (httpCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
        return true;
      }

      LOGGER.warn(httpCon.getResponseCode() + " Error in OSM response: " + httpCon.getResponseMessage());//NOSONAR
      return false;
    } catch (IOException e) {
      LOGGER.warn("OSM is not available: " + e.getMessage(), e);
    }
    return false;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}

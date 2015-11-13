package com.coremedia.blueprint.common.robots;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * POJO to represent the entire data structure of a Robots.txt
 */
public class RobotsBean {
  public static final String SETTINGS_NAME = "Robots.txt";

  private Log log = LogFactory.getLog(getClass());
  private CMNavigation rootChannel = null;
  private SettingsService settingsService;
  private SitesService sitesService;
  private List<RobotsEntry> robotsEntries = new ArrayList<>();

  public RobotsBean(@Nonnull CMNavigation rootChannel, @Nonnull SettingsService settingsService, @Nonnull SitesService sitesService) {
    this.rootChannel = rootChannel;
    this.settingsService = settingsService;
    this.sitesService = sitesService;
    readRobotsSettings();
  }

  public CMNavigation getRootChannel() {
    return rootChannel;
  }

  public List<RobotsEntry> getRobotsEntries() {
    return robotsEntries;
  }

  public Site getSite() {
    return sitesService.getContentSiteAspect(rootChannel.getContent()).getSite();
  }

  private void readRobotsSettings() {
    if (getRootChannel()!= null) {
      List<Map> settingsList = settingsService.settingAsList(RobotsBean.SETTINGS_NAME, Map.class, getRootChannel());
      if (settingsList != null && !settingsList.isEmpty()) {
        if (log.isDebugEnabled()) {
          log.debug("found a settings object for [" + RobotsBean.SETTINGS_NAME + "], start reading its entries...");
        }

        for (Map settingsMap : settingsList) {
          robotsEntries.add(new RobotsEntry(settingsMap));
        }
      } else if (log.isDebugEnabled()) {
        log.debug("No robots.txt settings for [" + getRootChannel().getContent().getName() + "], delivering empty file");
      }

    } else {
      log.error("invalid root channel, cannot create robots.txt");
    }
  }
}

package com.coremedia.blueprint.common.robots;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * POJO to represent one node in a Robots.txt
 */
public class RobotsEntry {

  public static final String SITEMAP_TAG = "Sitemap";
  public static final String USER_AGENT_TAG = "User-agent";
  public static final String DISALLOW_TAG = "Disallow";
  public static final String ALLOW_TAG = "Allow";
  public static final String CUSTOM_TAG = "custom-entries";

  private static final int DISALLOW_CASE = 0;
  private static final int ALLOW_CASE = 1;
  private static final int CUSTOM_CASE = 2;

  private Log log = LogFactory.getLog(getClass());

  private String sitemapLink;
  private String userAgent;
  private List<CMLinkable> disallowed;
  private List<CMLinkable> allowed;
  private List<String> custom;

  public RobotsEntry(Map settings) {
    clear();
    readRobotsSettings(settings);
  }

  private void clear() {
    sitemapLink = "";
    userAgent = "";
    disallowed = new ArrayList<>();
    allowed = new ArrayList<>();
    custom = new ArrayList<>();
  }

  private void readRobotsSettings(Map settings) {

    // reading settings map and populate corresponding fields with configured values:
    if (settings != null) {

      // check for regular user agent node:
      Object value = settings.get(USER_AGENT_TAG);

      if (value instanceof String) {

        String userAgentValue = (String) value;
        readUserAgentNode(userAgentValue, settings);

      } else {

        readSitemapLink(settings);
      }
    }
  }

  private void readUserAgentNode(String userAgent, Map settings) {

    if (log.isDebugEnabled()) {
      log.debug("Found new entry of user agent node [" + userAgent + "]");
    }

    setUserAgent(userAgent);

    Object value = settings.get(DISALLOW_TAG);
    readSetting(value, DISALLOW_CASE, userAgent);

    value = settings.get(ALLOW_TAG);
    readSetting(value, ALLOW_CASE, userAgent);

    value = settings.get(CUSTOM_TAG);
    readSetting(value, CUSTOM_CASE, userAgent);

    if (log.isDebugEnabled()) {
      log.debug("added new node for robots.txt for user agent [" + userAgent + "]");
    }
  }

  private void readSitemapLink(Map settings) {

    // no user agent node, but maybe a sitemap node:
    Object value = settings.get(SITEMAP_TAG);

    if (value instanceof String) {

      if (log.isDebugEnabled()) {
        log.debug("Found new entry of sitemap node for robots.txt ...");
      }

      setSitemapLink((String) value);
    }
  }


  private void setSitemapLink(String sitemapLink) {
    this.sitemapLink = sitemapLink;
  }

  private void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  private void addDisallow(CMLinkable linkable) {

    if (linkable != null) {
      disallowed.add(linkable);
    }
  }

  private void addAllow(CMLinkable linkable) {

    if (linkable != null) {
      allowed.add(linkable);
    }
  }

  private void addCustom(String entry) {

    if (!StringUtil.isEmpty(entry)) {
      custom.add(entry);
    }
  }

  public String getSitemapLink() {
    return sitemapLink;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public List<CMLinkable> getDisallowed() {
    return disallowed;
  }

  public List<CMLinkable> getAllowed() {
    return allowed;
  }

  public List<String> getCustom() {
    return custom;
  }

  private void readSetting(Object value, int type, String userAgentValue) {
    // inside a user agent node we expect only collections (for disallow, allow and custom entries):
    if (value instanceof Collection) {
      Collection collection = (Collection) value;

      for (Object link : collection) {
        readUserAgentLinks(link, type, userAgentValue);
      }
    }
  }

  private void readUserAgentLinks(Object link, int type, String userAgentValue) {

    switch (type) {

      case DISALLOW_CASE:

        if (link instanceof CMLinkable) {
          addDisallow((CMLinkable) link);
        } else if (log.isWarnEnabled()) {
          log.warn("ignoring unknown type for disallow entry to [" + userAgentValue + "] for robots.txt");
        }
        break;

      case ALLOW_CASE:

        if (link instanceof CMLinkable) {
          addAllow((CMLinkable) link);
        } else if (log.isWarnEnabled()) {
          log.warn("ignoring unknown type for allow entry to [" + userAgentValue + "] for robots.txt");
        }
        break;

      case CUSTOM_CASE:

        if (link instanceof String) {
          addCustom((String) link);
        } else if (log.isWarnEnabled()) {
          log.warn("ignoring unknown type for custom entry to [" + userAgentValue + "] for robots.txt");
        }
        break;

      default:
        throw new IllegalStateException("type [" + type + "] not supported for robots.txt");
    }
  }

}

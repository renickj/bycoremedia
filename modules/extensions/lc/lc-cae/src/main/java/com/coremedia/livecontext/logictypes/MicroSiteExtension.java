package com.coremedia.livecontext.logictypes;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Link Builder for CMChannels that are supposed to link to the IBM WCS system.
 */
public class MicroSiteExtension extends ExtensionBase {

  // Logic

  public static final String MICROSITES_PATH_SEGMENT = "microsites";

  public static final String CONTENT_URL_KEYWORD_SETTING_NAME = "ContentURLKeyword";
  public static final String DEFAULT_MICROSITE_CONTENT_URL_KEYWORD = "microsite";

  public boolean isMicroSite(CMObject cmObject) {
    return (cmObject instanceof CMChannel) && (!(cmObject instanceof CMExternalChannel)) && isMicroSite((cmObject).getContent());
  }

  public boolean isMicroSite(Content c) {
    return c != null && !c.isDeleted() && !c.isDestroyed() && c.getPath().toLowerCase().contains(MICROSITES_PATH_SEGMENT);
  }

  // Link Builder

  protected static final String LIVECONTEXT_POLICY_COMMERCE_MICROSITE_LINKS = "livecontext.policy.commerce-microsite-links";

  /**
   * Create a link to the MicroSite, as used by the Studio preview.
   *
   * @param channel
   * @return
   */
  public Object createLinkForMicroSite(CMChannel channel) {
    if (!isMicroSite(channel)) {
      return null;
    }

    try {
      if (renderCommerceMicroSiteLinks(channel)) {
        Site site = getSitesService().getContentSiteAspect(channel.getContent()).getSite();
        StoreContext storeContext = getStoreContextProvider().findContextBySite(site);
        if (storeContext != null) {
          Map<String, String> parameterMap = getParameterMapForCommerceLink(channel, channel);
          return buildCommerceLinkFor(null, DEFAULT_MICROSITE_CONTENT_URL_KEYWORD, parameterMap);
        }
      }
    } catch (Exception e) {
      LOG.error("Error determining MicroSite URL", e);
    }
    return null;
  }

  /**
   * Return true if the given channel link shall be rendered as a WCS/Commerce link.
   * The scope of this setting can be finer grained than a site, since you may reuse AAS content / channels
   * within a PCS scenario.
   *
   * @param channel
   * @return
   */
  protected boolean renderCommerceMicroSiteLinks(CMChannel channel) {
    return getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_MICROSITE_LINKS, Boolean.class, false, channel);
  }

  /**
   * Provide placeholders that will be used for generating LiveContext 2.0 link placeholders
   *
   * @param bean
   * @return
   */
  public String getContentURLKeywordForMicroSite(CMObject bean) {
    if (!isMicroSite(bean)) {
      return null;
    }
    CMChannel microSiteChannel = (CMChannel) bean;

    // If the channel provides a setting named "ContentURLKeyword", use it as the WCS vanity segment
    try {
      String storefrontContentURLKeyword = getSettingsService().setting(CONTENT_URL_KEYWORD_SETTING_NAME, String.class, microSiteChannel);
      if (StringUtils.isNotBlank(storefrontContentURLKeyword)) {
        return storefrontContentURLKeyword;
      }
    } catch (Exception e) {
      LOG.error("Cannot retrieve ContentURLKeyword setting from channel", e);
    }

    return DEFAULT_MICROSITE_CONTENT_URL_KEYWORD;
  }
}

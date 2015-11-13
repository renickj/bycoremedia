package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.livecontext.logictypes.MicroSiteExtension;
import com.coremedia.livecontext.logictypes.SearchLandingPagesExtension;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Link Builder for CMChannels that are supposed to link to the IBM WCS system.
 */
@Link
@RequestMapping
public class LiveContextChannelLinkBuilder extends LiveContextPageHandlerBase {

  private MicroSiteExtension microSiteExtension;
  private SearchLandingPagesExtension searchLandingPagesExtension;

  @Required
  public void setSearchLandingPagesExtension(SearchLandingPagesExtension searchLandingPagesExtension) {
    this.searchLandingPagesExtension = searchLandingPagesExtension;
  }

  @Required
  public void setMicroSiteExtension(MicroSiteExtension microSiteExtension) {
    this.microSiteExtension = microSiteExtension;
  }

  // There is no request mapping, since micro site requests are handled by the IBM WCS System

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = CMChannel.class)
  @Nullable
  public Object buildLinkForChannel(
          @Nonnull CMChannel channel,
          @Nullable String viewName,
          @Nonnull Map<String, Object> linkParameters) {

    // MicroSite case
    if (microSiteExtension.isMicroSite(channel)) {
      return microSiteExtension.createLinkForMicroSite(channel);
    }

    // Search Landing Page case
    if (searchLandingPagesExtension.isSearchLandingPage(channel)) {
      return searchLandingPagesExtension.createSearchLandingPageURLFor(channel);
    }

    return null;
  }
}

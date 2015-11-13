package com.coremedia.blueprint.feeder.populate;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.ContentFeedableAspect;
import com.coremedia.cap.feeder.Feedable2;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.TextParameters;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class SiteFeedablePopulator implements FeedablePopulator<Content> {
  private static final Logger LOG = LoggerFactory.getLogger(SiteFeedablePopulator.class);
  private static final String SITE_SOLR_INDEX_FIELD = "site";

  private SitesService sitesService;

  //--- Spring configuration --
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Override
  public void populate(MutableFeedable feedable, Content content) {
    if (feedable == null || content == null) {
      throw new IllegalArgumentException("feedable and content must not be null");
    }

    // set the Content's site if this is a full index document update (add/replace) where all fields need to be set
    // or if it's a partial update and the content's path has changed to a possibly different site
    if (feedable.isPartialUpdate()
            && feedable instanceof Feedable2
            && !((Feedable2) feedable).getUpdatedAspects().contains(ContentFeedableAspect.PATH)) {
      return;
    }

    Site site = sitesService.getContentSiteAspect(content).getSite();
    String siteName = site != null ? site.getName() : null;
    LOG.debug("Site for {}: {}", content, siteName);
    feedable.setStringElement(SITE_SOLR_INDEX_FIELD, siteName, TextParameters.NONE.asMap());
  }
}

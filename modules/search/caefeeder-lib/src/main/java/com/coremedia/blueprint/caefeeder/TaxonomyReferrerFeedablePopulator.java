package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMLocTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link FeedablePopulator} that feeds a list of sites where a given taxonomy is used.
 */
public class TaxonomyReferrerFeedablePopulator implements FeedablePopulator<CMObject> {

  private static final String CONTENT_QUERY_BELOW_PATH = "BELOW ?0 LIMIT 1";

  private SitesService sitesService;

  //--- Spring configuration --
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  //--- functionality ---

  @Override
  public void populate(MutableFeedable feedable, CMObject source) {
    if (!(source instanceof CMTaxonomy)) {
      return;
    }
    Set<String> taxonomyInPages = new HashSet<>();

    Set<Site> sites = sitesService.getSites();
    for (Site availableSite : sites) {
      Content siteIndicator = availableSite.getSiteIndicator();
      // search
      if (source instanceof CMLocTaxonomy) {
        Content locationTaxonomy = source.getContent().getReferrerWithDescriptorFulfilling(CMLinkable.NAME,
                CMLinkable.LOCATION_TAXONOMY, CONTENT_QUERY_BELOW_PATH, availableSite.getSiteRootFolder());
        if (locationTaxonomy != null) {
          taxonomyInPages.add(availableSite.getSiteRootFolder().getPath());
        }
      } else {
        Content subjectTaxonomy = source.getContent().getReferrerWithDescriptorFulfilling(CMLinkable.NAME,
                CMLinkable.SUBJECT_TAXONOMY, CONTENT_QUERY_BELOW_PATH, availableSite.getSiteRootFolder());
        if (subjectTaxonomy != null) {
          taxonomyInPages.add(availableSite.getSiteRootFolder().getPath());
        }
      }
    }
    feedable.setElement(SearchConstants.FIELDS.TAXONOMY_REFERRERS_IN_SITE.toString(), taxonomyInPages);
  }
}

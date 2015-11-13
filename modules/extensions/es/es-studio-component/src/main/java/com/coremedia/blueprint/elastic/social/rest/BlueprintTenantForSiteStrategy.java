package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.social.rest.api.TenantForSiteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *  Uses the {@link com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin} to find the tenant for the given site.
 *  It returns the current tenant if none is found.
 */
@Named
public class BlueprintTenantForSiteStrategy implements TenantForSiteStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(BlueprintTenantForSiteStrategy.class);

  @Inject
  private ElasticSocialPlugin elasticSocialPlugin;

  @Inject
  private SitesService sitesService;

  @Override
  public String getTenantForSiteId(String siteId) {
    Site site = sitesService.getSite(siteId);
    if (site != null) {
      String tenant = elasticSocialPlugin.getElasticSocialConfiguration(site).getTenant();
      if (isNotBlank(tenant)) {
        return tenant;
      } else {
        LOG.warn("Site {} has no tenant, using current tenant instead", site);
      }
    }
    return null;
  }

}

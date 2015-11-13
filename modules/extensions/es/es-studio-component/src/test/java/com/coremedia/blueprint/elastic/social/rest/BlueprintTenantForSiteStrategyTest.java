package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BlueprintTenantForSiteStrategyTest {

  private static final String SITE_ID1 = "siteId1";

  @InjectMocks
  private BlueprintTenantForSiteStrategy strategy;

  @Mock
  private SitesService sitesService;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock Site site;

  @Before
  public void setup() {
    when(elasticSocialPlugin.getElasticSocialConfiguration(site)).thenReturn(elasticSocialConfiguration);
    when(elasticSocialConfiguration.getTenant()).thenReturn("myTenant");
    when(sitesService.getSite(SITE_ID1)).thenReturn(site);
  }

  @Test
  public void configuredTenant() {
    assertEquals("myTenant", strategy.getTenantForSiteId(SITE_ID1));
  }

  @Test
  public void noTenant() {
    assertEquals(null, strategy.getTenantForSiteId("unknownSiteId"));
  }

}

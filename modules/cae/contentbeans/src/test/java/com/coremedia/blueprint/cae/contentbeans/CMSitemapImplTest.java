package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMSitemap;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CMSitemapImplTest extends ContentBeanTestBase {

  private CMSitemap sitemap;

  @Before
  public void setUp() throws Exception {
    sitemap = getContentBean(86);
  }

  @Test
  public void testGetAspectByName() throws Exception {
    Assert.assertEquals(0, sitemap.getAspectByName().size());
  }

  @Test
  public void testGetAspects() throws Exception {
    Assert.assertEquals(0, sitemap.getAspects().size());
  }

  @Test
  public void testGetLocalizations() throws Exception {
    Assert.assertEquals(1, sitemap.getLocalizations().size());
  }

  @Test
  public void testGetVariantsByLocale() throws Exception {
    Assert.assertEquals(1, sitemap.getVariantsByLocale().size());
  }

  @Test
  public void testGetMaster() throws Exception {
    Assert.assertNull(sitemap.getMaster());
  }

  @Test
  public void testGetRoot() throws Exception {
    Assert.assertEquals(10, sitemap.getRoot().getContentId());
  }
}

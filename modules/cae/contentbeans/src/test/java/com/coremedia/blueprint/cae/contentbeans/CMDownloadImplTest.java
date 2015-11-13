package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CMDownloadImplTest extends ContentBeanTestBase {

  private CMDownload download;

  @Before
  public void setUp() throws Exception {
    download = getContentBean(88);
  }

  @Test
  public void testGetAspectByName() throws Exception {
    Assert.assertEquals(0, download.getAspectByName().size());
  }

  @Test
  public void testGetAspects() throws Exception {
    Assert.assertEquals(0, download.getAspects().size());
  }

  @Test
  public void testGetLocalizations() throws Exception {
    Assert.assertEquals(1, download.getLocalizations().size());
  }

  @Test
  public void testGetVariantsByLocale() throws Exception {
    Assert.assertEquals(1, download.getVariantsByLocale().size());
  }

  @Test
  public void testGetMaster() throws Exception {
    Assert.assertNull(download.getMaster());
  }

  @Test
  public void testGetData() throws Exception {
    Assert.assertNotNull(download.getData());
  }
}

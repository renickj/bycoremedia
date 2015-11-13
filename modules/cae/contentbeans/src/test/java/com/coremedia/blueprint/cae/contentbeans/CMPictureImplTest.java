package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CMPictureImplTest extends ContentBeanTestBase {

  private CMPicture contentBean;

  @Before
  public void setUp() throws Exception {
    contentBean = getContentBean(16);
  }

  @Test
  public void testGetAspectByName() throws Exception {
    Assert.assertEquals(0, contentBean.getAspectByName().size());
  }

  @Test
  public void testGetAspects() throws Exception {
    Assert.assertEquals(0, contentBean.getAspects().size());
  }

  @Test
  public void testGetLocalizations() throws Exception {
    Assert.assertEquals(1, contentBean.getLocalizations().size());
  }

  @Test
  public void testGetVariantsByLocale() throws Exception {
    Assert.assertEquals(1, contentBean.getVariantsByLocale().size());
  }

  @Test
  public void testGetMaster() throws Exception {
    Assert.assertNull(contentBean.getMaster());
  }

  @Test
  public void testGetData() throws Exception {
    Assert.assertNotNull(contentBean.getData());
  }

  @Test
  public void testGetDataUrl() throws Exception {
    Assert.assertEquals("http://coremedia.com/", contentBean.getDataUrl());
  }

  @Test
  public void testGetWidth() throws Exception {
    Assert.assertEquals(800, contentBean.getWidth().intValue());
  }

  @Test
  public void testGetHeight() throws Exception {
    Assert.assertEquals(600, contentBean.getHeight().intValue());
  }
}

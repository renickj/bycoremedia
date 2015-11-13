package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeaser;
import com.coremedia.blueprint.testing.ContentBeanTestBase;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.common.CapStructHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CMTeaserImplTest extends ContentBeanTestBase {

  private CMTeaser teaser;

  @Before  public void setUp() throws Exception {
    teaser = getContentBean(56);
  }

  @Test
  public void testGetLocalSettings() throws Exception {
    Struct localSettings = teaser.getLocalSettings();
    Assert.assertNotNull(localSettings);
    Assert.assertTrue(CapStructHelper.getBoolean(localSettings, "setIndirectly"));
    Assert.assertTrue(CapStructHelper.getBoolean(localSettings, "setDirectly"));
    Assert.assertFalse(CapStructHelper.getBoolean(localSettings, "willBeOverridden"));
    Assert.assertEquals("size", 3, localSettings.toNestedMaps().size());
  }

  @Test
  public void testGetAspectByName() throws Exception {
    Assert.assertEquals(0, teaser.getAspectByName().size());
  }

  @Test
  public void testGetAspects() throws Exception {
    Assert.assertEquals(0, teaser.getAspects().size());
  }

  @Test
  public void testGetLocalizations() throws Exception {
    Assert.assertEquals(1, teaser.getLocalizations().size());
  }

  @Test
  public void testGetVariantsByLocale() throws Exception {
    Assert.assertEquals(1, teaser.getVariantsByLocale().size());
  }

  @Test
  public void testGetMaster() throws Exception {
    Assert.assertNull(teaser.getMaster());
  }

  @Test
  public void testGetTarget() {
    Assert.assertEquals(6, teaser.getTarget().getContentId());
  }

  @Test
  public void testHandleTargetIsSelf() throws Exception {
    // reproducer for CMS-2361
    teaser = getContentBean(144);
    List<? extends CMPicture> pictures = teaser.getPictures();// no recursive call expected!
    assertEquals(1, pictures.size());
    assertEquals(16, pictures.get(0).getContentId());
  }

  @Test
  public void testHandlePicturesFromTarget() throws Exception {
    teaser = getContentBean(146);
    List<? extends CMPicture> pictures = teaser.getPictures();// no recursive call expected!
    assertEquals(1, pictures.size());
    assertEquals(16, pictures.get(0).getContentId());
  }

  @Test
  public void testHandlePicturesFromSelf() throws Exception {
    teaser = getContentBean(148);
    List<? extends CMPicture> pictures = teaser.getPictures();// no recursive call expected!
    assertEquals(1, pictures.size());
    assertEquals(20, pictures.get(0).getContentId());
  }

  @Test
  public void testLocalSettingsForTargetIsSelf() throws Exception {
    // may not result in endless recursion
    teaser = getContentBean(144);
    Struct localSettings = teaser.getLocalSettings();
    assertNotNull(localSettings);
    assertTrue(!localSettings.getProperties().isEmpty());

  }
}

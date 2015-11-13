package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProductReferenceHelper.class})
public class AssetChangesTest {

  private AssetChanges assetChanges;

  @Mock
  private SitesService sitesService;
  @Mock
  private Content content;
  @Mock
  private ContentSiteAspect contentSiteAspect;
  @Mock
  private Site site;

  @Before
  public void setup() throws Exception {
    mockStatic(ProductReferenceHelper.class);
    assetChanges = new AssetChanges();
    assetChanges.setSitesService(sitesService);
    assetChanges.afterPropertiesSet();
    when(sitesService.getContentSiteAspect(content)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);
  }

  @Test
  public void test() {
    // test the assetChanges is correctly filled
    when(ProductReferenceHelper.getExternalIds(content)).thenReturn(Arrays.asList("a", "b"));
    assetChanges.update(content);
    Collection<Content> contents = assetChanges.get("a", site);
    assertEquals(1, contents.size());
    assertEquals(content, contents.iterator().next());
    contents = assetChanges.get("b", site);
    assertEquals(content, contents.iterator().next());
    // test the assetChanges is correctly updated
    when(contentSiteAspect.getSite()).thenReturn(null);
    assetChanges.update(content);
    assertTrue(assetChanges.get("a", site).isEmpty());
    assertTrue(assetChanges.get("b", site).isEmpty());
  }
}
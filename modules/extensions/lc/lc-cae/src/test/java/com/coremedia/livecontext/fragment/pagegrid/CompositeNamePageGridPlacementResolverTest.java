package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.navigation.PagePrefixContentKeywords;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
public class CompositeNamePageGridPlacementResolverTest {

  private static String CART_BANNER = "cart-banner";

  private String CART_BANNER_PAGE_PREFIX = "livecontext.relatedpage.cart";
  private String CART_BANNER_PLACEMENT_NAME = "banner";

  @Mock
  private SettingsService settingsService;

  @Mock
  private CMChannel context;

  @Mock
  private CMChannel placementChannel;

  @Mock
  private CMChannel deadEndChannel;

  @Mock
  private CMArticle notAChannel;

  @Mock
  private PageGrid pageGrid;

  @Mock
  private PageGridPlacement targetPlacement;

  private CompositeNamePageGridPlacementResolver testling;

  @Before
  public void beforeEachTest() {
    testling = new CompositeNamePageGridPlacementResolver();
    testling.setSettingsService(settingsService);
  }

  @Test
  public void testNull() throws Exception {
    assertNull(testling.resolvePageGridPlacement(context, null));
    assertNull(testling.resolvePageGridPlacement(null, "valid-name"));
  }

  @Test
  public void testNonCompositeNames() throws Exception {
    assertNull(testling.resolvePageGridPlacement(context, "notacompositeplacementname"));
  }

  @Test
  public void testIncorrectCompositeNames() throws Exception {
    assertNull(testling.resolvePageGridPlacement(context, "-prefixmissing"));
    assertNull(testling.resolvePageGridPlacement(context, "namemissing-"));
  }

  @Test
  public void testNoPrefixMap() throws Exception {
    when(settingsService.settingAsMap(CART_BANNER_PAGE_PREFIX, String.class, Object.class, context)).thenReturn(null);

    assertNull(testling.resolvePageGridPlacement(context, CART_BANNER));
  }

  @Test
  public void testNoPageGridSetting() throws Exception {
    Map<String, Object> prefixMap = new HashMap<>();
    when(settingsService.settingAsMap(CART_BANNER_PAGE_PREFIX, String.class, Object.class, context)).thenReturn(prefixMap);

    assertNull(testling.resolvePageGridPlacement(context, CART_BANNER));
  }

  @Test
  public void testPageGridSettingOfWrongType() throws Exception {
    Map<String, Object> prefixMap = new HashMap<>();
    when(settingsService.settingAsMap(CART_BANNER_PAGE_PREFIX, String.class, Object.class, context)).thenReturn(prefixMap);
    prefixMap.put(PagePrefixContentKeywords.PAGEGRID_SETTING_NAME, notAChannel);

    assertNull(testling.resolvePageGridPlacement(context, CART_BANNER));
  }

  @Test
  public void testNoSuchPlacement() throws Exception {
    Map<String, Object> prefixMap = new HashMap<>();
    when(settingsService.settingAsMap(CART_BANNER_PAGE_PREFIX, String.class, Object.class, context)).thenReturn(prefixMap);
    prefixMap.put(PagePrefixContentKeywords.PAGEGRID_SETTING_NAME, placementChannel);
    when(placementChannel.getPageGrid()).thenReturn(pageGrid);
    when(pageGrid.getPlacementForName(CART_BANNER_PLACEMENT_NAME)).thenReturn(null);

    assertNull(testling.resolvePageGridPlacement(context, CART_BANNER));
  }

  @Test
  public void testCartBannerPlacementLookup() throws Exception {
    Map<String, Object> prefixMap = new HashMap<>();
    when(settingsService.settingAsMap(CART_BANNER_PAGE_PREFIX, String.class, Object.class, context)).thenReturn(prefixMap);
    prefixMap.put(PagePrefixContentKeywords.PAGEGRID_SETTING_NAME, placementChannel);
    when(placementChannel.getPageGrid()).thenReturn(pageGrid);
    when(pageGrid.getPlacementForName(CART_BANNER_PLACEMENT_NAME)).thenReturn(targetPlacement);

    assertEquals(targetPlacement, testling.resolvePageGridPlacement(context, CART_BANNER));
  }
}
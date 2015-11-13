package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompositeNameHelperTest {

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

  @Test
  public void testNull() {
    assertFalse(CompositeNameHelper.isCompositeName(null));
  }

  @Test
  public void testNonCompositeNames() {
    assertFalse(CompositeNameHelper.isCompositeName("notacompositeplacementname"));
  }

  @Test
  public void testIncorrectCompositeNames() {
    assertFalse(CompositeNameHelper.isCompositeName(CompositeNameHelper.PREFIX_SEPARATOR + "prefixmissing"));
    assertFalse(CompositeNameHelper.isCompositeName("namemissing" + CompositeNameHelper.PREFIX_SEPARATOR));
  }

  @Test
  public void testBadIdeaCompositeNames() {
    String compositeName = "prefix" + CompositeNameHelper.PREFIX_SEPARATOR + CompositeNameHelper.PREFIX_SEPARATOR + "name";
    assertTrue(CompositeNameHelper.isCompositeName(compositeName));
    assertEquals(CompositeNameHelper.PREFIX_QUALIFIER + "prefix", CompositeNameHelper.getPagePrefix(compositeName));
    assertEquals(CompositeNameHelper.PREFIX_SEPARATOR + "name", CompositeNameHelper.getPlacementName(compositeName));
  }

  @Test
  public void testNoPrefixMap() {
    String compositeName = "cart" + CompositeNameHelper.PREFIX_SEPARATOR + "banner";
    assertTrue(CompositeNameHelper.isCompositeName(compositeName));
    assertEquals(CompositeNameHelper.PREFIX_QUALIFIER + "cart", CompositeNameHelper.getPagePrefix(compositeName));
    assertEquals("banner", CompositeNameHelper.getPlacementName(compositeName));
  }
}
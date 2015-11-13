package com.coremedia.livecontext.imagemap;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.contentbeans.CMImageMapImpl;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class OutOfStockImageMapAreaFilterTest {

  public static final String HIDE_OUT_OF_STOCK_PRODUCTS = "hideOutOfStockProducts";
  public static final String OVERLAY = "overlay";
  private static final String LINKED_CONTENT = "linkedContent";
  public static final boolean AVAILABLE = true;
  public static final boolean NOT_AVAILABLE = false;

  private CMTeasable teasable;
  private CMProductTeaser productTeaser;
  private Product product;
  private ProductVariant productVariant;
  private OutOfStockImageMapAreaFilter areaFilter;
  private SettingsService settingsService;
  private CMImageMapImpl imageMap;
  private Map<String, Boolean> overlayConfiguration;


  @Before
  public void setup() {
    imageMap = mock(CMImageMapImpl.class);
    teasable = mock(CMTeasable.class);
    productTeaser = mock(CMProductTeaser.class);
    product = mock(Product.class);
    productVariant = mock(ProductVariant.class);
    settingsService = mock(SettingsService.class);

    areaFilter = new OutOfStockImageMapAreaFilter();
    areaFilter.setSettingsService(settingsService);

    overlayConfiguration = new HashMap<>();
    overlayConfiguration.put(HIDE_OUT_OF_STOCK_PRODUCTS, true);
  }

  @Test
  public void testProductInStock() {
    when(productTeaser.getProduct()).thenReturn(product);
    when(product.isAvailable()).thenReturn(AVAILABLE);
    when(settingsService.setting("overlay", Map.class, imageMap)).thenReturn(overlayConfiguration);

    List<Map<String, Object>> filteredResult = areaFilter.filter(getAreasWithProductTeaserAndTeasable(), imageMap);
    assertTrue(filteredResult.size() == 2);
  }

  @Test
  public void testProductOutOfStock() {
    when(productTeaser.getProduct()).thenReturn(product);
    when(product.isAvailable()).thenReturn(NOT_AVAILABLE);
    when(settingsService.setting("overlay", Map.class, imageMap)).thenReturn(overlayConfiguration);

    List<Map<String, Object>> filteredResult = areaFilter.filter(getAreasWithProductTeaserAndTeasable(), imageMap);
    assertTrue(filteredResult.size() == 1);
  }

  @Test
  public void testProductVariantInStock() {
    when(productTeaser.getProduct()).thenReturn(productVariant);
    when(productVariant.isAvailable()).thenReturn(AVAILABLE);
    when(settingsService.setting("overlay", Map.class, imageMap)).thenReturn(overlayConfiguration);

    List<Map<String, Object>> filteredResult = areaFilter.filter(getAreasWithProductTeaserAndTeasable(), imageMap);
    assertTrue(filteredResult.size() == 2);
  }

  @Test
  public void testProductVariantOutOfStock() {
    when(productTeaser.getProduct()).thenReturn(productVariant);
    when(productVariant.isAvailable()).thenReturn(NOT_AVAILABLE);
    when(settingsService.setting("overlay", Map.class, imageMap)).thenReturn(overlayConfiguration);

    List<Map<String, Object>> filteredResult = areaFilter.filter(getAreasWithProductTeaserAndTeasable(), imageMap);
    assertTrue(filteredResult.size() == 1);
  }

  @Test
  public void testDoNotHideOutOfStock() {
    overlayConfiguration.put(HIDE_OUT_OF_STOCK_PRODUCTS, false);
    when(settingsService.setting(OVERLAY, Map.class, imageMap)).thenReturn(overlayConfiguration);

    List<Map<String, Object>> filteredResult = areaFilter.filter(getAreasWithProductTeaserAndTeasable(), imageMap);
    assertTrue(filteredResult.size() == 2);
  }

  private List<Map<String, Object>> getAreasWithProductTeaserAndTeasable() {
    return getAreasFor(ImmutableList.of((Object) productTeaser, teasable));
  }

  private List<Map<String, Object>> getAreasFor(List<Object> contents) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object content : contents) {
      final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
      builder.put(LINKED_CONTENT, content);
      result.add(builder.build());
    }
    return result;
  }
}
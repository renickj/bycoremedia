package com.coremedia.livecontext.fragment;

import com.coremedia.cache.Cache;
import com.coremedia.cache.EvaluationException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryFragmentContextStrategyTest {
  @SuppressWarnings("ConstantConditions")
  @Test(expected = IllegalArgumentException.class)
  public void findNearestCategoryForNoExternalTechIdProvided() {
    testling.findNearestCategoryFor(null, defaultContext);
  }

  @Test(expected = IllegalArgumentException.class)
  public void findNearestCategoryForEmptyExternalTechIdProvided() {
    testling.findNearestCategoryFor("         ", defaultContext);
  }

  @SuppressWarnings("ConstantConditions")
  @Test(expected = IllegalArgumentException.class)
  public void findNearestCategoryForNoStoreContextProvided() {
    testling.findNearestCategoryFor(DEFAULT_EXTERNAL_TECH_ID, null);
  }

  @Test
  public void findNearestCategoryForSuccessfully() {
    Category result = testling.findNearestCategoryFor(DEFAULT_EXTERNAL_TECH_ID, defaultContext);

    assertEquals(defaultCategory, result);
    verify(catalogService, times(1)).findCategoryById(DEFAULT_ID);
  }

  @SuppressWarnings("ConstantConditions")
  @Test(expected = IllegalArgumentException.class)
  public void resolveContextNoShopNameProvided() {
    testling.resolveContext(null, DEFAULT_EXTERNAL_TECH_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveContextNoExternalTechIdProvided() {
    //noinspection ConstantConditions
    testling.resolveContext(site, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void resolveContextEmptyShopNameProvided() {
    testling.resolveContext(site, "   ");
  }

  @Test
  public void resolveContextSuccessfully() {
    LiveContextNavigation result = testling.resolveContext(site, DEFAULT_EXTERNAL_TECH_ID);
    assertEquals(defaultLiveContextNavigation, result);
  }

  @Test
  public void resolveContextSuccessfullyTestTheCache() throws InterruptedException {
    LiveContextNavigation result = testling.resolveContext(site, DEFAULT_EXTERNAL_TECH_ID);
    assertEquals(defaultLiveContextNavigation, result);

    // Resolve second time and assert that there was only one value inserted into the cache.
    result = testling.resolveContext(site, DEFAULT_EXTERNAL_TECH_ID);
    assertEquals(defaultLiveContextNavigation, result);
    assertEquals(1, cache.getNumberOfValues().get(Object.class.getName()).longValue());
    assertEquals(1, cache.getNumberOfInserts().get(Object.class.getName()).longValue());
    assertEquals(1, cache.getNumberOfEvaluations().get(Object.class.getName()).longValue());
    assertTrue(cache.getNumberOfRemoves().isEmpty());

    // Wait for the cache to invalidate the value and check the cache parameters accordingly
    waitUntilCacheIsEmpty((DEFAULT_CACHED_TIME_SECONDS + 5) * 1000);
    assertTrue(cache.getNumberOfValues().isEmpty());
    assertEquals(1, cache.getNumberOfRemoves().get(Object.class.getName()).longValue());

    // Resolve again and check the cache for the second evaluation.
    result = testling.resolveContext(site, DEFAULT_EXTERNAL_TECH_ID);
    assertEquals(defaultLiveContextNavigation, result);
    assertEquals(1, cache.getNumberOfValues().get(Object.class.getName()).longValue());
    assertEquals(2, cache.getNumberOfInserts().get(Object.class.getName()).longValue());
    assertEquals(2, cache.getNumberOfEvaluations().get(Object.class.getName()).longValue());
    assertEquals(1, cache.getNumberOfRemoves().get(Object.class.getName()).longValue());
  }

  @Test
  public void resolveContextNoCategoryFoundForGivenExternalTechId() {
    when(catalogService.findCategoryById(DEFAULT_ID)).thenReturn(null);
    try {
      testling.resolveContext(site, DEFAULT_EXTERNAL_TECH_ID);
      assertTrue("Expected an exception here.", false);
    } catch (EvaluationException e) {
      assertTrue(e.getCause() instanceof IllegalArgumentException);
    }
  }

  @Test
  public void resolveContextForStableId() {
    testling.setUseStableIds(true);
    when(catalogService.findCategoryById(Commerce.getCurrentConnection().getIdProvider().formatProductTechId(DEFAULT_EXTERNAL_ID))).thenReturn(defaultCategory);
    LiveContextNavigation result = testling.resolveContext(site, DEFAULT_EXTERNAL_ID);
    assertEquals(defaultLiveContextNavigation, result);
  }

  @SuppressWarnings("unchecked")
  @Test(expected = EvaluationException.class)
  public void resolveContextExceptionFromECommerceBackend() {
    when(catalogService.findCategoryById(DEFAULT_ID)).thenThrow(EvaluationException.class);
    testling.resolveContext(site, DEFAULT_EXTERNAL_TECH_ID);
  }

  @Before
  public void defaultSetup() {
    cache = new Cache("test-cache");

    testling = new CategoryFragmentContextStrategy();
    testling.setCache(cache);
    testling.setCachedInSeconds(DEFAULT_CACHED_TIME_SECONDS);
    testling.setLiveContextNavigationFactory(liveContextNavigationFactory);

    Commerce.setCurrentConnection(connection);
    when(connection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(connection.getCatalogService()).thenReturn(catalogService);
    when(connection.getIdProvider()).thenReturn(idProvider);

    when(storeContextProvider.findContextBySite(site)).thenReturn(defaultContext);
    when(catalogService.withStoreContext(defaultContext)).thenReturn(catalogService);
    when(idProvider.formatCategoryTechId(DEFAULT_EXTERNAL_TECH_ID)).thenReturn(DEFAULT_ID);
    when(catalogService.findCategoryById(DEFAULT_ID)).thenReturn(defaultCategory);
    when(liveContextNavigationFactory.createNavigation(defaultCategory, site)).thenReturn(defaultLiveContextNavigation);
    when(site.getId()).thenReturn(DEFAULT_SITE_ID);
    when(site.getName()).thenReturn(DEFAULT_SITE_ID);
  }

  private void waitUntilCacheIsEmpty(long maximumWaitMillis) throws InterruptedException {
    long start = System.currentTimeMillis();
    long duration = 0;
    while (duration <= maximumWaitMillis) {
      sleep(1000);
      if (cache.getNumberOfValues().isEmpty()) {
        return;
      }
      duration = System.currentTimeMillis() - start;
    }
  }

  private CategoryFragmentContextStrategy testling;
  private Cache cache;

  @Mock
  private CatalogService catalogService;

  @Mock
  private StoreContext defaultContext;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private Site site;

  @Mock
  private Category defaultCategory;

  @Mock
  private LiveContextNavigationFactory liveContextNavigationFactory;

  @Mock
  private LiveContextNavigation defaultLiveContextNavigation;

  @Mock
  private CommerceConnection connection;

  @Mock
  private CommerceIdProvider idProvider;

  private static final String DEFAULT_EXTERNAL_TECH_ID = "billion-year-bunker";
  private static final String DEFAULT_EXTERNAL_ID = "external-id";
  private static final String DEFAULT_ID = "ibm://catalog/category/techId:billion-year-bunker";
  private static final long DEFAULT_CACHED_TIME_SECONDS = 2;
  private static final String DEFAULT_SITE_ID = "Sirius-Cybernetics-Corporation";
}

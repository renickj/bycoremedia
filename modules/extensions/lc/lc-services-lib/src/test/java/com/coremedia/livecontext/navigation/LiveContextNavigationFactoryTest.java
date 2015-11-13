package com.coremedia.livecontext.navigation;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LiveContextNavigationFactoryTest {
  private LiveContextNavigationFactory testling;

  @Mock
  private CatalogService catalogService;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private LiveContextNavigationTreeRelation liveContextNavigationTreeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  @Mock
  private Content content;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private CommerceConnection connection;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    Commerce.setCurrentConnection(connection);

    when(connection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(connection.getCatalogService()).thenReturn(catalogService);

    testling = new LiveContextNavigationFactory();
    testling.setLiveContextNavigationTreeRelation(liveContextNavigationTreeRelation);
    testling.setSitesService(sitesService);
    when(site.getId()).thenReturn("deadbeef");
    when(sitesService.getContentSiteAspect(content)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);
  }

  @Test
  public void testCreateNavigationWithValidCategory() throws Exception {
    Category categoryToCreateFrom = mock(Category.class);
    LiveContextNavigation actual = testling.createNavigation(categoryToCreateFrom, site);
    assertNotNull("The returned Navigation must not be null", actual);
    Category categoryInNavigation = actual.getCategory();
    assertSame("The created LiveContextNavigation is expected to contain the category given by the first parameter of this method", categoryToCreateFrom, categoryInNavigation);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCreateNavigationWithCategoryIsNull() {
    testling.createNavigation(null, site);
  }

  @Test
  public void testCreateNavigationBySeoSegment() throws Exception {
    String existingSeoSegment = "existingSeoSegment";
    StoreContext storeContext = mock(StoreContext.class);
    Category category = mock(Category.class);

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);
    when(catalogService.findCategoryBySeoSegment(existingSeoSegment)).thenReturn(category);

    LiveContextNavigation actual = testling.createNavigationBySeoSegment(content, existingSeoSegment);
    assertNotNull("The returned Navigation must not be null", actual);
    assertSame("The created LiveContextNavigation is expected to contain the category given by the first parameter of this method", category, actual.getCategory());
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentContentWithoutContext() throws Exception {
    Content content = mock(Content.class);
    String existingSeoSegment = "existingSeoSegment";
    StoreContext storeContext = null;

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);

    testling.createNavigationBySeoSegment(content, existingSeoSegment);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentNoValidSeoSegment() throws Exception {
    Content content = mock(Content.class);
    String notExistingSeoSegment = "notExistingSeoSegment";
    StoreContext storeContext = mock(StoreContext.class);
    Category category = null;

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);
    when(catalogService.findCategoryBySeoSegment(notExistingSeoSegment)).thenReturn(category);

    testling.createNavigationBySeoSegment(content, notExistingSeoSegment);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentSeoIsNull() throws Exception {
    Content content = mock(Content.class);
    String nullSeoSegment = null;
    testling.createNavigationBySeoSegment(content, nullSeoSegment);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testCreateNavigationBySeoSegmentContentIsNull() throws Exception {
    Content content = null;
    String nullSeoSegment = null;
    testling.createNavigationBySeoSegment(content, nullSeoSegment);
  }

  @Test (expected = InvalidContextException.class)
  public void testCreateNavigationBySeoSegmentInvalidContextException() throws Exception {
    Content invalidContent = mock(Content.class);
    String anySeo = "anySeo";
    when(storeContextProvider.findContextByContent(invalidContent)).thenThrow(InvalidContextException.class);
    testling.createNavigationBySeoSegment(invalidContent, anySeo);
  }

  @Test (expected = CommerceException.class)
  public void testCreateNavigationBySeoSegmentCommerceException() throws Exception {
    Content content = mock(Content.class);
    String anySeoSegment = "anySeoSegment";
    StoreContext storeContext = mock(StoreContext.class);

    when(storeContextProvider.findContextByContent(content)).thenReturn(storeContext);
    when(catalogService.findCategoryBySeoSegment(anySeoSegment)).thenThrow(CommerceException.class);

    testling.createNavigationBySeoSegment(content, anySeoSegment);
  }
}

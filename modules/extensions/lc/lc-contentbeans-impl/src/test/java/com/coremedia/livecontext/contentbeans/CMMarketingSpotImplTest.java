package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.CategoryInSite;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMMarketingSpotImplTest {

  private static final String MY_MARKETING_SPOT_NAME = "myMarketingSpotName";

  @Mock
  private CommerceConnection connection;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private Content content;

  @Mock
  private MarketingSpot marketingSpot;

  @Mock
  private MarketingSpotService marketingSpotService;

  @Mock
  private LiveContextNavigationFactory liveContextNavigationFactory;

  @Mock
  private SitesService sitesService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private Site site;

  @Mock
  private CommerceObject commerceObject;

  @Mock
  private Product product;

  @Mock
  private Category category;

  @Mock
  private ProductInSite productInSite;

  @Mock
  private CategoryInSite categoryInSite;

  private CMMarketingSpotImpl testling;

  @Before
  public void init() {
    initMocks(this);
    Commerce.setCurrentConnection(connection);

    when(connection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(storeContextProvider.findContextByContent(any(Content.class))).thenReturn(storeContext);
    when(connection.getCommerceBeanFactory()).thenReturn(commerceBeanFactory);
    when(connection.getMarketingSpotService()).thenReturn(marketingSpotService);

    when(marketingSpotService.findMarketingSpotById(anyString())).thenReturn(marketingSpot);
    when(marketingSpotService.withStoreContext(storeContext)).thenReturn(marketingSpotService);
    when(marketingSpot.getName()).thenReturn(MY_MARKETING_SPOT_NAME);

    when(content.getString(CMMarketingSpotImpl.EXTERNAL_ID)).thenReturn("myExternalId");

    testling = new TestCMMarketingSpotImpl();
    testling.setSitesService(sitesService);
    testling.setLiveContextNavigationFactory(liveContextNavigationFactory);
    when(sitesService.getContentSiteAspect(any(Content.class))).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);
  }

  @Test
  public void testGetItems() {
    assertEquals("There should be no item", 0, testling.getItems().size());

    List<CommerceObject> entities = new ArrayList<>();
    entities.add(commerceObject);
    entities.add(product);
    entities.add(category);
    when(marketingSpot.getEntities()).thenReturn(entities);
    when(liveContextNavigationFactory.createProductInSite(product, site)).thenReturn(productInSite);
    when(liveContextNavigationFactory.createCategoryInSite(category, site)).thenReturn(categoryInSite);

    List<CommerceObject> items = testling.getItems();
    assertEquals("There should be 3 items", 3, items.size());
    assertEquals(commerceObject, items.get(0));
    assertEquals(productInSite, items.get(1));
    assertEquals(categoryInSite, items.get(2));
  }

  @Test
  public void testGetTeaserTitle() {
    //teaser title is set
    String myTeaserTitle = "myTeaserTitle";
    when(content.getString(CMTeasable.TEASER_TITLE)).thenReturn(myTeaserTitle);
    assertEquals("the teaser title of the CMMarketingSpot is the same as the teaserTitle string property of the content",
            myTeaserTitle, testling.getTeaserTitle());

    //teaser title is not set
    when(content.getString(CMTeasable.TEASER_TITLE)).thenReturn(null);
    assertEquals("the teaser title of the CMMarketingSpot is the same as the teaserTitle string property of the content",
            MY_MARKETING_SPOT_NAME, testling.getTeaserTitle());

  }

  private class TestCMMarketingSpotImpl extends CMMarketingSpotImpl {
    @Override
    public Content getContent() {
      return content;
    }
  }
}
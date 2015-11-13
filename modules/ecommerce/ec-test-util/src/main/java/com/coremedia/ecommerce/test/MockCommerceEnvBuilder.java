package com.coremedia.ecommerce.test;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityService;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.pricing.PriceService;
import com.coremedia.livecontext.ecommerce.search.SearchService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextBuilder;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import org.apache.commons.lang3.LocaleUtils;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Currency;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MockCommerceEnvBuilder {

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private CatalogService catalogService;

  @Mock
  private MarketingSpotService marketingSpotService;

  @Mock
  private PriceService priceService;

  @Mock
  private SegmentService segmentService;

  @Mock
  private WorkspaceService workspaceService;

  @Mock
  private AvailabilityService availabilityService;

  @Mock
  private UserService userService;

  @Mock
  private UserSessionService userSessionService;

  @Mock
  private CartService cartService;

  @Mock
  private AssetService assetService;

  @Mock
  private AssetUrlProvider assetUrlProvider;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  @Mock
  private SearchService searchService;


  public static MockCommerceEnvBuilder create() {
    return new MockCommerceEnvBuilder();
  }

  public BaseCommerceConnection setupEnv() {
    initMocks(this);
    StoreContext storeContext = StoreContextBuilder.create().build();

    storeContext.put(StoreContextBuilder.CONFIG_ID, "aurora");
    storeContext.put(StoreContextBuilder.STORE_ID, "10001");
    storeContext.put(StoreContextBuilder.STORE_NAME, "aurora");
    storeContext.put(StoreContextBuilder.CATALOG_ID, "10051");
    storeContext.put(StoreContextBuilder.LOCALE, LocaleUtils.toLocale("en_US"));
    storeContext.put(StoreContextBuilder.CURRENCY, Currency.getInstance("USD"));

    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);
    when(storeContextProvider.findContextBySite((Site) anyObject())).thenReturn(storeContext);

    UserContext userContext = UserContextBuilder.create().build();
    when(userContextProvider.getCurrentContext()).thenReturn(userContext);
    when(userContextProvider.createContext((HttpServletRequest) anyObject(), anyString())).thenReturn(userContext);

    BaseCommerceConnection commerceConnection = new BaseCommerceConnection();
    commerceConnection.setIdProvider(new BaseCommerceIdProvider("vendor"));
    commerceConnection.setStoreContextProvider(storeContextProvider);
    commerceConnection.setUserContextProvider(userContextProvider);
    commerceConnection.setCatalogService(catalogService);
    commerceConnection.setCartService(cartService);
    commerceConnection.setMarketingSpotService(marketingSpotService);
    commerceConnection.setPriceService(priceService);
    commerceConnection.setSegmentService(segmentService);
    commerceConnection.setWorkspaceService(workspaceService);
    commerceConnection.setAvailabilityService(availabilityService);
    commerceConnection.setUserService(userService);
    commerceConnection.setUserSessionService(userSessionService);
    commerceConnection.setCommerceBeanFactory(commerceBeanFactory);
    commerceConnection.setAssetService(assetService);
    commerceConnection.setAssetUrlProvider(assetUrlProvider);
    commerceConnection.setStoreContext(storeContext);
    commerceConnection.setUserContext(userContext);
    Commerce.setCurrentConnection(commerceConnection);

    return commerceConnection;
  }

}

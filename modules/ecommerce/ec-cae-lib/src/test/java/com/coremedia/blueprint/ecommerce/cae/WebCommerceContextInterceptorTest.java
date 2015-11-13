package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebCommerceContextInterceptorTest {

  @Mock
  private SiteResolver siteLinkHelper;

  @Mock
  private Site site;

  @Mock
  private HttpServletRequest request;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private CommerceConnection connection;

  private StoreContext storeContext;

  private WebCommerceContextInterceptor testling = new WebCommerceContextInterceptor();

  @Before
  public void setup() {

    connection = MockCommerceEnvBuilder.create().setupEnv();

    testling.setSiteResolver(siteLinkHelper);
    testling.setInitUserContext(false);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setPreview(false);
    testling.afterPropertiesSet();

    when(siteLinkHelper.findSiteBySegment("helios")).thenReturn(site);
  }

  // --- test base class features -----------------------------------

  @Test
  public void testPreHandle() {
    String path = "/helios";
    when(request.getPathInfo()).thenReturn(path);
    when(testling.getSite(request, path)).thenReturn(site);
    testling.preHandle(request, null, null);
    verify(commerceConnectionInitializer).init(any(Site.class));
  }

  @Test
  public void testNoopPreHandle() {
    String path = "/nosite";
    when(request.getPathInfo()).thenReturn(path);
    when(testling.getSite(request, path)).thenReturn(null);
    testling.preHandle(request, null, null);
    verify(connection.getStoreContextProvider(), never()).setCurrentContext(any(StoreContext.class));
  }
}

package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.PREVIEW_DATE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.WORKSPACE_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCommerceContextInterceptorTest {

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private HttpServletRequest request;

  @Mock
  private SiteResolver siteResolver;

  private AbstractCommerceContextInterceptor testling;


  // --- setup ------------------------------------------------------

  @Before
  public void setup() {
    MockCommerceEnvBuilder.create().setupEnv();
    testling = new NonAbstractTestling();

    // Set all @Required properties to make it afterPropertiesSet safe.
    // Tests may override, so do not call afterPropertiesSet yet.
    testling.setSiteResolver(siteResolver);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
  }


  // --- tests ------------------------------------------------------

  @Test
  public void testNormalizePath() {
    String path = "/helios";
    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(path);
    assertEquals("changed path", path, normalizedPath);
  }

  @Test
  public void testNormalizeDynamicFragmentPath() {
    String path = "/cart/helios/action/cart";
    String dynpath = "/" + UriConstants.Prefixes.PREFIX_DYNAMIC + path;
    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(dynpath);
    assertEquals("path not normalized", path, normalizedPath);
  }

  @Test
  public void testInitStoreContextProvider() {
    // This does not work with the @Mock request.
    MockHttpServletRequest request = new MockHttpServletRequest();
    testling.afterPropertiesSet();
    assertFalse("StoreContext must not have been initialized yet", testling.isStoreContextInitialized(request));
    testling.initStoreContext(site, request);
    assertTrue("StoreContext must have been initialized", testling.isStoreContextInitialized(request));
  }

  @Test
  public void testHookExecution() {
    // This does not work with the @Mock request.
    MockHttpServletRequest request = new MockHttpServletRequest();
    CommerceContextInterceptorHooks hooks = mock(CommerceContextInterceptorHooks.class);
    testling.setHooks(Collections.singletonList(hooks));
    testling.afterPropertiesSet();
    testling.initStoreContext(site, request);
    verify(hooks, times(1)).enhanceStoreContext(site, request);
    verify(hooks, times(1)).updateStoreContext(request);
  }

  @Test
  public void testInitStoreContextProviderWithPreviewParameters() {
    when(request.getParameter(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE)).thenReturn("12-06-2014 13:00 Europe/Berlin");
    when(request.getParameter(AbstractCommerceContextInterceptor.QUERY_PARAMETER_WORKSPACE_ID)).thenReturn("aWorkspaceId");
    testling.setPreview(true);
    testling.afterPropertiesSet();

    testling.initStoreContext(site, request);

    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    assertNotNull(currentConnection);
    StoreContext context = currentConnection.getStoreContext();
    assertNotNull(context);
    assertNotNull(context.get(PREVIEW_DATE));
    assertNotNull(context.get(WORKSPACE_ID));
  }


  // --- internal ---------------------------------------------------

  private class NonAbstractTestling extends AbstractCommerceContextInterceptor {
    @Nullable
    @Override
    protected Site getSite(HttpServletRequest request, String normalizedPath) {
      throw new UnsupportedOperationException("This dummy impl is not sufficient for your test.");
    }
  }
}

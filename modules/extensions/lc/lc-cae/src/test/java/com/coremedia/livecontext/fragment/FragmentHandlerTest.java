package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.layout.ContentBeanBackedPageGridPlacement;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMSite;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.pagegrid.PageGridPlacementResolver;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FragmentHandlerTest {

  @Test
  public void testPlacementFound() {
    when(channelBean.getContext()).thenReturn(channelBean);
    when(pageGridPlacementResolver.resolvePageGridPlacement(channelBean, PLACEMENT)).thenReturn(placement);
    ModelAndView result = testling.createFragmentModelAndViewForPlacementAndView(channelBean, PLACEMENT, VIEW, channelBean);
    assertNotNull(result);
  }

  @Test
  public void testInvalidChannel() {
    when(validationService.validate(channelBean)).thenReturn(false);
    ModelAndView result = testling.createFragmentModelAndViewForPlacementAndView(channelBean, PLACEMENT, VIEW, channelBean);

    HttpError error = (HttpError) result.getModel().get(HandlerHelper.MODEL_ROOT);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, error.getErrorCode());
    when(validationService.validate(channelBean)).thenReturn(true);
  }

  @Test
  public void testNoPlacementFound() {
    when(channelBean.getContext()).thenReturn(channelBean);
    when(pageGridPlacementResolver.resolvePageGridPlacement(channelBean, PLACEMENT)).thenReturn(null);
    ModelAndView result = testling.createFragmentModelAndViewForPlacementAndView(channelBean, PLACEMENT, VIEW, channelBean);

    HttpError error = (HttpError) result.getModel().get(HandlerHelper.MODEL_ROOT);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, error.getErrorCode());
  }

  @Test
  public void testCreateModelAndViewForPlacementAndView() {
    when(pageGridPlacementResolver.resolvePageGridPlacement(channelBean, PLACEMENT)).
            thenReturn(contentBeanBackedPageGridPlacement);
    when(contentBeanBackedPageGridPlacement.getNavigation()).thenReturn(placementChannelBean);
    ModelAndView modelAndView = testling.createModelAndViewForPlacementAndView(channelBean, PLACEMENT, null);
    PageImpl page = (PageImpl) modelAndView.getModel().get("cmpage");
    assertEquals("The specific placement channel is taken for the page", placementChannelBean, page.getContent());

  }

  @Test
  public void testCreateModelAndViewForPlacementAndView_NoPlacementChannelAvailable() {
    when(pageGridPlacementResolver.resolvePageGridPlacement(channelBean, PLACEMENT)).
            thenReturn(contentBeanBackedPageGridPlacement);
    when(contentBeanBackedPageGridPlacement.getNavigation()).thenReturn(null);
    ModelAndView modelAndView = testling.createModelAndViewForPlacementAndView(channelBean, PLACEMENT, null);
    PageImpl page = (PageImpl) modelAndView.getModel().get("cmpage");
    assertEquals("The given channel is taken for the page because the placement does not provide a navigation",
            channelBean, page.getContent());

  }

  @Before
  public void setUp() {
    testling = new ExternalPageFragmentHandler();
    testling.setPageGridPlacementResolver(pageGridPlacementResolver);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setSitesService(sitesService);
    testling.setValidationService(validationService);

    Commerce.setCurrentConnection(connection);
    when(connection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(connection.getStoreContext()).thenReturn(storeContext);

    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);
    when(storeContext.getSiteId()).thenReturn(SITE_ID);
    when(sitesService.getSite(SITE_ID)).thenReturn(site);

    when(site.getSiteIndicator()).thenReturn(siteIndicator);
    when(siteIndicator.getLink(CMSite.ROOT)).thenReturn(rootChannel);
    when(request.getAttribute(SITE_ATTRIBUTE_NAME)).thenReturn(site);

    when(validationService.validate(any())).thenReturn(true);

    FragmentContext context = new FragmentContext();
    context.setFragmentRequest(true);
    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;";
    this.fragmentParameters = FragmentParametersFactory.create(url);
    context.setParameters(this.fragmentParameters);
    when(request.getAttribute(FragmentContextProvider.FRAGMENT_CONTEXT_ATTRIBUTE)).thenReturn(context);

    when(channelBean.getContent()).thenReturn(rootChannel);
    when(rootChannel.getType()).thenReturn(rootChannelType);
    when(rootChannelType.getName()).thenReturn("contentTypeName");
    when(contentBeanFactory.createBeanFor(rootChannel, CMChannel.class)).thenReturn(channelBean);

    when(placementChannel.getType()).thenReturn(placementChannelType);
    when(placementChannelType.getName()).thenReturn("contentTypeName");
    when(placementChannelBean.getContent()).thenReturn(placementChannel);
  }

  private FragmentHandler testling;
  private FragmentParameters fragmentParameters;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private StoreContext storeContext;

  @Mock
  private SitesService sitesService;

  @Mock
  private PageGridPlacementResolver pageGridPlacementResolver;

  @Mock
  private Site site;

  @Mock
  private Content rootChannel;

  @Mock
  private ContentType rootChannelType;

  @Mock
  private Content siteIndicator;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private PageGridPlacement placement;

  @Mock
  private ContentBeanBackedPageGridPlacement contentBeanBackedPageGridPlacement;

  @Mock
  private Content placementChannel;

  @Mock
  private ContentType placementChannelType;

  @Mock
  private CMChannel placementChannelBean;

  @Mock
  private CMChannel channelBean;

  @Mock
  private Linkable linkableBean;

  @Mock
  private Content linkable;

  @Mock
  private HttpServletResponse response;

  @Mock
  private HttpServletRequest request;

  @Mock
  private CommerceConnection connection;

  @Mock
  private ValidationService validationService;

  private final static String STORE_ID = "10001";
  private final static String SITE_ID = "123456789";
  private final static String PLACEMENT = "main";
  private final static String VIEW = "asTeaser";
  private final static Locale LOCALE = Locale.CANADA;
  private final static String SITE_ATTRIBUTE_NAME = SiteHelper.class.getName() + "site";
}

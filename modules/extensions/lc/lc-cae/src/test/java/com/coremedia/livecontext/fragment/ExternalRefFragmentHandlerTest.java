package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.fragment.resolver.ExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.LinkableAndNavigation;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalRefFragmentHandlerTest extends FragmentHandlerTestBase<ExternalRefFragmentHandler> {

  @Mock
  private CMChannel channelBean;

  @Test
  public void testWithoutMatchingResolver() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    ModelAndView result = getTestling().createModelAndView(params, null);
    assertErrorPage(result, HttpServletResponse.SC_NOT_FOUND);
    verifyDefault();
  }

  @Test
  public void testResolverReturnsNull() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);

    ModelAndView result = getTestling().createModelAndView(params, request);
    assertErrorPage(result, HttpServletResponse.SC_NOT_FOUND);
    verifyDefault();
  }

  @Test
  public void testResolverReturnsNoLinkable() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);
    when(contentCapIdExternalReferenceResolver.resolveExternalRef(params, site)).thenReturn(new LinkableAndNavigation(null, null));

    ModelAndView result = getTestling().createModelAndView(params, request);
    assertErrorPage(result, HttpServletResponse.SC_NOT_FOUND);
    verifyDefault();
  }

  @Test
  public void testResolverReturnsNotALinkable() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    when(linkableAndNavigation.getNavigation()).thenReturn(null);
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);
    when(contentCapIdExternalReferenceResolver.resolveExternalRef(params, site)).thenReturn(linkableAndNavigation);

    ModelAndView result = getTestling().createModelAndView(params, request);
    assertErrorPage(result, HttpServletResponse.SC_NOT_FOUND);
    verifyDefault();
  }

  @Test
  public void testSiteCheckFails() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);
    when(contentCapIdExternalReferenceResolver.resolveExternalRef(params, site)).thenReturn(linkableAndNavigation);
    when(contentBeanFactory.createBeanFor(linkable, Navigation.class)).thenReturn(getRootChannelBean());
    when(getSitesService().getContentSiteAspect(linkable)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.isPartOf(site)).thenReturn(false);

    ModelAndView modelAndView = getTestling().createModelAndView(params, request);
    assertNotNull(modelAndView);
    HttpError httpError = (HttpError) modelAndView.getModel().get("self");
    assertEquals(400, httpError.getErrorCode());
  }

  @Test
  public void testResolverReturnsLinkable() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);
    when(contentCapIdExternalReferenceResolver.resolveExternalRef(params, site)).thenReturn(linkableAndNavigation);
    when(contentBeanFactory.createBeanFor(linkable, Navigation.class)).thenReturn(getRootChannelBean());
    when(getSitesService().getContentSiteAspect(linkable)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.isPartOf(site)).thenReturn(true);
    when(contentBeanFactory.createBeanFor(linkable, CMChannel.class)).thenReturn(getRootChannelBean());

    ModelAndView result = getTestling().createModelAndView(params, request);
    assertNotNull(result);
    verifyDefault();
  }

  @Test
  public void testIgnorePlacementIfTheLinkableIsDifferentThanTheNavigation() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);
    when(contentCapIdExternalReferenceResolver.resolveExternalRef(params, site)).thenReturn(linkableAndNavigation);
    when(getSitesService().isContentInSite(site, navigationDoc)).thenReturn(true);
    when(getSitesService().isContentInSite(site, linkable)).thenReturn(true);

    TestExternalRefFragmentHandler testling = (TestExternalRefFragmentHandler) getTestling();
    testling.createModelAndView(params, request);
//    verify(fragmentPageHandler).createModelAndViewForLinkable(navigationDoc, linkable, VIEW);
    assertTrue(testling.createModelAndViewForLinkableIsCalled);
  }

  @Test
  public void testIgnorePlacementIfTheLinkableIsDifferentThanTheNavigationDefaultViewGiven() {
    FragmentParameters params = getFragmentParametersWithExternalRef(EXTERNAL_REF);
    params.setView("default");
    when(contentCapIdExternalReferenceResolver.include(params)).thenReturn(true);
    when(contentCapIdExternalReferenceResolver.resolveExternalRef(params, site)).thenReturn(linkableAndNavigation);
    when(getSitesService().isContentInSite(site, navigationDoc)).thenReturn(true);
    when(getSitesService().isContentInSite(site, linkable)).thenReturn(true);

    TestExternalRefFragmentHandler testling = (TestExternalRefFragmentHandler) getTestling();
    testling.createModelAndView(params, request);
//    verify(fragmentPageHandler).createModelAndViewForLinkable(navigationDoc, linkable, VIEW);
    assertTrue(testling.createModelAndViewForLinkableIsCalled);
  }

  @Test
  public void testDefaultModelAndView() {
    when(channelBean.getContext()).thenReturn(channelBean);
    when(pageGridPlacementResolver.resolvePageGridPlacement(channelBean, PLACEMENT)).thenReturn(null);

    when(contentBeanFactory.createBeanFor(linkable, Linkable.class)).thenReturn(linkableBean);
    when(contentBeanFactory.createBeanFor(rootChannel, Navigation.class)).thenReturn(channelBean);

    ModelAndView result = getTestling().createModelAndViewForLinkable(rootChannel, linkable, VIEW);
    assertNotNull(result);
  }

  @Override
  protected ExternalRefFragmentHandler createTestling() {
    return new TestExternalRefFragmentHandler();
/*
    when(fragmentPageHandler.createFragmentModelAndViewForPlacementAndView(navigation, PLACEMENT, VIEW, rootChannelBean)).thenReturn(modelAndView);
    when(fragmentPageHandler.createFragmentModelAndView(navigation, VIEW, rootChannelBean)).thenReturn(modelAndView);
    when(fragmentPageHandler.createModelAndViewForPlacementAndView(rootChannelBean, PLACEMENT, VIEW)).thenReturn(modelAndView);
*/
  }

  @Before
  public void defaultSetup() {
    super.defaultSetup();

    List<ExternalReferenceResolver> resolvers = new ArrayList<>();
    resolvers.add(contentCapIdExternalReferenceResolver);
    resolvers.add(contentNumericIdWithChannelIdExternalReferenceResolver);
    resolvers.add(contentNumericIdExternalReferenceResolver);
    resolvers.add(contentPathExternalReferenceResolver);

    when(contentBeanFactory.createBeanFor(linkable, Linkable.class)).thenReturn(linkableBean);
    when(linkableBean.getTitle()).thenReturn(TITLE);
    when(linkableBean.getContentId()).thenReturn(CMCONTEXT_ID);
    when(linkableBean.getContent()).thenReturn(linkable);
    when(linkable.getType()).thenReturn(cmContextContentType);

    getTestling().setExternalReferenceResolvers(resolvers);
//    getTestling().setSitesService(getSitesService());
  }

  private FragmentParameters getFragmentParametersWithExternalRef(String ref) {
    String url = "http://localhost:40081/blueprint/servlet/service/fragment/" + STORE_ID + "/" + LOCALE_STRING + "/params;";
    FragmentParameters  params = FragmentParametersFactory.create(url);
    params.setView(VIEW);
    params.setPlacement(PLACEMENT);
    params.setExternalReference(ref);
    return params;
  }

  private class TestExternalRefFragmentHandler extends ExternalRefFragmentHandler {

    public boolean createModelAndViewForLinkableIsCalled = false;

    @Nonnull
    @Override
    protected ModelAndView createModelAndViewForLinkable(@Nonnull Content channel, @Nonnull Content child, String view) {
      createModelAndViewForLinkableIsCalled = true;
      return modelAndView;
    }
  }

}

package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalPageFragmentHandlerTest extends FragmentHandlerTestBase<ExternalPageFragmentHandler> {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private CMExternalChannel aboutUsPage;
  @Mock
  private Content aboutUsContent;
  @Mock
  private TreeRelation<Content> treeRelation;

  @Before
  public void defaultSetup() {
    super.defaultSetup();
    ExternalPageContextStrategy contextStrategy = new ExternalPageContextStrategy();
    contextStrategy.setCache(new Cache("testCache"));
    contextStrategy.setSitesService(getSitesService());
    contextStrategy.setTreeRelation(treeRelation);
    contextStrategy.setContentBeanFactory(contentBeanFactory);

    getTestling().setContextStrategy(contextStrategy);

    when(validationService.validate(anyObject())).thenReturn(true);
    doReturn(Collections.singletonList(aboutUsPage)).when(getRootChannelBean()).getChildren();
    when(aboutUsPage.isCatalogPage()).thenReturn(false);
    when(aboutUsPage.getExternalId()).thenReturn("aboutUs");
    when(aboutUsPage.getContext()).thenReturn(aboutUsPage);
    when(aboutUsPage.getContent()).thenReturn(aboutUsContent);
    when(aboutUsContent.getString("externalId")).thenReturn("aboutUs");
    when(contentBeanFactory.createBeanFor(aboutUsContent)).thenReturn(aboutUsPage);

    when(treeRelation.pathToRoot(any(Content.class))).thenReturn(singletonList(rootChannel));
  }

  @Override
  protected ExternalPageFragmentHandler createTestling() {
    return new ExternalPageFragmentHandler() {
      @Override
      protected Page asPage(Navigation context, Linkable content) {
        return new PageImpl(context, content, false, getSitesService(), null);
      }
    };
  }

  @Test
  public void testRootChannelCanBeResolved() {
    FragmentParameters params = getFragmentParametersWithExternalPage("");
    ModelAndView modelAndView = getTestling().createModelAndView(params, request);
    assertEquals(getRootChannelBean(), ((PageImpl)modelAndView.getModel().get("cmpage")).getContent());
  }

  @Test
  public void testAboutUsPageCanBeResolved() {
    CMExternalChannelCacheKeyTest.channelsFulfilling(Collections.singleton(aboutUsPage.getContent()), contentRepository);

    FragmentParameters params = getFragmentParametersWithExternalPage("aboutUs");
    ModelAndView modelAndView = getTestling().createModelAndView(params, request);
    assertEquals(aboutUsPage, ((PageImpl)modelAndView.getModel().get("cmpage")).getContent());
  }

  @Test
  public void testRootChannelFallback() {
    FragmentParameters params = getFragmentParametersWithExternalPage("unknown-page-id");
    ModelAndView modelAndView = getTestling().createModelAndView(params, request);
    assertEquals(getRootChannelBean(), ((PageImpl)modelAndView.getModel().get("cmpage")).getContent());
  }

  private FragmentParameters getFragmentParametersWithExternalPage(String pageId) {
    String url = "http://localhost:40081/blueprint/servlet/service/fragment/" + STORE_ID + "/" + LOCALE_STRING + "/params;";
    FragmentParameters  params = FragmentParametersFactory.create(url);
    params.setView("default");
    params.setPlacement(PLACEMENT);
    params.setPageId(pageId);
    return params;
  }

}

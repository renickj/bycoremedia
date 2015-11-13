package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.cae.handlers.HandlerBaseTest;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.beans.ContentBeanIdConverter;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationHandlerTest extends HandlerBaseTest {
  @Test
  public void testShowPageForUnknownActionWhereTheActionNameEqualsTheVanityName() throws Exception {
    when(contentLinkBuilder.getVanityName(actionContent)).thenReturn("unknownAction");
    when(action.getSegment()).thenReturn("unknownAction");
    when(getUrlPathFormattingHelper().tidyUrlPath("unknownAction")).thenReturn("unknownAction");

    assertModelWithPageBean(handleRequest('/'+ PREFIX_DYNAMIC+"/auth/root/4711/unknownAction"), rootNavigation, action);
  }

  @Test
  public void testNotFoundForUnknownActionWithSegmentMismatch() throws Exception {
    when(action.getSegment()).thenReturn(null);

    assertNotFound("segment mismatch for unknown action", handleRequest('/'+ PREFIX_DYNAMIC+"/auth/root/4711/unknownAction"));
  }

  @Test
  public void testNotFoundForUnknownRootSegment() throws Exception {
    ImmutableMap<String, String> autocompleteParams = ImmutableMap.of(
            "rootNavigationId", ID,
            "query", "test"
    );

    assertNotFound("unknown root segment", handleRequest('/'+ PREFIX_DYNAMIC+"/auth/unknown/4711/login"));
    assertNotFound("unknown root segment", handleRequest(newRequest('/'+ PREFIX_DYNAMIC+"/auth/unknown/4711/login", autocompleteParams)));
    assertNotFound("unknown root segment", handleRequest('/'+ PREFIX_DYNAMIC+"/auth/unknown/4711/unknownAction"));
  }

  @Test
  public void testGenerateActionLink() {
    when(contentLinkBuilder.getVanityName(actionContent)).thenReturn(SOME_ACTION);
    assertEquals(
            '/'+ PREFIX_DYNAMIC+"/auth/root/4711/" + SOME_ACTION,
            formatLink(authenticationState, null, false, ImmutableMap.<String, Object>of("action", SOME_ACTION)));
  }

  @Test
  public void testGenerateGenericActionLink() {
    when(contentLinkBuilder.getVanityName(actionContent)).thenReturn(SOME_ACTION);
    assertEquals('/'+ PREFIX_DYNAMIC+"/auth/root/4711/" + SOME_ACTION, formatLink(authenticationState, null, false));
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();

    AuthenticationHandler testling = new AuthenticationHandler();
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    testling.setContentBeanIdConverter(converter);
    testling.setContextHelper(getContextHelper());
    testling.setUrlPathFormattingHelper(getUrlPathFormattingHelper());
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setSitesService(getSitesService());
    testling.setContentLinkBuilder(contentLinkBuilder);

    registerHandler(testling);

    when(authenticationState.getAction()).thenReturn(action);
    when(navigationSegmentsUriHelper.parsePath(eq(asList(CONTEXT_NAME)))).thenReturn(rootNavigation);
    when(converter.convert(action)).thenReturn(ID);
    when(getIdContentBeanConverter().convert(ID)).thenReturn(action);
    when(contentBeanFactory.createBeanFor(actionContent)).thenReturn(action);
    when(action.getSegment()).thenReturn(SOME_ACTION);
    when(action.getSegment()).thenReturn(SOME_ACTION);
    when(getUrlPathFormattingHelper().tidyUrlPath(SOME_ACTION)).thenReturn(SOME_ACTION);
    when(navigationSegmentsUriHelper.getPathList(rootNavigation)).thenReturn(asList(CONTEXT_NAME));
    when(getIdActionDocConverter().convert("4711")).thenReturn(action);
    when(action.getContent()).thenReturn(actionContent);
    when(actionContent.getType()).thenReturn(actionContentType);

    setContextFor(action, rootNavigation);
  }

  private static final String ID = "4711";
  private static final String SOME_ACTION = "someAction";

  @Mock
  private Content actionContent;

  @Mock
  private ContentType actionContentType;

  @Mock
  private CMAction action;

  @Mock
  private AuthenticationState authenticationState;

  @Mock
  private CMLinkable linkable;

  @Mock
  private CMNavigation rootNavigation;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private ContentBeanIdConverter converter;

  @Mock
  private ContentLinkBuilder contentLinkBuilder;

  private static final String CONTEXT_NAME = "root";
}

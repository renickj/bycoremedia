package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.base.links.VanityUrlMapper;
import com.coremedia.blueprint.base.navigation.context.finder.TopicpageContextFinder;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.cap.content.Content;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPageHandlerTest extends PageHandlerBaseTest<DefaultPageHandler> {

  @Test
  public void testHandleRequestInternalNavigationPathNull() {
    assertNotFound("Should not be found", testling.handleRequestInternal(defaultActionBean, DEFAULT_CONTENT_ID,
            null, DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void handleRequestInternalInvalidSegment() {
    when(contentLinkBuilder.getVanityName(defaultActionContent)).thenReturn("invalid segment");
    when(defaultActionBean.getSegment()).thenReturn("invalid segment");
    assertNotFound("Should not be found.", testling.handleRequestInternal(defaultActionBean, DEFAULT_CONTENT_ID,
            DEFAULT_NAVIGATION_PATH, DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void handleRequestInternalNoNavigationFound() {
    when(contentLinkBuilder.getVanityName(defaultActionContent)).thenReturn(DEFAULT_VANITY_NAME);
    doReturn(null).when(defaultActionBean).getContexts();
    assertNotFound("Should not be found.", testling.handleRequestInternal(defaultActionBean, DEFAULT_CONTENT_ID,
            DEFAULT_NAVIGATION_PATH, DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void handleRequestInternal() {
    when(contentLinkBuilder.getVanityName(defaultActionContent)).thenReturn(DEFAULT_VANITY_NAME);
    assertDefaultPage(testling.handleRequestInternal(defaultActionBean, DEFAULT_CONTENT_ID, DEFAULT_NAVIGATION_PATH,
            DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void testHandleRequestInternalChannelWithDashAndNumber() {
    when(navigationSegmentsUriHelper.parsePath(Arrays.asList(DEFAULT_CONTEXT, "segment-2014")))
            .thenReturn(defaultNavigation);
    assertNavigationPage(testling.handleRequestInternal(null, 2014, Arrays.asList(DEFAULT_CONTEXT), "segment", null));
  }

  @Test
  public void handleRequestInternalForTaxonomyWithOnlyOnePathSegment() {
    when(contentLinkBuilder.getVanityName(defaultTaxonomyContent)).thenReturn(DEFAULT_VANITY_NAME);
    assertNotFound("Should not be found.", testling.handleRequestInternal(defaultTaxonomy, DEFAULT_CONTENT_ID,
            DEFAULT_NAVIGATION_PATH, DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void handleRequestInternalForTaxonomyWithTwoPathSegmentsNoRootChannelFound() {
    when(contentLinkBuilder.getVanityName(defaultTaxonomyContent)).thenReturn(DEFAULT_VANITY_NAME);
    List<String> expectedNavigationPath = ImmutableList.of(DEFAULT_CONTEXT, ADDITIONAL_SEGMENT);
    when(navigationSegmentsUriHelper.lookupRootSegment(DEFAULT_CONTEXT)).thenReturn(null);
    assertNotFound("Should not be found.", testling.handleRequestInternal(defaultTaxonomy, DEFAULT_CONTENT_ID,
            expectedNavigationPath, DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void handleRequestInternalForTaxonomyWithTwoPathSegmentsInvalidNavigationPath() {
    when(contentLinkBuilder.getVanityName(defaultTaxonomyContent)).thenReturn(DEFAULT_VANITY_NAME);
    List<String> expectedNavigationPath = ImmutableList.of(DEFAULT_CONTEXT, ADDITIONAL_SEGMENT);
    assertNotFound("Should not be found.", testling.handleRequestInternal(defaultTaxonomy, DEFAULT_CONTENT_ID,
            expectedNavigationPath, DEFAULT_VANITY_NAME, null));
  }

  @Test
  public void handleVanityRequestInternalEmptyNavigationPathProvided() {
    assertNotFound("Should not be found.", testling.handleRequestInternal(null, null));
    assertNotFound("Should not be found.", testling.handleRequestInternal(Collections.<String>emptyList(), null));
  }

  @Test
  public void handleVanityRequestInternalNoNavigationFound() {
    when(navigationSegmentsUriHelper.parsePath(DEFAULT_NAVIGATION_PATH)).thenReturn(null);
    assertNotFound("Should not be found.", testling.handleRequestInternal(DEFAULT_NAVIGATION_PATH, null));
  }

  @Test
  public void handleVanityRequestInternalSuccessfullyWithOnlyOnePathSegment() {
    assertNavigationPage(testling.handleRequestInternal(DEFAULT_NAVIGATION_PATH, null));
  }

  @Test
  public void handleVanityRequestInternalWithMultiplePathSegmentsVanitySegmentDoesNotMatchAnySegment() {
    List<String> expectedPathList = ImmutableList.of(DEFAULT_CONTEXT, ADDITIONAL_SEGMENT);
    when(contextHelper.findAndSelectContextFor(defaultNavigation, defaultNavigation)).thenReturn(null);
    assertNotFound("Should not be found", testling.handleRequestInternal(expectedPathList, null));
  }

  @Test
  public void handleVanityRequestInternalSuccessfullyWithMultiplePathSegmentsVanitySegment() {
    List<String> expectedPathList = ImmutableList.of(DEFAULT_CONTEXT, ADDITIONAL_SEGMENT);
    assertNavigationPage(testling.handleRequestInternal(expectedPathList, null));
  }

  @Test
  public void handleVanityRequestInternalSuccessfullyWithMultiplePathSegmentsVanitySegmentDoesNotMatchAnySegmentButFallbackWorks() {
    List<String> expectedPathList = ImmutableList.of(DEFAULT_CONTEXT, ADDITIONAL_SEGMENT);
    when(contextHelper.findAndSelectContextFor(defaultNavigation, defaultNavigation)).thenReturn(null);
    when(navigationSegmentsUriHelper.parsePath(expectedPathList)).thenReturn(defaultNavigation);
    assertNavigationPage(testling.handleRequestInternal(expectedPathList, null));
  }

  @Test
  public void buildLinkForTaxonomyNoTopicPageChannelFound() {
    when(contextHelper.contextFor(defaultTaxonomy)).thenReturn(null);
    assertNull(testling.buildLinkForTaxonomyInternal(defaultTaxonomy, null, Collections.<String, Object>emptyMap()));
  }

  @Test
  public void buildLinkForTaxonomyNoTopicPageSegment() {
    when(topicpageContextFinder.findDefaultTopicpageChannelFor(defaultTaxonomyContent, defaultNavigationContent)).thenReturn(null);
    assertNull(testling.buildLinkForTaxonomyInternal(defaultTaxonomy, null, Collections.<String, Object>emptyMap()));
  }

  @Test
  public void buildLinkForTaxonomy() {
    when(contentLinkBuilder.getVanityName(defaultTaxonomyContent)).thenReturn(DEFAULT_VANITY_NAME);
    UriComponentsBuilder result = testling.buildLinkForTaxonomyInternal(defaultTaxonomy, null, Collections.<String, Object>emptyMap());

    assertNotNull(result);
    assertEquals("/" + DEFAULT_CONTEXT + "/" + DEFAULT_ACTION + "-" + DEFAULT_CONTENT_ID, result.build().toUriString());
  }

  @Test
  public void buildLinkForLinkableNoNavigationFound() {
    when(contextHelper.contextFor(defaultActionBean)).thenReturn(null);

    assertNull(testling.buildLinkForLinkableInternal(defaultActionBean, null, Collections.<String, Object>emptyMap()));
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void buildLinkForLinkable() {
    UriComponentsBuilder result = testling.buildLinkForLinkableInternal(defaultActionBean, null, Collections.<String, Object>emptyMap());
    assertEquals("", result.build().toUriString());
  }

  @Override
  protected DefaultPageHandler createTestling() {
    return new DefaultPageHandler();
  }

  @Before
  public void defaultSetup() {
    super.defaultSetup();

    navigationResolver = spy(new NavigationResolver());
    navigationResolver.setContextHelper(contextHelper);
    navigationResolver.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    navigationResolver.setTopicPageContextFinder(topicpageContextFinder);
    doNothing().when(navigationResolver).setPageModelToRequestConstants(defaultTaxonomy);

    testling.setViewToBean(Collections.<String, Class>emptyMap());
    testling.setTopicPageContextFinder(topicpageContextFinder);
    testling.setContentLinkBuilder(contentLinkBuilder);
    testling.setNavigationResolver(navigationResolver);

    UriComponentsBuilder defaultUriComponentsBuilder = UriComponentsBuilder.newInstance();

    when(defaultNavigation.getVanityUrlMapper()).thenReturn(vanityUrlMapper);
    when(vanityUrlMapper.forPattern(ADDITIONAL_SEGMENT)).thenReturn(defaultNavigation);
    when(contextHelper.findAndSelectContextFor(defaultNavigation, defaultNavigation)).thenReturn(defaultNavigation);

    when(defaultTaxonomy.getContent()).thenReturn(defaultTaxonomyContent);
    when(contextHelper.contextFor(defaultTaxonomy)).thenReturn(defaultNavigation);
    when(contextHelper.findAndSelectContextFor(defaultNavigation, defaultTaxonomy)).thenReturn(defaultNavigation);
    when(defaultNavigation.getRootNavigation()).thenReturn(defaultNavigation);
    when(topicpageContextFinder.findDefaultTopicpageChannelFor(defaultTaxonomyContent, defaultNavigationContent)).thenReturn(defaultNavigationContent);
    when(defaultNavigationContent.getString(CMLinkable.SEGMENT)).thenReturn(DEFAULT_CONTEXT);
    when(defaultTaxonomy.getSegment()).thenReturn(DEFAULT_VANITY_NAME);
    when(contentBeanIdConverter.convert(defaultTaxonomy)).thenReturn(Integer.toString(DEFAULT_CONTENT_ID));
    when(contentLinkBuilder.buildLinkForPage(defaultActionContent, defaultNavigationContent)).thenReturn(defaultUriComponentsBuilder);
    when(navigationSegmentsUriHelper.lookupRootSegment(DEFAULT_CONTEXT)).thenReturn(defaultNavigation);
  }

  @Mock
  private VanityUrlMapper vanityUrlMapper;

  @Mock
  private Content defaultTaxonomyContent;

  @Mock
  private CMTaxonomy defaultTaxonomy;

  @Mock
  private TopicpageContextFinder topicpageContextFinder;

  @Mock
  private ContentLinkBuilder contentLinkBuilder;

  private NavigationResolver navigationResolver;

  @Mock
  private ServletRequestAttributes servletRequestAttributes;

  private static final List<String> DEFAULT_NAVIGATION_PATH = asList(DEFAULT_CONTEXT);
  private static final String DEFAULT_VANITY_NAME = DEFAULT_ACTION;
  private static final String ADDITIONAL_SEGMENT = "Crisis Inducer";
}

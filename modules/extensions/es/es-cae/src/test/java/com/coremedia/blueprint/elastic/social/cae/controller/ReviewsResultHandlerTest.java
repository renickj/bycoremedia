package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.blueprint.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.elastic.social.api.ContributionType.ANONYMOUS;
import static com.coremedia.elastic.social.api.ContributionType.DISABLED;
import static com.coremedia.elastic.social.api.ContributionType.REGISTERED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewsResultHandlerTest {

  private String contextId = "5678";
  private String targetId = "1234";
  private String text = "test test test test test test test";
  private String title = "title";
  private int rating = 5;

  @InjectMocks
  private ReviewsResultHandler handler;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ElasticSocialService elasticSocialService;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private SettingsService settingsService;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private Content content;

  @Mock
  private Content navigationContent;

  @Mock
  private CMLinkable contentBean;

  @Mock
  private ContentType contentType;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private Navigation navigation;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private CMContext navigationContext;

  @Mock
  private CMNavigation cmNavigation;

  @Mock
  private CommunityUser user;

  @Mock
  private Review review;

  @Mock
  private CMContext context;

  @Mock
  private Site site;

  @Mock
  private UriTemplate uriTemplate;

  @Mock
  private HttpServletRequest request;

  @Mock
  private ContributionTargetHelper contributionTargetHelper;

  @Before
  public void setup() {
    String contextPath = "perfectchef";
    String siteId = "123";

    handler.setElasticSocialUserHelper(new ElasticSocialUserHelper(communityUserService));
    handler.setContextHelper(contextHelper);
    handler.setElasticSocialPlugin(elasticSocialPlugin);
    handler.setContributionTargetHelper(contributionTargetHelper);
    handler.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);

    when(contentRepository.getContent(IdHelper.formatContentId(targetId))).thenReturn(content);
    when(contentBeanFactory.createBeanFor(content)).thenReturn(contentBean);
    when(contentBean.getContent()).thenReturn(content);
    when(content.getType()).thenReturn(contentType);
    when(content.getId()).thenReturn(targetId);
    when(navigation.getContext()).thenReturn(context);
    when(context.getContent()).thenReturn(content);
    when(site.getId()).thenReturn(siteId);
    when(contributionTargetHelper.getContentFromTarget(any(ContentWithSite.class))).thenReturn(content);
    when(navigationSegmentsUriHelper.parsePath(contextPath)).thenReturn(navigation);

    when(contentRepository.getContent(IdHelper.formatContentId(contextId))).thenReturn(navigationContent);
    when(contentBeanFactory.createBeanFor(navigationContent)).thenReturn(cmNavigation);

    when(navigation.getContext()).thenReturn(navigationContext);
    when(navigationContext.getContentId()).thenReturn(Integer.parseInt(contextId));

    when(elasticSocialPlugin.getElasticSocialConfiguration(anyVararg())).thenReturn(elasticSocialConfiguration);

    when(elasticSocialConfiguration.isFeedbackEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isReviewingEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isWritingReviewsEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isAnonymousReviewingEnabled()).thenReturn(true);

    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(site);
  }

  @Test
  public void getReviews() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    String view = "default";
    List<Review> reviews = ImmutableList.of(review);
    when(elasticSocialService.getReviews(any(ContentWithSite.class), any(CommunityUser.class))).thenReturn(reviews);
    when(contentRepository.getContent(IdHelper.formatContentId(targetId))).thenReturn(content);
    ModelAndView result = handler.getReviews(contextId, targetId, view, request);

    assertEquals(view, result.getViewName());
    assertEquals(cmNavigation, result.getModelMap().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION));
    ReviewsResult reviewsResultResult = getModel(result, ReviewsResult.class);
    ContentWithSite target = (ContentWithSite) reviewsResultResult.getTarget();
    assertEquals(content, target.getContent());
    assertEquals(site, target.getSite());
    assertEquals(ANONYMOUS, reviewsResultResult.getContributionType());
    verify(contentRepository, atLeastOnce()).getContent(IdHelper.formatContentId(targetId));
    verify(contentRepository, atLeastOnce()).getContent(IdHelper.formatContentId(contextId));
    verify(contentBeanFactory, atLeastOnce()).createBeanFor(navigationContent);

    // make sure reviews are lazily loaded
    reviewsResultResult.getReviews();
    verify(elasticSocialService).getReviews(any(ContentWithSite.class), any(CommunityUser.class));
  }

  @Test
  public void createReview() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    when(elasticSocialService.createReview(eq(user), any(ContentWithSite.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class))).thenReturn(review);
    when(elasticSocialConfiguration.getReviewModerationType()).thenReturn(ModerationType.POST_MODERATION);
    UserContext.setUser(user);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertTrue(resultModel.isSuccess());
    verify(elasticSocialService).createReview(eq(user), any(ContentWithSite.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(content), eq(cmNavigation)); // page
  }

  @Test
  public void createReviewRatingNull() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    when(elasticSocialService.createReview(eq(user), any(ContentWithSite.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class))).thenReturn(review);
    when(elasticSocialConfiguration.getReviewModerationType()).thenReturn(ModerationType.POST_MODERATION);
    UserContext.setUser(user);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, null, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertFalse(resultModel.isSuccess());
    verify(elasticSocialService, never()).createReview(eq(user), any(ContentWithSite.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService, never()).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Page.class)); // page
  }

  @Test
  public void createReviewDisabled() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(DISABLED);
    when(elasticSocialConfiguration.isWritingReviewsEnabled()).thenReturn(false);
    UserContext.setUser(user);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertFalse(resultModel.isSuccess());
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(ContentWithSite.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService, never()).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Page.class)); // page
  }

  @Test
  public void createReviewAnonymousDisabled() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(REGISTERED);
    when(elasticSocialConfiguration.isAnonymousReviewingEnabled()).thenReturn(false);
    when(user.isAnonymous()).thenReturn(true);
    UserContext.setUser(user);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertFalse(resultModel.isSuccess());
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(ContentWithSite.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService, never()).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Page.class)); // page
  }

  @Test
  public void buildFragmentLink() throws URISyntaxException {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    Map<String, Object> linkParameters = new HashMap<>();
    List<String> pathList = new ArrayList<>();
    String path = "path/" + contextId;
    pathList.add(path);
    when(contextHelper.contextFor(contentBean)).thenReturn(cmNavigation);
    when(cmNavigation.getContext()).thenReturn(context);
    when(context.getContentId()).thenReturn(Integer.parseInt(contextId));
    when(navigationSegmentsUriHelper.getPathList(cmNavigation)).thenReturn(pathList);
    URI uri = new URI(path);
    when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);

    ReviewsResult reviewsResult = new ReviewsResult(content);
    UriComponents uriComponents = handler.buildFragmentLink(reviewsResult, uriTemplate, linkParameters, request);

    assertNotNull(uriComponents);
    assertEquals(path, uriComponents.getPath());
  }

  @Test
  public void buildReviewInfoLink() throws URISyntaxException {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    List<String> pathList = new ArrayList<>();
    String path = "path/" + contextId;
    pathList.add(path);
    when(contextHelper.contextFor(contentBean)).thenReturn(cmNavigation);
    when(cmNavigation.getContext()).thenReturn(context);
    when(context.getContentId()).thenReturn(Integer.parseInt(contextId));
    when(navigationSegmentsUriHelper.getPathList(cmNavigation)).thenReturn(pathList);
    URI uri = new URI(path);
    when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);

    ReviewsResult reviewsResult = new ReviewsResult(content);
    UriComponents uriComponents = handler.buildInfoLink(reviewsResult, uriTemplate, request);

    assertNotNull(uriComponents);
    assertEquals(path, uriComponents.getPath());
  }

  private <T> T getModel(ModelAndView modelAndView, Class<T> type) {
    return getModel(modelAndView, "self", type);
  }

  private <T> T getModel(ModelAndView modelAndView, String key, Class<T> type) {
    Map<String, Object> modelMap = modelAndView.getModel();
    Object model = modelMap.get(key);
    // assertTrue(model instanceof type);
    return (T) model; // NO_SONAR
  }
}

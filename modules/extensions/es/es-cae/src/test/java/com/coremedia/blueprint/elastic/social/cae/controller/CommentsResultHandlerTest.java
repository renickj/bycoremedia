package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HttpError;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.elastic.social.cae.controller.CommentsResultHandler.ERROR_MESSAGE;
import static com.coremedia.elastic.social.api.ContributionType.ANONYMOUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentsResultHandlerTest {
  @InjectMocks
  private CommentsResultHandler handler;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ElasticSocialService elasticSocialService;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private NavigationSegmentsUriHelper uriHelper;

  @Mock
  private SettingsService settingsService;

  @Mock
  private Comment comment;

  @Mock
  private CommunityUser user;

  @Mock
  private Content content;

  @Mock
  private Content navigationContent;

  @Mock
  private ContentWithSite contentWithSite;

  @Mock
  private ContentType contentType;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private ContributionTargetHelper contributionTargetHelper;

  @Mock
  private CMLinkable contentBean;

  @Mock
  private CMNavigation navigation;

  @Mock
  private CMContext navigationContext;

  @Mock
  private ElasticSocialUserHelper elasticSocialUserHelper;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private UriTemplate uriTemplate;

  @Mock
  private Site site;

  private String context = "context";
  private String contextId = "1234";
  private String targetId = "12";
  private String view = "view";
  private String text = "default comment"; // we could randomize this one :-)
  private String authorName;
  private String replyTo;
  private String permittedParamName = "test";
  String uriPath = "helios";

  @Before
  public void setup() throws URISyntaxException {
    handler.setPermittedLinkParameterNames(Collections.singletonList(permittedParamName));

    when(contentRepository.getContent(IdHelper.formatContentId(targetId))).thenReturn(content);
    when(contentBeanFactory.createBeanFor(content)).thenReturn(contentBean);
    when(contentRepository.getContent(IdHelper.formatContentId(contextId))).thenReturn(navigationContent);
    when(contentBeanFactory.createBeanFor(navigationContent)).thenReturn(navigation);

    when(uriHelper.parsePath(context)).thenReturn(navigation);
    when(contentBean.getContent()).thenReturn(content);
    when(navigation.getContent()).thenReturn(content);
    when(content.getType()).thenReturn(contentType);
    when(content.getId()).thenReturn(targetId);
    when(contentBean.getContentId()).thenReturn(Integer.parseInt(targetId));

    when(contentWithSite.getContent()).thenReturn(content);
    when(contributionTargetHelper.getContentFromTarget(any())).thenReturn(content);

    URI uri = new URI(uriPath);
    when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);
    when(uriHelper.getPathList(navigation)).thenReturn(Collections.singletonList(uriPath));
    when(contextHelper.contextFor(any(CMLinkable.class))).thenReturn(navigation);
    when(navigation.getContext()).thenReturn(navigationContext);
    when(navigationContext.getContentId()).thenReturn(Integer.parseInt(contextId));

    when(elasticSocialPlugin.getElasticSocialConfiguration(anyVararg())).thenReturn(elasticSocialConfiguration);

    when(elasticSocialConfiguration.isFeedbackEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.getCommentType()).thenReturn(ANONYMOUS);
    when(elasticSocialConfiguration.isCommentingEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isWritingCommentsEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isAnonymousCommentingEnabled()).thenReturn(true);

    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(site);
  }

  @Test
  public void getCommentsWithNoTarget() {
    targetId = " ";
    ModelAndView modelAndView = handler.getComments(contextId, targetId, view, request);

    HttpError httpError = getModel(modelAndView, HttpError.class);
    assertEquals(404, httpError.getErrorCode());
  }

  @Test
  public void getCommentsWithUnknownTarget() {
    targetId = "123";
    ModelAndView modelAndView = handler.getComments(contextId, targetId, view, request);

    HttpError httpError = getModel(modelAndView, HttpError.class);
    assertEquals(404, httpError.getErrorCode());
  }

  @Test
  public void getComments() {
    ModelAndView result = handler.getComments(contextId, targetId, view, request);
    CommentsResult commentsResultResult = getModel(result, CommentsResult.class);

    assertNotNull(commentsResultResult);
    ContentWithSite target = (ContentWithSite) commentsResultResult.getTarget();
    assertEquals(content, target.getContent());

    assertEquals(view, result.getViewName());
    assertEquals(navigation, result.getModelMap().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION));
    assertEquals(ANONYMOUS, commentsResultResult.getContributionType());
  }

  @Test
  public void createComment() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(user);
    when(elasticSocialService.createComment(Matchers.eq(user), isNull(String.class), eq(content), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(String.class), isNull(List.class))).thenReturn(comment);

    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.POST_MODERATION);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());

    verify(elasticSocialService).createComment(Matchers.eq(user), isNull(String.class), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(String.class), isNull(List.class));  // NO_SONAR suppress warning

    verify(settingsService).settingWithDefault(ContributionMessageKeys.COMMENT_FORM_SUCCESS, String.class, ContributionMessageKeys.COMMENT_FORM_SUCCESS, content, navigation);
  }

  @Test
  public void createCommentForAnonymousNotAllowed() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(null);

    when(elasticSocialConfiguration.getCommentType()).thenReturn(ContributionType.REGISTERED);
    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.POST_MODERATION);
    when(elasticSocialConfiguration.isAnonymousCommentingEnabled()).thenReturn(false);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    assertFalse(resultModel.isSuccess());
    assertEquals(1, resultModel.getMessages().size());

    verify(elasticSocialService, never()).createComment(any(CommunityUser.class), isNull(String.class), eq(content), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(String.class), isNull(List.class));  // NO_SONAR suppress warning

    verify(settingsService).settingWithDefault(ContributionMessageKeys.COMMENT_FORM_NOT_LOGGED_IN, String.class, ContributionMessageKeys.COMMENT_FORM_NOT_LOGGED_IN, content, navigation);
  }

  @Test
  public void createCommentForUnknownContent() {
    String unknownContentId = "12345";
    ModelAndView modelAndView = handler.createComment(contextId, unknownContentId, text, authorName, replyTo, request);

    HttpError httpError = getModel(modelAndView, HttpError.class);
    assertEquals(404, httpError.getErrorCode());
  }

  @Test
  public void createCommentWithPreModeration() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(user);
    when(elasticSocialService.createComment(Matchers.eq(user), isNull(String.class), eq(content), any(Navigation.class),
            eq(text), eq(ModerationType.PRE_MODERATION), isNull(String.class), isNull(List.class))).thenReturn(comment);

    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.PRE_MODERATION);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    assertEquals(1, resultModel.getMessages().size());
    assertTrue(resultModel.isSuccess());
    verify(elasticSocialService).createComment(Matchers.eq(user), isNull(String.class), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.PRE_MODERATION), isNull(String.class), isNull(List.class));  // NO_SONAR suppress warning

    verify(settingsService).settingWithDefault(ContributionMessageKeys.COMMENT_FORM_SUCCESS_PREMODERATION, String.class, ContributionMessageKeys.COMMENT_FORM_SUCCESS_PREMODERATION, content, navigation);
  }

  @Test
  public void createCommentWithException() {
    when(elasticSocialUserHelper.getCurrentUser()).thenReturn(user);
    when(elasticSocialService.createComment(Matchers.eq(user), isNull(String.class), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(String.class), isNull(List.class))).thenThrow(new RuntimeException("intended"));

    when(elasticSocialConfiguration.getCommentModerationType()).thenReturn(ModerationType.POST_MODERATION);

    ModelAndView modelAndView = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);

    List<HandlerInfo.Message> messages = resultModel.getMessages();
    assertEquals(1, messages.size());
    assertFalse(resultModel.isSuccess());
    assertEquals(ERROR_MESSAGE, messages.get(0).getType());

    verify(elasticSocialService).createComment(Matchers.eq(user), isNull(String.class), any(ContentWithSite.class), any(Navigation.class),
            eq(text), eq(ModerationType.POST_MODERATION), isNull(String.class), isNull(List.class));  // NO_SONAR suppress warning

    verify(settingsService).settingWithDefault(ContributionMessageKeys.COMMENT_FORM_ERROR, String.class, ContributionMessageKeys.COMMENT_FORM_ERROR, content, navigation);
  }

  @Test
  public void buildCommentInfoLink() throws URISyntaxException {
    String notPermittedParamName = "not permitted";
    String paramValue = "value";
    Map<String, Object> linkParameters = ImmutableMap.<String, Object>of(permittedParamName, paramValue, notPermittedParamName, paramValue);
    CommentsResult commentsResult = new CommentsResult(contentWithSite);
    UriComponents result = handler.buildCommentInfoLink(commentsResult, uriTemplate, linkParameters, request);

    assertNotNull(result);
    assertEquals(uriPath, result.getPath());
    MultiValueMap<String, String> queryParams = result.getQueryParams();
    assertEquals(0, queryParams.size());

    verify(uriTemplate).expand(uriPath, Integer.parseInt(contextId), Integer.parseInt(targetId));
  }

  @Test
  public void buildFragmentLink() throws URISyntaxException {
    String notPermittedParamName = "not permitted";
    String paramValue = "value";
    Map<String, Object> linkParameters = ImmutableMap.<String, Object>of(permittedParamName, paramValue, notPermittedParamName, paramValue);
    CommentsResult commentsResult = new CommentsResult(contentWithSite);
    UriComponents result = handler.buildFragmentLink(commentsResult, uriTemplate, linkParameters, request);

    assertNotNull(result);
    assertEquals(uriPath, result.getPath());
    MultiValueMap<String, String> queryParams = result.getQueryParams();
    assertEquals(1, queryParams.size());
    List<String> queryParamValues = queryParams.get(permittedParamName);
    assertEquals(1, queryParamValues.size());
    assertEquals(paramValue, queryParamValues.get(0));
    verify(uriTemplate).expand(uriPath, Integer.parseInt(contextId), Integer.parseInt(targetId));
  }

  @Test
  public void anonymousCommentingNotEnabled() {
    when(elasticSocialConfiguration.isAnonymousCommentingEnabled()).thenReturn(false);

    ModelAndView mv = handler.createComment(contextId, targetId, text, authorName, replyTo, request);
    HandlerInfo result = getModel(mv, HandlerInfo.class);
    assertFalse(result.isSuccess());
    assertNotNull(result.getMessages());
    assertEquals(1, result.getMessages().size());
  }

  @Test
  public void commentTextBlank() {
    ModelAndView mv = handler.createComment(contextId, targetId, "", authorName, replyTo, request);
    HandlerInfo result = getModel(mv, HandlerInfo.class);
    assertFalse(result.isSuccess());
    assertNotNull(result.getMessages());
    assertEquals(1, result.getMessages().size());
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

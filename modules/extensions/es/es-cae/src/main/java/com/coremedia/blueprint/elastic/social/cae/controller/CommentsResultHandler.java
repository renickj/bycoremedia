package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.TARGETVIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Link
@RequestMapping
public class CommentsResultHandler extends ElasticContentHandler<CommentsResult> {

  public static final String SUCCESS_MESSAGE = "success";
  public static final String ERROR_MESSAGE = "error";

  private static final String COMMENTS_PREFIX = "comments";

  /**
   * URI pattern, for URIs like "/dynamic/fragment/comments/{segment}/{contextId}/{id}"
   */
  public static final String DYNAMIC_PATTERN_COMMENTS = "/" + PREFIX_DYNAMIC +
    "/" + SEGMENTS_FRAGMENT +
    "/" + COMMENTS_PREFIX +
    "/{" + ROOT_SEGMENT + "}" +
    "/{" + CONTEXT_ID + "}" +
    "/{" + ID + "}";


  @RequestMapping(value = DYNAMIC_PATTERN_COMMENTS, method = RequestMethod.GET)
  public ModelAndView getComments(@PathVariable("contextId") String contextId,
                                  @PathVariable("id") String targetId,
                                  @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view,
                                  HttpServletRequest request) {
    Navigation navigation = getNavigation(contextId);

    Site site = SiteHelper.getSiteFromRequest(request);
    if (site == null) {
      return HandlerHelper.notFound();
    }
    Object contributionTarget = getContributionTarget(targetId, site);
    if (contributionTarget == null) {
      return HandlerHelper.notFound();
    }
    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();

    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    CommentsResult commentsResult = new CommentsResult(contributionTarget, getElasticSocialUserHelper().getCurrentUser(),
            getElasticSocialService(), elasticSocialConfiguration.isFeedbackEnabled(), elasticSocialConfiguration.getCommentType());

    ModelAndView modelWithView = HandlerHelper.createModelWithView(commentsResult, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);

    return modelWithView;
  }

  /**
   * Handler to create comments for the currently logged in user
   * @param contextId     the context for a comment
   * @param targetId    the target for a comment
   * @param text        the text for a comment
   * @param authorName  the author name for anonymous comments (if allowed)
   * @param replyToId   if the comment is the reply to another comment
   * @return the newly created comment
   */
  @RequestMapping(value= DYNAMIC_PATTERN_COMMENTS, method = RequestMethod.POST)
  public ModelAndView createComment(@PathVariable("contextId") String contextId,
                                    @PathVariable("id") String targetId,
                                    @RequestParam(value = "comment", required = false) String text,
                                    @RequestParam(value = "authorName", required = false) String authorName,
                                    @RequestParam(value = "replyTo", required = false) String replyToId,
                                    HttpServletRequest request) {
    Navigation navigation = getNavigation(contextId);

    Site site = SiteHelper.getSiteFromRequest(request);
    if (site == null) {
      return HandlerHelper.notFound();
    }
    Object contributionTarget = getContributionTarget(targetId, site);
    if( contributionTarget == null ) {
      return HandlerHelper.notFound();
    }
    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();

    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    // workaround to prevent creating anonymous users when no comment can be written because of validation errors etc.
    CommunityUser author = getElasticSocialUserHelper().getCurrentUser();

    HandlerInfo result = new HandlerInfo();
    validateInput(result, author, text, beans);

    if (result.isSuccess()) {
      ModerationType moderation = elasticSocialConfiguration.getCommentModerationType();
      try {
        if (author == null) {
          author = getElasticSocialUserHelper().getAnonymousUser();
        }
        Comment comment = getElasticSocialService().createComment(author, authorName, contributionTarget,
                navigation, text, moderation, replyToId, null);
        result.setModel(comment);
        if (moderation.equals(ModerationType.PRE_MODERATION)) {
          result.addMessage(SUCCESS_MESSAGE, null, getMessage(ContributionMessageKeys.COMMENT_FORM_SUCCESS_PREMODERATION, beans));
        } else {
          result.addMessage(SUCCESS_MESSAGE, null, getMessage(ContributionMessageKeys.COMMENT_FORM_SUCCESS, beans));
        }
      } catch (Exception e) {
        LOG.error("Could not write a comment", e);
        addErrorMessage(result, null, ContributionMessageKeys.COMMENT_FORM_ERROR, beans);
      }
    }

    return HandlerHelper.createModel(result);
  }

  // ---------------------- building links ---------------------------------------------------------------------
  @Link(type = CommentsResult.class, view = VIEW_FRAGMENT, uri = DYNAMIC_PATTERN_COMMENTS)
  public UriComponents buildFragmentLink(CommentsResult commentsResult,
                                         UriTemplate uriTemplate,
                                         Map<String, Object> linkParameters,
                                         HttpServletRequest request) {
    return super.buildFragmentUri(SiteHelper.getSiteFromRequest(request), commentsResult, uriTemplate, linkParameters);
  }

  @Link(type = CommentsResult.class, uri = DYNAMIC_PATTERN_COMMENTS)
  public UriComponents buildCommentInfoLink(CommentsResult commentsResult,
                                            UriTemplate uriTemplate,
                                            Map<String, Object> linkParameters,
                                            HttpServletRequest request) {
    return getUriComponentsBuilder(SiteHelper.getSiteFromRequest(request), commentsResult, uriTemplate).build();
  }



  private void validateInput(HandlerInfo handlerInfo, CommunityUser user, String text, Object... beans) {
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    validateEnabled(handlerInfo, user, beans);
    if (isBlank(text)) {
      addErrorMessage(handlerInfo, "comment", ContributionMessageKeys.COMMENT_FORM_ERROR_COMMENT_BLANK, beans);
    }
  }

  private void validateEnabled(HandlerInfo handlerInfo, CommunityUser user, Object... beans) {
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    if (!elasticSocialConfiguration.isWritingCommentsEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.COMMENT_FORM_ERROR_NOT_ENABLED, beans);
    }
    if ((user == null || user.isAnonymous()) && !elasticSocialConfiguration.isAnonymousCommentingEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.COMMENT_FORM_NOT_LOGGED_IN, beans);
    }
  }
}

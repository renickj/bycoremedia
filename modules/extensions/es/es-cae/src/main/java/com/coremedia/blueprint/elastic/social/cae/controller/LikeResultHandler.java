package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.cap.multisite.Site;
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

@Link
@RequestMapping
public class LikeResultHandler extends ElasticContentHandler<LikeResult> {

  private static final String LIKE_PREFIX = "like";
  public static final String LIKE_PARAMETER = "like";

  /**
   * URI pattern, for URIs like "/dynamic/fragment/likes/{segment}/{contextId}/{id}"
   */
  public static final String DYNAMIC_PATTERN_LIKE = "/" + PREFIX_DYNAMIC +
          "/" + SEGMENTS_FRAGMENT +
          "/" + LIKE_PREFIX +
          "/{" + ROOT_SEGMENT + "}" +
          "/{" + CONTEXT_ID + "}" +
          "/{" + ID + "}";

  @RequestMapping(value = DYNAMIC_PATTERN_LIKE, method = RequestMethod.GET)
  public ModelAndView getLikeResult(@PathVariable(CONTEXT_ID) String contextId,
                                         @PathVariable(ID) String id,
                                         @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view,
                                         HttpServletRequest request) {

    Navigation navigation = getNavigation(contextId);

    Site site = SiteHelper.getSiteFromRequest(request);
    if (site == null) {
      return HandlerHelper.notFound();
    }
    Object contributionTarget = getContributionTarget(id, site);
    if (contributionTarget == null) {
      return HandlerHelper.notFound();
    }

    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    CommunityUser currentUser = getElasticSocialUserHelper().getCurrentUser();
    LikeResult likeResult = new LikeResult(contributionTarget, currentUser,
            getElasticSocialService(), elasticSocialConfiguration.isFeedbackEnabled(), elasticSocialConfiguration.getLikeType());

    ModelAndView modelWithView = HandlerHelper.createModelWithView(likeResult, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);

    return modelWithView;
  }

  @RequestMapping(value = DYNAMIC_PATTERN_LIKE, method = RequestMethod.POST)
  public ModelAndView createLike(@PathVariable(CONTEXT_ID) String contextId,
                                      @PathVariable(ID) String targetId,
                                      @RequestParam(value = LIKE_PARAMETER) boolean like,
                                      @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view,
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
    // workaround to prevent creating anonymous users when no like can be written because of validation errors etc.
    CommunityUser author = getElasticSocialUserHelper().getCurrentOrAnonymousUser();

    HandlerInfo result = new HandlerInfo();
    validateEnabled(result, author, beans);

    if (result.isSuccess()) {
      boolean created = getElasticSocialService().updateLike(author, contributionTarget, navigation.getContext(), like);
      // update message? result.addMessage(SUCCESS_MESSAGE, null, getMessage(COMMENT_FORM_SUCCESS, beans));
    }

    return HandlerHelper.createModel(result);
  }

  protected void validateEnabled(HandlerInfo handlerInfo, CommunityUser user, Object... beans) {
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    if (!elasticSocialConfiguration.isLikeEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.LIKE_FORM_ERROR_NOT_ENABLED, beans);
    } else if ((user == null || user.isAnonymous()) && !elasticSocialConfiguration.isAnonymousLikeEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.LIKE_FORM_ERROR_NOT_LOGGED_IN, beans);
    }
  }


  // ---------------------- building links ---------------------------------------------------------------------
  @Link(type = LikeResult.class, view = VIEW_FRAGMENT, uri = DYNAMIC_PATTERN_LIKE)
  public UriComponents buildFragmentLink(LikeResult likeResult,
                                         UriTemplate uriTemplate,
                                         Map<String, Object> linkParameters,
                                         HttpServletRequest request) {
    return buildFragmentUri(SiteHelper.getSiteFromRequest(request), likeResult, uriTemplate, linkParameters);
  }

  @Link(type = LikeResult.class, uri = DYNAMIC_PATTERN_LIKE)
  public UriComponents buildInfoLink(LikeResult likeResult, UriTemplate uriTemplate,HttpServletRequest request) {
    return getUriComponentsBuilder(SiteHelper.getSiteFromRequest(request), likeResult, uriTemplate).build();
  }
}

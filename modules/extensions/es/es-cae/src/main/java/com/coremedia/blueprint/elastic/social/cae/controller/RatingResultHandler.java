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
public class RatingResultHandler extends ElasticContentHandler<RatingResult> {

  private static final String RATING_PREFIX = "rating";
  public static final String RATING_PARAMETER = "rating";

  /**
   * URI pattern, for URIs like "/dynamic/fragment/Rating/{segment}/{contextId}/{id}"
   */
  public static final String DYNAMIC_PATTERN_RATING = "/" + PREFIX_DYNAMIC +
          "/" + SEGMENTS_FRAGMENT +
          "/" + RATING_PREFIX +
          "/{" + ROOT_SEGMENT + "}" +
          "/{" + CONTEXT_ID + "}" +
          "/{" + ID + "}";

  @RequestMapping(value = DYNAMIC_PATTERN_RATING, method = RequestMethod.GET)
  public ModelAndView getRatingResult(@PathVariable(CONTEXT_ID) String contextId,
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
    RatingResult commentsResult = new RatingResult(contributionTarget, getElasticSocialUserHelper().getCurrentUser(),
            getElasticSocialService(), elasticSocialConfiguration.isFeedbackEnabled(), elasticSocialConfiguration.getRatingType());

    ModelAndView modelWithView = HandlerHelper.createModelWithView(commentsResult, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);

    return modelWithView;
  }

  @RequestMapping(value = DYNAMIC_PATTERN_RATING, method = RequestMethod.POST)
  public ModelAndView createRating(@PathVariable(CONTEXT_ID) String contextId,
                                   @PathVariable(ID) String targetId,
                                   @RequestParam(value = RATING_PARAMETER) int rating,
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
    // workaround to prevent creating anonymous users when no comment can be written because of validation errors etc.
    CommunityUser author = getElasticSocialUserHelper().getCurrentUser();

    HandlerInfo result = new HandlerInfo();
    validateEnabled(result, author, beans);

    if (result.isSuccess()) {
      getElasticSocialService().updateRating(author, contributionTarget, navigation.getContext(), rating);
      // update message? result.addMessage(SUCCESS_MESSAGE, null, getMessage(COMMENT_FORM_SUCCESS, beans));
    }

    return HandlerHelper.createModelWithView(result, view);
  }

  protected void validateEnabled(HandlerInfo handlerInfo, CommunityUser user, Object... beans) {
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    if (!elasticSocialConfiguration.isRatingEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.RATING_FORM_ERROR_NOT_ENABLED, beans);
    } else if ((user == null || user.isAnonymous()) && !elasticSocialConfiguration.isAnonymousRatingEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.RATING_FORM_NOT_LOGGED_IN, beans);
    }
  }


  // ---------------------- building links ---------------------------------------------------------------------
  @Link(type = RatingResult.class, view = VIEW_FRAGMENT, uri = DYNAMIC_PATTERN_RATING)
  public UriComponents buildFragmentLink(RatingResult RatingResult,
                                         UriTemplate uriTemplate,
                                         Map<String, Object> linkParameters,
                                         HttpServletRequest request) {
    return buildFragmentUri(SiteHelper.getSiteFromRequest(request), RatingResult, uriTemplate, linkParameters);
  }

  @Link(type = RatingResult.class, uri = DYNAMIC_PATTERN_RATING)
  public UriComponents buildInfoLink(RatingResult RatingResult, UriTemplate uriTemplate,HttpServletRequest request) {
    return getUriComponentsBuilder(SiteHelper.getSiteFromRequest(request), RatingResult, uriTemplate).build();
  }
}

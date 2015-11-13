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
import static com.coremedia.elastic.core.api.users.UserService.USERS_COLLECTION;
import static com.coremedia.elastic.social.api.comments.CommentService.COMMENTS_COLLECTION;

@Link
@RequestMapping
public class ComplaintResultHandler extends ElasticContentHandler<ComplaintResult> {

  private static final String COMPLAINT_PREFIX = "complaint";
  public static final String COLLECTION_PARAMETER = "collection";
  public static final String MODEL_PARAMETER = "model";
  public static final String COMPLAIN_PARAMETER = "complain";

  /**
   * URI pattern, for URIs like "/dynamic/fragment/complaint/{segment}/{contextId}/{id}"
   */
  public static final String DYNAMIC_PATTERN_COMPLAINT = "/" + PREFIX_DYNAMIC +
          "/" + SEGMENTS_FRAGMENT +
          "/" + COMPLAINT_PREFIX +
          "/{" + ROOT_SEGMENT + "}" +
          "/{" + CONTEXT_ID + "}" +
          "/{" + ID + "}";

  @RequestMapping(value = DYNAMIC_PATTERN_COMPLAINT, method = RequestMethod.GET)
  public ModelAndView getComplaintResult(@PathVariable(CONTEXT_ID) String contextId,
                                 @PathVariable(ID) String id,
                                 @RequestParam(value = COLLECTION_PARAMETER) String collection,
                                 @RequestParam(value = MODEL_PARAMETER) String modelId,
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

    Object complaintTarget = getComplaintTarget(collection, id);
    if (complaintTarget == null) {
      return HandlerHelper.notFound();
    }

    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    ComplaintResult commentsResult = new ComplaintResult(complaintTarget, getElasticSocialUserHelper().getCurrentUser(),
            getElasticSocialService(), elasticSocialConfiguration.isFeedbackEnabled(), elasticSocialConfiguration.getComplaintType());

    ModelAndView modelWithView = HandlerHelper.createModelWithView(commentsResult, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);

    return modelWithView;
  }

  public Object getComplaintTarget(String collection, String id) {
    Object realTarget = null;
    if (USERS_COLLECTION.equals(collection)) {
      realTarget = getElasticSocialService().getUser(id);
    } else if (COMMENTS_COLLECTION.equals(collection)) {
      realTarget = getElasticSocialService().getComment(id);
    }
    return realTarget;
  }

  @RequestMapping(value = DYNAMIC_PATTERN_COMPLAINT, method = RequestMethod.POST)
  public ModelAndView createComplaint(@PathVariable(CONTEXT_ID) String contextId,
                                   @PathVariable(ID) String targetId,
                                   @RequestParam(value = COMPLAIN_PARAMETER) boolean complain,
                                   @RequestParam(value = COLLECTION_PARAMETER) String collection,
                                   @RequestParam(value = MODEL_PARAMETER) String modelId,
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
    Object complaintTarget = getComplaintTarget(collection, targetId);
    if (complaintTarget == null) {
      return HandlerHelper.notFound();
    }

    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();
    // workaround to prevent creating anonymous users when no comment can be written because of validation errors etc.
    CommunityUser author = getElasticSocialUserHelper().getCurrentUser();

    HandlerInfo result = new HandlerInfo();
    validateEnabled(result, author, beans);

    if (result.isSuccess()) {
      getElasticSocialService().updateComplaint(author, complaintTarget, complain);
      // update message? result.addMessage(SUCCESS_MESSAGE, null, getMessage(COMMENT_FORM_SUCCESS, beans));
    }

    return HandlerHelper.createModelWithView(result, view);
  }

  protected void validateEnabled(HandlerInfo handlerInfo, CommunityUser user, Object... beans) {
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    if (!elasticSocialConfiguration.isComplainingEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.COMPLAINT_FORM_ERROR_NOT_ENABLED, beans);
    } else if ((user == null || user.isAnonymous()) && !elasticSocialConfiguration.isAnonymousComplainingEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.COMPLAINT_FORM_ERROR_NOT_LOGGED_IN, beans);
    }
  }


  // ---------------------- building links ---------------------------------------------------------------------
  @Link(type = ComplaintResult.class, view = VIEW_FRAGMENT, uri = DYNAMIC_PATTERN_COMPLAINT)
  public UriComponents buildFragmentLink(ComplaintResult complaintResult,
                                         UriTemplate uriTemplate,
                                         Map<String, Object> linkParameters,
                                         HttpServletRequest request) {
    return buildFragmentUri(SiteHelper.getSiteFromRequest(request), complaintResult, uriTemplate, linkParameters);
  }

  @Link(type = ComplaintResult.class, uri = DYNAMIC_PATTERN_COMPLAINT)
  public UriComponents buildInfoLink(ComplaintResult complaintResult, UriTemplate uriTemplate,HttpServletRequest request) {
    return getUriComponentsBuilder(SiteHelper.getSiteFromRequest(request), complaintResult, uriTemplate).build();
  }
}

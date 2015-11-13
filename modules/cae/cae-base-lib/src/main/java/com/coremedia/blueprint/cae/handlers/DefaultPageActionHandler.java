package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.action.webflow.WebflowActionState;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.objectserver.beans.ContentBean;
import com.google.common.collect.ImmutableMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ACTION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;

@SuppressWarnings("LocalCanBeFinal")
public class DefaultPageActionHandler extends WebflowHandlerBase {

  @Override
  @Nonnull
  protected ModelAndView handleRequestInternal(
          @Nonnull ContentBean contentBean,
          @Nonnull String context,
          @Nonnull String action,
          @Nonnull HttpServletRequest request,
          @Nonnull HttpServletResponse response) {
    return super.handleRequestInternal(contentBean, context, action, request, response);
  }

  @Nullable
  protected UriComponents buildLinkInternal(
          @Nonnull CMAction action,
          @Nonnull UriTemplate uriPattern,
          @Nonnull Map<String, Object> linkParameters) {
    //noinspection ConstantConditions
    if (action == null) {
      throw new IllegalStateException("No action provided for building a link.");
    }

    String actionName = getVanityName(action);
    Navigation context = getNavigation(action);
    UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriPattern.toString());
    result = addLinkParametersAsQueryParameters(result, linkParameters);
    List<String> pathSegments = getPathSegments(context);
    if (isEmpty(pathSegments)) {
      throw new IllegalStateException("Could not calculate the path segments for " + context);
    }

    return result.buildAndExpand(ImmutableMap.of(
            SEGMENT_ID, getId(action),
            SEGMENT_ROOT, getPathSegments(context).get(0),
            SEGMENT_ACTION, actionName
    ));
  }

  @Override
  protected WebflowActionState getWebflowActionState(CMAction action, ModelAndView webFlowOutcome, String flowId, String flowViewId) {
    return new WebflowActionState(action, webFlowOutcome.getModelMap(), flowId, flowViewId);
  }

}

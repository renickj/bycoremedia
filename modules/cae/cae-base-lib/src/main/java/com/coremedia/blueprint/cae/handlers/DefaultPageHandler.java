package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.navigation.context.finder.TopicpageContextFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.VIEW_PARAMETER;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Controller and Linkscheme
 * for {@link com.coremedia.blueprint.common.contentbeans.Page}
 */
@Link
@RequestMapping
public class DefaultPageHandler extends PageHandlerBase {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultPageHandler.class);

  /**
   * The name of the view specific bean which is derived from the viewToBean
   * Map (configured via Spring) and added to the model.
   * <p/>
   * The value is "viewBean".
   */
  public static final String VIEW_BEAN = "viewBean";

  private NavigationResolver navigationResolver;
  private TopicpageContextFinder topicPageContextFinder;
  private SettingsService settingsService;
  private Map<String, Class> viewToBean;

  /**
   * Handles URIs like /media/travel/europe/england/knowing-all-about-london-1234
   */
  @Nonnull
  protected ModelAndView handleRequestInternal(@Nullable CMLinkable linkable,
                                               int segmentId,
                                               @Nullable List<String> navigationPath,
                                               @Nonnull String vanity,
                                               @Nullable String view) {

    if (navigationPath == null) {
      return HandlerHelper.notFound();
    }

    // first check if a vanity or channel URL exists with exactly the given path (fixes CMS-352)
    List<String> fullPath = new ArrayList<>(navigationPath.size() + 1);
    fullPath.addAll(navigationPath);
    fullPath.add(vanity + '-' + segmentId);
    ModelAndView result = createModelAndView(fullPath, view);
    if (result != null) {
      return result;
    }

    // not a vanity or channel url, try to resolve URL for CMLinkable
    if (linkable == null || !validSegment(linkable, vanity)) {
      return HandlerHelper.notFound();
    }
    Navigation navigation = navigationResolver.getNavigation(linkable, navigationPath);
    if (navigation == null) {
      return HandlerHelper.notFound();
    }

    Page page = asPage(navigation, isContextBean(linkable) ? navigation : linkable);

    return createModelAndView(page, view);
  }

  /**
   * Handles a request for a vanity URL containing a root segment and two additional segment, e.g. /sports/football/results/recent
   */
  @Nonnull
  protected ModelAndView handleRequestInternal(@Nullable List<String> navigationPath,
                                               @Nullable String view) {
    ModelAndView modelAndView = createModelAndView(navigationPath, view);
    return modelAndView != null ? modelAndView : HandlerHelper.notFound();
  }

  @Nullable
  protected ModelAndView createModelAndView(@Nullable List<String> navigationPath,
                                            @Nullable String view) {
    if (CollectionUtils.isEmpty(navigationPath)) {
      return null;
    }
    @SuppressWarnings("ConstantConditions")
    CMChannel rootChannel = (CMChannel) getNavigation(navigationPath.get(0));
    if (rootChannel == null) {
      return null;
    }
    if (navigationPath.size() == 1) {
      // The URL references the root channel
      return createModelAndView(asPage(rootChannel, rootChannel), view);
    }

    // try to resolve rest of path as vanity URL: this will be null, if there is no vanity mapping
    List<String> vanitySegments = navigationPath.subList(1, navigationPath.size());
    CMLinkable target = (CMLinkable) rootChannel.getVanityUrlMapper().forPattern(joinPath(vanitySegments));
    if (target != null) {
      // vanity URL found: determine the context for the target in the current site
      CMContext context = getContext(rootChannel, target);
      if (context != null) {
        Page page = asPage(context, target);
        return createModelAndView(page, view);
      }
    }

    // no vanity URL defined, try to parse full path as navigation segment path
    Navigation navigation = getNavigation(navigationPath);
    if (navigation != null) {
      return createModelAndView(asPage(navigation, navigation), view);
    }

    // give up
    return null;
  }

  @Nullable
  protected UriComponentsBuilder buildLinkForTaxonomyInternal(
          @Nonnull CMTaxonomy taxonomy,
          @Nullable String viewName,
          @Nonnull Map<String, Object> linkParameters) {
    CMNavigation topicPageChannel = getContextHelper().contextFor(taxonomy);
    if (topicPageChannel == null) {
      LOG.error("Found no context for taxonomy " + taxonomy);
      return null;
    }

    // build link: /root/taxonomychannel/taxonomy/id
    List<String> navigationPath = newArrayList();
    Navigation rootNavigation = topicPageChannel.getRootNavigation();
    navigationPath.add(rootNavigation.getSegment());
    String segment = getDefaultTopicpageSegment(rootNavigation, taxonomy);
    if (segment == null) {
      LOG.error("Actually responsible for this link, but could not determine the segment of the default topic page channel for " + taxonomy + " / " + rootNavigation);
      return null;
    }
    navigationPath.add(segment);
    appendNameAndId(taxonomy, navigationPath);
    return buildUri(navigationPath, viewName, linkParameters);
  }

  @Nullable
  protected UriComponentsBuilder buildLinkForLinkableInternal(
          @Nonnull CMLinkable linkable,
          @Nullable String viewName,
          @Nonnull Map<String, Object> linkParameters) {
    Navigation context = getNavigation(linkable);
    if (context == null) {
      LOG.warn("Content " + linkable.getContentId() + " has no navigation context, cannot build link.");
      return null;
    }
    return buildLink(linkable, context, viewName, linkParameters);
  }

  @Nonnull
  private UriComponentsBuilder buildLink(
          @Nonnull CMLinkable linkable,
          @Nonnull Navigation navigationContext,
          @Nullable String viewName,
          @Nonnull Map<String, Object> linkParameters) {
    Content targetContent = linkable.getContent();
    //todo we have to generalize the contentLinkBuilder to cope with Navigation instances if we want to link to {CM}Linkables below a LiveContextNavigation, say
    Content navigationContent = ((CMNavigation) navigationContext).getContent();
    UriComponentsBuilder uriComponentsBuilder = getContentLinkBuilder().buildLinkForPage(targetContent, navigationContent);

    // add optional view query parameter
    if (viewName != null) {
      uriComponentsBuilder.queryParam(VIEW_PARAMETER, viewName);
    }

    // add additional query parameters
    addLinkParametersAsQueryParameters(uriComponentsBuilder, linkParameters);
    return uriComponentsBuilder;
  }

  // --- PageHandlerBase --------------------------------------------

  /**
   * Add a view specific bean named viewBean to the model.
   * <p/>
   * The view specific interface is taken from the viewToBean Map
   * and instatiated with a settings proxy.
   */
  @Override
  protected ModelAndView createModelAndView(Page page, String view) {
    ModelAndView modelAndView = super.createModelAndView(page, view);
    Class viewBeanClass = viewToBean.get(view);
    if (viewBeanClass != null) {
      Object viewBeanProxy = settingsService.createProxy(viewBeanClass, page);
      modelAndView.addObject(VIEW_BEAN, viewBeanProxy);
    }
    return modelAndView;
  }


  // --- internal ---------------------------------------------------

  private String getDefaultTopicpageSegment(Navigation siteContext, CMTaxonomy taxonomy) {
    if (!(siteContext instanceof CMNavigation)) {
      return null;
    }
    Content defaultTopicPageChannel = topicPageContextFinder.findDefaultTopicpageChannelFor(taxonomy.getContent(), ((CMNavigation) siteContext).getContent());
    return defaultTopicPageChannel == null ? null : defaultTopicPageChannel.getString(CMLinkable.SEGMENT);
  }

  private UriComponentsBuilder buildUri(List<String> navigationPath, String viewName, Map<String, Object> linkParameters) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .newInstance()
            .pathSegment(Iterables.toArray(navigationPath, String.class));
    addViewAndParameters(uriBuilder, viewName, linkParameters);
    return uriBuilder;
  }

  /**
   * Checks if the vanity segment is correct.
   */
  private boolean validSegment(CMLinkable linkable, String vanity) {
    // Ensure that the segment in the URL matches the content's segment.
    // Otherwise the URL was not invented here.
    return getVanityName(linkable).equals(vanity);
  }

  /**
   * If true, the given linkable should not be rendered itself but
   * is used in the context of the navigation that has been resolved.
   * Therefore the ModelAndView should be rendered as page.
   *
   * @param linkable The linkable to check the rendering for.
   */
  private boolean isContextBean(CMLinkable linkable) {
    return linkable instanceof CMTaxonomy;
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setNavigationResolver(NavigationResolver navigationResolver) {
    this.navigationResolver = navigationResolver;
  }

  @Required
  public void setTopicPageContextFinder(TopicpageContextFinder topicPageContextFinder) {
    this.topicPageContextFinder = topicPageContextFinder;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setViewToBean(Map<String, Class> viewToBean) {
    this.viewToBean = viewToBean;
  }
}

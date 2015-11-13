package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.ContentLinkBuilder;
import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.VIEW_PARAMETER;

/**
 * Base implementation of resources that deal with {@link Page pages}
 * (or {@link CMLinkable} / {@link CMNavigation} respectively)
 */
public abstract class PageHandlerBase extends HandlerBase {

  private static final String DEFAULT_VANITY_NAME = "-";

  private ContextHelper contextHelper;
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;
  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;

  private boolean developerModeEnabled;
  private Cache cache;


  // --- configuration ----------------------------------------------

  @Required
  public void setContextHelper(ContextHelper contextHelper) {
    this.contextHelper = contextHelper;
  }

  @Required
  public void setNavigationSegmentsUriHelper(NavigationSegmentsUriHelper navigationSegmentsUriHelper) {
    this.navigationSegmentsUriHelper = navigationSegmentsUriHelper;
  }

  @Required
  public void setDeveloperModeEnabled(boolean developerModeEnabled) {
    this.developerModeEnabled = developerModeEnabled;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  protected SitesService getSitesService() {
    return sitesService;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  protected Cache getCache() {
    return cache;
  }

  protected ContentBeanFactory getContentBeanFactory() {
    return contentBeanFactory;
  }

  public boolean getDeveloperModeEnabled() {
    return developerModeEnabled;
  }

  protected NavigationSegmentsUriHelper getNavigationSegmentsUriHelper() {
    return navigationSegmentsUriHelper;
  }


  // --- features ---------------------------------------------------

  protected ContentLinkBuilder getContentLinkBuilder() {
    return contentLinkBuilder;
  }

  protected Page asPage(Navigation context, Linkable content) {
    PageImpl page = new PageImpl(context, content, developerModeEnabled, sitesService, cache);
    page.setTitle(content.getTitle());

    //todo this is the original blueprint semantic for descriptions - do we really want the title as description?
    page.setDescription(page.getTitle());

    page.setKeywords(content.getKeywords());
    if (content instanceof CMLinkable) {
      CMLinkable cmLinkable = (CMLinkable) content;
      page.setContentId(String.valueOf(cmLinkable.getContentId()));
      page.setContentType(cmLinkable.getContent().getType().getName());
      page.setValidFrom(cmLinkable.getValidFrom());
      page.setValidTo(cmLinkable.getValidTo());
    }
    // load a dataview for the page
    if (getDataViewFactory() != null) {
      page = getDataViewFactory().loadCached(page, null);
    }
    return page;
  }

  protected ModelAndView createModelAndView(Page page, String view) {
    ModelAndView result = HandlerHelper.createModelWithView(page, view);
    addPageModel(result, page);
    return result;
  }

  protected ModelAndView createModelAndView(Page page, String view, String orientation) {
    ModelAndView result = createModelAndView(page, view);
    if (!StringUtils.isEmpty(orientation)) {
      result.addObject("orientation", orientation);
    }
    return result;
  }
  /**
   * Adds a page to the model and view as additional model
   *
   * @param modelAndView The target model and view
   * @param page         The page to add as model
   */
  protected void addPageModel(ModelAndView modelAndView, Page page) {

    RequestAttributeConstants.setPage(modelAndView, page);
    NavigationLinkSupport.setNavigation(modelAndView, page.getNavigation());

  }

  /**
   * Creates a {@link ModelAndView model} for a given page
   *
   * @param page The page
   * @return The model
   */
  protected final ModelAndView createModel(Page page) {
    return createModelAndView(page, null);
  }

  /**
   * Fetches the path segments from a {@link CMNavigation}
   *
   * @see #getNavigation(java.util.List)
   */
  protected List<String> getPathSegments(Navigation navigation) {
    return navigationSegmentsUriHelper.getPathList(navigation);
  }

  /**
   * Returns the ContextHelper
   */
  protected ContextHelper getContextHelper() {
    return contextHelper;
  }

  /**
   * Provides a {@link CMNavigation} from a sequence of segments
   */
  protected Navigation getNavigation(String navigationPathElement) {
    return getNavigation(Collections.singletonList(navigationPathElement));
  }

  /**
   * Provides a {@link CMNavigation} from a sequence of segments
   */
  protected Navigation getNavigation(List<String> navigationPath) {
    return navigationSegmentsUriHelper.parsePath(navigationPath);
  }

  /**
   * Determines the navigation context for a target {@link CMLinkable}, using
   * {@link com.coremedia.blueprint.common.services.context.ContextHelper#contextFor(CMLinkable)}.
   */
  protected Navigation getNavigation(CMLinkable target) {
    return contextHelper.contextFor(target);
  }

  /**
   * Chooses an appropriate navigation context for a target {@link CMLinkable}, given a current context,
   * using {@link com.coremedia.blueprint.common.services.context.ContextHelper#findAndSelectContextFor}.
   */
  @Nullable
  protected CMContext getContext(@Nonnull CMContext current, @Nonnull CMLinkable target) {
    return contextHelper.findAndSelectContextFor(current, target);
  }

  /**
   * Appends vanity name and id of the linkable to the urlPath.
   */
  protected void appendNameAndId(CMLinkable linkable, List<String> urlPath) {
    urlPath.add(getVanityName(linkable) + '-' + getId(linkable));
  }

  /**
   * Provides a (vanity) name for a linkable to be used inside a link
   */
  protected String getVanityName(CMLinkable bean) {
    return getContentLinkBuilder().getVanityName(bean.getContent());
  }


  protected void addViewAndParameters(UriComponentsBuilder uriBuilder, String viewName, Map<String, Object> linkParameters) {
    // add optional view query parameter
    if( viewName != null ) {
      uriBuilder.queryParam(VIEW_PARAMETER, viewName);
    }
    // add additional query parameters
    addLinkParametersAsQueryParameters(uriBuilder, linkParameters);
  }
}

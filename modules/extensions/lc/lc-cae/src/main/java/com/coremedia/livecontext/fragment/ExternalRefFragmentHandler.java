package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.resolver.ExternalReferenceResolver;
import com.coremedia.livecontext.fragment.resolver.LinkableAndNavigation;
import com.coremedia.objectserver.beans.UnexpectedBeanTypeException;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.coremedia.objectserver.web.HandlerHelper.badRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ExternalRefFragmentHandler extends FragmentHandler {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalRefFragmentHandler.class);

  private List<ExternalReferenceResolver> externalReferenceResolvers;
  private String defaultPlacementName;

  @Override
  public ModelAndView createModelAndView(FragmentParameters parameters, HttpServletRequest request) {
    String externalRef = parameters.getExternalRef();
    String view = parameters.getView();

    ExternalReferenceResolver resolver = findReferenceResolver(parameters);
    if(resolver == null) {
      LOG.warn("Cannot resolve external reference value '" + externalRef + "'");
      return HandlerHelper.notFound("ExternalRefFragmentHandler could not find an external reference resolver for '" + externalRef + "'");
    }

    // resolve the external reference
    Site site = SiteHelper.getSiteFromRequest(request);
    if (site == null) {
      throw new IllegalStateException("No site available from SiteHelper.");
    }
    LinkableAndNavigation linkableAndNavigation = resolver.resolveExternalRef(parameters, site);

    if (linkableAndNavigation == null || linkableAndNavigation.getLinkable() == null) {
      LOG.info("No content could be resolved for external reference value '" + externalRef + "'");
      return HandlerHelper.notFound(resolver + " could not find content for '" + externalRef + "'");
    }

    Content linkable = linkableAndNavigation.getLinkable();
    Content navigation = linkableAndNavigation.getNavigation();
    if (navigation == null) {
      try {
        Navigation contentAsBean = getContentBeanFactory().createBeanFor(linkable, Navigation.class);
        if (contentAsBean != null && contentAsBean instanceof CMNavigation) {
          navigation = linkable;
        }
      } catch (UnexpectedBeanTypeException e) {
        LOG.debug("No navigation found and given content is not a navigation", e);
      }
    }

    if (navigation == null) {
      LOG.warn("No navigation could be resolved for external reference value '" + externalRef + "'");
      return HandlerHelper.notFound("No navigation could be resolved for external reference value '" + externalRef + "'");
    }

    String placement = parameters.getPlacement();

    //check if the content and navigation belong to the selected site
    if(!getSitesService().isContentInSite(site, navigation)) {
      return badRequest("Resolved context is not part of the given site");
    }
    if(!getSitesService().isContentInSite(site, linkable)) {
      return badRequest("The content resolved for the given external reference (" + externalRef + ") is not part of the given site");
    }

    //A channel is linked in the include tag. Since we can only render placements, we assume the "main" section should be used.
    if(linkable.getType().isSubtypeOf(CMChannel.NAME) && placement == null && view == null) {
      placement = defaultPlacementName;
    }

    if(!isRequestToPlacement(linkable, navigation, placement)) {
      // The default view is used only for placement requests, that do not requested a certain view. For
      // any other requests, the default view is null (as usual).
      if ("default".equals(view)) {
        view = null;
      }
      return createModelAndViewForLinkable(navigation, linkable, view);
    }

    //include a page fragment for the given channel
    if (placement != null && isNotBlank(placement)) {
      CMChannel channelBean = getContentBeanFactory().createBeanFor(navigation, CMChannel.class);
      return createModelAndViewForPlacementAndView(channelBean, placement, view);
    }

    return badRequest("No placement name given.");
  }

  @Override
  public boolean include(FragmentParameters params) {
    return !StringUtil.isEmpty(params.getExternalRef());
  }

  @Nonnull
  protected ModelAndView createModelAndViewForLinkable(@Nonnull Content channel, @Nonnull Content child, String view) {
    Navigation navigation = getContentBeanFactory().createBeanFor(channel, Navigation.class);
    Linkable linkable = getContentBeanFactory().createBeanFor(child, Linkable.class);
    if (getDataViewFactory() != null) {
      linkable = getDataViewFactory().loadCached(linkable, null);
    }

    Page page = asPage(navigation, navigation);
    ModelAndView modelAndView = HandlerHelper.createModelWithView(linkable, view);
    RequestAttributeConstants.setPage(modelAndView, page);
    NavigationLinkSupport.setNavigation(modelAndView, navigation);

    return modelAndView;
  }

  // --------------- Helper -----------------

  /**
   * Based on the linkable the navigation and the placement name this method evaluates if the request
   * is meant to be a placement request or not. The livecontext include tag allows any combination
   * of request parameter, hence it is allowed to provide a placement name together with a
   * content id for a linkable and a navigation document. In case they both are different, for example
   * linkable is a requested article and navigation its context, no placement shall be rendered but
   * the whole article. The placement name must then be ignored.
   */
  private boolean isRequestToPlacement(Content linkable, Content navigation, String placementName) {
    if (isBlank(placementName)) {
      return false;
    }

    //noinspection RedundantIfStatement
    if (linkable != null && !linkable.equals(navigation)) {
      return false;
    }

    return true;
  }

  /**
   * Finds the matching resolver for the given fragment attribute values.
   */
  private ExternalReferenceResolver findReferenceResolver(FragmentParameters params) {
    for(ExternalReferenceResolver resolver : externalReferenceResolvers) {
      if(resolver.include(params)) {
        return resolver;
      }
    }
    return null;
  }

  // ---------- Config ----------------------------

  @Required
  public void setExternalReferenceResolvers(List<ExternalReferenceResolver> externalReferenceResolvers) {
    this.externalReferenceResolvers = externalReferenceResolvers;
  }

  @Required
  public void setDefaultPlacementName(String defaultPlacementName) {
    this.defaultPlacementName = defaultPlacementName;
  }
}

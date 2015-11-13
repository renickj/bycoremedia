package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles fragment request that depend on a category id.
 */
public class CategoryFragmentHandler extends FragmentHandler {
  private ResolveContextStrategy contextStrategy;

  /**
   * Renders the complete context (which is a CMChannel) of the given <code>category</code> using the given <code>view</code>.
   * If no context can be found for the category, the <code>view</code> of the root channel will be rendered. The site
   * is determined by the tuple <code>(storeId, locale)</code>, which must be unique across all sites. If the placement
   * value is passed as part of the fragment parameters, the model and view will be created for it.
   *
   * @param parameters All parameters that have been passed for the fragment call.
   *
   * @return the {@link ModelAndView model and view} containing the {@link com.coremedia.blueprint.common.contentbeans.Page page}
   * as <code>self</code> object, that contains the context (CMChannel) that shall be rendered.
   */
  @Override
  public ModelAndView createModelAndView(FragmentParameters parameters, HttpServletRequest request) {
    Site site = SiteHelper.getSiteFromRequest(request);
    if (site != null) {
      String view = parameters.getView();
      Navigation navigation = contextStrategy.resolveContext(site, parameters.getCategoryId());
      if (navigation != null) {
        Content rootChannelContent = site.getSiteRootDocument();
        CMChannel rootChannel = getContentBeanFactory().createBeanFor(rootChannelContent, CMChannel.class);
        if (StringUtils.isEmpty(parameters.getPlacement())) {
          return createFragmentModelAndView(navigation, view, rootChannel);
        }

        return createFragmentModelAndViewForPlacementAndView(navigation, parameters.getPlacement(), view, rootChannel);
      }
    }

    throw new IllegalStateException("CategoryFragmentHandler did not find a navigation for storeId \"" + parameters.getStoreId() +
            "\", locale \"" + parameters.getLocale() + "\", category id \"" + parameters.getCategoryId() + "\"");
  }

  @Override
  public boolean include(FragmentParameters params) {
    return !StringUtils.isEmpty(params.getCategoryId()) && (StringUtils.isEmpty(params.getExternalRef()) || !params.getExternalRef().startsWith("cm-"));
  }

  // ------------------- Config ---------------------------------

  @Required
  public void setContextStrategy(ResolveContextStrategy contextStrategy) {
    this.contextStrategy = contextStrategy;
  }
}

package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public class ExternalPageFragmentHandler extends FragmentHandler {

  private ContextStrategy<String, Navigation> contextStrategy;

  @Override
  ModelAndView createModelAndView(FragmentParameters params, HttpServletRequest request) {
    Site site = SiteHelper.getSiteFromRequest(request);
    if (site != null) {
      String pageId = params.getPageId();
      String view = params.getView();
      String placement = params.getPlacement();

      Content rootChannelContent = site.getSiteRootDocument();
      CMChannel rootChannel = getContentBeanFactory().createBeanFor(rootChannelContent, CMChannel.class);

      if (rootChannel == null) {
        throw new IllegalStateException("ExternalPageFragmentHandler did not find a root channel for site \"" + site.getName() +
                "\", page id \"" + params.getPageId() + "\"");
      }

      Navigation navigation = contextStrategy.findAndSelectContextFor(pageId, rootChannel);

      // Fallback to rootChannel if the page cannot be found
      if (navigation == null) {
        navigation = rootChannel;
      }

      if (StringUtils.isEmpty(placement)) {
        return createFragmentModelAndView(navigation, view, rootChannel);
      }
      return createFragmentModelAndViewForPlacementAndView(navigation, placement, view, rootChannel);
    }

    return null;
  }

  @Override
  public boolean include(FragmentParameters params) {
    return !StringUtils.isEmpty(params.getPageId()) &&
            StringUtils.isEmpty(params.getProductId()) &&
            StringUtils.isEmpty(params.getCategoryId()) &&
            (params.getExternalRef() == null || !params.getExternalRef().startsWith("cm-"));
  }

  // ------------ Config --------------------------------------------

  @Required
  public void setContextStrategy(ContextStrategy<String, Navigation> contextStrategy) {
    this.contextStrategy = contextStrategy;
  }

}

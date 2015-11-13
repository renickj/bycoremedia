package com.coremedia.livecontext.p13n.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.personalization.preview.PreviewPersonalizationHandlerInterceptor;
import com.coremedia.personalization.preview.TestContextSource;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * Suitable for URLs whose second segment denotes the store, e.g. /fragment/10001/...
 * It sets p13n-related request attributes out of the store context.
 */
public class FragmentCommerceP13nContextInterceptor extends AbstractCommerceContextInterceptor {

  private LiveContextSiteResolver liveContextSiteResolver;

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }

  @Override
  protected void initStoreContext(Site site, HttpServletRequest request) {
    super.initStoreContext(site, request);
    if (isCommerceContextAvailable()) {
      StoreContext currentContext = Commerce.getCurrentConnection().getStoreContext();
      if (currentContext != null) {
        Context context = LiveContextContextHelper.fetchContext(request);
        if (context != null) {
          if (isPreview()) {
            String testContextFlag = (String) context.get(PreviewPersonalizationHandlerInterceptor.QUERY_PARAMETER_TESTCONTEXT);
            String testContextId = (String) context.get(TestContextSource.QUERY_PARAMETER_TESTCONTEXTID);
            if ("true".equals(testContextFlag) && testContextId != null && !testContextId.equals("0")) {
              request.setAttribute(PreviewPersonalizationHandlerInterceptor.QUERY_PARAMETER_TESTCONTEXT, testContextFlag);
              request.setAttribute(TestContextSource.QUERY_PARAMETER_TESTCONTEXTID, testContextId);
            }
          }
        }
      }
    }
  }

  @Override
  @Nullable
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    FragmentParameters parameters = FragmentContextProvider.getFragmentContext(request).getParameters();
    return liveContextSiteResolver.findSiteFor(parameters);
  }
}

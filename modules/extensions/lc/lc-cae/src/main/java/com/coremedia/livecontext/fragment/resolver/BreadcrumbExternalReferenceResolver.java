package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.resolver.ContentSeoSegmentExternalReferenceResolver.Ids;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Resolves the breadcrumb for full page layouts in the commerce led scenario.
 */
public class BreadcrumbExternalReferenceResolver extends ExternalReferenceResolverBase {
  private static final String PREFIX = "cm-breadcrumb";
  public BreadcrumbExternalReferenceResolver() {
    super(PREFIX);
  }

  // --- properties --------------------------------------------------

  private String storefrontUrl;
  private StoreContextProvider storeContextProvider;
  private boolean lowerCaseSiteName = true;

  @Required
  public void setStorefrontUrl(String storefrontUrl) {
    this.storefrontUrl = storefrontUrl;
  }

  @Required
  public void setStoreContextProvider(StoreContextProvider storeContextProvider) {
    this.storeContextProvider = storeContextProvider;
  }

  public void setLowerCaseSiteName(boolean lowerCaseSiteName) {
    this.lowerCaseSiteName = lowerCaseSiteName;
  }

  // --- interface --------------------------------------------------
  @Override
  protected boolean include(@Nonnull FragmentParameters fragmentParameters, @Nonnull String referenceInfo) {
    return true;
  }

  @Nullable
  @Override
  protected LinkableAndNavigation resolveExternalRef(@Nonnull FragmentParameters fragmentParameters,
                                                     @Nonnull String referenceInfo,
                                                     @Nonnull Site site) {
    //no SEO segment passed, so we only render the root channel which results in an empty breadcrumb
    if(fragmentParameters.getParameter() == null) {
      Content channel = site.getSiteRootDocument();
      return new LinkableAndNavigation(channel, channel);
    }

    //regular breadcrumb building using the SEO segment instead                                               
    Ids ids = ContentSeoSegmentExternalReferenceResolver.parseExternalReferenceInfo(fragmentParameters.getParameter());
    String contentId= IdHelper.formatContentId(ids.contentId);
    Content linkable = contentRepository.getContent(contentId);

    Content channel;
    if(ids.contextId != null) {
      String contextId= IdHelper.formatContentId(ids.contextId);
      channel = contentRepository.getContent(contextId);
    }
    else {
      channel = linkable;
    }

    //use the fragment parameters locale, not the one of the site
    StoreContext storeContext = storeContextProvider.getCurrentContext();
    String storeName = storeContext.getStoreName();
    if(lowerCaseSiteName) {
      storeName = storeName.toLowerCase(fragmentParameters.getLocale());
    }
    String homepageUrl = storefrontUrl + fragmentParameters.getLocale().getLanguage() + "/" + storeName;
    fragmentParameters.getMatrixParams().put(FragmentParameters.PARAMETER, homepageUrl);

    return new LinkableAndNavigation(linkable, channel);
  }
}

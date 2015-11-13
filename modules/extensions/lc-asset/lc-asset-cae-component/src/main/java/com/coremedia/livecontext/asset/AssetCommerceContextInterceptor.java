package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Initialize Context for asset urls (e.g. {@link CMCatalogPictureHandler#URI_PATTERN})
 */
public class AssetCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  LiveContextSiteResolver liveContextSiteResolver;
  @Nullable
  @Override
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    String storeId = extractStoreId(normalizedPath);
    Locale locale = extractLocale(normalizedPath);

    return liveContextSiteResolver.findSiteFor(storeId, locale) ;
  }

  private String extractStoreId(String path) {
    String[] split = path.split("/");
    if (split.length != 6) {
      throw new IllegalArgumentException("Cannot handle path " + path);
    }
    return split[2];
  }

  private Locale extractLocale(String path) {
    String[] split = path.split("/");
    if (split.length != 6) {
          throw new IllegalArgumentException("Cannot handle path " + path);
        }
    return LocaleUtils.toLocale(split[3]);
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }
}

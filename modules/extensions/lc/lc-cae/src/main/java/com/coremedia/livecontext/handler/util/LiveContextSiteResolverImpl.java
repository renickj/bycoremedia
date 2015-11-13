package com.coremedia.livecontext.handler.util;

import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Utility class for resolving a site from an URL.
 */
public class LiveContextSiteResolverImpl implements LiveContextSiteResolver {
  private static final Logger LOG = LoggerFactory.getLogger(LiveContextSiteResolverImpl.class);

  private SiteResolver delegate;
  private SitesService sitesService;
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Nullable
  @Override
  public Site findSiteFor(@Nonnull FragmentParameters fragmentParameters) {
    String environment = fragmentParameters.getEnvironment();
    if (!StringUtils.isEmpty(environment)) {
      Site site = findSiteForEnvironment(fragmentParameters.getLocale(), environment);
      if (site != null) {
        return site;
      }
    }
    return findSiteFor(fragmentParameters.getStoreId(), fragmentParameters.getLocale());
  }

  // --------------- Helper ---------------------------------


  @Nullable
  @Override
  public Site findSiteFor(@Nonnull String storeId, @Nonnull Locale locale) {
    Set<Site> sites = sitesService.getSites();
    CommerceConnection oldConnection = Commerce.getCurrentConnection();
    Set<Site> matchingSites = new HashSet<>();
    try {

      for (Site site : sites) {
        try {
          commerceConnectionInitializer.init(site);
          CommerceConnection connection = Commerce.getCurrentConnection();
          if (connection != null) {
            StoreContext storeContext = connection.getStoreContext();
            if (storeContext != null && storeId.equals(storeContext.get("storeId"))) {
              Locale siteLocale = site.getLocale();
              if (locale.equals(siteLocale)) {
                matchingSites.add(site);
              }
              if (StringUtil.isEmpty(locale.getCountry()) && locale.getLanguage().equals(siteLocale.getLanguage())) {
                matchingSites.add(site);
              }
            }
          }
        } catch (Exception e) {
          LOG.warn("Something is wrong with store context for site {}({})", site.getName(), site.getLocale(), e);
        }
      }

    }
    finally {
      Commerce.setCurrentConnection(oldConnection);
    }

    if (matchingSites.size() > 1) {
      throw new IllegalStateException("Found more than one site for store.id: " + storeId + " and locale: " + locale);
    } else if (matchingSites.size() == 1) {
      return matchingSites.iterator().next();
    }

    return null;
  }

  /**
   * Extracts the site name out of the environment parameter String, e.g. site:PerfectChef
   *
   * @param locale The locale passed for the fragment request.
   * @param environment The name of the environment which contains the site name to use.
   * @return The site that was resolved by the environment (name matching by default).
   */
  private Site findSiteForEnvironment(@Nonnull Locale locale, @Nonnull String environment) {
    if (environment.contains("site:")) {
      String siteName = environment.split(":")[1];
      Set<Site> sites = sitesService.getSites();
      for (Site site : sites) {
        if (site.getName().equals(siteName) && site.getLocale().equals(locale)) {
          return site;
        }
      }
    }
    return null;
  }


  // -------------- Defaults ------------------------------

  @Override
  public Site findSiteByPath(String normalizedPath) {
    return delegate.findSiteByPath(normalizedPath);
  }

  @Override
  public Site findSiteBySegment(String siteSegment) {
    return delegate.findSiteBySegment(siteSegment);
  }

  @Override
  public Site findSiteForPathWithContentId(String normalizedPath) {
    return delegate.findSiteForPathWithContentId(normalizedPath);
  }

  @Override
  public Site findSiteForContentId(int contentId) {
    return delegate.findSiteForContentId(contentId);
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setDelegate(SiteResolver delegate) {
    this.delegate = delegate;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }
}

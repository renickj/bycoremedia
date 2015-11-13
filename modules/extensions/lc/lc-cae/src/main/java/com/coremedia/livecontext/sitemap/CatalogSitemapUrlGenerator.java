package com.coremedia.livecontext.sitemap;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.sitemap.SitemapUrlGenerator;
import com.coremedia.blueprint.cae.sitemap.UrlCollector;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.handler.ExternalPageHandler;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.livecontext.product.ProductPageHandler;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;

public class CatalogSitemapUrlGenerator implements SitemapUrlGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(CatalogSitemapUrlGenerator.class);

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private LinkFormatter linkFormatter;
  private SettingsService settingsService;

  // --- configuration ----------------------------------------------

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }


  // --- SitemapUrlGenerator ----------------------------------------

  @Override
  public void generateUrls(HttpServletRequest request, HttpServletResponse response, Site site, boolean absoluteUrls, String protocol, UrlCollector urlCollector) {
    if (site==null) {
      throw new IllegalArgumentException("Cannot derive a site from " + request.getPathInfo());
    }
    try {
      StoreContext storeContext = getStoreContextProvider().findContextBySite(site);
      if (storeContext != null) {
        // Deep links have a different domain and must thus not be included
        // in sitemaps.org sitemaps.
        boolean deepLinksOnly = useCommerceCategoryLinks(site) && useCommerceProductLinks(site);
        if (!deepLinksOnly) {
          request.setAttribute(ABSOLUTE_URI_KEY, absoluteUrls);
          List<Category> categories = getCatalogService().findTopCategories(site);
          generateUrls(categories, site, request, response, protocol, urlCollector);
        } else {
          LOG.debug("Only deep links for {}", site);
        }
      } else {
        // Legal state: A web presence may have sites which are not related to eCommerce.
        LOG.debug("No store context for {}", site);
      }
    } catch (InvalidContextException e) {
      LOG.info("Cannot create a sitemap for '{}' because the site has no valid store context. " +
              "I assume the site is not a shop and proceed without creating a catalog sitemap.", site.getName());
    }
  }


  // --- internal ---------------------------------------------------

  private boolean useCommerceProductLinks(Site site) {
    return settingsService.settingWithDefault(ProductPageHandler.LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS, Boolean.class, true, site);
  }

  private boolean useCommerceCategoryLinks(Site site) {
    return settingsService.settingWithDefault(ExternalPageHandler.LIVECONTEXT_POLICY_COMMERCE_CATEGORY_LINKS, Boolean.class, false, site);
  }

  private void generateUrls(List<Category> categories, Site site, HttpServletRequest request, HttpServletResponse response, String protocol, UrlCollector urlCollector) {
    // Must not include deep links in sitemap
    if (!useCommerceProductLinks(site)) {
      for (Category category : categories) {
        // Only include the category's products if the category has a context,
        // i.e. if some parent is linked into the navigation as an external channel.
        LiveContextNavigation liveContextNavigation = liveContextNavigationFactory.createNavigation(category, site);
        if (liveContextNavigation.getContext()!=null) {
          for (Product product : category.getProducts()) {
            ProductInSite productInSite = liveContextNavigationFactory.createProductInSite(product, site);
            generateUrl(productInSite, request, response, protocol, urlCollector);
          }
        }
        generateUrls(category.getChildren(), site, request, response, protocol, urlCollector);
      }
    }
  }

  /**
   * ecommerceItem is a Product or a LiveContextNavigation,
   * which have no common super class.
   */
  private void generateUrl(Object ecommerceItem, HttpServletRequest request, HttpServletResponse response, String protocol, UrlCollector urlCollector) {
    try {
      String link = linkFormatter.formatLink(ecommerceItem, null, request, response, false);
      // Must null-check, because there may be unlinkable ecommerce items.
      if (link!=null) {
        // Make absolutely absolute
        if (link.startsWith("//")) {
          link = protocol + ":" + link;
        }
        urlCollector.appendUrl(link);
      }
    } catch (Exception e) {
      LOG.warn("cannot create link for " + ecommerceItem, e);
    }
  }
}

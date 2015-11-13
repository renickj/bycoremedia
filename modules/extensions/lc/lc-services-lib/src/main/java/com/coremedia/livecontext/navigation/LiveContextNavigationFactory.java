package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.CategoryInSite;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;

import static org.springframework.util.Assert.notNull;

public class LiveContextNavigationFactory {

  private LiveContextNavigationTreeRelation liveContextNavigationTreeRelation;
  private SitesService sitesService;

  /**
   * Creates a new live context navigation from the given category.
   * Since the category and therefore the corresponding store is already resolved,
   * we don't need to pass the channel document here.
   *
   * @param category The category the navigation should be build for.
   */
  public LiveContextNavigation createNavigation(@Nonnull Category category, @Nonnull Site site) {
    notNull(category);
    notNull(site);
    return new LiveContextCategoryNavigation(category, site, liveContextNavigationTreeRelation);
  }

  /**
   * Creates a new LiveContextNavigation by searching a category in the catalog by the given seo segment.
   * The context of the catalog (which shop should contain the seo segment) is resolved from a channel.
   *
   * @param parentChannel the channel to resolve the store context for
   * @param seoSegment the seo segment of the category which should be wrapped in a LiveContextNavigation
   * @return category the category found for given seo segment
   */
  public LiveContextNavigation createNavigationBySeoSegment(@Nonnull Content parentChannel, @Nonnull String seoSegment) {
    notNull(parentChannel);
    notNull(seoSegment);

    StoreContext storeContext = getStoreContextProvider().findContextByContent(parentChannel);
    notNull(storeContext, "No StoreContext found for "+parentChannel.getName());
    Category category = getCatalogService().findCategoryBySeoSegment(seoSegment);
    notNull(category, "No category found for seo segment: "+seoSegment);
    Site site = sitesService.getContentSiteAspect(parentChannel).getSite();
    notNull(site, "No site found for " + parentChannel);

    return new LiveContextCategoryNavigation(category, site, liveContextNavigationTreeRelation);
  }

  public CategoryInSite createCategoryInSite(Category category, String siteId) {
    notNull(category);
    Site site = sitesService.getSite(siteId);
    return createCategoryInSite(category, site);
  }

  public ProductInSite createProductInSite(Product product, String siteId) {
    notNull(product);
    Site site = sitesService.getSite(siteId);
    return createProductInSite(product, site);
  }

  public CategoryInSite createCategoryInSite(Category category, Site site) {
    notNull(category);
    notNull(site);
    return new CategoryInSiteImpl(category, site);
  }

  public ProductInSite createProductInSite(Product product, Site site) {
    notNull(product);
    notNull(site);
    return new ProductInSiteImpl(product, site);
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setLiveContextNavigationTreeRelation(LiveContextNavigationTreeRelation liveContextNavigationTreeRelation) {
    this.liveContextNavigationTreeRelation = liveContextNavigationTreeRelation;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

}

package com.coremedia.livecontext.elastic.social.common;


import com.coremedia.blueprint.elastic.social.common.ContributionTargetTransformer;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.navigation.ProductInSiteImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

@Named
class ProductTransformer implements ContributionTargetTransformer<Product,ProductInSite> {

  @Inject
  private SitesService sitesService;

  @Override
  @Nonnull
  public ProductInSite transform(@Nonnull Product target) {
    StoreContext context = target.getContext();
    String siteId = context.getSiteId();
    Site site = sitesService.getSite(siteId);
    return new ProductInSiteImpl(target, site);
  }

  @Override
  @Nullable
  public Site getSite(@Nonnull Product target) {
    return sitesService.getSite(target.getContext().getSiteId());
  }

  @Override
  @Nonnull
  public Class<Product> getType() {
    return Product.class;
  }
}

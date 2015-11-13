package com.coremedia.livecontext.navigation;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;

import javax.annotation.Nonnull;

import static org.springframework.util.Assert.notNull;

/**
 * Immutable instances of ProductInSite.
 */
public class ProductInSiteImpl implements ProductInSite {
  private final Product product;
  private final Site site;

  public ProductInSiteImpl(Product product, Site site) {
    notNull(product);
    notNull(site);
    this.product = product;
    this.site = site;
  }

  @Nonnull
  @Override
  public Product getProduct() {
    return product;
  }

  @Nonnull
  @Override
  public Site getSite() {
    return site;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProductInSiteImpl that = (ProductInSiteImpl) o;

    if (product != null ? !product.equals(that.product) : that.product != null) {
      return false;
    }
    if (site != null ? !site.equals(that.site) : that.site != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = product != null ? product.hashCode() : 0;
    result = 31 * result + (site != null ? site.hashCode() : 0);
    return result;
  }
}

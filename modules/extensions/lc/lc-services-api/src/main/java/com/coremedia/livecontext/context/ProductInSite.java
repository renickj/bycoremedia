package com.coremedia.livecontext.context;

import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.cap.multisite.Site;

/**
 * A product can occur in several sites.
 * ProductInSite associates a product with a site for deterministic link building.
 */
public interface ProductInSite extends CommerceObject {
  /**
   * Returns the product.
   * @return the product
   */
  Product getProduct();

  /**
   * Returns the site.
   * @return the site.
   */
  Site getSite();
}

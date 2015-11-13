package com.coremedia.livecontext.context;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public class ResolveProductContextStrategy extends AbstractResolveContextStrategy {
  @Nullable
  @Override
  public LiveContextNavigation resolveContext(@Nonnull Site site, @Nonnull String seoSegment) {
    notNull(site);
    hasText(seoSegment);

    return getCache().get(new ProductContextProviderCacheKey(site, seoSegment));
  }

  @Override
  protected Category findNearestCategoryFor(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    notNull(storeContext);
    hasText(seoSegment);
    Product product = getCatalogService().withStoreContext(storeContext).findProductById(
            getCurrentCommerceIdProvider().formatProductSeoSegment(seoSegment));
    if (product != null) {
      return product.getCategory();
    }

    throw new IllegalArgumentException("Could not find a product with SEO segment \"" + seoSegment + "\"");
  }

  private final class ProductContextProviderCacheKey extends AbstractCommerceContextProviderCacheKey {
    private ProductContextProviderCacheKey(@Nonnull Site site, @Nonnull String seoSegment) {
      super(site, seoSegment);
    }
  }
}

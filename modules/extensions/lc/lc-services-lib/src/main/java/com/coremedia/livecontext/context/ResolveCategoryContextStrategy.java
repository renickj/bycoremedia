package com.coremedia.livecontext.context;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public class ResolveCategoryContextStrategy extends AbstractResolveContextStrategy {
  @Nullable
  @Override
  public LiveContextNavigation resolveContext(@Nonnull Site site, @Nonnull String seoSegment) {
    notNull(site);
    hasText(seoSegment);

    return getCache().get(new CategoryContextProviderCacheKey(site, seoSegment));
  }

  @Override
  protected @Nullable Category findNearestCategoryFor(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    hasText(seoSegment);
    notNull(storeContext);

    StoreContextProvider storeContextProvider = getStoreContextProvider();
    storeContextProvider.setCurrentContext(storeContext);
    return getCatalogService().withStoreContext(storeContext).findCategoryBySeoSegment(seoSegment);
  }

  private final class CategoryContextProviderCacheKey extends AbstractCommerceContextProviderCacheKey {
    private CategoryContextProviderCacheKey(@Nonnull Site site, @Nonnull String seoSegment) {
      super(site, seoSegment);
    }
  }
}

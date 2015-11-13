package com.coremedia.livecontext.context;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public abstract class AbstractResolveContextStrategy implements ResolveContextStrategy {
  private static final long DEFAULT_CACHED_IN_SECONDS = 24 * 60 * 60;

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private long cachedInSeconds = DEFAULT_CACHED_IN_SECONDS;
  private Cache cache;

  @Nullable
  protected abstract Category findNearestCategoryFor(@Nonnull String externalDescriptor, @Nonnull StoreContext storeContext);

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  protected Cache getCache() {
    return cache;
  }


  protected CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  protected StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public void setCachedInSeconds(long cachedInSeconds) {
    this.cachedInSeconds = cachedInSeconds;
  }

  protected long getCachedInSeconds() {
    return cachedInSeconds;
  }

  protected abstract class AbstractCommerceContextProviderCacheKey extends CacheKey<LiveContextNavigation> {
    private final String externalDescriptor;
    private final Site site;

    protected AbstractCommerceContextProviderCacheKey(@Nonnull Site site, @Nonnull String externalDescriptor) {
      this.site = site;
      this.externalDescriptor = externalDescriptor;
    }

    @Override
    public LiveContextNavigation evaluate(Cache cache) throws Exception {
      Cache.cacheFor(getCachedInSeconds(), TimeUnit.SECONDS);
      StoreContext storeContext = getStoreContextProvider().findContextBySite(site);

      if (storeContext == null) {
        throw new IllegalArgumentException("Could not find a store context for site \"" + site.getName()+ "\"");
      }

      Category category = findNearestCategoryFor(externalDescriptor, storeContext);
      if (category == null) {
        throw new IllegalArgumentException("Could not find a category for external descriptor \"" + externalDescriptor + "\"");
      }

      return liveContextNavigationFactory.createNavigation(category, site);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      AbstractCommerceContextProviderCacheKey that = (AbstractCommerceContextProviderCacheKey) o;

      if (!externalDescriptor.equals(that.externalDescriptor)) {
        return false;
      }
      //noinspection RedundantIfStatement
      if (!site.equals(that.site)) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result = externalDescriptor.hashCode();
      result = 31 * result + site.hashCode();
      return result;
    }
  }
}

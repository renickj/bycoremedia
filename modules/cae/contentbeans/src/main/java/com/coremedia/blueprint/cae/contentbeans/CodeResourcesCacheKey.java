package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.objectserver.dataviews.DataViewHelper;

/**
 * Cache Key for caching the {@link com.coremedia.blueprint.common.contentbeans.CodeResources} for a
 * {@link com.coremedia.blueprint.common.contentbeans.CMContext}
 */
public class CodeResourcesCacheKey extends CacheKey<CodeResources> {

  private final boolean developerMode;
  private final CMContext context;
  private final String codePropertyName;

  public CodeResourcesCacheKey(CMContext context, String codePropertyName, boolean developerMode) {
    this.developerMode = developerMode;
    this.context = DataViewHelper.getOriginal(context);
    this.codePropertyName = codePropertyName;
  }

  @Override
  public CodeResources evaluate(Cache cache) throws Exception {
    return new CodeResourcesImpl(context,codePropertyName,developerMode);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CodeResourcesCacheKey that = (CodeResourcesCacheKey) o;

    if (developerMode != that.developerMode) {
      return false;
    }
    if (!codePropertyName.equals(that.codePropertyName)) {
      return false;
    }
    if (!context.equals(that.context)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = developerMode ? 1 : 0;
    result = 31 * result + context.hashCode();
    result = 31 * result + codePropertyName.hashCode();
    return result;
  }

}

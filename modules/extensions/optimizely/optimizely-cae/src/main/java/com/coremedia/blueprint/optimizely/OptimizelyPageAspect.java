package com.coremedia.blueprint.optimizely;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cae.aspect.AspectAggregatorAware;

public interface OptimizelyPageAspect extends AspectAggregatorAware, Aspect<Page> {

  boolean isEnabled();

  String getOptimizelyId();
}

package com.coremedia.blueprint.osm;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cae.aspect.AspectAggregatorAware;

public interface OpenStreetMapAspect extends AspectAggregatorAware, Aspect<CMTeasable> {

  boolean isEnabled();
}

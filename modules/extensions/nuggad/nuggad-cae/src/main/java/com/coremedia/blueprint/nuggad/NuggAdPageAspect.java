package com.coremedia.blueprint.nuggad;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cae.aspect.AspectAggregatorAware;

public interface NuggAdPageAspect extends AspectAggregatorAware, Aspect<Page> {

  boolean isEnabled();

  String getNuggn();

  String getNuggsid();
}

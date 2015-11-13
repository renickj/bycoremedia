package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGridPlacement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultPageGridPlacementResolver implements PageGridPlacementResolver {
  @Nullable
  @Override
  public PageGridPlacement resolvePageGridPlacement(@Nonnull CMChannel context, @Nonnull String placementName) {
    return context.getPageGrid().getPlacementForName(placementName);
  }
}

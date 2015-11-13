package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGridPlacement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PageGridPlacementResolver {
  @Nullable
  PageGridPlacement resolvePageGridPlacement(@Nonnull CMChannel context, @Nonnull String placementName);
}

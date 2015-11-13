package com.coremedia.livecontext.context;


import com.coremedia.cap.multisite.Site;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ResolveContextStrategy {
  @Nullable
  LiveContextNavigation resolveContext(@Nonnull Site site, @Nonnull String subjectDescriptor);
}

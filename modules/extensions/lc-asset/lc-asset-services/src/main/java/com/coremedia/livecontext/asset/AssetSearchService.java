package com.coremedia.livecontext.asset;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;

import javax.annotation.Nonnull;
import java.util.List;

public interface AssetSearchService {

  /**
   * Find assets in a search index and return contents.
   * @param contentType the requested content type
   * @param externalId the external id of the requested asset
   * @param site the site
   * @return the found list of contents or an empty list
   */
  @Nonnull
  List<Content> searchAssets(@Nonnull String contentType, @Nonnull String externalId, @Nonnull Site site);
}

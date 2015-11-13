package com.coremedia.livecontext.fragment;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cache Key that computes all instances of CMExternalChannel documents that have a not empty externalId property.
 * It is done for each site and it will be invalidated automatically if the resulting set is changed because of
 * content changes.
 */
class CMExternalChannelBySiteCacheKey extends CacheKey<Map<String,Content>> {

  private static final Logger LOG = LoggerFactory.getLogger(CMExternalChannelBySiteCacheKey.class);

  private final Site site;

  CMExternalChannelBySiteCacheKey(@Nonnull Site site) {
    this.site = site;
  }

  public Map<String,Content> evaluate(Cache cache) throws Exception {
    Map<String, Content> result = new HashMap<>();
    Content siteRootFolder = site.getSiteRootFolder();
    ContentRepository repository = siteRootFolder.getRepository();
    // first get CMExternalChannel documents (in all sites)
    ContentType docType = repository.getContentType(CMExternalChannel.NAME);
    if (docType == null) {
      LOG.error("doctype {} is not defined in repository", CMExternalChannel.NAME);
      return result;
    }
    Set<Content> allExternalChannels = docType.getInstances();
    // now get only those CMExternalChannels in the desired site
    Collection<Content> externalChannels = repository.getQueryService().getContentsFulfilling(
            allExternalChannels,
            "TYPE CMExternalChannel: BELOW ?0 AND " + CMExternalChannel.EXTERNAL_ID + " IS NOT NULL ORDER BY id DESC",
            siteRootFolder);
    for (Content content : externalChannels) {
      if (content != null) {
        String externalId = content.getString(CMExternalChannel.EXTERNAL_ID);
        result.put(externalId, content);
      }
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CMExternalChannelBySiteCacheKey that = (CMExternalChannelBySiteCacheKey) o;

    //noinspection RedundantIfStatement
    if (!site.equals(that.site)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return site.hashCode();
  }
}

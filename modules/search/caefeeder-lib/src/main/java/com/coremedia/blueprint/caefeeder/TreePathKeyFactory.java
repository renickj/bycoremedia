package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.persistentcache.PersistentCache2;
import com.coremedia.cap.persistentcache.PersistentCacheKey;
import com.coremedia.cap.persistentcache.PersistentCacheKeyFactory;

/**
 * A factory for the CAE Feeder to compute the ids of all ancestors of a node in a {@link com.coremedia.blueprint.base.tree.TreeRelation}.
 * <p>
 * The path is a slash-separated string consisting of the numeric content IDs of all ancestor nodes.
 * example: "/2/10/6/4/8"
 */
public class TreePathKeyFactory implements PersistentCacheKeyFactory {

  private PersistentCache2 persistentCache;
  private ContentRepository contentRepository;
  private TreeRelation<Content> treeRelation;
  private String keyPrefix;

  public void setPersistentCache(PersistentCache2 persistentCache) {
    this.persistentCache = persistentCache;
  }

  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  public void setTreeRelation(TreeRelation<Content> treeRelation) {
    this.treeRelation = treeRelation;
  }

  public void setKeyPrefix(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }

  public String getKeyPrefix() {
    return keyPrefix;
  }

  @Override
  public PersistentCacheKey createKey(String serializedKey) {
    if (serializedKey.startsWith(keyPrefix)) {
      String s = serializedKey.substring(keyPrefix.length());
      return keyForId(Integer.parseInt(s));
    }
    return null;
  }

  protected PersistentCacheKey keyForId(int id) {
    return new TreePathKey(this, contentRepository, treeRelation, id);
  }

  public String getPath(Content node) {
    return (String)persistentCache.getCached(keyForId(IdHelper.parseContentId(node.getId())));
  }

  public String getPathSegment(Content content) {
    return String.valueOf(IdHelper.parseContentId(content.getId()));
  }
}

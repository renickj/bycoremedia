package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.persistentcache.InvalidPersistentCacheKeyException;
import com.coremedia.cap.persistentcache.RevalidatingFragmentPersistentCacheKey;

/**
 * A {@link com.coremedia.cap.persistentcache.RevalidatingFragmentPersistentCacheKey} for the CAE Feeder
 * to compute the ids of all ancestors of a node in a {@link com.coremedia.blueprint.base.tree.TreeRelation}.
 * <p>
 * The tree path is a string consisting of the slash-prefixed path segments of all ancestor taxonomy nodes,
 * where by default, the path segment is the numeric content ID.
 * example: "/2/10/6/4/8"
 * <p>
 *   Historical note regarding tree path syntax:
 *   an immutable list of strings would have served equally well, but the value hashes and therefore
 *   the path syntax must be compatible with the previously used NavigationPathKey
 *   to avoid invalidation and complete reindexing of the content base.
 * </p>
 */
public class TreePathKey extends RevalidatingFragmentPersistentCacheKey<String> {

  private final TreePathKeyFactory keyFactory;
  private final ContentRepository contentRepository;
  private final TreeRelation<Content> treeRelation;
  private final int id;

  public TreePathKey(TreePathKeyFactory keyFactory, ContentRepository contentRepository, TreeRelation<Content> treeRelation, int id) {
    this.keyFactory = keyFactory;
    this.contentRepository = contentRepository;
    this.treeRelation = treeRelation;
    this.id = id;
  }

  @Override
  public String getSerialized() {
    return keyFactory.getKeyPrefix() + id;
  }

  @Override
  public String evaluate() {
    Content content = contentRepository.getContent(IdHelper.formatContentId(id));
    if (content == null) {
      throw new InvalidPersistentCacheKeyException(getSerialized());
    }

    String current = '/' + keyFactory.getPathSegment(content);

    // cycles in the navigation structure will be detected by the PersistentCache (and answered with an IllegalStateException)
    Content parent = treeRelation.getParentUnchecked(content);
    if (parent==null) {
      return current;
    }

    String parentPath = keyFactory.getPath(parent);
    return parentPath + current;
  }
}

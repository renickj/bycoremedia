package com.coremedia.blueprint.common.contentbeans;

import java.util.List;

/**
 * Represents the list of code resources of a navigation context.
 */
public interface CodeResources {

  /**
   * @return the context
   */
  CMContext getContext();

  /**
   * @return a unique hash calculated for all resources in {@link #getLinkTargetList()}
   */
  String getETag();

  /**
   * @return the subset of resources that may be merged into a single resource.
   */
  List<CMAbstractCode> getMergeableResources();

  /**
   * The returned code resources are ordered as follows: 1. external resources, 2. non-IE-excludes resources 3. IE-excludes.
   *
   * @return a list of resources which are linked to the {@link #getContext() context} of this code resource.
   */
  List<?> getLinkTargetList();
}

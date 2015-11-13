package com.coremedia.blueprint.analytics.elastic.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

/**
 * The bean corresponding to the <code>CMALXPageList</code> document type. It selects its list of
 * {@link CMLinkable}s by firing an analytics query.
 */
public class CMALXPageListImpl extends CMALXPageListBase {

  /**
   * Get the list of {@link CMLinkable}s selected by an analytics query.
   *
   * @return the list of {@link CMLinkable}s selected by an analytics query
   */
  @Override
  public List<CMLinkable> getItemsUnfiltered() {
    final Iterable trackedLinkables = Iterables.filter(getTrackedItemsUnfiltered(), Predicates.instanceOf(CMLinkable.class));
    @SuppressWarnings("unchecked") // we've just removed the non-linkables!
    final List<CMLinkable> result = copyOf(trackedLinkables);
    return result.isEmpty() ? getDefaultContent() : result;
  }
}

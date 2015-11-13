package com.coremedia.blueprint.common.layout;

import java.util.List;

/**
 *
 * @param <T>
 */
public interface Container<T> {
  /**
   * Retrieves the items of the implementing class.
   * @return a list of items computed for the backing content 'proxy' object
   */
  List<? extends T> getItems();
}

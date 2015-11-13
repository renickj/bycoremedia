package com.coremedia.blueprint.common.contentbeans;


import com.coremedia.blueprint.common.feeds.FeedSource;
import com.coremedia.blueprint.common.layout.Container;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A collection of contents.
 * <p>
 * Although Collection is not abstract, you are encouraged
 * to subclass it and implement alternative strategies to fetch the items.
 *
 * <p>Represents the document type {@link #NAME CMCollection}.</p>
 */
public interface CMCollection<T> extends CMTeasable, FeedSource<T>, Container<T> {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMCollection'.
   */
  String NAME = "CMCollection";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMCollection} object
   */
  @Override
  CMCollection getMaster();

  @Override
  Map<Locale, ? extends CMCollection<T>> getVariantsByLocale();

  @Override
  Collection<? extends CMCollection<T>> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMCollection<T>>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMCollection<T>>> getAspects();

  /**
   * Name of the document property 'items'.
   */
  String ITEMS = "items";

  /**
   * Returns the value of the document property {@link #ITEMS}.
   *
   * @return a list of items computed for the backing content 'proxy' object
   */
  List<? extends T> getItems();
}

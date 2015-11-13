package com.coremedia.blueprint.ecommerce.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cae.aspect.Aspect;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface CMCategory extends CMChannel {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMChannel'.
   */
  String NAME = "CMCategory";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMCategory} object
   */
  @Override
  CMCategory getMaster();

  @Override
  Map<Locale, ? extends CMCategory> getVariantsByLocale();

  @Override
  Collection<? extends CMCategory> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMCategory>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMCategory>> getAspects();

  /**
   * Returns the subcategories of this category.
   *
   * @return immutable list of subcategories
   */
  @Nonnull
  List<CMCategory> getSubcategories();

  /**
   * Returns the products of this category.
   *
   * @return immutable list of products
   */
  @Nonnull
  List<CMProduct> getProducts();

}

package com.coremedia.blueprint.common.contentbeans;


import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.coremedia.xml.Markup;

/**
 * Currently CMArticle has no additional features compared to CMTeasable.
 * However, we decided to keep the abstract CMTeasable and the concrete CMArticle
 * in order to facilitate future changes.
 *
 * <p>Represents the document type {@link #NAME CMArticle}.</p>
 */
public interface CMArticle extends CMTeasable {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMArticle'.
   */
  String NAME = "CMArticle";
  String HERO_ITEM = "heroItems";
  String DESCRIPTION = "productDesc";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMArticle} object
   */
  @Override
  CMArticle getMaster();

  @Override
  Map<Locale, ? extends CMArticle> getVariantsByLocale();

  @Override
  Collection<? extends CMArticle> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMArticle>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMArticle>> getAspects();
  
  List<? extends CMTeasable> getHeroItems();
  
  Markup getProductDesc();
  
}

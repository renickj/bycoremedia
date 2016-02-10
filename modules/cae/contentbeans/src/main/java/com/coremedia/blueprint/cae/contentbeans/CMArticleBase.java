package com.coremedia.blueprint.cae.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.xml.Markup;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.coremedia.cap.content.Content;

/**
 * Generated base class for immutable beans of document type CMArticle.
 * Should not be changed.
 */
public abstract class CMArticleBase extends CMTeasableImpl implements CMArticle {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMArticle} objects
   */
  @Override
  public CMArticle getMaster() {
    return (CMArticle) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMArticle> getVariantsByLocale() {
    return getVariantsByLocale(CMArticle.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMArticle> getLocalizations() {
    return (Collection<? extends CMArticle>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMArticle>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMArticle>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMArticle>> getAspects() {
    return (List<? extends Aspect<? extends CMArticle>>) super.getAspects();
  }
  
  @Override
  public List<? extends CMTeasable> getHeroItems() {
    List<Content> contents = getContent().getLinks(HERO_ITEM);
    return createBeansFor(contents, CMTeasable.class);
  }
  
  @Override
  public Markup getProductDesc() {
    return getContent().getMarkup(DESCRIPTION);
  }
}

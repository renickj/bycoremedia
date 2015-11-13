package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMTeaser;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMTeaser.
 * Should not be changed.
 */
public abstract class CMTeaserBase extends CMTeasableImpl implements CMTeaser {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link com.coremedia.blueprint.common.contentbeans.CMTeaser} objects
   */
  @Override
  public CMTeaser getMaster() {
    return (CMTeaser) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMTeaser> getVariantsByLocale() {
    return getVariantsByLocale(CMTeaser.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMTeaser> getLocalizations() {
    return (Collection<? extends CMTeaser>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMTeaser>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMTeaser>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMTeaser>> getAspects() {
    return (List<? extends Aspect<? extends CMTeaser>>) super.getAspects();
  }

  /**
   * Returns the value of the document property {@link #TARGET},
   * but only if {@link #getValidationService()#validate} succeeds.
   *
   * @return the valid {@link CMLinkable} object
   */
  @Override
  public CMLinkable getTarget() {
    Content targetValue = getContent().getLink(TARGET);
    CMLinkable bean = createBeanFor(targetValue, CMLinkable.class);
    return bean != null && getValidationService().validate(bean) ? bean : null;
  }
}
  
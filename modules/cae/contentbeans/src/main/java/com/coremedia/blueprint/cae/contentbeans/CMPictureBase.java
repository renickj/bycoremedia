package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMPicture.
 * Should not be changed.
 */
public abstract class CMPictureBase extends CMVisualImpl implements CMPicture {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link com.coremedia.blueprint.common.contentbeans.CMPicture} objects
   */
  @Override
  public CMPicture getMaster() {
    return (CMPicture) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMPicture> getVariantsByLocale() {
    return getVariantsByLocale(CMPicture.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMPicture> getLocalizations() {
    return (Collection<? extends CMPicture>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMPicture>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMPicture>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMPicture>> getAspects() {
    return (List<? extends Aspect<? extends CMPicture>>) super.getAspects();
  }
}
  
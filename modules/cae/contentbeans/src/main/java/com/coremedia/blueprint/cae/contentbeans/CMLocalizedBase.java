package com.coremedia.blueprint.cae.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMLocalized;
import com.coremedia.blueprint.common.contentbeans.CMSymbol;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;


/**
 * Generated base class for immutable beans of document type CMLocalized.
 * Should not be changed.
 */
public abstract class CMLocalizedBase extends CMObjectImpl implements CMLocalized {

  private SitesService sitesService;

  public SitesService getSitesService() {
    return sitesService;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    if(sitesService == null) {
      throw new IllegalArgumentException("supplied 'sitesService' must not be null");
    }
    this.sitesService = sitesService;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMLocalized>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMLocalized>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMLocalized>> getAspects() {
    return (List<? extends Aspect<? extends CMLocalized>>) super.getAspects();
  }

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMLocalized} objects
   */
  @Override
  public CMLocalized getMaster() {
    Content masterValue = getContent().getLink(MASTER);
    return createBeanFor(masterValue, CMLocalized.class);
  }

  /**
   * Returns the value of the document property {@link #MASTER_VERSION}.
   *
   * @return the value of the document property {@link #MASTER_VERSION}
   */
  @Override
  public int getMasterVersion() {
    return getContent().getInt(MASTER_VERSION);
  }
}
  

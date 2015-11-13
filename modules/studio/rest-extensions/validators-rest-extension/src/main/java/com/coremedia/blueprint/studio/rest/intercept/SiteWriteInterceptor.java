package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.cap.multisite.SiteModel;
import com.coremedia.cap.multisite.impl.SiteIdUtil;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Initializes a site indicator document with a unique ID upon creation.
 * This is important as the site ID cannot be modified in CoreMedia Studio.
 */
public class SiteWriteInterceptor extends ContentWriteInterceptorBase {
  @Nullable
  private SiteModel siteModel;

  /**
   * SiteModel required in order to retrieve the ID property name
   * for the Site Indicator document type.
   *
   * @param siteModel the Site Model
   */
  public void setSiteModel(@Nullable final SiteModel siteModel) {
    this.siteModel = siteModel;
  }

  @Override
  public void intercept(ContentWriteRequest request) {
    final boolean isCreateRequest = request.getEntity() == null;
    if (siteModel != null && isCreateRequest) {
      final Map<String, Object> properties = request.getProperties();
      final String idPropertyKey = siteModel.getIdProperty();
      final Object previousValue = properties.get(idPropertyKey);
      if (previousValue == null
              || (previousValue instanceof String && ((String) previousValue).isEmpty())) {
        properties.put(idPropertyKey, SiteIdUtil.createRandomSiteId());
      }
    }
  }

}

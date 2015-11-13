package com.coremedia.blueprint.studio.rest.intercept;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;

import java.util.Map;

/**
 *
 */
public class ChannelWriteInterceptor extends ContentWriteInterceptorBase {

  private UrlPathFormattingHelper urlPathFormattingHelper;
  private String propertyName;

  public UrlPathFormattingHelper getUrlPathFormattingHelper() {
    return urlPathFormattingHelper;
  }

  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  @Override
  public void intercept(ContentWriteRequest request) {

    final Map<String,Object> properties = request.getProperties();
    final Object segment = properties.get(propertyName);

    if(segment != null) {
      properties.put(propertyName,urlPathFormattingHelper.tidyUrlPath(segment.toString()));
    }
  }

}

package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

/**
 * Provides preview token for commerce preview.
 */
public class PreviewTokenProvider implements CommercePropertyProvider {

  private LoginService loginService;

  @Override
  public Object provideValue(Map<String, Object> parameters) {
    String result = null;
    WcPreviewToken previewToken = loginService.getPreviewToken();
    if (previewToken != null) {
      result = previewToken.getPreviewToken();
    }
    return result;
  }

  @Required
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }

}

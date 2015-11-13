package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PreviewDateAppendingLinkTransformer implements LinkTransformer {

  private final ParameterAppendingLinkTransformer parameterAppender;

  public PreviewDateAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE);
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
    String previewDate = null;
    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    if (null != currentConnection) {
      previewDate = currentConnection.getStoreContext().getPreviewDate();
    }
    if (null != previewDate) {
      request.setAttribute(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE, previewDate);
    }
    return parameterAppender.transform(source, bean, view, request, response, forRedirect);
  }
}

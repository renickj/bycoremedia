package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.cae.action.CMActionState;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

/**
 * LinkTransformer implementation that adds the previewToken request parameter to page links.
 */
public class PreviewTokenAppendingLinkTransformer implements LinkTransformer {
  public static final String SCHEME_RELATIVE = "//";
  public static final String HTTP_SCHEME = "http://";
  public static final String HTTPS_SCHEME = "https://";
  public static final String QUERY_PARAMETER_P13N_TEST = "p13n_test";
  public static final String QUERY_PARAMETER_PREVIEW_TOKEN = "previewToken";

  private final ParameterAppendingLinkTransformer parameterAppender;

  private CommercePropertyProvider previewTokenProvider;

  private Pattern includePattern;

  private boolean preview;

  @Required
  public void setPreviewTokenProvider(CommercePropertyProvider previewTokenProvider) {
    this.previewTokenProvider = previewTokenProvider;
  }

  public boolean isPreview() {
    return preview;
  }

  @Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  @SuppressWarnings("unused")
  public void setIncludeFilter(String includeFilter) {
    includePattern = Pattern.compile(includeFilter);
  }

  public PreviewTokenAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(QUERY_PARAMETER_PREVIEW_TOKEN);
  }

  @PostConstruct
  void initialize() throws Exception {
    parameterAppender.afterPropertiesSet();
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
    if (isPreview()) {
      //if parameter is already available in current request append to all links
      if (request.getParameter(QUERY_PARAMETER_PREVIEW_TOKEN) != null){
        return parameterAppender.transform(source, bean, view, request, response, forRedirect);
      }

      // On AIX-environments regexp-pattern matching is time consuming.
      // Therefor only check schemes via "startsWith" by default.
      // all external link targets in a commerce context will have a preview token added
      if ((bean instanceof CMActionState ||
              (source != null && (source.startsWith(SCHEME_RELATIVE) || source.startsWith(HTTP_SCHEME) || source.startsWith(HTTPS_SCHEME))))
              && isStoreContextAvailable() && isStudioPreviewRequest(request)) {
        if (includePattern == null || includePattern.matcher(source).matches()) {
          String previewToken = (String) previewTokenProvider.provideValue(null);
          if (previewToken != null) {
            parameterAppender.setParameterValue(previewToken);
            return parameterAppender.transform(source, bean, view, request, response, forRedirect);
          }
        }
      }
    }
    return source;
  }

  private boolean isStudioPreviewRequest(HttpServletRequest request){
    return "true".equals(request.getParameter(QUERY_PARAMETER_P13N_TEST))
            || PreviewHandler.isStudioPreviewRequest();
  }

  private boolean isStoreContextAvailable(){
    return Commerce.getCurrentConnection() != null && Commerce.getCurrentConnection().getStoreContext() != null;
  }

}

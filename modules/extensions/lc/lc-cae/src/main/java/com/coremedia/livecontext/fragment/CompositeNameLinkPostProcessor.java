package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.livecontext.navigation.CompositeNameHelper;
import com.coremedia.objectserver.web.links.LinkPostProcessor;
import com.coremedia.objectserver.web.links.UriComponentsHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "TypeMayBeWeakened"})
@LinkPostProcessor
public class CompositeNameLinkPostProcessor {

  public static final String QUERY_PARAMETER_COMPOSITE_NAME = "compositeName";

  /**
   * Set or pass along the information if link was rendered from a PDP as query parameter
   *
   * @param originalUri
   * @param request
   * @return
   */
  private UriComponents addCompositeNameIfProvided(UriComponents originalUri, HttpServletRequest request) {
    String compositeName = CompositeNameHelper.getCurrentCompositeName();
    if (StringUtils.isNotBlank(compositeName)) {
      MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>();
      queryParameters.add(QUERY_PARAMETER_COMPOSITE_NAME, compositeName);
      UriComponentsBuilder uriBuilder = UriComponentsHelper.fromUriComponents(originalUri);
      UriComponentsHelper.addQueryParameters(queryParameters, uriBuilder);
      return uriBuilder.build();
    }
    return originalUri;
  }

  @SuppressWarnings("UnusedParameters")
  @LinkPostProcessor
  public Object addInPdpForFragmentLinks(UriComponents originalUri, Object o, HttpServletRequest request) {
    List<String> pathSegments = originalUri.getPathSegments();
    if (pathSegments.contains(UriConstants.Prefixes.PREFIX_DYNAMIC)) {
      return addCompositeNameIfProvided(originalUri, request);
    } else {
      return originalUri;
    }
  }
}

package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.common.contentbeans.CMExternalLink;
import com.coremedia.objectserver.web.links.Link;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Linkscheme for {@link com.coremedia.blueprint.common.contentbeans.CMExternalLink}
 */
@Link
public class ExternalLinkHandler extends HandlerBase {

  @Link(type = CMExternalLink.class)
  public UriComponents buildLinkForExternalLink(CMExternalLink externalLink) {
    String url = externalLink.getUrl();
    if (StringUtils.isBlank(url)) {
      return null;
    } else {
      return UriComponentsBuilder.fromUriString(url).build();
    }
  }
}

package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.links.Link;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ACTION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;

/**
 * Handler and Linkscheme for {@link com.coremedia.blueprint.common.contentbeans.CMAction} beans that are contained in a
 * {@link com.coremedia.blueprint.common.contentbeans.Page}. Can handle Webflows.
 */
@SuppressWarnings("LocalCanBeFinal")
@Link
@RequestMapping
public class PageActionHandler extends DefaultPageActionHandler {

  /**
   * URI pattern prefix for actions on page resources. The action name is appended to this URI with a slash.
   * The final URI might look like {@value #URI_PATTERN}
   */
  public static final String URI_PATTERN =
          '/' + PREFIX_DYNAMIC +
          '/' + SEGMENT_ACTION +
                  "/{" + SEGMENT_ROOT + '}' +
                  "/{" + SEGMENT_ID + ':' + PATTERN_NUMBER + '}' +
                  "/{" + SEGMENT_ACTION + '}';

  /**
   * Fallback: Handles all remaining actions by simply displaying the page
   */
  @RequestMapping(URI_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(SEGMENT_ROOT) String context,
                                    @PathVariable(SEGMENT_ACTION) String action,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
    return handleRequestInternal(contentBean, context, action, request, response);
  }

  @SuppressWarnings({"TypeMayBeWeakened", "UnusedParameters"})
  @Link(type = CMAction.class, uri = URI_PATTERN)
  @Nullable
  public UriComponents buildLink(
          @Nonnull CMAction action,
          @Nonnull UriTemplate uriPattern,
          @Nonnull Map<String, Object> linkParameters) {
    return buildLinkInternal(action, uriPattern, linkParameters);
  }
}

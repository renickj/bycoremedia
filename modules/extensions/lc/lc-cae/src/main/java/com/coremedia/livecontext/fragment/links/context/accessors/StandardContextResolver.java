package com.coremedia.livecontext.fragment.links.context.accessors;

import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.ContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Resolver for {@link Context} object from a given http request.
 * Counterpart of StandardContentConnector
 */
public class StandardContextResolver {
  private static final Logger LOG = LoggerFactory.getLogger(StandardContextResolver.class);

  public Context resolveContext(HttpServletRequest request) {

    ContextBuilder context = ContextBuilder.create();

    // extract context values from request
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = (String) headerNames.nextElement();
      if (headerName.startsWith(ContextBuilder.CONTEXT_KEY_PREFIX)) {
        String value = request.getHeader(headerName);
        context.withValue(headerName, value);
        LOG.debug("StandardContextResolver: added " + headerName + ":" + value);
      }
    }
    return context.build();
  }
}

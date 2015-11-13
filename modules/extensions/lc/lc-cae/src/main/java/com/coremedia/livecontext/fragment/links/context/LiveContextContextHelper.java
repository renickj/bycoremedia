package com.coremedia.livecontext.fragment.links.context;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper for accessing a {@link Context livecontext context}.
 */
public final class LiveContextContextHelper {

  private static final String CONTEXT_ATTRIBUTE = "com.coremedia.livecontext.CONTEXT";

  /**
   * Utility class should not have a public default constructor.
   */
  private LiveContextContextHelper() {
  }

  /**
   * Stores a context in the request and makes it available.
   */
  public static void setContext(HttpServletRequest request, Context context) {
    request.setAttribute(CONTEXT_ATTRIBUTE, context);
  }

  /**
   * Retrieve the context. Will NOT create a context if it does not exist
   */
  public static Context fetchContext(HttpServletRequest request) {
    if (request != null) {
      return (Context) request.getAttribute(CONTEXT_ATTRIBUTE);
    }
    return null;
  }

}

package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides access to the current {@link UserContext}. The handling itself is delegated to the {@link UserContextHelper}.
 */
public class UserContextProviderImpl implements UserContextProvider {

  private static final String STUDIO_PREVIEW_TEST_PARAM = "p13n_test";

  private UserSessionService userSessionService;

  @Override
  @Nonnull
  public UserContext getCurrentContext() {
    return UserContextHelper.getCurrentContext();
  }

  @Override
  public void setCurrentContext(UserContext userContext) {
    UserContextHelper.setCurrentContext(userContext);
  }

  @Nonnull
  @Override
  public UserContext createContext(@Nullable String loginName) {
    return createContext(null, loginName);
  }

  @Override
  @Nonnull
  public UserContext createContext(HttpServletRequest request, String loginName) {
    return createUserContext(request, loginName);
  }

  @Override
  public void clearCurrentContext() {
  }

  private UserContext createUserContext(HttpServletRequest request, String loginname) {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    StoreContextHelper.validateContext(storeContext);
    String userId = userSessionService.resolveUserId(request, storeContext.getStoreId(), false);
    UserContext context = UserContextHelper.createContext(loginname, userId);
    if (request != null && !isStudioPreviewRequest(request)) {
      String header = request.getHeader("Cookie");
      context.setCookieHeader(WcCookieHelper.rewritePreviewCookies(header));
    }
    return context;
  }

  private boolean isStudioPreviewRequest(HttpServletRequest request) {
    return "true".equals(request.getParameter(STUDIO_PREVIEW_TEST_PARAM));
  }

  // ------------ Config -----------------------

  @Required
  public void setUserSessionService(UserSessionService userSessionService) {
    this.userSessionService = userSessionService;
  }

}

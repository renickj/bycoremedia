package com.coremedia.livecontext.ecommerce.toko.user;

import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextBuilder;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public class UserContextProviderImpl implements UserContextProvider {

  private UserContext userContext;

  @Nonnull
  @Override
  public UserContext getCurrentContext() {
    return userContext;
  }

  @Override
  public void setCurrentContext(@Nullable UserContext userContext) {
    this.userContext = userContext;
  }

  @Nonnull
  @Override
  public UserContext createContext(@Nullable HttpServletRequest request, @Nullable String loginName) {
    return UserContextBuilder.create().build();
  }

  @Nonnull
  @Override
  public UserContext createContext(@Nullable String loginName) {
    return UserContextBuilder.create().build();
  }

  @Override
  public void clearCurrentContext() {
    this.userContext = null;
  }
}

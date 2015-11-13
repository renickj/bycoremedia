package com.coremedia.livecontext.web.links;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.objectserver.web.links.TokenResolver;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.springframework.core.annotation.Order;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@Named
@Order(50)
public class StoreContextTokenResolver implements TokenResolver {

  @Override
  public String resolveToken(String token, Object target, HttpServletRequest request) {
    if (Commerce.getCurrentConnection() != null) {
      StoreContext storeContext = Commerce.getCurrentConnection().getStoreContext();
      return storeContext != null && storeContext.get(token) != null ? String.valueOf(storeContext.get(token)) : null;
    }
    return null;
  }
}

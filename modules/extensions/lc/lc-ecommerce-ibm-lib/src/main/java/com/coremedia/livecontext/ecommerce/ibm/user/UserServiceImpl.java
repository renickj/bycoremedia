package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class UserServiceImpl implements UserService {

  private WcPersonWrapperService personWrapperService;
  private CommerceCache commerceCache;
  private CommerceBeanFactory commerceBeanFactory;

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setPersonWrapperService(WcPersonWrapperService wrapperService) {
    this.personWrapperService = wrapperService;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  // ----- methods that use WCS REST api -----------------------------

  @Override
  public User findCurrentUser() throws CommerceException {
    UserContext userContext = UserContextHelper.getCurrentContext();
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    @SuppressWarnings("unchecked")
    Map<String, Object> personWrapper = (Map<String, Object>) commerceCache.get(new FindCommercePersonCacheKey("" + userContext.getUserId(), storeContext, userContext, personWrapperService, commerceCache));
    return createUserBeanFor(personWrapper, storeContext);
  }

  @Override
  public User updateCurrentUser(User userData)  throws CommerceException {
    UserContext userContext = UserContextHelper.getCurrentContext();
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    UserImpl userImpl = (UserImpl) userData;
    Map<String, Object> userMap = userImpl.getDelegate();
    Map<String, Object> updatedPerson = personWrapperService.updatePerson(userMap, userContext, storeContext);
    return createUserBeanFor(updatedPerson, storeContext);
  }

  @Override
  public void updateCurrentUserPassword(String oldPassword, String password, String verifyPassword) throws CommerceException {
    personWrapperService.updatePassword(oldPassword, password, verifyPassword, UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
  }

  @Override
  public void resetPassword(String logonId, String challengeAnswer) throws CommerceException {
    personWrapperService.resetPassword(logonId, challengeAnswer, StoreContextHelper.getCurrentContext());
  }

  @Override
  public User registerUser(String login, String password, String email) throws CommerceException {
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    Map<String, Object> personWrapper = personWrapperService.registerPerson(login, password, email, storeContext);
    return createUserBeanFor(personWrapper, storeContext);
  }

  // ----- Helper -----------------------------

  protected User createUserBeanFor(Map<String, Object> personWrapper, StoreContext context) {
    if (personWrapper != null) {
      String id = CommerceIdHelper.formatPersonId(DataMapHelper.getValueForKey(personWrapper, "userId", String.class));
      if (StringUtils.isNotEmpty(id)) {
        User user = (User) commerceBeanFactory.createBeanFor(id, context);
        ((AbstractIbmCommerceBean) user).setDelegate(personWrapper);
        return user;
      }
    }
    return null;
  }

  @Nonnull
  @Override
  public UserService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, UserService.class);
  }
}

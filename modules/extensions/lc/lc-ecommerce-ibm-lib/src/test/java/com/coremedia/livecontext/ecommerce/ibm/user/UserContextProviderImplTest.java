package com.coremedia.livecontext.ecommerce.ibm.user;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class UserContextProviderImplTest extends AbstractServiceTest {

  public static final String BEAN_NAME_USER_CONTEXT_PROVIDER = "userContextProvider";

  UserContextProviderImpl testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_USER_CONTEXT_PROVIDER, UserContextProviderImpl.class);
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @After
  public void tearDown() throws Exception {
    LoginServiceImpl loginService = infrastructure.getBean("userLoginService", LoginServiceImpl.class);
    loginService.destroy();
  }

  @Test
  @Betamax(tape = "ucpi_testCreateUserContextFor", match = {MatchRule.path, MatchRule.query})
  public void testCreateUserContextFor() {
    UserContext userContext = testling.createContext("testUser");
    assertEquals(UserContextHelper.getForUserName(userContext), "testUser");
  }

  @Test
  public void testCurrentUserContext() {

    HashMap userContextContent = new HashMap();
    userContextContent.put(UserContextHelper.FOR_USER_NAME, "currentUser");
    UserContext userContext = (UserContext) UserContextBuilder.create().withValues(userContextContent).build();

    testling.setCurrentContext(userContext);
    assertNotNull(testling.getCurrentContext());
    assertEquals("currentUser", UserContextHelper.getForUserName(testling.getCurrentContext()));
  }


}

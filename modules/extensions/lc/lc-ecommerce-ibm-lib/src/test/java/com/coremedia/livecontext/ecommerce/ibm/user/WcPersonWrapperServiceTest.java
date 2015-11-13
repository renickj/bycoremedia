package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWrapperServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WcPersonWrapperServiceTest extends AbstractWrapperServiceTest {

  private static final String BEAN_NAME = "personWrapperService";

  private WcPersonWrapperService testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME, WcPersonWrapperService.class);
  }

  @Test
  public void testRegisterUser() throws Exception {
    if (!"*".equals(System.getProperties().get("betamax.ignoreHosts"))) return;
    String testUser = "testuser" + (System.currentTimeMillis()+"").hashCode();
    Map<String, Object> personMap = testling.registerPerson(testUser, "passw0rd", testUser + "@coremedia.com", testConfig.getStoreContext());
    assertNotNull(personMap);
    assertEquals("logonId should be identical", testUser, DataMapHelper.getValueForKey(personMap, "logonId", String.class));
  }

  @Test
  public void testRegisterUserAsAnonymous() throws Exception {
    if (!"*".equals(System.getProperties().get("betamax.ignoreHosts"))) return;

    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(null);
    userContext.setUserId("-1002");

    UserContextHelper.setCurrentContext(userContext);
    String testUser = "testuser" + (System.currentTimeMillis()+"").hashCode();
    Map<String, Object> personMap = testling.registerPerson(testUser, "passw0rd", testUser + "@coremedia.com", testConfig.getStoreContext());
    assertNotNull(personMap);
    assertEquals("logonId should be identical", testUser, DataMapHelper.getValueForKey(personMap, "logonId", String.class));
  }

}

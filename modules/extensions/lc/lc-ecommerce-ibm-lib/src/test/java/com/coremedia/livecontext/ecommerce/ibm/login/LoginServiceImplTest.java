package com.coremedia.livecontext.ecommerce.ibm.login;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.common.InvalidLoginException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class LoginServiceImplTest extends AbstractServiceTest {

  LoginServiceImpl testling;
  String origServiceUser;
  String origServicePassword;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean("userLoginService", LoginServiceImpl.class);
    origServiceUser = testling.getServiceUser();
    origServicePassword = testling.getServicePassword();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @After
  public void tearDown() throws Exception {
    testling.destroy();
    testling.setServiceUser(origServiceUser);
    testling.setServicePassword(origServicePassword);
  }

  @Test
  @Betamax(tape = "lsi_testLoginSuccess", match = {MatchRule.path, MatchRule.query})
  public void testLoginSuccess() throws Exception {
    WcCredentials credentials = testling.loginServiceIdentity();
    assertNotNull(credentials);
    assertNotNull("WcSession is not available", credentials.getSession());
  }

  /**
   * Attention: this test does not work with betamax tapes. It only works against the wcs system.
   * The reason is: the existence of a workspace id induces the request of a preview token that can only
   * be done by https. https calls are currently not supported by betamax. The test independently detects a
   * "ignoreHosts" configuration and runs only if such a java property is found.
   *
   * The other restriction is, the wcs system has to support workspaces.
   * Todo: If the reference system supports workspaces please remove the @Ignore flag
   * permanently.
   */
  @Test
  @Ignore
  public void testLoginSuccessWithWorkspaces() throws Exception {
    if (!"*".equals(System.getProperty("betamax.ignoreHosts"))) return;
    StoreContext storeContext = testConfig.getStoreContextWithWorkspace();
    StoreContextHelper.setCurrentContext(storeContext);
    WcCredentials credentials = testling.loginServiceIdentity();
    assertNotNull(credentials);
    assertNotNull("WcSession is not available", credentials.getSession());
  }

  @Test(expected = InvalidLoginException.class)
  @Betamax(tape = "lsi_testLoginFailure", match = {MatchRule.path, MatchRule.query, MatchRule.body})
  public void testLoginFailure() throws Exception {
    testling.setServicePassword("wrong password");
    testling.loginServiceIdentity();
  }

  @Test
  @Betamax(tape = "lsi_testLogout", match = {MatchRule.path, MatchRule.query})
  public void testLogout() throws Exception {
    WcCredentials credentials = testling.loginServiceIdentity();
    assertNotNull(credentials);
    boolean success = testling.logoutServiceIdentity();
    assertTrue(success);
  }

}

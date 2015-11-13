package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextProviderImpl;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.mock.web.MockServletContext;

import java.util.Properties;

public abstract class AbstractServiceTest {

  private static final String BEAN_NAME_COMMERCE = "commerce";
  private static final String BEAN_NAME_USER_CONTEXT_PROVIDER = "userContextProvider";
  private static final String BEAN_NAME_COMMERCE_USER_SERVICE = "commerceUserService";
  private static final String BEAN_NAME_COMMERCE_USER_SESSION_SERVICE = "commerceUserSessionService";
  private static final String BEAN_NAME_TEST_CONFIG = "testConfig";
  private static final String BEAN_NAME_REST_CONNECTOR = "restConnector";
  private static final String BEAN_NAME_LOGIN_SERVICE = "userLoginService";
  private static final String BEAN_NAME_COMMERCE_CACHE = "commerceCache";

  public static final String USER1_NAME = System.getProperty("lc.test.user1.name", "arover");
  public static final String USER1_ID = System.getProperty("lc.test.user1.id", "3");
  public static final String USER2_NAME = System.getProperty("lc.test.user1.name", "gstevens");
  public static final String USER2_ID = System.getProperty("lc.test.user1.id", "4");
  public static final String PREVIEW_USER_NAME = System.getProperty("lc.test.user1.name", "preview");

  protected Commerce commerce;

  protected UserContextProviderImpl userContextProvider;

  protected WcRestConnector restConnector;

  protected TestConfig testConfig;

  protected UserService commerceUserService;

  protected UserSessionService commerceUserSessionService;

  protected LoginService loginService;

  protected CommerceCache commerceCache;

  /*
  Set betamax default mode to READ_ONLY if not defined by user
   */
  private static final Properties sysProps;

  static {
    sysProps = System.getProperties();
    if (StringUtils.isEmpty(sysProps.getProperty("betamax.defaultMode"))) {
      sysProps.setProperty("betamax.defaultMode", "READ_ONLY");
    }
  }

  @Rule
  public Recorder recorder = new Recorder(sysProps);

  protected static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withBeans("classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml")
          .withBeans("classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-search.xml")
          .withBeans("classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-bod-customizers.xml")
          .withHandlers()
          .withSites()
          .asWebEnvironment(new MockServletContext())
          .build();

  @Before
  public void setup() {
    testConfig = infrastructure.getBean(BEAN_NAME_TEST_CONFIG, TestConfig.class);
    commerceUserService = infrastructure.getBean(BEAN_NAME_COMMERCE_USER_SERVICE, UserService.class);
    commerceUserSessionService = infrastructure.getBean(BEAN_NAME_COMMERCE_USER_SESSION_SERVICE, UserSessionService.class);
    userContextProvider = infrastructure.getBean(BEAN_NAME_USER_CONTEXT_PROVIDER, UserContextProviderImpl.class);
    restConnector = infrastructure.getBean(BEAN_NAME_REST_CONNECTOR, WcRestConnector.class);
    loginService = infrastructure.getBean(BEAN_NAME_LOGIN_SERVICE, LoginService.class);
    commerce = infrastructure.getBean(BEAN_NAME_COMMERCE, Commerce.class);
    commerceCache = infrastructure.getBean(BEAN_NAME_COMMERCE_CACHE, CommerceCache.class);
    CommerceConnection connection = commerce.getConnection("wcs1");
    connection.setStoreContext(testConfig.getStoreContext());
    Commerce.setCurrentConnection(connection);

    userContextProvider.clearCurrentContext();
    userContextProvider.setUserSessionService(commerceUserSessionService);
    loginService.clearIdentityCache();

    commerceCache.setEnabled(false);
  }

  public TestConfig getTestConfig() {
    return testConfig;
  }

}

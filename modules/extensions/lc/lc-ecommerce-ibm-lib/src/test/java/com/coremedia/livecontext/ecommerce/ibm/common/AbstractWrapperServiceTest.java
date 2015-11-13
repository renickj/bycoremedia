package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Recorder;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.springframework.mock.web.MockServletContext;

import java.util.Properties;

public class AbstractWrapperServiceTest {

  private static final String BEAN_NAME_LOGIN_SERVICE = "userLoginService";
  private static final String BEAN_NAME_USER_CONTEXT_PROVIDER = "userContextProvider";
  private static final String BEAN_NAME_CATALOG_CONNECTOR = "restConnector";
  private static final String BEAN_NAME_TEST_CONFIG = "testConfig";

  public static final String TEST_USER = "gstevens"; // Frequent Buyer, Male Customer

  protected TestConfig testConfig;

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
          .asWebEnvironment(new MockServletContext())
          .build();

  protected LoginService loginService;
  protected UserContextProvider userContextProvider;
  protected WcRestConnector wcRestConnector;

  public void setup() {
    testConfig = infrastructure.getBean(BEAN_NAME_TEST_CONFIG, TestConfig.class);
    loginService = infrastructure.getBean(BEAN_NAME_LOGIN_SERVICE, LoginService.class);
    userContextProvider = infrastructure.getBean(BEAN_NAME_USER_CONTEXT_PROVIDER, UserContextProvider.class);
    wcRestConnector = infrastructure.getBean(BEAN_NAME_CATALOG_CONNECTOR, WcRestConnector.class);
  }

  public TestConfig getTestConfig() {
    return testConfig;
  }

}

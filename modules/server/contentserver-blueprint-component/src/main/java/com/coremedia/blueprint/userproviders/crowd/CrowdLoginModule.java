package com.coremedia.blueprint.userproviders.crowd;

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import hox.corem.Corem;
import hox.corem.login.CommonLoginModule;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class CrowdLoginModule extends CommonLoginModule {
  private CrowdClient crowdClient = null;
  private String domain = "";

  @Override
  public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
    super.initialize(subject, callbackHandler, sharedState, options);

    String propfile = "properties/corem/jndi-crowd.properties";

    String option = (String) options.get("crowd.properties");
    if (option != null) {
      propfile = option;
    }

    option = (String) options.get("domain");
    if (option != null) {
      domain = option;
    }

    if (!new File(propfile).isAbsolute()) {
      String coremHome = Corem.getHome().getAbsolutePath();
      if (!coremHome.endsWith(File.separator)) {
        coremHome += File.separator;
      }
      propfile = coremHome + propfile;
    }
    Properties crowdProperties = new Properties();
    try {
      FileInputStream stream = new FileInputStream(propfile);
      try {
        crowdProperties.load(stream);
      } finally {
        stream.close();
      }
    } catch (IOException e) {
      CommonLoginModule.log.error("Error while loading properties file: " + propfile, e);
    }

    final ClientProperties clientProperties = ClientPropertiesImpl.newInstanceFromProperties(crowdProperties);
    crowdClient = new RestCrowdClientFactory().newInstance(clientProperties);


    if (CommonLoginModule.log.isDebugEnabled()) {
      CommonLoginModule.log.debug("Crowd authentication initialized");
    }
  }

  @Override
  public boolean login() throws LoginException {
    CommonLoginModule.log.debug("Invoke Crowd authentication");

    if (!super.login()) {
      return false;
    }

    NameCallback jndiCallback = new NameCallback("_jndiName");
    NameCallback domainCallback = new NameCallback("_domain");
    PasswordCallback passwordCallback = new PasswordCallback("password:", false);

    try {
      Callback[] callbacks = new Callback[]{domainCallback, jndiCallback, passwordCallback};
      callbackHandler.handle(callbacks);
    } catch (Exception e) {
      CommonLoginModule.log.error("Crowd authentication callback failed, cannot authenticate the user without the callback data.", e);
      return false;
    }

    String theDomain = domainCallback.getName();
    if (theDomain == null || "".equals(theDomain.trim()) || domain != null && !domain.equals(theDomain)) {
      CommonLoginModule.log.info("Not responsible for domain " + theDomain);
      return false;
    }

    String distinguishedName = jndiCallback.getName();
    if (distinguishedName == null) {
      CommonLoginModule.log.info("No Distinguished Name, cannot authenticate builtin users against Crowd");
      return false;
    }

    String password = passwordAsString(passwordCallback);
    if (password.isEmpty()) {
      CommonLoginModule.log.error("Password not available, cannot authenticate against Crowd");
      return false;
    }

    try {
      crowdClient.authenticateUser(distinguishedName, password);
      CommonLoginModule.log.info("User '" + distinguishedName + "' logged in successfully");
    } catch (Exception e) {
      // Not an exceptional case, regular login failure.
      // Do not log irrelevant alarming stacktraces.
      CommonLoginModule.log.info("Authentication against Crowd: The user is not accepted.");
      LoginException loginException = new LoginException("Authentication against Crowd: The user is not accepted.");
      loginException.initCause(e);
      // We *do* preserve the stacktrace, even though it is questionable in this particular case.
      throw loginException;  //NOSONAR
    }

    return true;
  }

}
package com.coremedia.blueprint.elastic.social.cae.flows;

import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.elastic.social.cae.controller.BlobRef;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

import static com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper.addErrorMessageWithSource;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A simple form bean for use by the registration web flow.
 */
public class Registration implements Serializable {
  private static final long serialVersionUID = 42L;

  private String username;
  private String givenname;
  private String surname;
  private String password;
  private String confirmPassword;
  private String emailAddress;
  private PasswordPolicy passwordPolicy;
  private boolean registeringWithProvider;
  private BlobRef profileImage;
  private boolean deleteProfileImage = false;
  private boolean acceptTermsOfUse = false;
  private String timeZoneId;
  private String recaptchaChallengeField;
  private String recaptchaResponseField;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getGivenname() {
    return givenname;
  }

  public void setGivenname(String givenname) {
    this.givenname = givenname;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public PasswordPolicy getPasswordPolicy() {
    return passwordPolicy;
  }

  public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
    this.passwordPolicy = passwordPolicy;
  }

  public boolean isRegisteringWithProvider() {
    return registeringWithProvider;
  }

  public void setRegisteringWithProvider(boolean registeringWithProvider) {
    this.registeringWithProvider = registeringWithProvider;
  }

  public BlobRef getProfileImage() {
    return profileImage;
  }

  public void setProfileImage(BlobRef profileImage) {
    this.profileImage = profileImage;
  }

  public boolean isDeleteProfileImage() {
    return deleteProfileImage;
  }

  public void setDeleteProfileImage(boolean deleteProfileImage) {
    this.deleteProfileImage = deleteProfileImage;
  }

  public boolean isAcceptTermsOfUse() {
    return acceptTermsOfUse;
  }

  public void setAcceptTermsOfUse(boolean acceptTermsOfUse) {
    this.acceptTermsOfUse = acceptTermsOfUse;
  }

  public String getTimeZoneId() {
    return timeZoneId;
  }

  public void setTimeZoneId(String timeZoneId) {
    this.timeZoneId = timeZoneId;
  }

  public String getRecaptcha_challenge_field() { // NOSONAR
    return recaptchaChallengeField;
  }

  public void setRecaptcha_challenge_field(String recaptchaChallengeField) { // NOSONAR
    this.recaptchaChallengeField = recaptchaChallengeField;
  }

  public String getRecaptcha_response_field() { // NOSONAR
    return recaptchaResponseField;
  }

  public void setRecaptcha_response_field(String recaptchaResponseField) { // NOSONAR
    this.recaptchaResponseField = recaptchaResponseField;
  }

  public void validateRegistration(ValidationContext context) {
    validateName(context);
    validateEmail(context);
    validateCaptcha(context);

    if (!registeringWithProvider) {
      validateRegistrationPassword(context);
    }

    if (!acceptTermsOfUse) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_ACCEPT_TERMS_OF_USE_ERROR, "acceptTermsOfUse");
    }
  }

  private void validateName(ValidationContext context) {
    if (isBlank(username)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_USERNAME_ERROR, "username");
    } else if (!ValidationUtil.validateUsernameLength(username)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_USERNAME_TOO_SHORT_ERROR, "username", ValidationUtil.MINIMUM_USERNAME_LENGTH);
    } else if (StringUtils.contains(username, '#')) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_USERNAME_SYNTAX_ERROR, "username");
    }

    if (isBlank(givenname)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_GIVENNAME_ERROR, "givenname");
    }

    if (isBlank(surname)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_SURNAME_ERROR, "surname");
    }
  }

  private void validateEmail(ValidationContext context) {
    if (isBlank(emailAddress)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_EMAIL_ADDRESS_ERROR, "emailAddress");
    } else if (!ValidationUtil.validateEmailAddressSyntax(emailAddress)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_EMAIL_ADDRESS_SYNTAX_ERROR, "emailAddress");
    }
  }

  private void validateCaptcha(ValidationContext context) {
    RequestContext requestContext = RequestContextHolder.getRequestContext();
    HttpServletRequest nativeRequest = (HttpServletRequest) requestContext.getExternalContext().getNativeRequest();

    Page page = RequestAttributeConstants.getPage(nativeRequest);

    final WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(nativeRequest.getServletContext());
    ElasticSocialPlugin elasticSocialPlugin = webApplicationContext.getBean(ElasticSocialPlugin.class);

    ElasticSocialConfiguration elasticSocialConfiguration = elasticSocialPlugin.getElasticSocialConfiguration(page);
    ServletRequest servletRequest = (ServletRequest) requestContext.getExternalContext().getNativeRequest();
    if (elasticSocialConfiguration.isCaptchaForRegistrationRequired() && !elasticSocialConfiguration.validateCaptcha(servletRequest)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_INVALID_CAPTCHA, "recaptcha_response_field");
    }
  }

  protected void validateRegistrationPassword(ValidationContext context) {
    if (isBlank(password)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_PASSWORD_ERROR, "password");
    }

    if (isBlank(confirmPassword)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_CONFIRM_PASSWORD_ERROR, "confirmPassword");
    }

    if (!isBlank(password) && !password.equals(confirmPassword)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_PASSWORDS_DO_NOT_MATCH, "confirmPassword");
    }

    if (!isBlank(password) && passwordPolicy != null && !passwordPolicy.verify(password)) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_PASSWORD_TOO_WEAK, "password", ValidationUtil.MINIMUM_PASSWORD_LENGTH);
    }
  }
}

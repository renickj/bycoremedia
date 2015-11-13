package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.UserDetails;
import com.coremedia.blueprint.elastic.social.cae.flows.ValidationUtil;
import com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.webflow.execution.RequestContext;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Custom User details class for live context.
 */
public class LiveContextUserDetails extends UserDetails {

  @Override
  public void validate(RequestContext context) {
    String username = getUsername();
    String emailAddress = getEmailAddress();

    if (isBlank(username)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_USERNAME_ERROR, "username");
    }

    validateName(context);

    if (isBlank(emailAddress)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_EMAIL_ADDRESS_ERROR, "emailAddress");
    }
    else if (!ValidationUtil.validateEmailAddressSyntax(emailAddress)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_EMAIL_ADDRESS_SYNTAX_ERROR, "emailAddress");
    }

    if (isValidationOfPasswordRequired()) {
      validatePassword(context);
    }
  }

  @Override
  protected void validatePassword(RequestContext context) {
    String password = getPassword();
    String newPassword = getNewPassword();
    String newPasswordRepeat = getNewPasswordRepeat();

    if (isBlank(password)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_PASSWORD_ERROR, "password");
    }
    if (isBlank(newPassword)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_NEW_PASSWORD_ERROR, "newPassword");
    }
    if (isBlank(newPasswordRepeat)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_NEW_PASSWORD_REPEAT_ERROR, "newPasswordRepeat");
    }
    if (isNotBlank(newPassword) && !StringUtils.equals(newPassword, newPasswordRepeat)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_PASSWORDS_DO_NOT_MATCH_ERROR, "newPasswordRepeat");
    }
    if (isNotBlank(password) && StringUtils.equals(password, newPassword)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_NEW_PASSWORD_MATCHES_OLD_ERROR, "newPassword");
    }
  }
}

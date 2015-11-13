package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.PasswordReset;
import com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys;
import org.springframework.webflow.execution.RequestContext;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Overwrites the default password reset form fields.
 * We are adding the current password here since the user already
 * has a new (generated) password and wants to update it.
 */
public class LiveContextPasswordReset extends PasswordReset {
  private String currentPassword;

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  @Override
  public void validateResetForm(RequestContext context) {
    super.validateResetForm(context);

    if (isBlank(currentPassword)) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.CONFIRM_PASSWORD_RESET_CURRENT_PASSWORD_ERROR, PASSWORD);
    }
  }
}

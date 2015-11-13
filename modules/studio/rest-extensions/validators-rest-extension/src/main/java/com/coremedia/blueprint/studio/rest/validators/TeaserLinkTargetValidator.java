package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

/**
 * Validates if the teaser target is set, if call to action button is enabled
 */
public class TeaserLinkTargetValidator extends ContentTypeValidatorBase {

  private static final String LOCAL_SETTINGS_PROPERTY_NAME = "localSettings";
  private static final String TARGET_PROPERTY_NAME = "target";
  private static final String CALL_TO_ACTION_DISABLED_PROPERTY_NAME = "callToActionDisabled";

  @Override
  public void validate(Content content, Issues issues) {
    Content linkTarget = content.getLink(TARGET_PROPERTY_NAME);
    boolean hasIssues = false;
    if (linkTarget == null) {
      Struct localSettings = content.getStruct(LOCAL_SETTINGS_PROPERTY_NAME);
      if (localSettings == null) {
        hasIssues = true;
      } else {
        if (!StructUtil.getBoolean(localSettings, CALL_TO_ACTION_DISABLED_PROPERTY_NAME)) {
          hasIssues = true;
        }
      }
    }

    if (hasIssues) {
      issues.addIssue(Severity.WARN, TARGET_PROPERTY_NAME, "teaser_target_not_set");
    }
  }
}

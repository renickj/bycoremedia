package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.Collection;

/**
 * Validates if a page is references by two different parent pages.
 * No re-usage of navigation trees supported yet.
 */
public class ChannelReferrerValidator extends ContentTypeValidatorBase {
  private static final String PROPERTY_CHILDREN = "children";
  private static final String IS_IN_PRODUCTION = "isInProduction";

  @Override
  public void validate(Content content, Issues issues) {
    Collection<Content> referrers = content.getReferrersWithDescriptorFulfilling("CMChannel", PROPERTY_CHILDREN, IS_IN_PRODUCTION);
    if (referrers.size() > 1) {
      //only log a warning: the application logic does not support reuse of navigation trees.
      issues.addIssue(Severity.WARN, "", "duplicate_referrer");
    }
  }
}

package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.Collection;

/**
 * Validates if link list properties of the given content do reference themselves.
 */
public class ChannelIsPartOfNavigationValidator extends ContentTypeValidatorBase {

  private static final String IS_IN_PRODUCTION = "isInProduction";

  @Override
  public void validate(Content content, Issues issues) {
    Collection<Content> referrers = content.getReferrersWithDescriptorFulfilling("CMChannel", "children", IS_IN_PRODUCTION);
    if (referrers.isEmpty()) {
      referrers = content.getReferrersWithDescriptorFulfilling("CMSite", "root", IS_IN_PRODUCTION);
      if (referrers.isEmpty()) {
        issues.addIssue(Severity.ERROR, "", "not_in_navigation");
      }
    }
  }
}

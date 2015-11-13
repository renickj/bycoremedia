package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Validates if the navigation tree is feasible.
 */
public class ChannelSegmentValidator extends ContentTypeValidatorBase {
  private static final String PROPERTY_SEGMENT = "segment";
  private static final String PROPERTY_CHILDREN = "children";
  private static final String IS_IN_PRODUCTION = "isInProduction";

  @Override
  public void validate(Content content, Issues issues) {
    Collection<Content> referrers = content.getReferrersWithDescriptorFulfilling("CMChannel", PROPERTY_CHILDREN, IS_IN_PRODUCTION);
    String thisSegment = content.getString(PROPERTY_SEGMENT);
    if(null != thisSegment) {
      for (Content ref : referrers) {
        List<Content> children = ref.getLinks(PROPERTY_CHILDREN);
        HashSet<Content> otherChildren = new HashSet<>(children);
        otherChildren.remove(content);
        for (Content child : otherChildren) {
          if (thisSegment.equalsIgnoreCase(child.getString(PROPERTY_SEGMENT))) {
            issues.addIssue(Severity.ERROR, PROPERTY_SEGMENT, "duplicate_segment");
            return;
          }
        }
      }
    }
  }
}

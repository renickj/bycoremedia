package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Validates if the navigation tree is feasible.
 */
public class ChannelNavigationValidator extends ContentTypeValidatorBase {
  private static final String PROPERTY_CHILDREN = "children";
  private static final String IS_IN_PRODUCTION = "isInProduction";

  @Override
  public void validate(Content content, Issues issues) {
    Collection<Content> parents = content.getReferrersWithDescriptorFulfilling("CMChannel", PROPERTY_CHILDREN, IS_IN_PRODUCTION);
    List<Content> visited = new ArrayList<>();
    searchDuplicates(content, visited, parents, issues);
  }

  private void searchDuplicates(Content content, List<Content> visited, Collection<Content> parents, Issues issues) {
    if (parents.contains(content)) {
      issues.addIssue(Severity.ERROR, "", "channel_loop");
    }
    else {
      for (Content parent : parents) {
        if(!visited.contains(parent)) {
          visited.add(parent);
          Collection<Content> parentParents = parent.getReferrersWithDescriptorFulfilling("CMChannel", PROPERTY_CHILDREN, IS_IN_PRODUCTION);
          searchDuplicates(content, visited, parentParents, issues);
        }
      }
    }
  }
}

package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.CapPropertyDescriptorType;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;

import java.util.List;

/**
 * Validates if link list properties of the given content do reference themselves.
 */
public class SelfReferringLinkListValidator extends ContentTypeValidatorBase {

  @Override
  public void validate(Content content, Issues issues) {
    List<CapPropertyDescriptor> propertyDescriptors = content.getType().getDescriptors();
    //search for link lists...
    for(CapPropertyDescriptor descriptor : propertyDescriptors) {
      if(descriptor.getType().equals(CapPropertyDescriptorType.LINK)) {
        List<Content> links = (List<Content>)content.get(descriptor.getName()); //NOSONAR
        if(links.contains(content)) {//...and check if the document contains itself in this list.
          issues.addIssue(Severity.ERROR, descriptor.getName(), "self_referring");
        }
      }
    }
  }
}

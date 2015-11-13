package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.xml.MarkupUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.coremedia.xml.Markup;

public class AbstractCodeValidator extends ContentTypeValidatorBase {
  @Override
  public void validate(Content content, Issues issues) {
    String dataUrl = content.getString("dataUrl");
    Markup code = content.getMarkup("code");
    if (isDataUrlEmpty(dataUrl) && isCodeEmpty(code)) {
      issues.addIssue(Severity.ERROR,"code","Abstract_Code_code_property_must_be_set");
      issues.addIssue(Severity.ERROR,"dataUrl","Abstract_Code_data_URL_property_must_be_set");
    }
  }

  private static boolean isDataUrlEmpty(String dataUrl) {
    return dataUrl==null || dataUrl.trim().isEmpty();
  }

  private static boolean isCodeEmpty(Markup code) {
    return !MarkupUtil.hasText(code, true);
  }
}

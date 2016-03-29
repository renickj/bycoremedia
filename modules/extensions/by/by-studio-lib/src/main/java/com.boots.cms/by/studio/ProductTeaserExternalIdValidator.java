package com.boots.cms.by.studio;

import com.coremedia.rest.validation.Issues;

import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;

public class ProductTeaserExternalIdValidator extends CatalogLinkValidator {
  private static final String CODE_ISSUE_ID_EMPTY = "productTeaserEmptyExternalId";
  private static final String CODE_ISSUE_ID_INVALID = "productTeaserInvalidId";
  private static final String CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE = "productTeaserValidInAWorkspace";
  private static final String CODE_ISSUE_CONTEXT_INVALID = "productTeaserInvalidStoreContext";
  private static final String CODE_ISSUE_CONTEXT_NOT_FOUND = "productTeaserStoreContextNotFound";

  @Override
  protected void emptyPropertyValue(Issues issues) {
    issues.addIssue(ERROR, getPropertyName(), CODE_ISSUE_ID_EMPTY);
  }

  @Override
  protected void invalidStoreContext(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_CONTEXT_INVALID, arguments);
  }

  @Override
  protected void storeContextNotFound(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_CONTEXT_NOT_FOUND, arguments);
  }

  @Override
  protected void invalidExternalId(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_ID_INVALID, arguments);
  }

  @Override
  protected void validOnlyInWorkspace(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE, arguments);
  }
}

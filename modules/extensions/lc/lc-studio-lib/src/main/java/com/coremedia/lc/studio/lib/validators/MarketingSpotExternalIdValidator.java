package com.coremedia.lc.studio.lib.validators;

import com.coremedia.rest.validation.Issues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;

public class MarketingSpotExternalIdValidator extends CatalogLinkValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MarketingSpotExternalIdValidator.class);

  private static final String CODE_ISSUE_ID_EMPTY = "marketingSpotEmptyExternalId";
  private static final String CODE_ISSUE_ID_INVALID = "marketingSpotInvalidId";
  private static final String CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE = "marketingSpotValidInAWorkspace";
  private static final String CODE_ISSUE_CONTEXT_INVALID = "marketingSpotInvalidStoreContext";
  private static final String CODE_ISSUE_CONTEXT_NOT_FOUND = "marketingSpotStoreContextNotFound";

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

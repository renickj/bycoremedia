package com.coremedia.blueprint.cae.contentbeans;

import org.apache.commons.lang3.StringUtils;

/**
 * Generated extension class for immutable beans of document type "CMViewtype".
 */
public class CMViewtypeImpl extends CMViewtypeBase {
  @Override
  public String getLayout() {
    // State of the art
    String layout = super.getLayout();
    if (!StringUtils.isEmpty(layout)) {
      return layout;
    }

    // Backward compatibility
    return getName();
  }
}
  
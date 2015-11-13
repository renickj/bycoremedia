package com.coremedia.livecontext.logictypes;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class of all extensions
 */
public class ExtensionBase extends LiveContextPageHandlerBase {

  /**
   * Return the parameter map for WCS link generation for the given navigation/linkable.
   */
  protected Map<String, String> getParameterMapForCommerceLink(CMNavigation navigation, CMLinkable linkable) {
    Map<String, String> parameterMap = new HashMap<>();
    parameterMap.put("externalRef", "cm-" + navigation.getContentId() + "-" + linkable.getContentId());
    return parameterMap;
  }
}

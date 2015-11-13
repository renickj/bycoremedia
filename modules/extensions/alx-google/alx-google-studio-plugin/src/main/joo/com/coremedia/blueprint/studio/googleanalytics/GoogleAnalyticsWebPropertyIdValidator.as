package com.coremedia.blueprint.studio.googleanalytics {

import ext.form.VTypes;

public class GoogleAnalyticsWebPropertyIdValidator {

  public static var WEB_PROPERTY_ID_KEY :String = "webPropertyId";

  VTypes['webPropertyIdVal'] = new RegExp(GoogleAnalyticsStudioPlugin_properties.INSTANCE.googleanalytics_webpropertyid_val);
  VTypes['webPropertyIdMask'] = new RegExp(GoogleAnalyticsStudioPlugin_properties.INSTANCE.googleanalytics_webpropertyid_mask);
  VTypes['webPropertyIdText'] = GoogleAnalyticsStudioPlugin_properties.INSTANCE.googleanalytics_webpropertyid_text;
  VTypes['webPropertyId'] = function(v:*):* {
    return VTypes['webPropertyIdVal'].test(v);
  };
}
}
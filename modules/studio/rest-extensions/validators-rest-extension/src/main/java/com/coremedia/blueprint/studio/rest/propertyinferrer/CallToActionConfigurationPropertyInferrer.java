package com.coremedia.blueprint.studio.rest.propertyinferrer;

import com.coremedia.cap.content.ContentObject;
import com.coremedia.cap.struct.Struct;
import com.coremedia.rest.cap.differencing.PropertiesInferrerBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallToActionConfigurationPropertyInferrer extends PropertiesInferrerBase {
  @Override
  public void inferProperties(ContentObject object, Map<String, Object> properties) {
    Struct struct = object.getStruct("localSettings");
    List<String> callToActionConfigurationValue = new ArrayList<>();

    if (struct != null) {
      callToActionConfigurationValue.add(struct.getString("callToActionCustomText") != null
              ? struct.getString("callToActionCustomText"): "");
      callToActionConfigurationValue.add(String.valueOf(struct.getBoolean("callToActionDisabled")));
    } else {
      callToActionConfigurationValue.add("");
      callToActionConfigurationValue.add(String.valueOf(false));
    }

    properties.put("callToActionConfiguration", callToActionConfigurationValue);
  }
}

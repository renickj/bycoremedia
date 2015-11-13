package com.coremedia.livecontext.studio.components {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.workArea;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtilInternal;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.CatalogObjectPropertyNames;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.studio.AddTimeZoneInfoPlugin_properties;
import com.coremedia.livecontext.studio.config.timeZoneInfoIconLabel;
import com.coremedia.ui.components.IconLabel;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.util.StringUtil;

public class TimeZoneInfoIconLabelBase extends IconLabel {

  private var wcsTimeZoneValueExpression:ValueExpression;

  public function TimeZoneInfoIconLabelBase(config:timeZoneInfoIconLabel) {
    super(config);
    getTooltipExpression(config).loadValue(function(tooltip:String):void {
      setTooltip(tooltip);
    });
  }

  private function getWcsTimeZoneValueExpression(config:timeZoneInfoIconLabel):ValueExpression {
    if (!wcsTimeZoneValueExpression) {
      wcsTimeZoneValueExpression = ValueExpressionFactory.createFromFunction(function ():String {
        var text:String;
        var entityExpression:ValueExpression = config.previewPanel.getCurrentPreviewContentValueExpression();
        var storeExpression:ValueExpression;
        if (entityExpression.getValue() is Content) {
          storeExpression = CatalogHelper.getInstance().getStoreForContentExpression(workArea.ACTIVE_CONTENT_VALUE_EXPRESSION);
        } else if (entityExpression.getValue() is CatalogObject) {
          storeExpression = entityExpression.extendBy(CatalogObjectPropertyNames.STORE);
        }
        if (storeExpression && storeExpression.getValue() && Store(storeExpression.getValue()).getWcsTimeZone()) {
          text = Store(storeExpression.getValue()).getWcsTimeZone().id;
        }
        return text;
      });
    }
    return wcsTimeZoneValueExpression;
  }

  private function getTooltipExpression(config:timeZoneInfoIconLabel):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var commerceTimeZoneId:String = getWcsTimeZoneValueExpression(config).getValue();
      if (!commerceTimeZoneId) return undefined;
      return StringUtil.format(AddTimeZoneInfoPlugin_properties.INSTANCE.Preview_Wcs_Timezone_Divergation_Warning_Message,
              ContentLocalizationUtilInternal.localizeTimeZoneID(commerceTimeZoneId));
    });
  }

  internal function getVisibilityExpression(config:timeZoneInfoIconLabel):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var commerceTimeZoneId:String = getWcsTimeZoneValueExpression(config).getValue();
      if (!commerceTimeZoneId) return false;
      var dateTimeModel:Bean = config.model;
      var timeZoneId:String = dateTimeModel.get("timeZone");
      return timeZoneId !== commerceTimeZoneId;
    });
  }
}
}
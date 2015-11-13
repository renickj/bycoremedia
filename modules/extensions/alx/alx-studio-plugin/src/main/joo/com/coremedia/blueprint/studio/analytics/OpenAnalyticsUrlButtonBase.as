package com.coremedia.blueprint.studio.analytics {
import com.coremedia.ui.data.ValueExpression;

import ext.Button;
import ext.Component;
import ext.config.button;
import ext.form.VTypes;

import joo.debug;

public class OpenAnalyticsUrlButtonBase extends Button {

  internal static const WINDOW_FEATURES:String = "menubar=yes,resizable=yes,scrollbars=yes,status=yes,location=yes";

  private static const HOME_URL:String = "homeUrl";

  internal native function get serviceName():String;
  internal native function get windowName():String;
  internal native function get urlValueExpression():ValueExpression;

  public function OpenAnalyticsUrlButtonBase(config:button) {
    super(config);
    setHandler(handler);
    mon(this, 'afterrender', onAfterRender);
  }

  private function onAfterRender():void {
    initUrlValueExpression();
    bindDisable(urlValueExpression, this);
  }

  internal static function bindDisable(valueExpression:ValueExpression, component:Component):void {
    valueExpression.addChangeListener(function (ve:ValueExpression):void {
      component.setDisabled(isNotUrlValue(ve.getValue()));
    });
    component.setDisabled(isNotUrlValue(valueExpression.getValue()));
  }

  private function handler():void {
    window.open(urlValueExpression.getValue(), windowName, WINDOW_FEATURES);
  }

  /**
   * Opens the current URL new browser window.
   */
  public static function openInBrowser(expr:ValueExpression, windowName:String):Function {
    return function ():void {
      const url:String = expr.getValue() as String;
      if (isNotUrlValue(url)) {
        if (debug) {
          trace("[WARN] cannot open non URL value", url);
        }
      } else {
        window.open(url, windowName, WINDOW_FEATURES);
      }
    }
  }

  internal static function isNotUrlValue(value:*):Boolean {
    return !VTypes.url(value);
  }

  internal function initUrlValueExpression():void {
    this['urlValueExpression'] = AnalyticsStudioPluginBase.SETTINGS.extendBy('properties.settings', serviceName, HOME_URL);
  }

}
}
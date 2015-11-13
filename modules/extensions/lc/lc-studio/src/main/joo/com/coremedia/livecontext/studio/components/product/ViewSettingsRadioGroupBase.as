package com.coremedia.livecontext.studio.components.product {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.viewSettingsRadioGroup;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.form.RadioGroup;

public class ViewSettingsRadioGroupBase extends RadioGroup {
  private static var radioButtonFormName:int = 0;
  private static const LOCAL_SETTINGS_PROPERTY:String = "localSettings";
  private static const SHOP_NOW_PROPERTY:String = "shopNow";

  private var radioGroupExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;

  private var defaultSetting:String;
  private var rootChannel:Boolean;
  private var bindTo:ValueExpression;

  public function ViewSettingsRadioGroupBase(config:viewSettingsRadioGroup) {
    super(config);
    bindTo = config.bindTo;
    defaultSetting = ViewSettingsRadioGroup.INHERITED_SETTING;
    propertyValueExpression = config.bindTo.extendBy('properties.localSettings.' + SHOP_NOW_PROPERTY);

    isNotRootChannelExpression(config.bindTo).loadValue(function (isNotRootChannel:Boolean):void {
      rootChannel = !isNotRootChannel;
      if (rootChannel) {
        defaultSetting = ViewSettingsRadioGroup.ENABLED_SETTING;
      }

      ValueExpressionFactory.createFromFunction(function ():String {
        var content:Content = config.bindTo.getValue();
        var localSettings:Struct = content.getProperties().get(LOCAL_SETTINGS_PROPERTY);
        if (!localSettings) {
          return undefined;
        }

        var shopNowProperty:String = localSettings.get(SHOP_NOW_PROPERTY);
        if (!shopNowProperty) {
          if (rootChannel) {
            return ViewSettingsRadioGroup.ENABLED_SETTING;
          }
          return ViewSettingsRadioGroup.INHERITED_SETTING;
        }

        return shopNowProperty;
      }).loadValue(function (setting:String):void {
        applyListeners(setting);
      });
    });
  }


  override protected function afterRender():void {
    super.afterRender();
    var group:* = this;
    getInheritLabelExpression(bindTo).loadValue(function(label:String):void {
      group.items.items[0].wrap.child('.x-form-cb-label').update(label);
    });
  }

  /**
   * Since we have to load a lot of stuff initially via FunctionValueExpression, we apply
   * the listeners after we have set the defaults.
   */
  private function applyListeners(defaultValue:String):void {
    getRadioGroupValueExpression().setValue(defaultValue);
    getRadioGroupValueExpression().addChangeListener(radioGroupChanged);
    propertyValueExpression.addChangeListener(propertyValueChanged);
  }

  /**
   * ExtJS is so stupid and handles all "name" attributes of radio buttons as global ids.
   * Therefore we have to generated the name attribute value for each premular the component is used on.
   * @return A unique name attribute value used for the radio boxes.
   */
  public static function getNameId():String {
    radioButtonFormName++;
    return "radioButtonFormName_" + radioButtonFormName;
  }

  /**
   * Returns the ValueExpression that calculates if the "inherit" option should be visible.
   */
  protected function getInheritOptionVisibleExpression(bindTo:ValueExpression):ValueExpression {
    return isNotRootChannelExpression(bindTo);
  }

  protected function getRadioGroupValueExpression():ValueExpression {
    if (!radioGroupExpression) {
      radioGroupExpression = ValueExpressionFactory.createFromValue(defaultSetting);
    }

    return radioGroupExpression;
  }

  protected function getInheritLabelExpression(bindTo:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():String {
      var content:Content = bindTo.getValue();
      if(!content.isLoaded()) {
        return undefined;
      }
      if(!content.getType().getName()) {
        return undefined;
      }
      if(content.getType().getName() === "CMChannel") {
        return LivecontextStudioPlugin_properties.INSTANCE.CMChannel_settings_inherit;
      }
      return LivecontextStudioPlugin_properties.INSTANCE.CMProductTeaser_settings_inherit;
    });
  }

  private function radioGroupChanged(ve:ValueExpression):void {
    var value:String = ve.getValue();

    if (value == defaultSetting) {
      var content:Content = bindTo.getValue();
      var localSettings:Struct = content.getProperties().get(LOCAL_SETTINGS_PROPERTY);
      localSettings.getType().removeProperty(SHOP_NOW_PROPERTY);
    }
    else {
      propertyValueExpression.setValue(value);
    }
  }

  /**
   * Evaluates if the current channel is a root channel.
   * The first radio box of this component is not visible in this case and the default is "enabled".
   *
   * @param bindTo the ValueExpression that contains the content
   */
  private function isNotRootChannelExpression(bindTo:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var content:Content = bindTo.getValue();
      if (!content.isLoaded()) {
        return undefined;
      }

      if (!content.getReferrersWithNamedDescriptor('CMSite', 'root')) {
        return undefined;
      }

      return content.getReferrersWithNamedDescriptor('CMSite', 'root').length == 0;
    });
  }

  private function propertyValueChanged(ve:ValueExpression):void {
    var value:String = ve.getValue();
    if (value) {
      radioGroupExpression.setValue(value);
    }
    else {
      radioGroupExpression.setValue(defaultSetting);
    }
  }
}
}
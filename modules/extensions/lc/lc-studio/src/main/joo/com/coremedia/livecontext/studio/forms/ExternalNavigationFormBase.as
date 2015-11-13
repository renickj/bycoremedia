package com.coremedia.livecontext.studio.forms {
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.livecontext.studio.config.externalNavigationForm;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.form.Radio;

public class ExternalNavigationFormBase extends CollapsibleFormPanel {

  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const CATALOG_STRUCT_NAME:String = 'catalog';
  private static var radioButtonFormName:int = 0;
  public static const CATALOG_PAGE_SETTING:String = 'catalog';
  public static const OTHER_PAGE_SETTING:String = 'other';

  public function ExternalNavigationFormBase(config:externalNavigationForm) {
    super(config);
  }


  internal static function createCatalogExpression(config:externalNavigationForm):ValueExpression {
    var valueExpression:ValueExpression = config.catalogExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, CATALOG_STRUCT_NAME);
    return ValueExpressionFactory.createTransformingValueExpression(valueExpression, null, null, true);
  }

  internal static function negate(valueExpression:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createTransformingValueExpression(valueExpression, not, not);
  }

  private static function not(value:Boolean):Boolean {
    return !value;
  }

  /**
   * ExtJS is so stupid and handles all "name" attributes of radio buttons as global ids.
   * Therefore we have to generated the name attribute value for each external channel form the component is used on.
   * @return A unique name attribute value used for the radio boxes.
   */
  public static function getNameId():String {
    radioButtonFormName++;
    return "radioButtonFormName_" + radioButtonFormName;
  }

  public static function stateToRadio(state:Boolean):String {
    return state? CATALOG_PAGE_SETTING : OTHER_PAGE_SETTING;
  }

  public static function radioToState(radio:Radio):Boolean {
    var inputValue:String = radio ? radio.inputValue : '';
    return inputValue === CATALOG_PAGE_SETTING;
  }

}
}
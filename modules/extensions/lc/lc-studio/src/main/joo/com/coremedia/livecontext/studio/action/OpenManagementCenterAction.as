package com.coremedia.livecontext.studio.action {
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.openInManagementCenterAction;
import com.coremedia.livecontext.studio.config.openManagementCenterAction;
import com.coremedia.livecontext.studio.mgmtcenter.ManagementCenterUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Action;
import ext.Component;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
public class OpenManagementCenterAction extends Action {

  private var disabledExpression:ValueExpression;

  /**
   * @param config the configuration object
   */
  public function OpenManagementCenterAction(config:openManagementCenterAction) {
    super(openInManagementCenterAction(ActionConfigUtil.extendConfiguration(LivecontextStudioPlugin_properties.INSTANCE, config, 'openManagementCenter',
      {handler: function():void{
        ManagementCenterUtil.openManagementCenterView();
      }})));
    disabledExpression = ValueExpressionFactory.createFromFunction(calculateDisabled);
    disabledExpression.addChangeListener(updateDisabledStatus);
    updateDisabledStatus();

  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    //broadcast the disable state after the add of a component
    updateDisabledStatus();
  }

  private function updateDisabledStatus():void {
    var value:* = disabledExpression.getValue();
    var disabled:Boolean = value === undefined || value;

    setDisabled(disabled);
  }

  private function calculateDisabled():Boolean {
    if (!ManagementCenterUtil.isSupportedBrowser()) {
      return true;
    }

    return  !ManagementCenterUtil.getUrl();
  }

}
}

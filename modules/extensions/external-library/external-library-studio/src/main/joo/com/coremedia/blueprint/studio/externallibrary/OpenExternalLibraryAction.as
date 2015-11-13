package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.blueprint.studio.config.externallibrary.externalLibraryWindow;
import com.coremedia.blueprint.studio.config.externallibrary.openExternalLibraryAction;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Action;
import ext.Ext;

/**
 * Action for opening the third party content library.
 */
public class OpenExternalLibraryAction extends Action {

  private var dataSourceValueExpression:ValueExpression;
  private var dataIndex:Number;

  /**
   * @param config
   */
  public function OpenExternalLibraryAction(config:openExternalLibraryAction) {
    super(config);
    this.dataIndex = config.dataIndex;
    dataSourceValueExpression = ValueExpressionFactory.create('dataSource', beanFactory.createLocalBean());
    if (!config['handler']) {
      setHandler(openLibrary, this);
    }
  }

  private function openLibrary() {
    var window:ExternalLibraryWindow = Ext.getCmp('externalLibrary') as ExternalLibraryWindow;
    if (!window) {
      window = new ExternalLibraryWindow(externalLibraryWindow({dataIndex:dataIndex}));
      window.show();
    }
  }
}
}
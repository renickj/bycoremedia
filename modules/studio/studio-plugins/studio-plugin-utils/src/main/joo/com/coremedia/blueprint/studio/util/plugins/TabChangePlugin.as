package com.coremedia.blueprint.studio.util.plugins {
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.Panel;
import ext.Plugin;
import ext.TabPanel;

/**
 * Hook that provides tab change events for the WorkArea and DocumentTabPanel.
 */
public class TabChangePlugin implements Plugin {

  public function init(component:Component):void {
    component.addListener('beforetabchange', tabChanged);
    component.addListener('destroy', doDestroy);
    component.addListener('beforedestroy', doBeforeDestroy);
    component.addListener('beforeremove', doRemove);
  }

  private static function doRemove(tabPanel:TabPanel, tab:Panel):void {
    if(tabPanel is WorkArea && StudioUtil.getWorkAreaTabChangeExpression().getValue() === tab) {
      StudioUtil.getWorkAreaTabChangeExpression().setValue(null);
    }
    else if(tabPanel is DocumentTabPanel && StudioUtil.getDocumentTabChangeExpression().getValue() === tab) {
      StudioUtil.getDocumentTabChangeExpression().setValue(null);
    }
  }

  private static function tabChanged(panel:TabPanel, tab:Panel):void {
    if(panel is WorkArea) {
      StudioUtil.getWorkAreaTabChangeExpression().setValue(tab);
    }
    else if(panel is DocumentTabPanel) {
      StudioUtil.getDocumentTabChangeExpression().setValue(tab);
    }
  }

  private function doBeforeDestroy(component:Component):void {
    if (component is WorkArea) {
      var workAreaTabChangeExpression:ValueExpression = StudioUtil.getWorkAreaTabChangeExpression();
      var currentWorkAreaTab:Component = workAreaTabChangeExpression.getValue() as Component;
      if (currentWorkAreaTab && currentWorkAreaTab.findParentBy(function(cmp:Component):Boolean {return cmp === component})) {
        workAreaTabChangeExpression.setValue(null);
      }
    } else if (component is DocumentTabPanel) {
      var documentTabChangeExpression:ValueExpression = StudioUtil.getDocumentTabChangeExpression();
      var currentDocumentTab:Component = documentTabChangeExpression.getValue() as Component;
      if (currentDocumentTab && currentDocumentTab.findParentBy(function(cmp:Component):Boolean {return cmp === component})) {
        documentTabChangeExpression.setValue(null);
      }
    }
  }

  private function doDestroy(component:Component):void {
    component.removeListener('beforetabchange', tabChanged);
    component.removeListener('destroy', doDestroy);
    component.removeListener('beforedestroy', doBeforeDestroy);
    component.removeListener('beforeremove', doRemove);
  }
}
}

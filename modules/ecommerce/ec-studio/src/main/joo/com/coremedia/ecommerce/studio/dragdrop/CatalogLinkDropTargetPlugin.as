package com.coremedia.ecommerce.studio.dragdrop {
import com.coremedia.ecommerce.studio.config.catalogLinkDropTargetPlugin;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.Plugin;

/**
 * A plugin to create a drop target that receives a single catalog link
 * and writes its id to the given value expression.
 */
public class CatalogLinkDropTargetPlugin implements Plugin {
  private var valueExpression:ValueExpression;
  private var catalogObjectType:String;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var multiple:Boolean;
  private var createStructFunction:Function;

  private var component:Component;
  private var linkDropTarget:CatalogLinkDropTarget;

  /**
   * A plugin to create a drop target that receives a single catalog link.
   *
   * @param config the config object
   *
   * @see com.coremedia.ecommerce.studio.config.catalogLinkDropTargetPlugin
   */
  public function CatalogLinkDropTargetPlugin(config:catalogLinkDropTargetPlugin) {
    valueExpression = config.valueExpression;
    catalogObjectType = config.catalogObjectType;
    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    multiple = config.multiple;
    createStructFunction = config.createStructFunction;
  }

  public function init(component:Component):void {
    this.component = component;
    component.mon(component, 'render', onRender);
    component.mon(component, 'beforedestroy', beforeCmpDestroy);
  }

  private function beforeCmpDestroy():void {
    linkDropTarget && linkDropTarget.unreg();
  }

  private function onRender():void {
    linkDropTarget = new CatalogLinkDropTarget(component, valueExpression, catalogObjectType,
            forceReadOnlyValueExpression, multiple, createStructFunction);
  }
}
}

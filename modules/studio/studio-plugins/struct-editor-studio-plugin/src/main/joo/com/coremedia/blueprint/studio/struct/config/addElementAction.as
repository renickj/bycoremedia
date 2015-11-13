package com.coremedia.blueprint.studio.struct.config {

import com.coremedia.blueprint.studio.struct.editor.StructHandler;
import com.coremedia.ui.data.ValueExpression;

import ext.config.action;

[ExtConfig(target="com.coremedia.blueprint.studio.struct.editor.AddElementAction")]
public class addElementAction extends action {

  public function addElementAction(config:Object = null) {
    super(config || {});
  }

  public native function get nodeType():Object;
  public native function set nodeType(value:Object):void;

  public native function get selectedNodeExpression():ValueExpression;
  public native function set selectedNodeExpression(value:ValueExpression):void;

  public native function get structHandler():StructHandler;
  public native function set structHandler(value:StructHandler):void;
}
}

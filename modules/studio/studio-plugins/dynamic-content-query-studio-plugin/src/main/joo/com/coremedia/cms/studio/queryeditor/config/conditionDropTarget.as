package com.coremedia.cms.studio.queryeditor.config {

import com.coremedia.ui.data.ValueExpression;

import ext.Ext;
import ext.config.container;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.components.ConditionDropTarget", xtype)]
public class conditionDropTarget extends ext.config.container {

  public static native function get xtype():String;

  public function conditionDropTarget(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;

  /**
   * a property path to the Content
   */
  public native function get bindTo():com.coremedia.ui.data.ValueExpression;
  /**
   * @private
   */
  public native function set bindTo(value:com.coremedia.ui.data.ValueExpression):void;
  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  public native function get forceReadOnlyValueExpression():com.coremedia.ui.data.ValueExpression;
  /**
   * @private
   */
  public native function set forceReadOnlyValueExpression(value:com.coremedia.ui.data.ValueExpression):void;

}
}

package com.coremedia.blueprint.personalization.editorplugin.config {

import joo.JavaScriptObject;

[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.taxonomy.TaxonomyProperty", xtype)]
public dynamic class taxonomyProperty extends JavaScriptObject {

  public static native function get xtype():String;

  public function taxonomyProperty(config:Object = null) {
    super(config || {});
  }

  /**
   * The property prefix
   */
  public native function get propertyPrefix():String;
  public native function set propertyPrefix(value:String):void;

  /**
   * Text for empty keyword
   */
  public native function get keywordEmptyText():String;
  public native function set keywordEmptyText(value:String):void;

  /**
   * Keyword text
   */
  public native function get keywordText():String;
  public native function set keywordText(value:String):void;

  /**
   * Text for empty text
   */
  public native function get valueEmptyText():String;
  public native function set valueEmptyText(value:String):void;

  /**
   * The value type
   */
  public native function get valueType():String;
  public native function set valueType(value:String):void;

  /**
   * The suffix text
   */
  public native function get suffixText():String;
  public native function set suffixText(value:String):void;
}
}
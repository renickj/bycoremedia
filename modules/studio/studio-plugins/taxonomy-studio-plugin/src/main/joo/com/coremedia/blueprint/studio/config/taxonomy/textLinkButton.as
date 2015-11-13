package com.coremedia.blueprint.studio.config.taxonomy {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;

import ext.config.button;
import ext.data.Record;

[ExtConfig(target="com.coremedia.blueprint.studio.taxonomy.chooser.TextLinkButton", xtype)]
public dynamic class textLinkButton extends button {

  public static native function get xtype():String;

  public function textLinkButton(config:Object = null) {
    super(config || {});
  }

  public native function get taxonomyNode():TaxonomyNode;
  public native function set taxonomyNode(value:TaxonomyNode):void;

  public native function get addable():Boolean;
  public native function set addable(value:Boolean):void;

  public native function get weight():String;
  public native function set weight(value:String):void;
}
}
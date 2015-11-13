package com.coremedia.blueprint.elastic.social.studio.forms {

import com.coremedia.blueprint.elastic.social.studio.ElasticSocialStudioPlugin_properties;

import ext.ComponentMgr;
import ext.Template;
import ext.Window;
import ext.util.StringUtil;

public class CMMailHelpWindowBase extends Window {
  public static const xtype:String = "com.coremedia.elastic.social.studio.form.CMMailHelpWindowBase";
  {
    ComponentMgr.registerType(xtype, CMMailHelpWindowBase);
  }

  public static const ID:String = "cmmail-help-window";

  internal static const TABLE_BODY_ID:String = "cmmail-help-table";
  internal static const TABLE:String = StringUtil.format(
          "<table><thead><tr><td>{0}</td><td>{1}</td></tr></thead><tbody id='{2}'></tbody></table><br/>",
          ElasticSocialStudioPlugin_properties.INSTANCE.cmmail_help_window_value,
          ElasticSocialStudioPlugin_properties.INSTANCE.cmmail_help_window_description,
          TABLE_BODY_ID
  );
  internal static const ROW:String = "<tr><td>{value}</td><td>{description}</td></tr>";
  internal static const KEY_PREFIX:String = "cmmail_help_window_value_";
  internal static const KEY_PATTERN:String = KEY_PREFIX + "(.+)";

  public function CMMailHelpWindowBase(config:* = undefined) {
    super(config);
    applyTemplate();
  }

  public function applyTemplate():void {
    var rowTpl:Template = new Template(ROW);
    rowTpl.compile();

    //noinspection JSMismatchedCollectionQueryUpdateInspection,JSMismatchedCollectionQueryUpdate
    var keys:Array = [];
    for (var key:String in ElasticSocialStudioPlugin_properties.INSTANCE) {
      //noinspection JSUnfilteredForInLoop
      var match:Array = key.match(KEY_PATTERN);
      if (match) {
        keys.push(match[1]);
      }
    }
    keys.sort();
    keys.forEach(function(key:String):void {
      //noinspection JSUnusedGlobalSymbols
      rowTpl.append(TABLE_BODY_ID, {
        value: StringUtil.format("${{0}}", key),
        description: ElasticSocialStudioPlugin_properties.INSTANCE[KEY_PREFIX + key]
      });
    })
  }
}
}

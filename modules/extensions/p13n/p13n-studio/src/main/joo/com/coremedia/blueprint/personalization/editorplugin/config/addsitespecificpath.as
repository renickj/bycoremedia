package com.coremedia.blueprint.personalization.editorplugin.config {

import com.coremedia.ui.data.ValueExpression;

import joo.JavaScriptObject;

/**
 * Plugin that adds a path relative to the current user's home folder to a {@link PersonaSelector}.
 *
 * <p>This class serves as a typed config object for the constructor of the plugin class <code>AddSiteSpecificPathsPlugin</code>.
 * Instantiating this class for the first time also registers the corresponding plugin class under the ptype
 * "com.coremedia.blueprint.personalization.editorplugin.config.addsitespecificpath" with ExtJS.
 * </p>
 *
 * @see com.coremedia.personalization.ui.plugin.AddPathFromHomeFolderPlugin
 */
[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.plugin.AddSiteSpecificPathPlugin", ptype)]
public dynamic class addsitespecificpath extends JavaScriptObject {

  public static native function get ptype():String;
  /**
   * <p>Use this constructor to create a typed config object for the constructor of the plugin class
   * <code>AddSiteSpecificPathsPlugin</code> and to register the plugin with ExtJS.</p>
   *
   * @see com.coremedia.blueprint.personalization.editorplugin.plugin.AddSiteSpecificPathPlugin
   */
  public function addsitespecificpath(config:Object = null) {
    super(config || {});
  }

  /**
   * A value expression evaluating to the active Content of the preview panel.
   */
  public native function get activeContentValueExpression():ValueExpression;
  /**
   * @private
   */
  public native function set activeContentValueExpression(value:ValueExpression):void;
  /**
   * path containing a placeholder for the site that will be added to a 'PersonaSelector'
   */
  public native function get path():String;
  /**
   * @private
   */
  public native function set path(value:String):void;
  /**
   * optional header label that will visible inside the PersonaSelector
   */
  public native function get groupHeaderLabel():String;
  /**
   * @private
   */
  public native function set groupHeaderLabel(value:String):void;
}
}
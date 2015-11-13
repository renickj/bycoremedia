package com.coremedia.blueprint.personalization.editorplugin.config {

import joo.JavaScriptObject;

/**
 * The field that uses this plugin retrieves the status of the search from the preview panel and adapts its validation state and tooltip accordingly, thus providing better error feedback to the Studio user.
 *
 * <p>This class serves as a typed config object for the constructor of the component class <code>SearchValidator</code>.
 * Instantiating this class for the first time also registers the corresponding component class under the xtype
 * "com.coremedia.blueprint.personalization.editorplugin.config.searchValidator" with ExtJS.
 * </p>
 *
 * @see com.coremedia.blueprint.personalization.editorplugin.SearchValidator
 */
[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.SearchValidator", ptype)]
public dynamic class searchValidator extends JavaScriptObject {

  public static native function get ptype():String;
  /**
   * <p>Use this constructor to create a typed config object for the constructor of the component class
   * <code>SearchValidator</code> and to register the component with ExtJS.</p>
   *
   * @see com.coremedia.blueprint.personalization.editorplugin.SearchValidator
   */
  public function searchValidator(config:Object = null) {
    super(config || {});
  }

}
}
package com.coremedia.blueprint.personalization.editorplugin.config {

import ext.config.combo;

/**
 *
 * <p>This class serves as a typed config object for the constructor of the component class <code>CMUserProfileFormComboBox</code>.
 * Instantiating this class for the first time also registers the corresponding component class under the xtype
 * "com.coremedia.blueprint.personalization.editorplugin.config.cMUserProfileFormComboBox" with ExtJS.
 * </p>
 *
 * @see com.coremedia.blueprint.personalization.editorplugin.property.CMPersonaFormComboBox
 */
[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.property.CMPersonaFormComboBox", xtype)]
public dynamic class cmPersonaFormComboBox extends combo {

  public static native function get xtype():String;

  /**
   * <p>Use this constructor to create a typed config object for the constructor of the component class
   * <code>CMUserProfileFormComboBox</code> and to register the component with ExtJS.</p>
   *
   * @see com.coremedia.blueprint.personalization.editorplugin.property.CMPersonaFormComboBox
   */
  public function cmPersonaFormComboBox(config:Object = null) {
    super(config || {});
  }

  /**
   * the enumeration of possible properties and their display names. See below
   */
  public native function get properties():Object;

  /**
   * @private
   */
  public native function set properties(value:Object):void;
}
}
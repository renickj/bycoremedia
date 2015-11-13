package com.coremedia.blueprint.personalization.editorplugin.property {
import com.coremedia.blueprint.personalization.editorplugin.config.cmPersonaFormComboBox;

import ext.Ext;
import ext.config.arraystore;
import ext.config.combo;
import ext.data.ArrayStore;
import ext.form.ComboBox;

public class CMPersonaFormComboBox extends ComboBox {

  /**
   * @cfg {Object} properties the enumeration of possible properties and their display names. See below
   * @param config
   */
  public function CMPersonaFormComboBox(config:cmPersonaFormComboBox) {

    super(combo(Ext.apply(config, {

      store: new ArrayStore(arraystore({
        id: 0,
        fields: [
          'myId',
          'displayText'
        ],
        data: config['properties']
      })),
      valueField: 'myId',
      displayField: 'displayText'
    })));
  }
}
}

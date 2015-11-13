package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyPropertyField;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.cap.common.session;

import ext.Container;
import ext.Ext;


/**
 * Base class for the taxonomy property editor.
 * The class is used to disable the suggestion panel if they are not required.
 */
public class TaxonomyPropertyFieldBase extends Container {

  private var disableSuggestions:Boolean;

  public function TaxonomyPropertyFieldBase(config:taxonomyPropertyField) {
    disableSuggestions = config.disableSuggestions;
    super(config);
  }


  override protected function initComponent():void {
    super.initComponent();
    if(disableSuggestions) {
      find('itemId','suggestionsPanel')[0].hide();
    }
  }
}
}

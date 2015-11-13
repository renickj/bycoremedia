package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.config.taxonomy.openTaxonomyChooserAction;
import com.coremedia.blueprint.studio.config.taxonomy.taxonomySelectionWindow;
import com.coremedia.blueprint.studio.taxonomy.chooser.TaxonomySelectionWindow;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Ext;
import ext.config.action;

/**
 * Shows the dialog for choosing taxonomies for a linklist property.
 */
public class OpenTaxonomyChooserAction extends Action {

  private var propertyValueExpression:ValueExpression;
  private var taxId:String;
  private var singleSelection:Boolean;

  /**
   * @param config
   */
  public function OpenTaxonomyChooserAction(config:openTaxonomyChooserAction) {
    propertyValueExpression = config.propertyValueExpression;
    singleSelection = config.singleSelection;
    taxId = config.taxonomyId;
    super(action(Ext.apply({
      handler: showChooser
    }, config)));
  }

  private function showChooser():void {
    var taxChooser:TaxonomySelectionWindow = new TaxonomySelectionWindow(taxonomySelectionWindow({
      taxonomyId: taxId,
      singleSelection: singleSelection,
      propertyValueExpression: propertyValueExpression
    }));
    taxChooser.show();
  }

}
}

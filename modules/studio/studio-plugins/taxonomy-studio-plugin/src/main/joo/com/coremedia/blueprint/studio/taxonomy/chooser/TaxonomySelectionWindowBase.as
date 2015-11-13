package com.coremedia.blueprint.studio.taxonomy.chooser {
import com.coremedia.blueprint.studio.config.taxonomy.taxonomySelectionWindow;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Window;

/**
 * The base class of the taxonomy selection window.
 */
public class TaxonomySelectionWindowBase extends Window {

  private var selectionExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  private var singleSelection:Boolean;

  public function TaxonomySelectionWindowBase(config:taxonomySelectionWindow) {
    super(config);
    singleSelection = config.singleSelection;
    propertyValueExpression = config.propertyValueExpression;

    var selection:Array = [];
    if (singleSelection) {
      var value:Content = propertyValueExpression.getValue() as Content;
      if (value) {
        selection.push(value);
      }
    }
    else {
      var values:Array = propertyValueExpression.getValue();
      if (values) {
        for (var i:int = 0; i < values.length; i++) {
          selection.push(values[i]);
        }
      }
    }

    getSelectionExpression().setValue(selection);
  }

  /**
   * Ok button handler.
   */
  protected function okPressed():void {
    var selection:Array = selectionExpression.getValue();
    if (!singleSelection) {
      propertyValueExpression.setValue(selection);
    }
    else {
      if (selection && selection.length > 0) {
        propertyValueExpression.setValue(selection[0]);
      }
      else {
        propertyValueExpression.setValue(null);
      }
    }

    close();
  }

  /**
   * Cancel button handler.
   */
  protected function cancelPressed():void {
    close();
  }

  /**
   * Depending on single selection mode, a different link list title is displayed
   * for the active selection.
   * @param singleSelection
   * @return
   */
  protected function resolveSelectionTitle(singleSelection:Boolean):String {
    if (singleSelection) {
      return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_singleSelection_title;
    }
    return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_title;
  }

  /**
   * Contains the entries selected by the user.
   * @return
   */
  protected function getSelectionExpression():ValueExpression {
    if (!selectionExpression) {
      selectionExpression = ValueExpressionFactory.create('selection', beanFactory.createLocalBean());
    }
    return selectionExpression;
  }
}
}
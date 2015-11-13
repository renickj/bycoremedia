package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.blueprint.studio.config.externallibrary.externalLibraryWindow;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Window;

/**
 * The base class of the external content library window, creates all value
 * expression for event handling between the panels.
 */
public class ExternalLibraryWindowBase extends Window {

  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  private var selectedValueExpression:ValueExpression;

  public function ExternalLibraryWindowBase(config:externalLibraryWindow) {
    super(config);
  }

  /**
   * Returns the value expression that contains the current filter string value.
   * @return
   */
  protected function getFilterValueExpression():ValueExpression {
    if(!filterValueExpression) {
      filterValueExpression = ValueExpressionFactory.create('searchFilter', beanFactory.createLocalBean());
    }
    return filterValueExpression;
  }

  /**
   * Returns the value expression that contains the current selected external data source record.
   * @return
   */
  protected function getDataSourceValueExpression():ValueExpression {
    if(!dataSourceValueExpression) {
      dataSourceValueExpression = ValueExpressionFactory.create('dataSource', beanFactory.createLocalBean());
    }
    return dataSourceValueExpression;
  }

  /**
   * Returns the value expression that contains selected list record.
   * @return
   */
  protected function getSelectedValueExpression():ValueExpression {
    if(!selectedValueExpression) {
      selectedValueExpression = ValueExpressionFactory.create('selectedValue', beanFactory.createLocalBean());
    }
    return selectedValueExpression;
  }
}
}
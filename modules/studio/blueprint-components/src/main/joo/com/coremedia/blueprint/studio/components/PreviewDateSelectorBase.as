package com.coremedia.blueprint.studio.components {
import com.coremedia.blueprint.studio.config.components.previewDateSelector;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.Calendar;
import com.coremedia.ui.data.ValueExpression;

import ext.Container;
import ext.Ext;

use namespace Ext;

/**
 * The preview date selector will be added to the toolbar
 * of the preview and allows the have a look on the preview for a
 * specific time range.
 */
public class PreviewDateSelectorBase extends Container {

  private var previewPanel:PreviewPanel;
  private var dateValueExpression:ValueExpression;

  public function PreviewDateSelectorBase(config:previewDateSelector) {
    super(config);
    previewPanel = config.previewPanel;
    dateValueExpression = config.dateValueExpression;
    dateValueExpression.addChangeListener(dateChanged);
  }

  override protected function onDestroy():void {
    super.onDestroy();
    dateValueExpression.removeChangeListener(dateChanged);
  }

  /**
   * Invoked when one of the date combos has been changed.
   */
  private function dateChanged():void {
    var value:Calendar = dateValueExpression.getValue();
    const params:Bean = previewPanel.getUrlParameterBean();
    if (value) {
      var dateString:String = formatCalendar(value);
      params.set('previewDate', dateString);
    }
    else {
      params.set('previewDate', null);
    }
  }

  /**
   * Formats a calendar string
   * @param value
   * @return String with format yyyy-mm-dd hh:mm T
   */
  private static function formatCalendar(value:Calendar):String {
    var date:Date = value.getDateWithoutOffset();
    var format:String = 'd-m-Y H:i';
    var dateString:String = date.format(format) + ' ' + value.getTimeZone();
    return dateString;
  }

}
}
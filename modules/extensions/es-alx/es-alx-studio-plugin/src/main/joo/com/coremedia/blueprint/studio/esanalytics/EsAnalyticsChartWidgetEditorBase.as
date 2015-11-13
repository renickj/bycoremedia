package com.coremedia.blueprint.studio.esanalytics {
import com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartWidgetEditor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.ui.components.StatefulContainer;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.form.ComboBox;

public class EsAnalyticsChartWidgetEditorBase extends StatefulContainer {

  private const ALX_API_ROOT_CHANNELS_BASE_URL:String = "blueprint/alx/rootchannels";
  private var rootChannelValueExpr:ValueExpression;

  public function EsAnalyticsChartWidgetEditorBase(config:esAnalyticsChartWidgetEditor) {
    super(config);

    getRootChannelValueExpression().addChangeListener(rootChannelChanged);
  }

  override protected function onDestroy():void {
    getRootChannelValueExpression().removeChangeListener(rootChannelChanged);
  }

  protected function getSelectedSiteExpression():ValueExpression {
    return ValueExpressionFactory.create("content", getModel());
  }

  protected function getRootChannelValueExpression():ValueExpression {
    if (!rootChannelValueExpr) {
      rootChannelValueExpr = ValueExpressionFactory.create('rootChannels', getRemoteBean());
    }
    return rootChannelValueExpr;
  }

  protected static function getContentFromId(id:String):Content {
    return ContentUtil.getContent(id);
  }

  protected static function getIdFromContent(content:Content):String {
    return content ? content.getId() : undefined;
  }

  private function rootChannelChanged():void {
    var comboBox:ComboBox = findByType("combo")[0] as ComboBox;
    if (comboBox) {
      mon(comboBox.getStore(), "load", function ():void {
        storeLoaded(comboBox)
      });
    }
  }

  private function storeLoaded(comboBox:ComboBox):void {
    var value:* = getSelectedSiteExpression().getValue();
    var index:int = comboBox.getStore().find("id", value);
    if (index >= 0) {
      var beanRecord:BeanRecord = comboBox.getStore().getAt(index) as BeanRecord;
      if (beanRecord.data && beanRecord.data.value) {
        comboBox.setValue(value);
      } else {
        mon(comboBox.getStore(), "update", function ():void {
          setComboBoxValue(comboBox, value)
        });
      }
    }
  }

  private static function setComboBoxValue(comboBox:ComboBox, value:int):void {
    comboBox.setValue(value);
  }

  private function getRemoteBean():RemoteBean {
    return beanFactory.getRemoteBean(ALX_API_ROOT_CHANNELS_BASE_URL);
  }
}
}
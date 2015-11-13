package com.coremedia.blueprint.studio.esanalytics {

import com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartPanel;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.workArea;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Panel;
import ext.data.Record;
import ext.list.ListView;

public class EsAnalyticsChartPanelBase extends CollapsibleFormPanel {

  protected var timeRangeValueExpression:ValueExpression;

  private const ALX_API_BASE_URL:String = "blueprint/alx/pageviews/";

  private var esChart:EsChart;

  public function EsAnalyticsChartPanelBase(config:esAnalyticsChartPanel) {
    super(config);
  }

  protected function getTimeRangeValueExpression():ValueExpression {
    if (!timeRangeValueExpression) {
      timeRangeValueExpression = ValueExpressionFactory.create('timerange', beanFactory.createLocalBean({'timerange':7}));
    }
    return timeRangeValueExpression;
  }

  override protected function afterRender():void {
    super.afterRender();

    var systemTabPanel:Panel = this.findParentByType(DocumentTabPanel) as Panel;
    var versionHistoryListView:ListView = systemTabPanel.find('itemId', 'versionHistory')[0];
    if(versionHistoryListView) {
      mon(versionHistoryListView, 'mouseenter', markEventInChartPanel);
    }
  }

  private function markEventInChartPanel(historyPanel:ListView, index:Number):void {
    if (getEsChart().getLineChart()) {
      var record:Record = historyPanel.getStore().getAt(index);
      var lifecycleStatus:String = record.data.lifecycleStatus as String;
      if ("published" === lifecycleStatus) {
        var date:Date = record.data.editionDate as Date;
        var now:Date = new Date();
        var today:Date = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        var diff:Number = Math.ceil((today.getTime() - date.getTime()) / 86400000); //in days
        var interval:Number = getEsChart().getLineChartData().length;
        if (interval - diff >= 0) {
          var pos:Number = interval - diff;
          getEsChart().getLineChart().displayHoverForPublication(pos - 1);
        }
      }
    }
  }

  protected static function getCurrentContent():Content {
    return workArea.ACTIVE_CONTENT_VALUE_EXPRESSION.getValue() as Content;
  }

  private function getEsChart():EsChart {
    if (!esChart) {
      esChart = this.find("itemId", EsAnalyticsChart.ES_CHART_ITEM_ID)[0] as EsChart;
    }
    return esChart;
  }

  public function getAlxData(serviceName:String, propertyName:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function (serviceName1:String, propertyName1:String):RemoteBean {
      var currentContent:Content = getCurrentContent();
      if (validContent(currentContent)) {
        var currentContentId:int = IdHelper.parseContentId(currentContent);
        return ValueExpressionFactory.create(propertyName1, beanFactory.getRemoteBean(ALX_API_BASE_URL + currentContentId + "/" + serviceName1
                + "?timeRange=" + getTimeRangeValueExpression().getValue())).getValue();
      }
      return null;
    }, serviceName, propertyName);

  }

  private static function validContent(content:Content):Boolean {
    return content && "CMArticle" === content.getType().getName();
  }
}
}

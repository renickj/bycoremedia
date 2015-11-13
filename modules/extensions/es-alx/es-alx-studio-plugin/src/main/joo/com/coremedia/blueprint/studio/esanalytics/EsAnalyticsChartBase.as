package com.coremedia.blueprint.studio.esanalytics {
import com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChart;
import com.coremedia.ui.data.ValueExpression;

import ext.Panel;

public class EsAnalyticsChartBase extends Panel {

  private var chartPanel:EsChart;

  public native function get timeRangeValueExpression():ValueExpression;
  public native function get bindTo():ValueExpression;

  public function EsAnalyticsChartBase(config:esAnalyticsChart) {
    super(config);
    mon(getChartPanel(), "resize", function():void {getChartPanel().initChartWhenAvailable()});
  }

  private function getChartPanel():EsChart {
    if (!chartPanel) {
      chartPanel = this.find('itemId', esAnalyticsChart.ES_CHART_ITEM_ID)[0] as EsChart
    }
    return chartPanel;
  }
}
}

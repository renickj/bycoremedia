package com.coremedia.blueprint.studio.esanalytics {
import com.coremedia.blueprint.studio.config.esanalytics.esChart;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.exml.ValueExpression;

import ext.Ext;
import ext.Panel;
import ext.form.Label;
import ext.util.Format;

public class EsChartBase extends Panel {

  private var lineChart:Object;
  private var lineChartData:Array;
  private var chartLabelName:String;
  private var chartPanel:Panel;
  private var color:String;
  private var switchContainer:SwitchingContainer;
  private static const MIN_Y_VALUE:Number = 10;
  private var addedListener:Boolean = false;

  private const CHART_X_AXIS_PROPERTY_NAME:String = "key";
  private const DEFAULT_COLOR:String = "#4189DD";
  private const CHART_VALUE_NAMES:Array = ['value'];
  private const CHART_LABEL_NAMES:Array = [EsAnalyticsStudioPlugin_properties.INSTANCE.chart_label_page_views];

  public native function get bindTo():ValueExpression;
  public native function get chartHeight():Number;
  public native function get emptyChartHeight():Number;

  public function EsChartBase(config:esChart) {
    super(config);
    color = config.color;
    chartLabelName = config.chartLabelName;
    mon(this, "resize", initChartWhenAvailable);

    getSwitchContainer().mon(switchContainer, "afterlayout", setHeightAccordingToInnerItem);
    bindTo.addChangeListener(setHeightAccordingToInnerItem);
  }

  override protected function onDestroy():void {
    bindTo.removeChangeListener(setHeightAccordingToInnerItem);
  }

  private function setHeightAccordingToInnerItem():void {
    if (getSwitchContainer().getActiveItem() is Panel) {
      ownerCt.setHeight(chartHeight + 80);
    } else if (getSwitchContainer().getActiveItem() is Label) {
      ownerCt.setHeight(emptyChartHeight);
    }
  }

  protected static function getActiveItemId(data:Array):String {
    return ((data && data.length > 0) ? esChart.CHART_PANEL_ITEM_ID : esChart.NO_DATA_FIELD_ITEM_ID);
  }

  /**
   * Convert the rawData from the Server to a localized chart data.
   * @param rawData the data array that comes from the server
   * @return the localized chart data
   */
  protected function localizeChartData(rawData:Array):Array {
    return rawData.map(function (rawDataEntry:Object):Object {
      const dateXLabel:String = rawDataEntry[CHART_X_AXIS_PROPERTY_NAME];

      const dateProperties:Object = {
        year:dateXLabel.slice(0, 4),
        month:dateXLabel.slice(4, 6),
        day:dateXLabel.slice(6, 8)
      };
      const date:Date = new Date(dateProperties.year, dateProperties.month - 1, dateProperties.day);
      return {
        key:Format.dateRenderer(EsAnalyticsStudioPlugin_properties.INSTANCE.shortDateFormat)(date),
        value:rawDataEntry[CHART_VALUE_NAMES[0]]
      };
    });
  }

  public function initChartWhenAvailable():void {
    var currentChartPanel:Panel = getChartPanel();
    if (currentChartPanel && currentChartPanel.getEl()) {
      initChart();
    } else if (currentChartPanel) {
      if (!addedListener) {
        mon(currentChartPanel, "afterrender", initChart);
        addedListener = true;
      }
    }
  }

  /**
   * Init the Morris LineChart.
   */
  private function initChart():void {
    if (lineChartData) {
      // unfortunately, we need to reset this container on every resize and re-init the chart
      // but at least the data is not fetched again from the server (is accessible via getData())
      Ext.get(getElementId()).dom.innerHTML = "";

      // a list of possible config options: http://www.oesmith.co.uk/morris.js/lines.html
      var lineChartConfig:Object = {
        element:getElementId(), // the ID of the element that this chart should be insert to
        xkey:CHART_X_AXIS_PROPERTY_NAME, // the name of the x-axis key
        ykeys:CHART_VALUE_NAMES, // the names of the value properties
        labels:[chartLabelName ? chartLabelName : CHART_LABEL_NAMES], // the labels that map the value properties
        parseTime:false, // the chart shouldn't automatically convert the values as time object
        smooth:false, // no smooth edges at the line,
        ymax:getYMax(getLineChartData()),
        data:localizeChartData(getLineChartData()),
        lineColors:[color ? color : DEFAULT_COLOR]
      };

      setLineChart(new Morris.Line(lineChartConfig));
      getSwitchContainer().doLayout();
    }
  }

  /**
   * @return the Element ID of the panel that the chart should be rendered to
   */
  private function getElementId():String {
    return getChartPanel().getEl().first().first().getAttribute("id");
  }

  private function getChartPanel():Panel {
    if (!chartPanel) {
      chartPanel = this.find('itemId', esChart.CHART_PANEL_ITEM_ID)[0] as Panel
    }
    return chartPanel;
  }

  private function getSwitchContainer():SwitchingContainer {
    if (!switchContainer) {
      switchContainer = this.find('itemId', esChart.ES_CHART_SWITCHER_ITEM_ID)[0] as SwitchingContainer
    }
    return switchContainer;
  }

  private function setLineChart(lineChart:*):void {
    this.lineChart = lineChart;
  }

  public function getLineChart():* {
    return this.lineChart;
  }

  public function setLineChartData(lineChartData:Array):void {
    this.lineChartData = lineChartData;
    initChartWhenAvailable();
  }

  public function getLineChartData():Array {
    return this.lineChartData ? this.lineChartData.slice(0, this.lineChartData.length) : [];
  }

  private static function getYMax(data:Array):Number {
    var maxYValue:Number = MIN_Y_VALUE;
    for (var i:int = 0; i < data.length; i++) {
      if (data[i].value > maxYValue) {
        maxYValue = data[i].value;
      }
    }

    var limit:Number = MIN_Y_VALUE;
    while (maxYValue / limit > 1) {
      limit = limit * 10;
    }

    for (i = 1; i < 4; i++) {
      var refinedLimit:Number = i * limit / 4;
      if (maxYValue <= refinedLimit) {
        return refinedLimit;
      }
    }
    return limit;
  }

  protected static function transformTime(timeStamp:Date):String {
    if (timeStamp) {
      return Format.dateRenderer(EsAnalyticsStudioPlugin_properties.INSTANCE.dateFormat)(timeStamp);
    } else {
      return EsAnalyticsStudioPlugin_properties.INSTANCE.chart_time_stamp_unavailable;
    }
  }
}
}
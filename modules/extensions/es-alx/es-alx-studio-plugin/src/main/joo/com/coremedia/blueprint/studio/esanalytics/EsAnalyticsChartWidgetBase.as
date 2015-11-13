package com.coremedia.blueprint.studio.esanalytics {
import com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartWidget;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.widgetWrapper;
import com.coremedia.cms.editor.sdk.dashboard.WidgetWrapper;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Container;
import ext.Panel;
import ext.Toolbar;
import ext.form.Label;

public class EsAnalyticsChartWidgetBase extends Container {

  public native function get content():Content;

  private const ALX_API_BASE_URL:String = "blueprint/alx/pageviews/";
  private const PUBLICATIONS_API_BASE_URL:String = "blueprint/alx/publications/";

  protected var timeRangeValueExpression:ValueExpression;

  public function EsAnalyticsChartWidgetBase(config:esAnalyticsChartWidget) {
    super(config);

    mon(this, "afterlayout", function ():void {
      var title:String = EsAnalyticsStudioPlugin_properties.INSTANCE.widget_title;
      if (config.content) {
        var content:Content = config.content;
        if (content) {
          content.load(function (cont:Content):void {
            getWidgetLabel().setText(title + ": " + cont.getName());
          });
        }
      } else {
        getWidgetLabel().setText(title + ": " + EsAnalyticsStudioPlugin_properties.INSTANCE.widget_title_channel_undefined);
      }
    }, null, {single:true});
  }

  private function getWidgetLabel():Label {
    var wrapper:WidgetWrapper = findParentByType(widgetWrapper.xtype) as WidgetWrapper;
    var innerWrapper:Panel = wrapper.find("itemId", "innerWrapper")[0];
    var widgetToolbar:Toolbar = innerWrapper.getTopToolbar();
    return widgetToolbar.find("itemId", "widgetWrapperLabel")[0] as Label;
  }

  public function getAlxData(serviceName:String, propertyName:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function (serviceName1:String, propertyName1:String):RemoteBean {
      if (content && content.getId()) {
        return ValueExpressionFactory.create(propertyName1, beanFactory.getRemoteBean(ALX_API_BASE_URL + convertIdField(content.getId()) + "/" + serviceName1
                + "?timeRange=" + getTimeRangeValueExpression().getValue())).getValue();
      }
      return null;
    }, serviceName, propertyName);
  }

  public function getPublicationData(propertyName:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function (propertyName1:String):RemoteBean {
      if (content && content.getId()) {
        return ValueExpressionFactory.create(propertyName1, beanFactory.getRemoteBean(PUBLICATIONS_API_BASE_URL + convertIdField(content.getId())
                + "?timeRange=" + getTimeRangeValueExpression().getValue())).getValue();
      }
      return null;
    }, propertyName);
  }

  protected function getTimeRangeValueExpression():ValueExpression {
    if (!timeRangeValueExpression) {
      timeRangeValueExpression = ValueExpressionFactory.create('timerange', beanFactory.createLocalBean({'timerange':7}));
    }
    return timeRangeValueExpression;
  }

  private static function convertIdField(id:String):String {
    return id.substr(id.lastIndexOf('/') + 1, id.length);
  }
}
}
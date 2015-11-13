package com.coremedia.blueprint.studio.googleanalytics {
import com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsRetrievalFields;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

public class GoogleAnalyticsRetrievalFieldsBase extends CollapsibleFormPanel {

  private static const GOOGLE_ANALYTICS:String = 'googleAnalytics';
  private static const P12_FILE:String = 'p12File';
  private static const LOCAL_SETTINGS:String = 'localSettings';
  private static const CM_DOWNLOAD:String = "CMDownload";

  private var p12FileVE:ValueExpression;
  private var localSettings:RemoteBean;

  public function GoogleAnalyticsRetrievalFieldsBase(config:googleAnalyticsRetrievalFields) {
    super(config);
    updateP12FileFromStruct();
    getP12FileVE().addChangeListener(updateStruct);
    bindTo.addChangeListener(updateP12FileFromStruct);
  }

  internal native function get bindTo():ValueExpression;

  private function updateStruct():void {
    var value:Array = getP12FileVE().getValue();
    if (value && value.length > 0) {
      applyToStruct(bindTo.getValue(), CM_DOWNLOAD, P12_FILE, value[0]);
    } else {
      removeLinkFromStruct(bindTo.getValue(), P12_FILE);
    }
  }

  private static function removeLinkFromStruct(content:Content, structPropertyName:String):void {
    var struct:Struct = content.getProperties().get(LOCAL_SETTINGS);
    if (struct) {
      var googleAnalytics:Struct = getStruct(struct, GOOGLE_ANALYTICS);
      if (googleAnalytics) {
        googleAnalytics.getType().removeProperty(structPropertyName);
      }
    }
  }

  private static function getStruct(struct:Struct, key:String):Struct {
    return struct.get(key);
  }


  protected function getP12FileVE():ValueExpression {
    if (!p12FileVE) {
      p12FileVE = ValueExpressionFactory.create('linkValue', beanFactory.createLocalBean());
    }
    return p12FileVE;
  }

  private function updateP12FileFromStruct():void {
    var c:Content = bindTo.getValue();
    c.load(function ():void {
      var props:ContentProperties = c.getProperties();
      var init:Boolean = false;
      if (!localSettings) {
        init = true;
      }
      localSettings = props.get(LOCAL_SETTINGS) as RemoteBean;
      if (init) {
        localSettings.addPropertyChangeListener(GOOGLE_ANALYTICS, updateP12FileFromLocalSettings);
      }
      localSettings.load(function ():void {
        updateP12FileFromLocalSettings();
      });
    });
  }

  private function updateP12FileFromLocalSettings():void {
    var googleAnalytics:Struct = getStruct(localSettings as Struct, GOOGLE_ANALYTICS);
    if (googleAnalytics) {
      var p12File:Struct = googleAnalytics.get(P12_FILE);
      if (!p12File) {
        getP12FileVE().setValue([]);
      } else {
        getP12FileVE().setValue([p12File]);
      }
    }
  }

  private static function applyToStruct(content:Content, contentType:String, structPropertyName:String, link:Content):void {
    var struct:Struct = content.getProperties().get(LOCAL_SETTINGS);
    struct.getType().addStructProperty(GOOGLE_ANALYTICS);
    var googleAnalytics:Struct = getStruct(struct, GOOGLE_ANALYTICS);

    var capType:CapType = session.getConnection().getContentRepository().getContentType(contentType);
    googleAnalytics.getType().addLinkProperty(structPropertyName, capType, link);
  }

  override protected function onDestroy():void {
    super.onDestroy();
    localSettings.removePropertyChangeListener(GOOGLE_ANALYTICS, updateP12FileFromLocalSettings);
    getP12FileVE().removeChangeListener(updateStruct);
    bindTo.removeChangeListener(updateP12FileFromStruct);
  }
}
}
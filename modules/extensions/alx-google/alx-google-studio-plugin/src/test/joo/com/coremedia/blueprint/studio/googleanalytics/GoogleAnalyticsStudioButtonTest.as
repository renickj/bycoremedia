package com.coremedia.blueprint.studio.googleanalytics {
import com.coremedia.blueprint.studio.config.googleanalytics.googleAnalyticsReportPreviewButton;
import com.coremedia.ui.data.test.AbstractRemoteTest;

import ext.Viewport;

import joo.getQualifiedObject; // don't remove this import

public class GoogleAnalyticsStudioButtonTest extends AbstractRemoteTest {

  // static initializer
  {
    joo.getQualifiedObject("com.coremedia.cms.editor.sdk.EditorContextImpl").initEditorContext();
  }

  private var viewPort:Viewport;

  public function GoogleAnalyticsStudioButtonTest() {
  }

  override public function setUp():void {
    super.setUp();
    viewPort = new GoogleAnalyticsStudioButtonTestView();
  }

  override public function tearDown():void {
    super.tearDown();
  }

  public function testButtonDisabled():void {
    var button:GoogleAnalyticsReportPreviewButton = viewPort.findByType(googleAnalyticsReportPreviewButton.xtype)[0];
    assertTrue(button.disabled);
  }

  public function testDeepLinkReportUrl():void {
    var args:Object = undefined;
    window.open = function (... myArgs) : void { args = myArgs;};

    var button:GoogleAnalyticsReportPreviewButton = viewPort.findByType(googleAnalyticsReportPreviewButton.xtype)[0];
    button.setContent({
      getNumericId : function():int {return 42;},
      type : {name : 'typeWithPreview'}
    });
    waitUntil("button still disabled",
            function():Boolean {
              return !button.disabled
            },
            button['handler'] // simulate click
    );
    waitUntil("no window opened",
            function():Boolean {return args !== undefined;}
    )
  }

  private static const DRILLDOWN_URL:String = "http://host.domain.net/gai/drilldown/42";

  protected override function getMockCalls():Array {
    return [
      {
        "request": { "uri": "alxservice/42" },
        "response": { "body": {
          "googleAnalytics": DRILLDOWN_URL
        }}
      }
    ];
  }

}
}

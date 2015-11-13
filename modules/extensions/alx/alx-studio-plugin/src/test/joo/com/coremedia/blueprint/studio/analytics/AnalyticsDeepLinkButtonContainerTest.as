package com.coremedia.blueprint.studio.analytics {
import com.coremedia.blueprint.studio.config.analytics.openAnalyticsDeepLinkUrlButton;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ui.data.test.AbstractRemoteTest;

import ext.Button;
import ext.menu.Item;

// don't remove this import
import joo.getQualifiedObject;

public class AnalyticsDeepLinkButtonContainerTest extends AbstractRemoteTest {

  private static const URL_1:String = "http://host.domain.net/drilldown";
  private static const URL_2:String = "http://my.url";
  private static const MY_ID:Number = 666;

  private static const content:Object = {
    getNumericId: function ():int {
      return MY_ID;
    },
    type: {
      getNumericId: function ():int {
        return MY_ID;
      },
      name: 'typeWithPreview'}
  };

  // static initializer
  {
    joo.getQualifiedObject("com.coremedia.cms.editor.sdk.EditorContextImpl").initEditorContext();
  }

  private var window_open:Function;
  private var args:Object;

  override public function setUp():void {
    super.setUp();
    window_open = window.open;
    window.open = function (... myArgs) : void { args = myArgs;};

    // Make sure that a new context manager is instantiated for each test.
    new ComponentContextManager();
  }

  override public function tearDown():void {
    super.tearDown();
    window.open = window_open;
    args = null;
  }

  public function testAnalyticsDeepLinkButtonContainerSingleView():void {
    var testView:SingleAnalyticsUrlButtonTestView = new SingleAnalyticsUrlButtonTestView();

    var contentContainer:ContentProvidingTestContainerBase = ContentProvidingTestContainerBase(testView.get("contentContainer"));
    var container:AnalyticsDeepLinkButtonContainer = AnalyticsDeepLinkButtonContainer(contentContainer.get("alxDeepLinkButtonContainer"));
    var item:Button = Button(container.items.get(0));

    assertEquals(openAnalyticsDeepLinkUrlButton.xtype, item.xtype);
    assertTrue(item.disabled);

    contentContainer.setContent(content);

    waitUntil("button still disabled",
            function ():Boolean {
              return !item.disabled;
            },
            function():void {
              // invoke handler on enabled buttons:
              item['handler']();
              assertNotNull(args);
              assertEquals(URL_1, args[0]);
            }
    );

  }

  public function testAnalyticsDeepLinkButtonContainerMultiView():void {
    var testView:MultipleAnalyticsUrlButtonsTestView = new MultipleAnalyticsUrlButtonsTestView();
    var contentContainer:ContentProvidingTestContainerBase = ContentProvidingTestContainerBase(testView.get("contentContainer"));
    var container:AnalyticsDeepLinkButtonContainer = AnalyticsDeepLinkButtonContainer(contentContainer.get("alxDeepLinkButtonContainer"));

    // the menu button should be disabled initially
    var item:Button = Button(container.items.get(0));
    assertEquals('button', item.xtype);
    assertNotNull(item.menu);
    assertTrue("menu button should be initially disabled", item.disabled);

    var items:Array = item.menu.items.getRange();
    assertEquals(3, items.length);
    items.forEach(function (item:Item, index:int):void {
      assertTrue("item " + index + " should be disabled",item.disabled);
    });

    contentContainer.setContent(content);

    waitUntil("button still disabled",
            function ():Boolean {
              return !item.disabled;
            },
            function():void {
              // state of menu items:
              assertTrue("first menu item should be disabled", items[0].disabled);
              assertFalse("second menu item should be enabled", items[1].disabled);
              assertFalse("third menu item should be enabled", items[2].disabled);

              // invoke handler on enabled menu items:
              items[1].handler();
              assertNotNull(args);
              assertEquals(URL_1, args[0]);
              args = null;
              items[2].handler();
              assertNotNull(args);
              assertEquals(URL_2, args[0]);
            }
    );
  }

  protected override function getMockCalls():Array {
    return [
      {
        "request": { "uri": "alxservice/" + MY_ID},
        "response": { "body": {
          "testService1": 'invalidUrl',
          "testService2": URL_1,
          "testService3": URL_2
        }}
      }
    ];
  }

}
}

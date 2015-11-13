package com.coremedia.livecontext.studio.forms {
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.config.externalNavigationForm;
import com.coremedia.livecontext.studio.config.externalNavigationFormTestView;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.test.Step;

import ext.Component;

import ext.QuickTips;

import ext.Viewport;
import ext.form.Radio;
import ext.form.RadioGroup;

public class ExternalNavigationFormTest extends AbstractProductTeaserComponentsTest {
  private var viewPort:Viewport;
  private var catalogLink:Component;
  private var externalIdField:Component;
  private var externalUriPathField:Component;
  private var catalogPageRadio:Radio;
  private var otherPageRadio:Radio;

  override public function setUp():void {
    super.setUp();
    QuickTips.init(true);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort && viewPort.destroy();
  }

  public function testExternalNavigationForm():void {
    chain(
            createTestling(),
            waitForCatalogLinkVisible(),
            waitForExternalIdStringPropertiesHidden(),
            selectOtherPage(),
            waitForCatalogLinkHidden(),
            waitForExternalIdStringPropertiesVisible(),
            selectCatalogPage(),
            waitForCatalogLinkVisible(),
            waitForExternalIdStringPropertiesHidden()
    )
  }

  private function createTestling():Step {
    return new Step("Create the testling",
            function ():Boolean {
              return true;
            },
            function ():void {
              var config:externalNavigationForm = new externalNavigationForm({});
              //start the test with catalog = true
              var catalogExpression:ValueExpression = ValueExpressionFactory.createFromValue(true);
              config.catalogExpression = catalogExpression;
              config.bindTo = getBindTo();
              viewPort = new ExternalNavigationFormTestView(new externalNavigationFormTestView(config));
              catalogLink = viewPort.find('itemId', externalNavigationForm.CATALOG_LINK_ITEM_ID)[0];
              externalIdField = viewPort.find('itemId', externalNavigationForm.EXTERNAL_ID_ITEM_ID)[0];
              externalUriPathField = viewPort.find('itemId', externalNavigationForm.EXTERNAL_URI_PATH_ITEM_ID)[0];
              var radioGroup:RadioGroup = viewPort.find('itemId', externalNavigationForm.RADIO_GROUP_ITEM_ID)[0];
              catalogPageRadio = radioGroup.items.items[0];
              otherPageRadio = radioGroup.items.items[1];
            }
    );
  }

  private function waitForCatalogLinkVisible():Step {
    return new Step("Wait for the catalog link to be visible",
            function ():Boolean {
              return catalogLink.isVisible();
            }
    );
  }

  private function waitForCatalogLinkHidden():Step {
    return new Step("Wait for the catalog link to be hidden",
            function ():Boolean {
              return catalogLink.hidden;
            }
    );
  }

  private function selectCatalogPage():Step {
    return new Step("Select Catalog Page",
            function ():Boolean {
              return true;
            },
            function ():void {
              catalogPageRadio.setValue(true);
            }
    );
  }
  private function selectOtherPage():Step {
    return new Step("Select Other Page",
            function ():Boolean {
              return true;
            },
            function ():void {
              otherPageRadio.setValue(true);
            }
    );
  }

  private function waitForExternalIdStringPropertiesHidden():Step {
    return new Step("Wait for External Id String Properties to be hidden",
            function ():Boolean {
              return externalIdField.hidden && externalUriPathField.hidden;
            }
    );
  }

  private function waitForExternalIdStringPropertiesVisible():Step {
    return new Step("Wait for External Id String Properties to be visible",
            function ():Boolean {
              return externalIdField.isVisible() && externalUriPathField.isVisible();
            }
    );
  }
}
}
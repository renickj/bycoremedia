package com.coremedia.livecontext.studio.forms {
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.components.product.ViewSettingsRadioGroup;
import com.coremedia.livecontext.studio.config.productTeaserSettingsFormTestView;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;

import ext.QuickTips;
import ext.Viewport;
import ext.form.Radio;

use namespace beanFactory;

public class ProductTeaserSettingsFormTest extends AbstractProductTeaserComponentsTest {
  private var viewPort:Viewport;
  private var viewSettings:ViewSettingsRadioGroup;

  override public function setUp():void {
    super.setUp();
    QuickTips.init(true);

    productTeaser.isLoaded = function ():Boolean {
      return true;
    };
    productTeaser.getReferrersWithNamedDescriptor = function (ct:String, property:String):Array {
      return []
    };

    var properties:Bean = beanFactory.createLocalBean();
    var localSettingsStruct:Bean = beanFactory.createLocalBean();
    properties.set('localSettings', localSettingsStruct);
    productTeaser.getProperties = function ():Object {
      return properties;
    };
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort && viewPort.destroy();
  }

  public function testProductTeaserSettingsForm():void {
    chain(
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            loadProductTeaser(),
            createTestlingStep(),

            waitForRadioToBeChecked(ViewSettingsRadioGroup.INHERITED_SETTING),
            checkRadio(ViewSettingsRadioGroup.ENABLED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.ENABLED_SETTING),
            checkRadio(ViewSettingsRadioGroup.DISABLED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.DISABLED_SETTING),
            checkRadio(ViewSettingsRadioGroup.INHERITED_SETTING),
            waitForRadioToBeChecked(ViewSettingsRadioGroup.INHERITED_SETTING)
    )
  }


  private function checkRadio(value:String):Step {
    return new Step("Wait for radio to be checked: " + value,
            function ():Boolean {
              viewSettings.setValue(value);
              viewSettings.fireEvent('change');
              return true;
            }
    );
  }

  private function waitForRadioToBeChecked(itemId:String):Step {
    return new Step("Wait for inherit radio button to be checked",
            function ():Boolean {
              return (viewSettings.getValue() as Radio).getItemId() === itemId;
            }
    );
  }

  private function createTestlingStep():Step {
    return new Step("Create the testling",
            function ():Boolean {
              return true;
            },
            createTestling
    );
  }

  private function createTestling():void {
    var config:productTeaserSettingsFormTestView = new productTeaserSettingsFormTestView({});
    config.bindTo = super.getBindTo();
    viewPort = new ProductTeaserSettingsFormTestView(new productTeaserSettingsFormTestView(config));
    viewSettings = viewPort.find('itemId', 'viewSettingsPropertyField')[0] as ViewSettingsRadioGroup;
  }
}
}
package com.coremedia.livecontext.studio.components.product {
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.config.productDetailsDocumentForm;
import com.coremedia.livecontext.studio.config.productNameTextFieldTestView;
import com.coremedia.ui.data.test.Step;

import ext.Viewport;
import ext.form.TextField;

public class ProductNameTextFieldTest extends AbstractProductTeaserComponentsTest{
  private var viewPort:Viewport;
  private var productNameField:TextField;

  override public function setUp():void {
    super.setUp();
    var conf:productDetailsDocumentForm = new productDetailsDocumentForm({});
    conf.bindTo = getBindTo();

    createTestling(conf);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort.destroy();
  }

  public function testProductNameField():void {
    chain(
            waitForProductTeaserToBeLoaded(),
            waitForTextFieldToBeEmpty(),
            setLink(ORANGES_ID),
            waitForTextFieldValue(ORANGES_NAME),
            setLink(ORANGES_ID + '503'),
            waitForTextFieldToBeEmpty(),
            setLink(ORANGES_SKU_ID),
            waitForTextFieldValue(ORANGES_SKU_NAME),
            setLink(null),
            waitForTextFieldToBeEmpty()
    )
  }

  private function waitForTextFieldToBeEmpty():Step {
    return new Step("Wait for the product name field to be empty",
            function ():Boolean {
              return !productNameField.getValue();
            }
    );
  }

  private function waitForTextFieldValue(value:String):Step {
    return new Step("Wait for the product name field to be " + value,
            function ():Boolean {
              return productNameField.getValue() === value;
            }
    );
  }

  private function createTestling(config:productDetailsDocumentForm):void {
    viewPort = new ProductNameTextFieldTestView(new productNameTextFieldTestView(config));
    productNameField = viewPort.get(0) as TextField;
  }
}
}
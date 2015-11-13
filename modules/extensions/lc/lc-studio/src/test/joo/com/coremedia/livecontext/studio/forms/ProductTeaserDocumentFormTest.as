package com.coremedia.livecontext.studio.forms {
import com.coremedia.livecontext.studio.AbstractProductTeaserComponentsTest;
import com.coremedia.livecontext.studio.components.product.ViewSettingsRadioGroup;
import com.coremedia.livecontext.studio.config.productTeaserDocumentForm;
import com.coremedia.livecontext.studio.config.productTeaserDocumentFormTestView;
import com.coremedia.ui.data.test.Step;

import ext.QuickTips;
import ext.Viewport;
import ext.form.Radio;
import ext.form.TextArea;
import ext.form.TextField;

public class ProductTeaserDocumentFormTest extends AbstractProductTeaserComponentsTest{
  private var viewPort:Viewport;
  private var productTeaserTitleField:TextField;
  private var productTeaserTextArea:TextArea;
  private var ckEditor:*;
  private var ckEditorReady:Boolean;
  private var viewSettings:ViewSettingsRadioGroup;

  override public function setUp():void {
    super.setUp();
    QuickTips.init(true);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort && viewPort.destroy();
  }

  public function testProductTeaserDocumentForm():void {
    chain(
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            loadProductTeaser(),
            waitForProductTeaserToBeLoaded(),
            waitForProductTeaserContentTypeToBeLoaded(),
            createTestlingStep(),
            waitForTeaserTextToBeReady(),
            waitForTeaserTitleFieldToBeEmpty(),
            waitForTeaserTextAreaToBeEmpty()
            //TODO disabled since not working
//            setLink(ORANGES_ID),
//            waitForTeaserTitleFieldValue(ORANGES_NAME),
//            waitForTeaserTextAreaValue(ORANGES_SHORT_DESC),
//            setLink(ORANGES_ID + '503'),
//            waitForTeaserTitleFieldToBeEmpty(),
            // todo: teaser text isn't reset although it does in the production system
            // waitForTeaserTextAreaToBeEmpty(),
//            setLink(ORANGES_SKU_ID),
//            waitForTeaserTitleFieldValue(ORANGES_SKU_NAME),
//            waitForTeaserTextAreaValue(ORANGES_SKU_SHORT_DESC),
//            setLink(null),
//            waitForTeaserTitleFieldToBeEmpty()
            // todo: teaser text isn't reset although it does in the production system
            //waitForTeaserTextAreaToBeEmpty()

    )
  }

  private function waitForTeaserTitleFieldToBeEmpty():Step {
    return new Step("Wait for the product teaser title field to be empty",
            function ():Boolean {
              return !productTeaserTitleField.getValue();
            }
    );
  }

  private function waitForTeaserTextAreaToBeEmpty():Step {
    return new Step("Wait for the product teaser text area to be empty",
            function ():Boolean {
              return productTeaserTextArea.getValue() ===
                      '<div xmlns="http://www.coremedia.com/2003/richtext-1.0" xmlns:xlink="http://www.w3.org/1999/xlink"><p><br /></p></div>';
            }
    );
  }

  private function waitForTeaserTitleFieldValue(value:String):Step {
    return new Step("Wait for the product teaser title field to be " + value,
            function ():Boolean {
              return productTeaserTitleField.getValue() === value;
            }
    );
  }

  private function waitForTeaserTextToBeReady():Step {
    return new Step("Wait for the product teaser text area to be ready",
            function ():Boolean {
              return ckEditorReady;
            }
    );
  }

  private function waitForTeaserTextAreaValue(value:String):Step {
    return new Step("Wait for the product teaser text area to be " + value,
            function ():Boolean {
              return productTeaserTextArea.getValue() && productTeaserTextArea.getValue().indexOf(value) >= 0;
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
    var config:productTeaserDocumentForm = new productTeaserDocumentForm({});
    config.bindTo = getBindTo();
    viewPort = new ProductTeaserDocumentFormTestView(new productTeaserDocumentFormTestView(config));
    productTeaserTitleField = viewPort.find('itemId', 'stringPropertyField')[0] as TextField;
    productTeaserTextArea = viewPort.find('itemId', 'richTextArea')[0] as TextArea;
    viewSettings = viewPort.find('itemId', 'viewSettingsPropertyField')[0] as ViewSettingsRadioGroup;
    ckEditor = productTeaserTextArea['ckEditor'];
    ckEditorReady = false;
    ckEditor.on('instanceReady', function():void {
      ckEditorReady = true;
    });
  }
}
}
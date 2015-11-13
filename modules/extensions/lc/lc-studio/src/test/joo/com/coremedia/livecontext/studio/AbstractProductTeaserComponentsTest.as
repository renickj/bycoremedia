package com.coremedia.livecontext.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.impl.ContentTypeImpl;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;

/**
 * Basis test class. Extend this class to test components related to product teaser
 */
public class AbstractProductTeaserComponentsTest extends AbstractCatalogStudioTest {
  protected var productTeaser:Content;
  private var bindTo:ValueExpression;
  private var propertyExpression:ValueExpression;
  private var createReadOnlyValueExpression:Function;
  private var forceReadOnlyValueExpression:ValueExpression;

  override public function setUp():void {
    super.setUp();
    productTeaser = beanFactory.getRemoteBean('content/100') as Content;
    //we need to mock the write access
    productTeaser.getRepository().getAccessControl().mayWrite = function():Boolean {return true;};

    bindTo = ValueExpressionFactory.createFromValue(productTeaser);
    propertyExpression = bindTo.extendBy('properties', 'externalId');
    forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);

    //Mock PropertyEditorUtil#createReadOnlyValueExpression
    createReadOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression;
    PropertyEditorUtil.createReadOnlyValueExpression = function (contentValueExpression:ValueExpression, forceReadOnlyValueExpression:ValueExpression = undefined):ValueExpression {
      return ValueExpressionFactory.createFromFunction(function ():Boolean {
        if (forceReadOnlyValueExpression && forceReadOnlyValueExpression.getValue()) {
          return true;
        }
        if (!contentValueExpression) {
          return false;
        }
        var mayWrite:* = true;
        return mayWrite === undefined ? undefined : !mayWrite;
      });

    };
  }


  override public function tearDown():void {
    super.tearDown();
    PropertyEditorUtil.createReadOnlyValueExpression = createReadOnlyValueExpression;
  }

  protected function loadProductTeaser():Step {
    return new Step("Load the product teaser",
            function ():Boolean {
              return true;
            },
            function ():void {
              productTeaser.load();
            }
    );
  }

  protected function waitForProductTeaserToBeLoaded():Step {
    return new Step("Wait for the product teaser to be loaded",
            function ():Boolean {
              return productTeaser.isLoaded();
            }
    );
  }

  protected function waitForProductTeaserContentTypeToBeLoaded():Step {
    return new Step("Wait for the product teaser content type to be loaded",
            function ():Boolean {
              return ContentTypeImpl(productTeaser.getType()).isLoaded();
            }
    );
  }

  protected function setLink(value:String):Step {
    return new Step("set product link to " + value,
            function ():Boolean {
              return true;
            },
            function ():void {
              propertyExpression.setValue(value);
            }
    );
  }

  protected function setForceReadOnly(value:Boolean):Step {
    return new Step("set forceReadOnlyValueExpression " + value,
            function ():Boolean {
              return true;
            },
            function ():void {
              forceReadOnlyValueExpression.setValue(value);
            }
    );
  }



  protected function getBindTo():ValueExpression {
    return bindTo;
  }

  protected function getForceReadOnlyValueExpression():ValueExpression {
    return forceReadOnlyValueExpression;
  }
}
}
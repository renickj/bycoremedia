package com.coremedia.ecommerce.studio.forms {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.AbstractCatalogStudioTest;
import com.coremedia.ecommerce.studio.components.CommerceObjectField;
import com.coremedia.ecommerce.studio.components.CommerceObjectSelector;
import com.coremedia.ecommerce.studio.config.commerceCatalogObjectsSelectForm;
import com.coremedia.ecommerce.studio.config.commerceCatalogObjectsSelectFormTestView;
import com.coremedia.ecommerce.studio.config.commerceObjectField;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;

import ext.Button;

import ext.Viewport;
import ext.data.Store;
import ext.form.Label;
import ext.util.StringUtil;

public class CommerceCatalogObjectsSelectFormTest extends AbstractCatalogStudioTest {

  private var persona:Content;
  private var bindTo:ValueExpression;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var catalogObjectIdsExpression:ValueExpression;
  private var createReadOnlyValueExpression:Function;

  private var viewport:Viewport;
  private var selector:CommerceObjectSelector;

  override public function setUp():void {
    super.setUp();

    persona = beanFactory.getRemoteBean('content/300') as Content;
    //we need to mock the write access
    persona.getRepository().getAccessControl().mayWrite = function():Boolean {return true;};
  }

  private function createTestling(catalogObjectIds:Array):void {
    bindTo = ValueExpressionFactory.createFromValue(persona);
    forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);
    catalogObjectIdsExpression = ValueExpressionFactory.createFromValue(catalogObjectIds);

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

    var conf:commerceCatalogObjectsSelectFormTestView = new commerceCatalogObjectsSelectFormTestView({});
    conf.bindTo = bindTo;
    conf.forceReadOnlyValueExpression = forceReadOnlyValueExpression;
    conf.catalogObjectIdsExpression = catalogObjectIdsExpression;

    viewport = new CommerceCatalogObjectsSelectFormTestView(conf);
    selector = viewport.find("itemId", commerceCatalogObjectsSelectForm.SELECTOR_ITEM_ID)[0];
  }

  override public function tearDown():void {
    super.tearDown();
    viewport.destroy();
    PropertyEditorUtil.createReadOnlyValueExpression = createReadOnlyValueExpression;
  }

  public function testSelectAndRemove():void {
    createTestling([]);
    chain(
            waitForPersonaToBeLoaded(),
            waitForTheSelectorLoadTheContracts(),
            selectTheExteriorContract(),
            waitForTheSelectorHasOnlyInteriorContract(),
            waitForOnlyOneContractFieldLoaded(),
            waitForTheExteriorContractFieldDisplayed(),
            selectTheInteriorContract(),
            waitForTheSelectorHasNoContract(),
            waitForTheContractFieldsLoaded(),
            waitForTheContractFieldsDisplayed(),
            removeTheExteriorContract(),
            waitForOnlyOneContractFieldLoaded(),
            waitForTheInteriorContractFieldDisplayed(),
            waitForTheSelectorHasOnlyExteriorContract()
    );
  }

  public function testInvalidCatalogObjectId():void {
    createTestling(["ibm:///catalog/contract/invalidid"]);
    chain(
            waitForOnlyOneContractFieldLoaded(),
            waitForContractFieldDisplaysInvalidMessage()
    );
  }

  private function waitForPersonaToBeLoaded():Step {
    return new Step("Wait for the persona to be loaded",
            function ():Boolean {
              return persona.isLoaded();
            }
    );
  }

  private function waitForTheSelectorLoadTheContracts():Step {
    return new Step("Wait for the selector to load the contracts",
            function ():Boolean {
              var store:Store = selector.getStore();
              return store.getCount() === 2 &&
              store.getAt(0).data.name === 'Contract for CoreMedia Preview Exterior' &&
              store.getAt(1).data.name === 'Contract for CoreMedia Preview Interior';
            }
    );
  }

  private function selectTheExteriorContract():Step {
    return new Step("Select the exterior contract",
            function ():Boolean {return true},
            function ():void {
              var exteriorContractID:String = "ibm:///catalog/contract/4000000000000000507";
              selector.setValue(exteriorContractID);
              selector.fireEvent('change', selector, undefined, exteriorContractID);
            }
    );
  }

  private function selectTheInteriorContract():Step {
    return new Step("Select the interior contract",
            function ():Boolean {return true},
            function ():void {
              var interiorContractID:String = "ibm:///catalog/contract/4000000000000000508";
              selector.setValue(interiorContractID);
              selector.fireEvent('change', selector, undefined, interiorContractID);
            }
    );
  }

  private function waitForTheSelectorHasOnlyInteriorContract():Step {
    return new Step("Wait for the selector to have only the interior contracts",
            function ():Boolean {
              var store:Store = selector.getStore();
              return store.getCount() === 1 &&
                      store.getAt(0).data.name === 'Contract for CoreMedia Preview Interior';
            }
    );
  }

  private function waitForTheSelectorHasOnlyExteriorContract():Step {
    return new Step("Wait for the selector to have only the exterior contracts",
            function ():Boolean {
              var store:Store = selector.getStore();
              return store.getCount() === 1 &&
                      store.getAt(0).data.name === 'Contract for CoreMedia Preview Exterior';
            }
    );
  }

  private function waitForTheSelectorHasNoContract():Step {
    return new Step("Wait for the selector to have no contracts",
            function ():Boolean {
              var store:Store = selector.getStore();
              return store.getCount() === 0;
            }
    );
  }

  private function waitForOnlyOneContractFieldLoaded():Step {
    return new Step("Wait for only one contract field to be loaded",
            function ():Boolean {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              return commerceObjectFields && commerceObjectFields.length === 1;
            }
    );
  }

  private function waitForTheContractFieldsLoaded():Step {
    return new Step("Wait for the contract fields to be loaded",
            function ():Boolean {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              return commerceObjectFields && commerceObjectFields.length === 2;
            }
    );
  }

  private function waitForTheExteriorContractFieldDisplayed():Step {
    return new Step("Wait for the exterior contract field to be displayed",
            function ():Boolean {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              var commerceObjectLabel:Label = CommerceObjectField(commerceObjectFields[0])
                      .getComponent(commerceObjectField.LABEL_ITEM_ID) as Label;
              return commerceObjectLabel.text === 'Contract for CoreMedia Preview Exterior';
            }
    );
  }

  private function waitForTheInteriorContractFieldDisplayed():Step {
    return new Step("Wait for the interior contract field to be displayed",
            function ():Boolean {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              var commerceObjectLabel:Label = CommerceObjectField(commerceObjectFields[0])
                      .getComponent(commerceObjectField.LABEL_ITEM_ID) as Label;
              return commerceObjectLabel.text === 'Contract for CoreMedia Preview Interior';
            }
    );
  }

  private function waitForContractFieldDisplaysInvalidMessage():Step {
    return new Step("Wait for the contract field to show invalid message.",
            function ():Boolean {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              var commerceObjectLabel:Label = CommerceObjectField(commerceObjectFields[0])
                      .getComponent(commerceObjectField.LABEL_ITEM_ID) as Label;
              return commerceObjectLabel.text === StringUtil.format('Invalid e-Commerce user contract ID: {0}', "invalidid");
            }
    );
  }

  private function waitForTheContractFieldsDisplayed():Step {
    return new Step("Wait for the contract fields to be displayed",
            function ():Boolean {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              var commerceObjectLabel1:Label = CommerceObjectField(commerceObjectFields[0])
                      .getComponent(commerceObjectField.LABEL_ITEM_ID) as Label;
              var commerceObjectLabel2:Label = CommerceObjectField(commerceObjectFields[1])
                      .getComponent(commerceObjectField.LABEL_ITEM_ID) as Label;
              return commerceObjectLabel1.text === 'Contract for CoreMedia Preview Exterior'
                      && commerceObjectLabel2.text === 'Contract for CoreMedia Preview Interior';
            }
    );
  }

  private function removeTheExteriorContract():Step {
    return new Step("Remove the exterior contract",
            function ():Boolean {return true},
            function ():void {
              var commerceObjectFields:Array = viewport.findByType(commerceObjectField.xtype);
              var commerceObjectRemoveButton1:Button = CommerceObjectField(commerceObjectFields[0])
                              .getComponent(commerceObjectField.REMOVE_BUTTON_ITEM_ID) as Button;
              commerceObjectRemoveButton1.baseAction.execute() // simulate click
            }
    );
  }

}
}
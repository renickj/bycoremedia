package com.coremedia.livecontext.studio.components.link {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkContextMenu;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkField;
import com.coremedia.ecommerce.studio.components.link.ProductLinksPropertyField;
import com.coremedia.ecommerce.studio.components.link.ProductLinksPropertyFieldBase;
import com.coremedia.ecommerce.studio.config.catalogLinkContextMenu;
import com.coremedia.ecommerce.studio.config.catalogLinkPropertyField;
import com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin;
import com.coremedia.ecommerce.studio.config.productLinksPropertyField;
import com.coremedia.ecommerce.studio.dragdrop.CatalogLinkDropTarget;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.livecontext.studio.AbstractCatalogStudioTest;
import com.coremedia.livecontext.studio.config.livecontextStudioPlugin;
import com.coremedia.livecontext.studio.config.productLinksPropertyFieldTestView;
import com.coremedia.livecontext.studio.mgmtcenter.ManagementCenterUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Ext;
import ext.Viewport;
import ext.dd.DragSource;
import ext.grid.RowSelectionModel;
import ext.menu.Item;

import js.HTMLElement;

public class ProductLinksPropertyFieldTest extends AbstractCatalogStudioTest {
  private var picture:Content;
  private var link:CatalogLinkField;
  private var openButton:Button;
  private var removeButton:Button;
  private var openMenuItem:Item;
  private var removeMenuItem:Item;
  private var viewPort:Viewport;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var propertyExpression:ValueExpression;
  private var createReadOnlyValueExpression:Function;


  override public function setUp():void {
    super.setUp();
    picture = beanFactory.getRemoteBean('content/200') as Content;
    //we need to mock the write access
    picture.getRepository().getAccessControl().mayWrite = function ():Boolean {
      return true;
    };
    var bindTo:ValueExpression = ValueExpressionFactory.createFromValue(picture);
    propertyExpression = bindTo.extendBy('properties', ProductLinksPropertyFieldBase.PROPERTY_NAME);

    var conf:productLinksPropertyField = new productLinksPropertyField({});
    conf.bindTo = bindTo;

    forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);
    conf.forceReadOnlyValueExpression = forceReadOnlyValueExpression;

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


    createTestling(conf);
  }

  override public function tearDown():void {
    super.tearDown();
    viewPort.destroy();
    PropertyEditorUtil.createReadOnlyValueExpression = createReadOnlyValueExpression;
  }

  public function testCatalogLink():void {
    chain(
      waitForPictureToBeLoaded(),
//      setProducts([ORANGES_EXTERNAL_ID, BABY_SHOES_EXTERNAL_ID]),
      checkProductLinkDisplaysValue(ORANGES_NAME, 0),
      checkSkuLinkDisplaysValue(ORANGES_SKU_NAME, 1),
      //still nothing selected
      checkRemoveButtonDisabled(),
      checkOpenButtonDisabled(),
      openContextMenu(), //this selects the link
      checkContextMenuOpened(),
      checkRemoveButtonEnabled(),
      checkOpenButtonEnabled(),
      checkRemoveContextMenuEnabled(),
      checkOpenContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu() //this selects the link

/* TODO: the struct change cannot be simulated...
      checkRemoveButtonDisabled(),
      //valid selected link can be always opened
      checkOpenButtonEnabled(),
      checkRemoveContextMenuDisabled(),
      checkOpenContextMenuEnabled(),
      setProducts([]),
      checkCatalogLinkIsEmpty(),
      checkRemoveButtonDisabled(),
      checkOpenButtonDisabled(),
      openContextMenu(),
      //no link --> no context menu
      checkContextMenuNotOpened(),


      setProducts([ORANGES_EXTERNAL_ID + '503']),
      checkCatalogLinkDisplaysErrorValue(ORANGES_EXTERNAL_ID + '503'),
      setProducts([ORANGES_EXTERNAL_ID + '404']),
      checkCatalogLinkDisplaysErrorValue(ORANGES_EXTERNAL_ID + '404'),
      openContextMenu(), //this selects the link
      //still forceReadOnly = true
      checkRemoveButtonDisabled(),
      //invalid link --> cannot open
      checkOpenButtonDisabled(),
      checkRemoveContextMenuDisabled(),
      //invalid link --> cannot open
      checkOpenContextMenuDisabled(),
      setForceReadOnly(false),
      openContextMenu(), //this selects the link
      checkRemoveButtonEnabled(),
      //invalid link --> cannot open
      checkOpenButtonDisabled(),
      checkRemoveContextMenuEnabled(),
      //invalid link --> cannot open
      checkOpenContextMenuDisabled(),
      setProducts([ORANGES_SKU_ID]),
      checkSkuLinkDisplaysValue(ORANGES_SKU_NAME, 0),
      openContextMenu(), //this selects the link
      checkContextMenuOpened(),
      checkRemoveButtonEnabled(),
      checkOpenButtonEnabled(),
      checkRemoveContextMenuEnabled(),
      checkOpenContextMenuEnabled(),
      setForceReadOnly(true),
      openContextMenu(), //this selects the link
      checkRemoveButtonDisabled(),
      //valid selected link can be always opened
      checkOpenButtonEnabled(),
      checkRemoveContextMenuDisabled(),
      checkOpenContextMenuEnabled()
*/
    );
  }

  private function openContextMenu():Step {
    return new Step("open Context Menu",
      function ():Boolean {
        return true;
      },
      function ():void {
        var empty:Boolean = link.getView().getRow(0) === undefined;
        var event:Object = {
          type: "contextmenu",

          getXY: function():Array {
            return empty ? link.getView().mainBody.getXY() : Ext.fly(link.getView().getCell(0, 1)).getXY();
          },
          preventDefault : function():void{
            //do nothing
          },
          getTarget: function():HTMLElement {
            return link.getView().getCell(0, 1);
          }
        };
        if (empty) {
          link.fireEvent("contextmenu", event);
        } else {
          link.fireEvent("rowcontextmenu", link, 0, event);
        }
      }
    );
  }

  private function checkProductLinkDisplaysValue(value:String, row:Number):Step {
    return new Step("check if product is linked and data is displayed",
      function ():Boolean {
        return ORANGES_EXTERNAL_ID === link.getView().getCell(row, 2)['textContent'] &&
          value === link.getView().getCell(row, 3)['textContent'];
      }
    );
  }

  private function checkSkuLinkDisplaysValue(value:String, row:Number):Step {
    return new Step("check if sku is linked and data is displayed",
      function ():Boolean {
        return ORANGES_SKU_EXTERNAL_ID === link.getView().getCell(row, 2)['textContent'] &&
          value === link.getView().getCell(row, 3)['textContent'];
      }
    );
  }

  private function checkCatalogLinkDisplaysErrorValue(value:String):Step {
    return new Step("check if broken product is linked and fallback data '" + value + "' is displayed",
      function ():Boolean {
        return link.getStore().getCount() === 1 &&
          value === link.getView().getCell(0, 2)['textContent'];
      }
    );
  }

  private function checkCatalogLinkIsEmpty():Step {
    return new Step("check if is catalog link is empty and set product link",
      function ():Boolean {
        return link && link.getStore() && link.getStore().getCount() === 0
      }
    );
  }

  private function checkRemoveButtonDisabled():Step {
    return new Step("check remove button disabled",
      function ():Boolean {
        return removeButton.disabled;
      }
    );
  }

  private function checkRemoveButtonEnabled():Step {
    return new Step("check remove button enabled",
      function ():Boolean {
        return !removeButton.disabled;
      }
    );
  }

  private function checkRemoveContextMenuDisabled():Step {
    return new Step("check remove context menu disabled",
      function ():Boolean {
        return removeMenuItem.disabled;
      }
    );
  }

  private function checkRemoveContextMenuEnabled():Step {
    return new Step("check remove context menu enabled",
      function ():Boolean {
        return !removeMenuItem.disabled;
      }
    );
  }

  private function checkOpenButtonDisabled():Step {
    return new Step("check open button disabled",
      function ():Boolean {
        return openButton.disabled;
      }
    );
  }

  private function checkOpenButtonEnabled():Step {
    return new Step("check open button enabled",
      function ():Boolean {
        return !ManagementCenterUtil.isSupportedBrowser() || !openButton.disabled;
      }
    );
  }

  private function checkOpenContextMenuDisabled():Step {
    return new Step("check open context menu disabled",
      function ():Boolean {
        return !ManagementCenterUtil.isSupportedBrowser() || openMenuItem.disabled;
      }
    );
  }

  private function checkOpenContextMenuEnabled():Step {
    return new Step("check open context menu enabled",
    function ():Boolean {
      return !ManagementCenterUtil.isSupportedBrowser() || !openMenuItem.disabled;
    }
    );
  }

  private function checkContextMenuOpened():Step {
    return new Step("check context menu opened",
      function ():Boolean {
        return findCatalogLinkContextMenu();
      }
    );
  }

  private function checkContextMenuNotOpened():Step {
    return new Step("check context menu is not opened",
      function ():Boolean {
        return !findCatalogLinkContextMenu();
      }
    );
  }

  private function waitForPictureToBeLoaded():Step {
    return new Step("Wait for the picture to be loaded",
            function ():Boolean {
              return picture.isLoaded();
            }
    );
  }

  private function selectLink():Step {
    return new Step("select the product link",
      function ():Boolean {
        return true;
      },
      function ():void {
        RowSelectionModel(link.getSelectionModel()).selectFirstRow();
      }
    );
  }

  private function setProducts(newProducts:Array):Step {
    return new Step("set product link to " + newProducts,
            function ():Boolean {
              return true;
            },
            function ():void {
//              propertyExpression.setValue(newProducts);
              var dropTarget:CatalogLinkDropTarget = link['CatalogLinkDropTarget'];
              var source:DragSource = new DragSource(link.el, {});
              source['groups'] = {'CatalogDD': true};
              var productBeans:Array = newProducts.map(function(externalId:String):Product {
                return beanFactory.getRemoteBean("livecontext/product/HeliosSiteId/NO_WS/" + externalId) as Product;
              });

              var data:Object = {contents: productBeans};
              dropTarget.notifyDrop(source, null, data);
            }
    );
  }

  private function setForceReadOnly(value:Boolean):Step {
    return new Step("set forceReadOnlyValueExpression " + value,
            function ():Boolean {
              return true;
            },
            function ():void {
              forceReadOnlyValueExpression.setValue(value);
            }
    );
  }

  /**
   * private helper method to create the container for tests
   */
  private function createTestling(config:productLinksPropertyField):void {
    viewPort = new ProductLinksPropertyFieldTestView(new productLinksPropertyFieldTestView(config));
    var testling:ProductLinksPropertyField =
            viewPort.get(productLinksPropertyFieldTestView.TESTLING_ITEM_ID) as ProductLinksPropertyField;
    link = testling.find('itemId', catalogLinkPropertyField.CATALOG_LINK_FIELD_ITEM_ID)[0] as CatalogLinkField;
    openButton = link.getTopToolbar().find('itemId', livecontextStudioPlugin.OPEN_IN_MCENTER_BUTTON_ITEM_ID)[0];
    removeButton = link.getTopToolbar().find('itemId', eCommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID)[0];
  }

  private function findCatalogLinkContextMenu():CatalogLinkContextMenu {
    var contextMenu:CatalogLinkContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
      return !component.ownerCt && !component.hidden && component.isXType(catalogLinkContextMenu.xtype);
    }) as CatalogLinkContextMenu;
    if (contextMenu) {
      openMenuItem = contextMenu.getComponent(livecontextStudioPlugin.OPEN_IN_MCENTER_MENU_ITEM_ID) as Item;
      removeMenuItem = contextMenu.getComponent(eCommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }


}
}
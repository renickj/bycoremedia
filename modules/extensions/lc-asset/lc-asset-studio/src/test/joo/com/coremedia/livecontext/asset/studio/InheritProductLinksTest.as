package com.coremedia.livecontext.asset.studio {
import com.coremedia.cap.common.impl.StructRemoteBeanImpl;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.components.link.CatalogLink;
import com.coremedia.ecommerce.studio.components.link.CatalogLinkContextMenu;
import com.coremedia.ecommerce.studio.config.catalogLinkContextMenu;
import com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin;
import com.coremedia.livecontext.asset.studio.action.InheritProductListAction;
import com.coremedia.livecontext.asset.studio.components.InheritProductListButton;
import com.coremedia.livecontext.asset.studio.config.inheritProductLinksTestView;
import com.coremedia.livecontext.asset.studio.config.inheritProductListAction;
import com.coremedia.livecontext.asset.studio.config.inheritProductListButton;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Ext;
import ext.QuickTip;
import ext.QuickTips;
import ext.menu.Item;

import js.HTMLElement;

public class InheritProductLinksTest extends AbstractCatalogAssetTest {
  private var bindTo:ValueExpression;
  private var picture:Content;
  private var localSettings:StructRemoteBeanImpl;
  private var forceReadOnlyValueExpression:ValueExpression;
  private var contentReadOnlyExpression:ValueExpression;
  private var register:Function;
  private var getQuickTip:Function;

  private var viewport:InheritProductLinksTestView;
  private var inheritButton:InheritProductListButton;
  private var catalogLink:CatalogLink;
  private var removeMenuItem:Item;
  private var removeButton:Button;

  private var inheritExpression:ValueExpression;
  private var productListExpression:ValueExpression;
  private var originProductListExpression:ValueExpression;
  private var inheritAction:InheritProductListAction;

  override public function setUp():void {
    super.setUp();

    bindTo = ValueExpressionFactory.createFromValue();
    forceReadOnlyValueExpression = ValueExpressionFactory.createFromValue(false);
    contentReadOnlyExpression = ValueExpressionFactory.createFromValue(false);

    //TODO: MFA: this fixes the test problem that the "makeContentReadOnly()" call has no effect
    contentReadOnlyExpression.addChangeListener(function(ve:ValueExpression):void {
      forceReadOnlyValueExpression.setValue(ve.getValue());
    });

    //Obviously the inherit toogle button in the test setup has a problem with QuickTips...
    register = QuickTips.register;
    QuickTips.register = function(){};

    //We have to mock QuickTips.getQuickTip as this returns undefined
    getQuickTip = QuickTips.getQuickTip;
    QuickTips.getQuickTip = function():QuickTip {
      return new QuickTip({});
    };
  }

  private function setBindTo(path:String):void {
    picture = beanFactory.getRemoteBean(path) as Content;
    //we need to mock the write access
    picture.getRepository().getAccessControl().mayWrite = function ():Boolean {
      return !contentReadOnlyExpression.getValue();
    };
    localSettings = beanFactory.getRemoteBean(path + '/structs/localSettings') as StructRemoteBeanImpl;
    //PUT should cause no trouble
    localSettings["doWriteChanges"] = function (changedProperties) {
      //ignore
    };

    bindTo.setValue(picture);

  }

  override public function tearDown():void {
    super.tearDown();
    viewport && viewport.destroy();
    register && (QuickTips.register = register);
    getQuickTip && (QuickTips.getQuickTip = getQuickTip);
  }

  public function testDisableStateWhenNoInherit():void {
    chain(
            //open the grid with the content inherit=false
            createTestling('content/200'),

            waitForInheritButtonVisible(),
            waitForInheritButtonUnpressed(),
            waitForGridWritable(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuEnabled(),
            waitRemoveButtonEnabled(),

            forceReadOnly(),

            waitForInheritButtonDisabled(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled(),

            forceWritable(),

            waitForInheritButtonEnabled(),
            waitForGridWritable(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuEnabled(),
            waitRemoveButtonEnabled(),

            makeContentReadOnly(),

            waitForInheritButtonDisabled(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled(),

            makeContentWritable(),

            waitForInheritButtonEnabled(),
            waitForGridWritable(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuEnabled(),
            waitRemoveButtonEnabled()
    );
  }

  public function testDisableStateWhenInherit():void {
    chain(
            //open the grid with the content inherit=true
            createTestling('content/202'),

            waitForInheritButtonVisible(),
            waitForInheritButtonPressed(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled(),

            forceReadOnly(),

            waitForInheritButtonDisabled(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled(),

            forceWritable(),

            waitForInheritButtonEnabled(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled(),

            makeContentReadOnly(),

            waitForInheritButtonDisabled(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled(),

            makeContentWritable(),

            waitForInheritButtonEnabled(),
            waitForGridReadonly(),
            openContextMenu(),
            waitContextMenuOpened(),
            waitRemoveContextMenuDisabled(),
            waitRemoveButtonDisabled()
    );
  }

  public function testInheritAction():void {
    inheritExpression = ValueExpressionFactory.createFromValue(true);
    var originalList:Array = ["A", "B"];
    productListExpression = ValueExpressionFactory.createFromValue(originalList);
    originProductListExpression = ValueExpressionFactory.createFromValue(originalList);
    inheritAction = new InheritProductListAction(
            inheritProductListAction({
              bindTo: ValueExpressionFactory.createFromValue(),
              inheritExpression: inheritExpression,
              productListExpression: productListExpression,
              originProductListExpression: originProductListExpression
            }));

    var changedList:Array = ["C", "D"];
    chain(
            toggleInheritAction(),
            waitInheritFalse(), //inherit is now false: we can edit the list manually
            changeProductList(changedList),
            toggleInheritAction(),
            waitInheritTrue(), //inherit is now true: the list must be original again
            waitProductListEqual(originalList),
            toggleInheritAction(),
            waitInheritFalse(), //inherit is now false the list must be the previously changed one
            waitProductListEqual(changedList)
    );
  }

  private function toggleInheritAction():Step {
    return new Step("Toggle inherit",
            function ():Boolean {
              return true;
            },
            function ():void {
              inheritAction.execute();
            });
  }

  private function changeProductList(list:Array):Step {
    return new Step("Change the product list to " + list,
            function ():Boolean {
              return true;

            },
            function ():void {
              productListExpression.setValue(list);
            });
  }

  private function waitInheritFalse(): Step{
    return new Step("Wait Inherit False",
            function ():Boolean {
              return !inheritExpression.getValue();
            }
    );
  }

  private function waitInheritTrue(): Step{
    return new Step("Wait Inherit True",
            function ():Boolean {
              return inheritExpression.getValue();
            }
    );
  }

  private function waitProductListEqual(list:Array): Step{
    return new Step("Wait the Product list to be equal to " + list,
            function ():Boolean {
              return productListExpression.getValue() === list;
            }
    );
  }


  private function waitForGridReadonly(): Step{
    return new Step("Wait for grid is read-only",
            function ():Boolean {
              return catalogLink && catalogLink.getEl().dom.getAttribute("class").indexOf("readonly") !== -1;
            }
    );
  }

  private function waitForGridWritable(): Step{
    return new Step("Wait for grid is writable",
            function ():Boolean {
              return catalogLink && catalogLink.getEl().dom.getAttribute("class").indexOf("readonly") === -1;
            }
    );
  }

  private function createTestling(path:String):Step {
    return new Step("Create the testling",
            function ():Boolean {
              return true;
            },
            function ():void {
              setBindTo(path);
              var conf:inheritProductLinksTestView = new inheritProductLinksTestView({});
              conf.bindTo = bindTo;
              conf.forceReadOnlyValueExpression = forceReadOnlyValueExpression;
              viewport = new InheritProductLinksTestView(conf);
              catalogLink = findCatalogLink();
            }
    );
  }

  private function waitForInheritButtonVisible():Step {
    return new Step("Wait for the inherit button to be visible",
            function ():Boolean {
              return findInheritButton();
            }
    );
  }

  private function waitForInheritButtonUnpressed():Step {
    return new Step("Wait for the inherit button to be unpressed",
            function ():Boolean {
              return !inheritButton.pressed;
            }
    );
  }

  private function waitForInheritButtonPressed():Step {
    return new Step("Wait for the inherit button to be pressed",
            function ():Boolean {
              return inheritButton.pressed;
            }
    );
  }


  private function forceReadOnly():Step {
    return new Step("Force to read only",
            function ():Boolean {
              return true;

            },
            function ():void {
              forceReadOnlyValueExpression.setValue(true);
            });
  }

  private function forceWritable():Step {
    return new Step("Force to writeable",
            function ():Boolean {
              return true;

            },
            function ():void {
              forceReadOnlyValueExpression.setValue(false);
            });

  }

  private function makeContentReadOnly():Step {
    return new Step("Make Content read only",
            function ():Boolean {
              return true;

            },
            function ():void {
              contentReadOnlyExpression.setValue(true);
            });

  }

  private function makeContentWritable():Step {
    return new Step("Make Content writeable",
            function ():Boolean {
              return true;

            },
            function ():void {
              contentReadOnlyExpression.setValue(false);
            });
  }

  private function waitForInheritButtonDisabled():Step {
    return new Step("Wait for the inherit button to be disabled",
            function ():Boolean {
              return inheritButton.disabled;
            }
    );

  }

  private function waitForInheritButtonEnabled():Step {
    return new Step("Wait for the inherit button to be enabled",
            function ():Boolean {
              return !inheritButton.disabled;
            }
    );

  }

  private function findInheritButton():Boolean {
    inheritButton = ComponentMgr.all.find(function (component:Component):Boolean {
      return component.isXType(inheritProductListButton.xtype);
    }) as InheritProductListButton;

    return inheritButton && inheritButton.isVisible();
  }

  private function findCatalogLink():CatalogLink {
    catalogLink = ComponentMgr.all.find(function (component:Component):Boolean {
      return component.isXType(catalogLink.xtype);
    }) as CatalogLink;
    removeButton = catalogLink.getTopToolbar().find('itemId', eCommerceStudioPlugin.REMOVE_LINK_BUTTON_ITEM_ID)[0];
    return catalogLink;
  }

  private function openContextMenu():Step {
    return new Step("open Context Menu",
            function ():Boolean {
              //wait for the list filled
              return catalogLink.getStore().data.length > 0;
            },
            function ():void {
              var event:Object = {
                getXY: function():Array {
                  return Ext.fly(catalogLink.getView().getCell(0, 1)).getXY();
                },
                preventDefault : function():void{
                  //do nothing
                },
                getTarget: function():HTMLElement {
                  return catalogLink.getView().getCell(0, 1);
                }
              };
              catalogLink.fireEvent("rowcontextmenu", catalogLink, 0, event);
            }
    );
  }

  private function waitRemoveButtonDisabled():Step {
    return new Step("Wait remove button disabled",
            function ():Boolean {
              return removeButton.disabled;
            }
    );
  }

  private function waitRemoveButtonEnabled():Step {
    return new Step("Wait remove button enabled",
            function ():Boolean {
              return !removeButton.disabled;
            }
    );
  }

  private function waitRemoveContextMenuDisabled():Step {
    return new Step("Wait remove context menu disabled",
            function ():Boolean {
              return removeMenuItem.disabled;
            }
    );
  }

  private function waitRemoveContextMenuEnabled():Step {
    return new Step("Wait remove context menu enabled",
            function ():Boolean {
              return !removeMenuItem.disabled;
            }
    );
  }

  private function waitContextMenuOpened():Step {
    return new Step("Wait context menu opened",
            function ():Boolean {
              return findContextMenu();
            }
    );
  }

  private function waitContextMenuNotOpened():Step {
    return new Step("Wait context menu is not opened",
            function ():Boolean {
              return !findContextMenu();
            }
    );
  }

  private function findContextMenu():CatalogLinkContextMenu {
    var contextMenu:CatalogLinkContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
              return !component.ownerCt && !component.hidden && component.isXType(catalogLinkContextMenu.xtype);
            }) as CatalogLinkContextMenu;
    if (contextMenu) {
      removeMenuItem = contextMenu.getComponent(eCommerceStudioPlugin.REMOVE_LINK_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }

}
}
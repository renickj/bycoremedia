package com.coremedia.blueprint.studio.analytics {

import com.coremedia.blueprint.studio.config.analytics.openAnalyticsDeepLinkUrlButton;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;

import ext.Button;
import ext.Container;
import ext.Ext;
import ext.config.button;
import ext.config.container;
import ext.config.menu;
import ext.config.menuitem;
import ext.menu.Item;
import ext.menu.Menu;

internal class AnalyticsDeepLinkButtonContainerBase extends Container {
  public function AnalyticsDeepLinkButtonContainerBase(config:container) {
    super(config);
    addListener('beforerender', onBeforeRender);
  }

  private function onBeforeRender():void {
    if (items && items.length > 1) {
      renderButtonMenu();
    }
  }

  private function renderButtonMenu():void {
    const originalItems:Array = this.removeAll();
    const menuItemsFromButtons:Array = [];

    // iterate through all buttons and create new menuItems out of them
    originalItems.forEach(function (item:OpenAnalyticsDeepLinkUrlButton):void {
      var config:Object = item.initialConfig;
      delete config.iconCls;
      var menuItem:Item = createMenuItem(item);
      menuItem.addListener('enable', updateAnalyticsReportButton);
      menuItem.addListener('disable', updateAnalyticsReportButton);

      menuItemsFromButtons.push(menuItem);
    });

    // add menuItems to button menu
    var buttonMenu:Menu = new Menu(
            new menu({
              cls: 'analytics-menu',
              items: menuItemsFromButtons,
              allowFunctions: true,
              width: 233,
              defaultOffsets: [-190, 1]
            })
    );

    // create menu button that holds the above menu
    var newButton:Button = new Button(new button({
      iconCls: 'btn-analytics-report',
      scale: 'medium',
      itemId: 'analyticsReportButton',
      tooltip: AnalyticsStudioPlugin_properties.INSTANCE.multi_analytics_button_tooltip,
      disabled: true,
      menu: buttonMenu
    }));

    newButton.addListener('menushow', addPressedIndicator);
    newButton.addListener('menuhide', removePressedIndicator);

    add(newButton);
    doLayout();
  }

  internal function updateAnalyticsReportButton():void {
    var component:Button = this.getComponent('analyticsReportButton') as Button;
    var allMenuItemsDisabled:Boolean = component.menu.items.getRange().every(function (item:Item):Boolean {
      return item.disabled;
    });
    component.setDisabled(allMenuItemsDisabled);
  }

  internal function createMenuItem(item:OpenAnalyticsDeepLinkUrlButton):Item {
    // make sure button is properly initialized
    if (!item.urlValueExpression) {
      item.initUrlValueExpression();
    }

    // copy config - except 'xtype'!
    var buttonConfig:openAnalyticsDeepLinkUrlButton = openAnalyticsDeepLinkUrlButton(item.initialConfig);
    var menuConfig:Object = {
      iconCls: "analytics-icon",
      handler: OpenAnalyticsUrlButtonBase.openInBrowser(item.urlValueExpression, buttonConfig.windowName),
      text: buttonConfig.tooltip,
      urlValueExpression: item.urlValueExpression,
      contentExpression: item.contentExpression
    };
    Ext.apply(menuConfig, buttonConfig);
    delete menuConfig.xtype;

    // init IOC
    ComponentContextManager.configOwnerCt(menuConfig, this);

    var result:OpenAnalyticsUrlMenuItemBase = new OpenAnalyticsUrlMenuItemBase(menuitem(menuConfig));
    OpenAnalyticsUrlButtonBase.bindDisable(item.urlValueExpression, result);

    item.destroy();
    return result;
  }

  private static function addPressedIndicator(button:Button):void {
    button.addClass('preview-panel-button-pressed')
  }

  private static function removePressedIndicator(button:Button):void {
    button.removeClass('preview-panel-button-pressed')
  }
}
}

package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.StructEditor_properties;
import com.coremedia.blueprint.studio.struct.config.addElementAction;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.components.ContextMenuNoIcon;
import com.coremedia.ui.config.contextMenuNoIcon;

import ext.config.menuitem;
import ext.tree.TreeNode;
import ext.util.StringUtil;

public class ContextMenuFactory {

  /**
   * Creates the context menu items for the given node.
   */
  public static function createContextMenu(node:TreeNode, selectionExpression:ValueExpression, handler:StructHandler):ContextMenuNoIcon {
    var ctxMenu:ContextMenuNoIcon = new ContextMenuNoIcon(contextMenuNoIcon({}));
    var model:ElementModel = handler.getData(node);
    var type:int = model.getType();
    
    var types:Array = handler.getEnabledTypes(node);
    if(!types ||types.length == 0) {
      return null;
    }

    types.forEach(function(eType:int):void {
      addMenuItem(ctxMenu, eType, selectionExpression, handler);
    });
    return ctxMenu;
  }

  /**
   * Creates a new menu item and adds it to the menu.
   * @param menu
   * @param type
   * @param selectionExpression
   * @param handler
   */
  private static function addMenuItem(menu:ContextMenuNoIcon, type:int, selectionExpression:ValueExpression, handler:StructHandler):void {
    var label:String = StructEditor_properties.INSTANCE['Struct_btn_tooltip_' + type];
    label = StringUtil.format(StructEditor_properties.INSTANCE.Struct_menu_item_text, label);

    var menuItem:menuitem = menuitem({xtype:"menuitem", text:label,
      baseAction:new AddElementAction(addElementAction({structHandler:handler, selectedNodeExpression:selectionExpression, nodeType:type}))});
    menu.addMenuItem(menuItem);
  }
}
}
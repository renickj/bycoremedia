package com.coremedia.blueprint.studio {

import com.coremedia.blueprint.base.components.quickcreate.QuickCreate;
import com.coremedia.blueprint.studio.config.blueprintFormsStudioPlugin;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;

public class BlueprintFormsStudioPluginBase extends StudioPlugin {
  public function BlueprintFormsStudioPluginBase(config:blueprintFormsStudioPlugin) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);
    
    // Register Navigation Parent for CMChannel
    CMChannelExtension.register();

    // Fix wrong icon mappings for quick create menu items
    QuickCreate.registerIconClass("CMQueryList", "CMDynamicList");
    QuickCreate.registerIconClass("CMALXEventList", "CMDynamicList");
    QuickCreate.registerIconClass("CMALXPageList", "CMDynamicList");
    QuickCreate.registerIconClass("ESDynamicList", "CMDynamicList");
    QuickCreate.registerIconClass("CMSelectionRules", "SelectionRuleList");
    QuickCreate.registerIconClass("CMP13NSearch", "SelectionRuleList");
    QuickCreate.registerIconClass("CMLocTaxonomy", "CMTaxonomy");
    QuickCreate.registerIconClass("CMFolderProperties", "CMObject");
    QuickCreate.registerIconClass("CMMail", "CMObject");
    QuickCreate.registerIconClass("CMPlaceholder", "CMAction");
    QuickCreate.registerIconClass("CMTemplateSet", "CMDownload");
    QuickCreate.registerIconClass("CMSegment", "Segment");
  }

  /**
   * Used for columns setup:
   * Extends the sort by sorting by name.
   *
   * @param field the sortfield which is selected and should be extended
   * @param direction the sortdirection which is selected
   * @return array filled with additional order by statements
   */
  public static function extendOrderByName(field:String, direction:String):Array {
    var orderBys:Array = [];
    orderBys.push('name ' + direction);
    return orderBys;
  }

}
}

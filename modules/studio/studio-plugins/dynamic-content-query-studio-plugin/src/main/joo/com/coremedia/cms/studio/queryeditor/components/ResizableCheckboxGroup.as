package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cms.studio.queryeditor.config.resizableCheckboxGroup;

import ext.Ext;
import ext.Resizable;
import ext.config.resizable;
import ext.form.CheckboxGroup;

public class ResizableCheckboxGroup extends CheckboxGroup {

  private var element:ResizableCheckboxGroup;
  private var containerHeight:Number;

  public function ResizableCheckboxGroup(config:resizableCheckboxGroup = null) {
    super(resizableCheckboxGroup(Ext.apply({}, config)));
    element = this;
    containerHeight = config.documentTypesFilterHeight;
  }

  override protected function afterRender():void {
    super.afterRender();

    if(element.getHeight() > containerHeight){
      element.setHeight(containerHeight);
    }

    var resizer:Resizable = new Resizable(element.getId(), resizable({
      'handles': 's',
      'pinned': true,
      'dynamic':true,
      'minHeight': 30
      })
    );

    /**
     * With every resize a Resizable element sets height and width even though
     * the element was resized in only one dimension (e.g. height in this case).
     * Because of that, fixed width has to be removed after resize. Inner
     * layout has to be recalculated also, because the appearance of a vertical
     * scroll bar will decrease the available width and a horizontal scroll bar
     * will appear.
     */
    resizer.on('resize', function():void {
      element.getEl().dom.style.width = "auto";
      element.ownerCt.doLayout();
    });
  }

}
}

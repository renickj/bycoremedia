package com.coremedia.blueprint.studio.util.plugins {

import com.coremedia.cms.editor.sdk.premular.fields.RichTextPropertyField;

import ext.Component;
import ext.Plugin;

/**
 * Hides the toolbar of a <code>RichTextPropertyField</code>.
 */
public class HideRichTextToolbarPlugin implements Plugin {

  private var component:RichTextPropertyField;

  public function init(component:Component):void {
    this.component = component as RichTextPropertyField;
    //we have to wait for the toolbar to be rendered to hide it.
    this.component.addListener('afterrender', hideToolbar);
  }

  private function hideToolbar():void {
    this.component.removeListener('afterrender', hideToolbar);
    component.getTopToolbar().hide();
  }
}
}

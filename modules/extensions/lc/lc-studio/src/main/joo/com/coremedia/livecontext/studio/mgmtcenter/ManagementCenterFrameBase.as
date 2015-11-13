package com.coremedia.livecontext.studio.mgmtcenter {

import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.managementCenterFrame;

import ext.BoxComponent;
import ext.Element;
import ext.Ext;

import js.Window;

public class ManagementCenterFrameBase extends BoxComponent {

  public function ManagementCenterFrameBase(config:managementCenterFrame = null) {
    super(managementCenterFrame(Ext.apply({
      layout:'fit',
      title:LivecontextStudioPlugin_properties.INSTANCE.Window_ManagementCenter_title
    }, config)));
  }

  private function setUrl():void {
    var url:String = ManagementCenterUtil.getUrl();
    var elem:Element = getEl();
    if (elem) {
      elem.set({src: url});

      // only IE doesn't reload the iframe now, so we have to enforce it:
      if (Ext.isIE) {
        var contentWindow:Window = getContentWindow();
        if(contentWindow) {
          contentWindow.location.href = url; // necessary for IE only!
        }
      }
    }
  }

  protected override function onRender(ct:Element, position:Element):void {
    super.onRender(ct, position);
    setUrl();
    this['el'] = ct.createChild({
      tag:'iframe',
      id:'iframe-' + this.getId(),
      frameBorder:0,
      width:"100%",
      height:"100%",
      src:ManagementCenterUtil.getUrl()
    });
  }

  public function getContentWindow():Window{
    return (this.el && this.el.dom) ? this.el.dom.contentWindow : null;
  }

}
}
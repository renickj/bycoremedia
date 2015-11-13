package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.blueprint.studio.config.externallibrary.iFrameWindow;

import ext.Ext;
import ext.Window;
import ext.form.Label;


/**
 * Window with an iframe.
 */
public class IFrameWindowBase extends Window {

  private var url:String;

  public function IFrameWindowBase(config:iFrameWindow) {
    this.url = config.url;
    super(config);
    addListener('afterlayout', initFrame);
  }

  private function initFrame():void {
    removeListener('afterlayout', initFrame);
    var label:Label = find('itemId', 'embedded')[0] as Label;
    label.setText('<iframe width="100%" height="100%" src="' + url + '" />', false);
  }
}
}
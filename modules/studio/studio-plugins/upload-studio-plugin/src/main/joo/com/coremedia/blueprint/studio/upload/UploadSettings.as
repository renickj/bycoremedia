package com.coremedia.blueprint.studio.upload {

import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.BeanImpl;

import ext.Ext;

/**
 * Contains all information that are stored in the UploadSettings settings document.
 * The document is retrieved server side so that it can be merged with site depending information.
 */
public class UploadSettings extends BeanImpl {
  private static const DEFAULT_FOLDER_PROPERTY:String = 'defaultFolder';
  private static const DEFAULT_CONTENT_TYPE_PROPERTY:String = 'defaultContentType';
  private static const MIME_TYPE_MAPPINGS:Object = 'mimeTypeMappings';

  public static const CHECKIN_PROPERTY:String = 'checkIn';
  public static const OPEN_IN_TAB_PROPERTY:String = 'openInTab';
  public static const TIMEOUT_PROPERTY:String = 'timeout';

  private var configBean:RemoteBean;

  //creates a new settings object for the preferred site.
  public function UploadSettings() {
    var url:String = 'upload/config';
    var preferredSiteId:String = editorContext.getSitesService().getPreferredSiteId();
    if(preferredSiteId) {
      url+='?' + Ext.urlEncode({site: editorContext.getSitesService().getPreferredSiteId()});
    }
    configBean = beanFactory.getRemoteBean(url);
    set(UploadSettings.OPEN_IN_TAB_PROPERTY, true);
  }

  public function ensureLoaded():UploadSettings {
    if (getDefaultUploadPath() === undefined) {
      configBean.get(DEFAULT_FOLDER_PROPERTY);
      load(Ext.emptyFn);
    }
    return this;
  }

  public function load(callback:Function):void {
    configBean.load(function ():void {
      set(DEFAULT_FOLDER_PROPERTY, configBean.get(DEFAULT_FOLDER_PROPERTY));
      set(DEFAULT_CONTENT_TYPE_PROPERTY, configBean.get(DEFAULT_CONTENT_TYPE_PROPERTY));
      set(MIME_TYPE_MAPPINGS, configBean.get(MIME_TYPE_MAPPINGS));

      if(configBean.get(TIMEOUT_PROPERTY) > 0 ) {
        set(TIMEOUT_PROPERTY, configBean.get(TIMEOUT_PROPERTY));
      }

      callback();
    });
  }

  public function getTimeout():Number {
    return get(TIMEOUT_PROPERTY);
  }

  public function getDefaultUploadPath():String {
    return get(DEFAULT_FOLDER_PROPERTY);
  }

  public function getDefaultContentType():String {
    return get(DEFAULT_CONTENT_TYPE_PROPERTY);
  }

  public function getCheckIn():Boolean {
    return get(CHECKIN_PROPERTY);
  }

  public function getOpenInTab():Boolean {
    return get(OPEN_IN_TAB_PROPERTY);
  }

  public function getMimeTypeMappings():Object {
    return get(MIME_TYPE_MAPPINGS);
  }
}
}

package com.coremedia.blueprint.studio.topicpages.administration {

import com.coremedia.blueprint.base.components.util.StringHelper;
import com.coremedia.blueprint.studio.Blueprint_properties;
import com.coremedia.blueprint.studio.TopicsHelper;
import com.coremedia.blueprint.studio.topicpages.config.openTopicPagesEditorAction;
import com.coremedia.blueprint.studio.topicpages.config.topicPagesEditor;
import com.coremedia.blueprint.studio.util.UserUtil;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.desktop.WorkAreaTabType;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.Bean;

import ext.Action;
import ext.Button;
import ext.Component;
import ext.Ext;

/** Opens the Topic page editor **/
public class OpenTopicPagesEditorAction extends Action {

  internal native function get items():Array;

  public function OpenTopicPagesEditorAction(config:openTopicPagesEditorAction) {
    config.handler = showTopicPagesEditor;
    super(config);
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    isAdministrationEnabled(function(enabled:Boolean):void {
      setDisabled(!enabled);
      updateTooltip(enabled);
    });
    editorContext.getSitesService().getPreferredSiteIdExpression().addChangeListener(preferredSiteChangedHandler);
  }

  private function preferredSiteChangedHandler():void {
    isAdministrationEnabled(function(enabled:Boolean):void {
      setDisabled(!enabled);
      updateTooltip(enabled);
      if(!enabled) {
        var topicPagesAdminTab:TopicPagesEditor = TopicPagesEditorBase.getInstance();
        if(topicPagesAdminTab) {
          topicPagesAdminTab.destroy();
        }
      }
    });
  }

  private static function updateTooltip(enabled:Boolean):void {
    var msg:String = Blueprint_properties.INSTANCE.Button_disabled_insufficient_privileges;
    if(enabled) {
      msg = '';
    }

    var button:Button = (Ext.getCmp('btn-topicpages-editor') as Button);
    if(button) {
      button.setTooltip(msg);
    }
  }

  /**
   * Returns true if the current user can administrate the taxonomies.
   * @return
   */
  public static function isAdministrationEnabled(callback:Function):void {
    TopicsHelper.loadSettings(function (settingsRemoteBean:Bean):void {
      var topicPageChannel:Content = settingsRemoteBean.get('topicPageChannel');
      if(!topicPageChannel) {
        trace("[INFO]", "Topic Pages: could not find root channel for topic pages, please check the TopicPages settings document of the preferred site.");
        callback.call(null, false);
      }
      else {
        var adminGroups:Array = settingsRemoteBean.get('adminGroups');
        if(session.getUser().isAdministrative()) {
          callback.call(null, true);
        } else {
          for(var i:int = 0; i<adminGroups.length; i++) {
            var groupName:String = StringHelper.trim(adminGroups[i],'');
            if(UserUtil.isInGroup(groupName)) {
              callback.call(null, true);
              return;
            }
          }
          callback.call(null, false);
        }
      }
    });
  }


  /**
   * Static call to open the taxonomy admin console.
   */
  private static function showTopicPagesEditor():void {
    var workArea:WorkArea = editorContext.getWorkArea() as WorkArea;
    var topicPagesAdminTab:TopicPagesEditor = TopicPagesEditorBase.getInstance();

    if (!topicPagesAdminTab) {
      var workAreaTabType:WorkAreaTabType = workArea.getTabTypeById(topicPagesEditor.xtype);
      topicPagesAdminTab = workAreaTabType.createTab(null) as TopicPagesEditor;
      workArea.addTab(workAreaTabType, topicPagesAdminTab);
    }

    workArea.setActiveTab(topicPagesAdminTab);
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      editorContext.getSitesService().getPreferredSiteIdExpression().removeChangeListener(preferredSiteChangedHandler);
    }
  }
}
}

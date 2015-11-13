package com.coremedia.cms.studio.queryeditor.conditions {

import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManager;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.studio.queryeditor.config.contentDropTarget;

import ext.Container;
import ext.Ext;

/**
 * This class represents the visual drop target of the folder list and opens
 * the collection view on click, but the real drop logic is in the FolderList
 * class. The whole folder list is a drop target to help the user interaction.
 */
public class ContentDropTarget extends Container {

  private var contentType:String;

  public function ContentDropTarget(config:contentDropTarget = null) {
    super(contentDropTarget(Ext.apply({}, config)));
    this.contentType = config.contentType;
  }

  override protected function afterRender():void {
    this.getEl().addListener("click", openCollectionView);
    this.getEl().setStyle("cursor", "pointer");
    super.afterRender();
  }

  /**
   * Opens the collection view with folder tree as the default view.
   */
  private function openCollectionView():void {
    var collectionViewManager:CollectionViewManager = editorContext.getCollectionViewManager();
    if(contentType) {
      collectionViewManager.openSearchForType(contentType);
    } else {
      collectionViewManager.openRepository();
    }
  }

}
}

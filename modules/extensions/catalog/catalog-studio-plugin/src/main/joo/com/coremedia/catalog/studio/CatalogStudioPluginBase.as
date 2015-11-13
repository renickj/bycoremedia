package com.coremedia.catalog.studio {

import com.coremedia.blueprint.studio.config.catalog.catalogStudioPlugin;
import com.coremedia.blueprint.studio.util.ContentInitializer;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.blueprint.studio.util.UserUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cap.content.search.SearchParameters;
import com.coremedia.catalog.studio.preferences.CatalogPreferencesBase;
import com.coremedia.catalog.studio.tree.RepositoryCatalogTreeModel;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.collectionview.tree.RepositoryTreeDragDropModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.WindowUtil;

public class CatalogStudioPluginBase extends StudioPlugin {

  public function CatalogStudioPluginBase(config:catalogStudioPlugin) {
    super(config)
  }

  private var treeModel:RepositoryCatalogTreeModel;

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    //apply defaults
    editorContext.registerContentInitializer(CatalogTreeRelation.CONTENT_TYPE_CATEGORY, ContentInitializer.initChannel);

    addCatalogTreeModel();

    addViewPreferencesListener();
  }

  /**
   * Registers the catalog tree model and its dnd model.
   */
  private function addCatalogTreeModel():void {
    var collectionViewManagerInternal:CollectionViewManagerInternal =
            ((editorContext.getCollectionViewManager()) as CollectionViewManagerInternal);

    treeModel= new RepositoryCatalogTreeModel();
    collectionViewManagerInternal.addTreeModel(treeModel, new RepositoryTreeDragDropModel(treeModel));
    collectionViewManagerInternal.addRepositoryListSorter(new CatalogRepositoryListSorter());

    //add extension for custom search document types
    collectionViewManagerInternal.addExtension(new CatalogCollectionViewExtension());
  }

  /**
   * We have to force a reload if the catalog view settings are changed.
   * Maybe this is possible without a Studio reload in the future, but this is the easiest way to apply the setting.
   */
  private function addViewPreferencesListener():void {
    //load the catalog view settings and apply it to the tree model
    var doShow:Boolean = StudioUtil.getPreference(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY);
    treeModel.setEnabled(doShow === undefined || doShow);

    //add change listener to the catalog view settings
    var preferencesVE:ValueExpression = ValueExpressionFactory.create(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY, editorContext.getPreferences());
    preferencesVE.addChangeListener(function(ve:ValueExpression):void {
      var doShow:Boolean = ve.getValue();
      var enabled:Boolean = treeModel.isEnabled();
      treeModel.setEnabled(doShow === undefined || doShow);
      if(enabled !== treeModel.isEnabled()) {
        WindowUtil.forceReload();
      }
    });
  }

  /**
   * Tests whether the current user is a member of one of the given groups
   * @param groups an array of strings of group names
   * @return true if the current user is a member of one of the given groups, false otherwise
   */
  public static function isMemberOfAnyGroup(groups:Array):Boolean {
    for each(var group:String in groups) {
      if(UserUtil.isInGroup(group)) {
        return true;
      }
    }
    return false;
  }
}
}
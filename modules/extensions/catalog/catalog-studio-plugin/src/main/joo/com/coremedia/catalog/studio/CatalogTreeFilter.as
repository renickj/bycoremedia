package com.coremedia.catalog.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.catalog.studio.preferences.CatalogPreferencesBase;
import com.coremedia.catalog.studio.tree.RepositoryCatalogTreeModel;
import com.coremedia.cms.editor.configuration.TreeFilter;
import com.coremedia.cms.editor.sdk.collectionview.tree.CompoundChildTreeModel;
import com.coremedia.cms.editor.sdk.collectionview.tree.RepositoryTreeModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.models.TreeModel;

/**
 * Filters products from the content library tree.
 * Hides deleted content from the RepositoryCatalogTree.
 */
public class CatalogTreeFilter implements TreeFilter {

  public function filter(treeModel:TreeModel, child:Object):Boolean {
    if (treeModel && child is Content) {

      //Check if the tree model is enabled. If not, the whole tree isn't visible anyway.
      if(treeModel is CompoundChildTreeModel && !(treeModel as CompoundChildTreeModel).isEnabled()) {
        return true;
      }

      var content:Content = child as Content;
      if (!content.getPath()) {
        return undefined;
      }

      return filterContent(treeModel, content);
    }
    return false;
  }

  /**
   * Checks the settings state for the catalog tree, the location of the content and the origin site
   * if the given content should be hidden. Since the filter is also used for the library list/thumbnail view it
   * is not sufficient only to check if the TreeModel is enabled. If the catalog should be hidden, we have to show
   * the products instead.
   * @param treeModel the tree model the filter is executed for
   * @param content the content that should be displayed/filtered
   * @return true if the content should be display, undefined if the content or other properties not loaded yet.
   */
  private function filterContent(treeModel:TreeModel, content:Content):Boolean {
    var rootId:String = treeModel.getRootId();
    if (rootId === undefined) {
      return undefined;
    }

    var prefs:Struct = editorContext.getPreferences();
    if(!prefs) {
      return undefined;
    }

    //if the catalog should be hidden, then disable the filter
    var showCatalog:Boolean = prefs.get(CatalogPreferencesBase.PREFERENCE_SHOW_CATALOG_KEY);
    if(showCatalog !== undefined && !showCatalog) {
      return false;
    }

    if (rootId == RepositoryTreeModel.REPOSITORY_ROOT_ID) {
      //check if there is currently a CoreMedia store selected
      if (CatalogHelper.getInstance().isActiveCoreMediaStore()) {
        //retrieve the active site id...
        var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
        //and check if the current content belongs to the active site
        if (siteId == editorContext.getSitesService().getPreferredSiteId()) {
          //if the content belongs to the active site, check if it is a content and should be  hidden
          return content.getPath().indexOf("/Products") !== -1;
        }
      }
    }

    if (treeModel as RepositoryCatalogTreeModel) {
      return content.isDeleted();
    }

    return false;
  }
}
}
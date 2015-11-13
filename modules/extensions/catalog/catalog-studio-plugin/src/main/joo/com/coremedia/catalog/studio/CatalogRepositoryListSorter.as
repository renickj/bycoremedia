package com.coremedia.catalog.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.collectionview.sort.RepositoryListSorterImpl;

/**
 * Extends the default RepositoryListSorterImpl to override the resolving of children.
 * The parent/children relationship differs for the catalog documents and is implemented here.
 */
public class CatalogRepositoryListSorter extends RepositoryListSorterImpl {

  override public function isApplicable(item:Object):Boolean {
    //TODO mfa: will fail once we want yet another content based tree (asset management?)
    return (item is Content) && item.isDocument();
  }

  override public function getChildren(folder:Content):Array {
    //get categories from the parent category
    var linkedChildren:Array = folder.getProperties().get("children");
    if (linkedChildren === undefined) {
      return undefined;
    }

    //get the children of the selected category
    var linkingChildren:Array = folder.getReferrersWithNamedDescriptor("CMHasContexts", "contexts");
    if (linkingChildren === undefined) {
      return undefined;
    }

    return linkedChildren.concat(linkingChildren);
  }


  override public function filter(folder:Content, children:Array):Array {
    children = children.filter(function (item:Content):Boolean {
      return !item.isDeleted();
    });
    return children;
  }

}
}

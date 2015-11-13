package com.coremedia.catalog.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.ContentTypeNames;
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cap.content.search.SearchParameters;
import com.coremedia.cms.editor.sdk.collectionview.RepositoryCollectionViewExtension;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;

public class CatalogCollectionViewExtension extends RepositoryCollectionViewExtension {

  protected static const PRODUCT_TYPE_RECORD:Object = {
    name: ContentTypeNames.DOCUMENT,
    label: CatalogStudioPlugin_properties.INSTANCE.CMProduct_text,
    icon: CatalogStudioPlugin_properties.INSTANCE.CMProduct_icon
  };

  protected static const CATEGORY_TYPE_RECORD:Object = {
    name: CatalogTreeRelation.CONTENT_TYPE_CATEGORY,
    label: CatalogStudioPlugin_properties.INSTANCE.CMCategory_text,
    icon: CatalogStudioPlugin_properties.INSTANCE.CMCategory_icon
  };

  override public function isApplicable(model:Object):Boolean {
    if (model is Content) {
      var type:ContentType = (model as Content).getType();
      var isCmStore:Boolean = CatalogHelper.getInstance().isActiveCoreMediaStore();
      if(isCmStore && (type.getName() == CatalogTreeRelation.CONTENT_TYPE_CATEGORY || type.getName() == CatalogTreeRelation.CONTENT_TYPE_PRODUCT)) {
        return true;
      }
    }
    return false;
  }

  override public function getAvailableSearchTypes(folder:Object):Array {
    return [PRODUCT_TYPE_RECORD, CATEGORY_TYPE_RECORD];
  }

  /**
   * Adds an additional query fragment to filter for categories if a category is selected
   */
  override public function applySearchParameters(folder:Content, filterQueryFragments:Array, searchParameters:SearchParameters):SearchParameters {
    filterQueryFragments.push((searchParameters.includeSubfolders ? "allProductCategories" : "directProductCategories") + ":" + ContentImpl(folder).getNumericId());
    searchParameters.folder = null;
    return searchParameters;
  }
}
}
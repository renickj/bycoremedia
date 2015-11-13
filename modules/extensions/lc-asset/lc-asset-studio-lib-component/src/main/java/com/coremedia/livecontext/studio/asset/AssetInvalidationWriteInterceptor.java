package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@link com.coremedia.rest.cap.intercept.ContentWriteInterceptor}
 * which delegates the invalidation of CommerceRemote Beans to the write post processor
 * if commerce product reference list of the given asset or the image data and the transformation meta dta are changed.
 * Only the difference between the old and new list is invalidated
 * The difference is only accessible before the write operation.
 */
public class AssetInvalidationWriteInterceptor extends ContentWriteInterceptorBase {
  public static final String STRUCT_PROPERTY_NAME = "localSettings";

  private AssetInvalidationWritePostProcessor postProcessor;

  @Inject
  @Named("lcAssetManagementLicenseInspector")
  private AssetManagementLicenseInspector licenseInspector;

  @Override
  public void intercept(ContentWriteRequest request) {
    Content content = request.getEntity();
    Map<String, Object> properties = request.getProperties();

    if (content != null && properties != null
            && licenseInspector.isFeatureActive()
            && properties.containsKey(STRUCT_PROPERTY_NAME)) {
      Struct localSettings = (Struct) properties.get(STRUCT_PROPERTY_NAME);

      Collection<String> references = getInvalidReferences(content, localSettings);

      //we delegate the invaliations to the write post processor
      //as the write interceptor has too old sequence number
      postProcessor.addInvalidations(references);
    }
  }

  private Collection<String> getInvalidReferences(Content content, Struct localSettings) {
    //the list of references to the products might have been changed
    //let's calculate the diff between the old and new lists
    List<String> newIds = ProductReferenceHelper.getExternalReferences(localSettings);
    List<String> oldIds = ProductReferenceHelper.getExternalReferences(content);

    Collection<String> invalidations = (Collection<String>) CollectionUtils.disjunction(newIds, oldIds);  // NOSONAR  non generic legacy library

    //if the references aren't changed...
    if (invalidations.isEmpty()) {
      //... let's check if other properties of the local settings have changed
      if (!localSettings.equals(content.getStruct(STRUCT_PROPERTY_NAME))) {
        //if so all references have to be invalidated
        invalidations.addAll(oldIds);
      }
    }

    return invalidations;
  }

  @Required
  public void setPostProcessor(AssetInvalidationWritePostProcessor postProcessor) {
    this.postProcessor = postProcessor;
  }
}

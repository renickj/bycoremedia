package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.rest.cap.intercept.ContentWritePostprocessorBase;
import com.coremedia.rest.intercept.WriteReport;
import org.springframework.beans.factory.annotation.Required;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@link com.coremedia.rest.cap.intercept.ContentWritePostprocessor}
 * which invalidates CommerceRemote Beans if image data is changed.
 * We cannot use a repository listener nor write interceptor for this.
 * The first one doesn't tell us which property is changed.
 * The second one invalidates too early.
 */
public class AssetInvalidationWritePostProcessor extends ContentWritePostprocessorBase {

  static final String STRUCT_PROPERTY_NAME = "localSettings";

  private CommerceCacheInvalidationSource commerceCacheInvalidationSource;
  private CommerceConnectionInitializer commerceConnectionInitializer;
  @Inject
  @Named("lcAssetManagementLicenseInspector")
  private AssetManagementLicenseInspector licenseInspector;

  private List<String> invalidations = new ArrayList<>();

  @Override
  public void postProcess(WriteReport<Content> report) {
    Content content = report.getEntity();
    if (content != null) {
      initCommerceConnection(content);
    }

    commerceCacheInvalidationSource.invalidateReferences(invalidations);
    invalidations.clear();

    Map<String, Object> properties = report.getOverwrittenProperties();

    if (content != null && properties != null && properties.containsKey(CMPicture.DATA)
            && licenseInspector.isFeatureActive()) {

      Struct localSettings = (Struct) content.get(STRUCT_PROPERTY_NAME);
      List<String> productReferences = ProductReferenceHelper.getExternalReferences(localSettings);

      commerceCacheInvalidationSource.invalidateReferences(productReferences);
    }
  }

  public void addInvalidations(Collection<String> invalidations) {
    this.invalidations.addAll(invalidations);
  }

  List<String> getInvalidations() {
    return invalidations;
  }

  protected void initCommerceConnection(Content content) {
    commerceConnectionInitializer.init(content);
  }

  @Required
  public void setCommerceCacheInvalidationSource(CommerceCacheInvalidationSource commerceCacheInvalidationSource) {
    this.commerceCacheInvalidationSource = commerceCacheInvalidationSource;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }
}


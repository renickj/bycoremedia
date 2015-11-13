package com.coremedia.livecontext.asset;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.cap.content.events.ContentRepositoryListenerBase;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * A {@link com.coremedia.cap.content.events.ContentRepositoryListener} that reacts on changes
 * of CMPicture documents and stores them in {@link com.coremedia.livecontext.asset.AssetChanges}
 */
public class AssetChangesRepositoryListener extends ContentRepositoryListenerBase implements InitializingBean, DisposableBean {

  private static final String CMVISUAL_TYPE = "CMVisual";
  private static final String CMDOWNLOAD_TYPE = "CMDownload";

  private ContentRepository repository;
  private AssetChanges assetChanges;
  @Inject
  @Named("lcAssetManagementLicenseInspector")
  private AssetManagementLicenseInspector licenseInspector;

  @Override
  protected void handleContentEvent(ContentEvent event) {
    Content content = event.getContent();
    if (licenseInspector.isFeatureActive() && !content.isDestroyed() &&
            (content.getType().isSubtypeOf(CMVISUAL_TYPE) ||
                    content.getType().isSubtypeOf(CMDOWNLOAD_TYPE))) {
      assetChanges.update(content);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (repository != null) {
      repository.addContentRepositoryListener(this);
    }
  }

  @Override
  public void destroy() throws Exception {
    if (repository != null) {
      repository.removeContentRepositoryListener(this);
    }
  }


  @Required
  public void setRepository(ContentRepository repository) {
    this.repository = repository;
  }

  @Required
  public void setAssetChanges(AssetChanges assetChanges) {
    this.assetChanges = assetChanges;
  }


}

package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMVideo;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.cap.content.events.ContentRepositoryListenerBase;
import com.coremedia.cap.content.events.PropertiesChangedEvent;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * A {@link com.coremedia.cap.content.events.ContentRepositoryListener}
 * which invalidates commerce remote beans in the studio associated with assets in the repository.
 * The invalidation is triggered by atomic content events like creation, deletion etc.
 * Invalidation triggered by property change events are handled by
 * {@link AssetInvalidationWriteInterceptor} and
 * {@link AssetInvalidationWritePostProcessor}
 */
public class AssetInvalidationRepositoryListener extends ContentRepositoryListenerBase implements DisposableBean, InitializingBean {

  static List<String> EVENT_WHITELIST = Arrays.asList(
          ContentEvent.CONTENT_CREATED,
          ContentEvent.CONTENT_DELETED,
          ContentEvent.CONTENT_MOVED,
          ContentEvent.CONTENT_RENAMED,
          ContentEvent.CONTENT_REVERTED,
          ContentEvent.CONTENT_UNDELETED);

  private CommerceCacheInvalidationSource commerceCacheInvalidationSource;
  private ContentRepository repository;

  @Inject
  @Named("lcAssetManagementLicenseInspector")
  private AssetManagementLicenseInspector licenseInspector;

  @Override
  protected void handleContentEvent(ContentEvent event) {

    if (EVENT_WHITELIST.contains(event.getType())) {
      Content content = event.getContent();
      if (content != null && isRelevantType(content)
              && licenseInspector.isFeatureActive()
              && mayHaveExternalReferences(event)) {
        commerceCacheInvalidationSource.triggerDelayedInvalidation(
                new HashSet<>(Arrays.asList(CommerceCacheInvalidationSource.INVALIDATE_PRODUCTS_URI_PATTERN,
                        CommerceCacheInvalidationSource.INVALIDATE_PRODUCTVARIANTS_URI_PATTERN)));
      }
    }
  }

  @Override
  public void propertiesChanged(PropertiesChangedEvent event) {
    super.propertiesChanged(event);
  }

  @Override
  public void afterPropertiesSet() {
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
  public void setCommerceCacheInvalidationSource(CommerceCacheInvalidationSource commerceCacheInvalidationSource) {
    this.commerceCacheInvalidationSource = commerceCacheInvalidationSource;
  }

  @Required
  public void setRepository(ContentRepository repository) {
    this.repository = repository;
  }

  private boolean mayHaveExternalReferences(ContentEvent event) {
    return event.getType().equals(ContentEvent.CONTENT_REVERTED) ||
           !ProductReferenceHelper.getExternalReferences(event.getContent()).isEmpty();
  }

  /**
   *
   * @param content
   * @return true if the content is a picture, video or a download or one of their subtypes.
   */
  private boolean isRelevantType(Content content) {
    return content.getType().isSubtypeOf(CMPicture.NAME) ||
           content.getType().isSubtypeOf(CMVideo.NAME) ||
           content.getType().isSubtypeOf(CMDownload.NAME);
  }
}

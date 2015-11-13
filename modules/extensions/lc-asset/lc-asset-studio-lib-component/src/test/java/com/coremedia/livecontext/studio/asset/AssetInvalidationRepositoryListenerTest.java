package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProductReferenceHelper.class})
public class AssetInvalidationRepositoryListenerTest {

  @InjectMocks
  private AssetInvalidationRepositoryListener testling = new AssetInvalidationRepositoryListener();
  @Mock
  private AssetManagementLicenseInspector licenseInspector;
  @Mock
  private CommerceCacheInvalidationSource invalidationSource;
  @Mock
  private ContentType cmPictureType;
  @Mock
  private Content content;
  @Mock
  private ContentEvent event;
  @Mock
  private ContentRepository repository;

  @Before
  public void setUp() throws Exception {
    mockStatic(ProductReferenceHelper.class);

    testling.setCommerceCacheInvalidationSource(invalidationSource);
    testling.afterPropertiesSet();

    when(event.getType()).thenReturn(ContentEvent.CONTENT_CREATED);
    when(event.getContent()).thenReturn(content);
    when(content.getRepository()).thenReturn(repository);
    when(content.getType()).thenReturn(cmPictureType);
    when(cmPictureType.isSubtypeOf(CMPicture.NAME)).thenReturn(true);
    when(licenseInspector.isFeatureActive()).thenReturn(true);
  }

  @Test
  public void testHandleContentEvent() throws Exception {
    //content has any external references
    when(ProductReferenceHelper.getExternalReferences(content)).thenReturn(Arrays.asList("what", "ever"));
    testling.handleContentEvent(event);

    //then all products and product variants should be invalidated.
    verify(invalidationSource).triggerDelayedInvalidation(new HashSet<>(Arrays.asList(
            CommerceCacheInvalidationSource.INVALIDATE_PRODUCTS_URI_PATTERN,
            CommerceCacheInvalidationSource.INVALIDATE_PRODUCTVARIANTS_URI_PATTERN)));
  }
}
package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMVisual;
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
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetChangesRepositoryListenerTest {

  @InjectMocks
  private AssetChangesRepositoryListener testling = new AssetChangesRepositoryListener();

  @Mock
  private ContentRepository repository;
  @Mock
  private AssetManagementLicenseInspector licenseInspector;
  @Mock
  private AssetChanges assetChanges;
  @Mock
  private ContentEvent event;
  @Mock
  private Content content;
  @Mock
  private ContentType cmPictureType;

  @Before
  public void setUp() throws Exception {
    testling.setRepository(repository);
    testling.setAssetChanges(assetChanges);
    testling.afterPropertiesSet();
    when(event.getContent()).thenReturn(content);
    when(event.getType()).thenReturn(ContentEvent.CONTENT_CREATED);
    when(content.getType()).thenReturn(cmPictureType);
    when(content.getRepository()).thenReturn(repository);
    when(cmPictureType.isSubtypeOf(CMPicture.NAME)).thenReturn(true);
    when(cmPictureType.isSubtypeOf(CMVisual.NAME)).thenReturn(true);
    when(licenseInspector.isFeatureActive()).thenReturn(true);
  }

  @Test
  public void testHandleContentEvent() throws Exception {
    testling.handleContentEvent(event);
    verify(assetChanges).update(content);
  }
}
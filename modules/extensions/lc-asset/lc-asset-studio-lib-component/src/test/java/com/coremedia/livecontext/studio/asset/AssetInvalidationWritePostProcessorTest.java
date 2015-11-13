package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.rest.intercept.WriteReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProductReferenceHelper.class})
public class AssetInvalidationWritePostProcessorTest {
  @InjectMocks
  private AssetInvalidationWritePostProcessor testling = new AssetInvalidationWritePostProcessor();

  @Mock
  private AssetManagementLicenseInspector licenseInspector;
  @Mock
  private CommerceCacheInvalidationSource invalidationSource;
  @Mock
  private ContentType cmPictureType;
  @Mock
  private WriteReport<Content> report;
  @Mock
  private Content content;
  @Mock
  private ContentRepository repository;
  @Mock()
  private Struct localSettings;
  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Before
  public void setUp() throws Exception {
    mockStatic(ProductReferenceHelper.class);

    testling.setType(cmPictureType);
    testling.setCommerceCacheInvalidationSource(invalidationSource);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);

    when(report.getEntity()).thenReturn(content);
    Map<String, Object> properties = new HashMap<>();
    properties.put(CMPicture.DATA, new Object());
    when(report.getOverwrittenProperties()).thenReturn(properties);
    when(content.getRepository()).thenReturn(repository);
    when(content.get(AssetInvalidationWritePostProcessor.STRUCT_PROPERTY_NAME)).thenReturn(localSettings);
    when(licenseInspector.isFeatureActive()).thenReturn(true);
    mockStatic(AssetHelper.class);
  }

  @Test
  public void testPostProcess() throws Exception {
    List<String> references = Arrays.asList("a", "b", "c");
    when(ProductReferenceHelper.getExternalReferences(localSettings)).thenReturn(references);
    testling.postProcess(report);
    verify(invalidationSource).invalidateReferences(references);
  }
}
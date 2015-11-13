package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.studio.cache.CommerceCacheInvalidationSource;
import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.apache.commons.collections.CollectionUtils;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProductReferenceHelper.class})
public class AssetInvalidationWriteInterceptorTest {

  @InjectMocks
  private AssetInvalidationWriteInterceptor testling = new AssetInvalidationWriteInterceptor();

  private AssetInvalidationWritePostProcessor postProcessor;

  @Mock
  private CommerceCacheInvalidationSource invalidationSource;
  @Mock
  private AssetManagementLicenseInspector licenseInspector;
  @Mock
  private ContentType cmPictureType;
  @Mock
  private ContentWriteRequest contentWriteRequest;
  @Mock
  private Content content;
  @Mock
  private ContentRepository repository;
  @Mock
  private Struct oldLocalSettings, newLocalSettings;

  @Before
  public void setUp() throws Exception {
    mockStatic(ProductReferenceHelper.class);
    postProcessor = new AssetInvalidationWritePostProcessor();
    testling.setType(cmPictureType);
    testling.setPostProcessor(postProcessor);
    testling.afterPropertiesSet();

    when(contentWriteRequest.getEntity()).thenReturn(content);
    when(content.getRepository()).thenReturn(repository);
    Map<String, Object> properties = new HashMap<>();
    properties.put(AssetHelper.STRUCT_PROPERTY_NAME, newLocalSettings);
    when(contentWriteRequest.getProperties()).thenReturn(properties);
    when(licenseInspector.isFeatureActive()).thenReturn(true);
  }

  @Test
  public void testReferencesChange() throws Exception {
    //the old references
    when(ProductReferenceHelper.getExternalReferences(content)).thenReturn(Arrays.asList("a", "b", "c"));
    //the new references
    when(ProductReferenceHelper.getExternalReferences(newLocalSettings)).thenReturn(Arrays.asList("c", "d", "e"));
    testling.intercept(contentWriteRequest);
    List<String> expected = Arrays.asList("d", "e", "b", "a");
    List<String> actual = postProcessor.getInvalidations();
    assertTrue(CollectionUtils.disjunction(expected, actual).isEmpty());
  }

  @Test
  public void testLocalSettingsChange() throws Exception {
    //the references are not changed...
    when(ProductReferenceHelper.getExternalReferences(content)).thenReturn(Arrays.asList("a", "b", "c"));
    when(ProductReferenceHelper.getExternalReferences(newLocalSettings)).thenReturn(Arrays.asList("a", "b", "c"));
    //but the local seetings are changed.
    when(content.getStruct(AssetInvalidationWriteInterceptor.STRUCT_PROPERTY_NAME)).thenReturn(oldLocalSettings);

    testling.intercept(contentWriteRequest);

    assertEquals(Arrays.asList("a", "b", "c"), postProcessor.getInvalidations());
  }
}
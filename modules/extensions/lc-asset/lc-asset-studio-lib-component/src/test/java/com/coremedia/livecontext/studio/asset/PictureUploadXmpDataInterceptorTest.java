package com.coremedia.livecontext.studio.asset;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.asset.util.XmpImageMetadataExtractor;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoCommerceConnectionAvailable;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({XmpImageMetadataExtractor.class, AssetHelper.class, Commerce.class})
@RunWith(PowerMockRunner.class)
public class PictureUploadXmpDataInterceptorTest {

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private ContentWriteRequest contentWriteRequest;

  @Mock
  private Content parentFolder;

  @Mock
  private Content content;

  @Mock
  private Blob blob;

  @Mock
  private StoreContext defaultContext;

  private PictureUploadXmpDataInterceptor testling;

  @Before
  public void setup() {
    testling = new PictureUploadXmpDataInterceptor();
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setImageProperty("data");

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
  }

  @Test
  public void testInterceptNoMatch() {
    when(contentWriteRequest.getProperties()).thenReturn(Collections.<String, Object>emptyMap());
    testling.intercept(contentWriteRequest);

    PowerMockito.verifyStatic(times(0));
    XmpImageMetadataExtractor.extractInventoryInfo(Matchers.any(InputStream.class));
  }

  @Test
  public void testInterceptNoCommerceConnection() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("data", blob);
    when(contentWriteRequest.getProperties()).thenReturn(properties);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    doThrow(NoCommerceConnectionAvailable.class).when(commerceConnectionInitializer).init(parentFolder);

    testling.intercept(contentWriteRequest);

    PowerMockito.verifyStatic(times(0));
    XmpImageMetadataExtractor.extractInventoryInfo(Matchers.any(InputStream.class));
  }

  @Test
  public void testInterceptNoXmpData() {
    Map<String, Object> propertiesMock = mock(Map.class);
    when(propertiesMock.get("data")).thenReturn(blob);
    when(contentWriteRequest.getProperties()).thenReturn(propertiesMock);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    PowerMockito.mockStatic(XmpImageMetadataExtractor.class);
    when(XmpImageMetadataExtractor.extractInventoryInfo(blob.getInputStream())).thenReturn(Collections.EMPTY_LIST);

    testling.intercept(contentWriteRequest);

    verify(propertiesMock, never()).put(Matchers.anyString(), Matchers.anyObject());
  }

  @Test
  public void testInterceptWithXmpData() {
    Map<String, Object> propertiesMock = mock(Map.class);
    when(propertiesMock.get("data")).thenReturn(blob);
    when(propertiesMock.containsKey("data")).thenReturn(true);

    when(contentWriteRequest.getProperties()).thenReturn(propertiesMock);
    when(contentWriteRequest.getParent()).thenReturn(parentFolder);
    when(contentWriteRequest.getEntity()).thenReturn(content);
    List<String> xmpData = Arrays.asList("PC_EVENING_DRESS", "PC_EVENING_DRESS-RED-M");
    PowerMockito.mockStatic(XmpImageMetadataExtractor.class);
    when(XmpImageMetadataExtractor.extractInventoryInfo(blob.getInputStream())).thenReturn(xmpData);
    PowerMockito.mockStatic(AssetHelper.class);
    when(AssetHelper.updateCMPictureForExternalIds(content, xmpData, contentRepository)).thenReturn(null);

    Product productMock = mock(Product.class);
    when(productMock.getId()).thenReturn("vendor:///catalog/product/PC_EVENING_DRESS");
    when(productMock.isVariant()).thenReturn(false);
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/PC_EVENING_DRESS")).thenReturn(productMock);

    testling.intercept(contentWriteRequest);
    verify(propertiesMock, times(1)).put(Matchers.anyString(), Matchers.anyObject());
  }

  @Test
  public void testRetrieveProductOrVariant(){
    String aProductExtId = "PC_EVENING_DRESS";
    String aSkuExtId = "PC_EVENING_DRESS-RED-M";
    String unknown = "unknown";
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/" + aProductExtId)).thenReturn(mock(Product.class));
    when(commerceConnection.getCatalogService().findProductVariantById("vendor:///catalog/sku/" + aSkuExtId)).thenReturn(mock(ProductVariant.class));
    when(commerceConnection.getCatalogService().findProductById("vendor:///catalog/product/" + unknown)).thenReturn(null);
    when(commerceConnection.getCatalogService().findProductVariantById("vendor:///catalog/sku/" + unknown)).thenReturn(null);

    Assert.assertNotNull(testling.retrieveProductOrVariant(aProductExtId));
    Assert.assertNotNull(testling.retrieveProductOrVariant(aSkuExtId));
    Assert.assertNull(testling.retrieveProductOrVariant("unkown"));
  }
}
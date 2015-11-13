package com.coremedia.livecontext.ecommerce.ibm.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class AssetUrlProviderImplTest {

  private static final String HTTP_ASSET_URL = "http://url/to/storefront";
  private static final String HTTPS_ASSET_URL = "https://url/to/storefront";
  private static final String SCHEMELESS_ASSET_URL = "//url/to/storefront";
  private static final String VALID_PREFIX_WITHOUT_SLASHES = "ExtendedCatalog/perfectchef";
  private static final String VALID_PREFIX_LEADING_SLASH = "/ExtendedCatalog/perfectchef";
  private static final String VALID_PREFIX_TRAILED_SLASH = "ExtendedCatalog/perfectchef/";
  private static final String VALID_PREFIX_TRAILED_AND_LEADING_SLASH = "/ExtendedCatalog/perfectchef/";

  private AssetUrlProviderImpl testling = new AssetUrlProviderImpl();
  private String productImageSegment = "product/url";
  private String productImageSegmentWithLeadingSlash = VALID_PREFIX_TRAILED_AND_LEADING_SLASH + productImageSegment; // happens when Search based REST handlers are used


  @Before
  public void setUp() {
    Commerce.setCurrentConnection(new BaseCommerceConnection());
    testling.setCmsHost("localhost");
    testling.setCommercePreviewUrl("//preview/url");
  }

  //TEST CHECK STATE
  @Test (expected = IllegalStateException.class)
  public void whenHostIsNull_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl(null);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    testling.getImageUrl(productImageSegment);
  }


  @Test (expected = IllegalStateException.class)
  public void whenHostIsEmpty_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl("");
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    testling.getImageUrl(productImageSegment);
  }

  @Test (expected = IllegalStateException.class)
  public void whenHostIsBlank_IllegalArgumentExceptionIsExpected() {
    testling.setCommerceProductionUrl("    ");
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    testling.getImageUrl(productImageSegment);
  }

  @Test (expected = IllegalStateException.class)
  public void whenAPathPrefixShouldBeAppendedButIsNotGiven_AnIllegalStateExceptionMustBeThrown() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(null);

    testling.getImageUrl(productImageSegment, true);
  }

  //TEST CHECK LOGIC - Without path prefix
  @Test
  public void whenAValidHostAndImageUrlIsGiven_AValidURLMustBeBuild() {
    testling.setCommerceProductionUrl(HTTP_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);
    Assert.assertEquals("http://url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenAnInvalidProductImageUrlIsGiven_theUrlIsNull() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    assertNull(testling.getImageUrl("  "));
  }

  @Test
  public void whenAHttpAssetUrlIsGiven_AHttpUrlMustBeBuild() {
    testling.setCommerceProductionUrl(HTTP_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    Assert.assertEquals("http://url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenAHttpsAssetUrlIsGiven_AHttpsUrlMustBeBuild() {
    testling.setCommerceProductionUrl(HTTPS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    Assert.assertEquals("https://url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenASchemelessAssetUrlIsGiven_ASchemelessUrlMustBeBuild() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    Assert.assertEquals("//url/to/storefront/product/url", testling.getImageUrl(productImageSegment));
  }

  @Test
  public void whenAnAbsoluteAssetUrlIsGiven_ASchemelessURLMustBeReturned() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    Assert.assertEquals("//url/to/storefront/product/url", testling.getImageUrl("http://url/to/storefront/product/url"));
  }

  //TEST CHECK LOGIC - With path prefix
  @Test
  public void whenAPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_WITHOUT_SLASHES);

    Assert.assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void whenALeadingSlashPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_LEADING_SLASH);

    Assert.assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void whenAServerRelativePathIsGiven_TheURLMustNotContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_LEADING_SLASH);

    Assert.assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegmentWithLeadingSlash, true));
  }

  @Test
  public void whenATrailedSlashPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_SLASH);

    Assert.assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }

  @Test
  public void whenASlashesPathPrefixShouldBeAppendedAndIsGiven_TheURLMustContainThePathPrefix() {
    testling.setCommerceProductionUrl(SCHEMELESS_ASSET_URL);
    testling.setCatalogPathPrefix(VALID_PREFIX_TRAILED_AND_LEADING_SLASH);

    Assert.assertEquals("//url/to/storefront/ExtendedCatalog/perfectchef/product/url", testling.getImageUrl(productImageSegment, true));
  }
}

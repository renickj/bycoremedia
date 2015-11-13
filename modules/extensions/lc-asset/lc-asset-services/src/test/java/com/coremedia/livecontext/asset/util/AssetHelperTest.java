package com.coremedia.livecontext.asset.util;

import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructService;
import com.coremedia.cap.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.xmlrepo.XmlUapiConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AssetHelperTest.AssetTestConfiguration.class)
public class AssetHelperTest {

  static final String CONTENT_XML_URI = "classpath:/com/coremedia/livecontext/ecommerce/asset/assetRepository.xml";

  static final List<String> AB_LIST = Arrays.asList("ibm:///catalog/product/A", "ibm:///catalog/sku/B");
  static final List<String> ACD_LIST = Arrays.asList("ibm:///catalog/product/A", "ibm:///catalog/sku/C", "ibm:///catalog/sku/D");
  static final List<String> EF_LIST = Arrays.asList("ibm:///catalog/product/E", "ibm:///catalog/sku/F");
  static final List<String> EMPTY_LIST = Collections.emptyList();

  @Inject
  private ContentRepository contentRepository;
  @Inject
  private StructService structService;

  private Struct originStruct;
  private Content content;

  @Import(XmlRepoConfiguration.class)
  @Configuration
  @ImportResource(value = "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml",
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  static class AssetTestConfiguration {
    @Bean
    XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_XML_URI);
    }

  }

  // --- setup ------------------------------------------------------
  @Before
  public void setup() {
    content = contentRepository.getContent("2");
    originStruct = content.getStruct("localSettings");
    setupOriginStruct(false, null, null);
  }

  public void setupContentForLocalSettingsNull() {
    content = contentRepository.getContent("4");
    originStruct = content.getStruct("localSettings");
  }

  @Test
  public void updateStructForExternalIdsCase05Test() {
    List<String> newOriginProducts = EMPTY_LIST;

    resetOriginStruct();

    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);
    Struct commerceStruct = StructUtil.getSubstruct(updatedStruct, AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertTrue(commerceStruct == null);
  }

  @Test
  public void updateStructForExternalIdsCase06Test() {
    List<String> newOriginProducts = AB_LIST;

    resetOriginStruct();

    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);
    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertNotNull(commerceStruct);
    assertEquals(true, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));

    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsNoLocalSettingsCase06Test() {
    setupContentForLocalSettingsNull();
    List<String> newOriginProducts = AB_LIST;

    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);
    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertNotNull(commerceStruct);
    assertEquals(true, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));

    assertEquals(1, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase07Test() {
    Boolean inherit = true;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = AB_LIST;
    List<String> newOriginProducts = ACD_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);
    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertNotNull(commerceStruct);
    assertEquals(true, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase08Test() {
    Boolean inherit = true;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = AB_LIST;
    List<String> newOriginProducts = EMPTY_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = StructUtil.getSubstruct(updatedStruct, AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertTrue(commerceStruct == null);
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(7, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase09Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = ACD_LIST;
    List<String> newOriginProducts = EF_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(false, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(oldProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase10Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = ACD_LIST;
    List<String> newOriginProducts = EMPTY_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(false, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(oldProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase11Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = EMPTY_LIST;
    List<String> newOriginProducts = EF_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(true, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase12Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = EMPTY_LIST;
    List<String> oldProducts = ACD_LIST;
    List<String> newOriginProducts = EF_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(false, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(oldProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase13Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = EMPTY_LIST;
    List<String> oldProducts = ACD_LIST;
    List<String> newOriginProducts = EF_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(false, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(oldProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase14Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = EMPTY_LIST;
    List<String> newOriginProducts = EMPTY_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(false, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(EMPTY_LIST, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(EMPTY_LIST, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertNotNull(StructUtil.getSubstruct(updatedStruct, "focusArea"));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateStructForExternalIdsCase15Test() {
    Boolean inherit = false;
    List<String> oldOriginProducts = EMPTY_LIST;
    List<String> oldProducts = EMPTY_LIST;
    List<String> newOriginProducts = EF_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureForExternalIds(content, newOriginProducts, contentRepository);

    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);
    assertNotNull(commerceStruct);
    assertEquals(true, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(newOriginProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  @Test
  public void updateCMPictureOnBlobDeleteTest() {
    Boolean inherit = true;
    List<String> oldOriginProducts = AB_LIST;
    List<String> oldProducts = EF_LIST;

    setupOriginStruct(inherit, oldOriginProducts, oldProducts);
    Struct updatedStruct = AssetHelper.updateCMPictureOnBlobDelete(content);
    Struct commerceStruct = updatedStruct.getStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertNotNull(updatedStruct);
    assertEquals(false, commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(Collections.<String>emptyList(), commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(Collections.<String>emptyList(), commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertEquals(8, updatedStruct.getProperties().size());

    resetOriginStruct();

    updatedStruct = AssetHelper.updateCMPictureOnBlobDelete(content);
    commerceStruct = StructUtil.getSubstruct(updatedStruct, AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertNull(commerceStruct);
    assertEquals(7, updatedStruct.getProperties().size());

    resetOriginStruct();

    setupOriginStruct(false, oldOriginProducts, oldProducts);
    updatedStruct = AssetHelper.updateCMPictureOnBlobDelete(content);
    commerceStruct = StructUtil.getSubstruct(updatedStruct, AssetHelper.COMMERCE_SUBSTRUCT_NAME);

    assertNotNull(commerceStruct);
    assertFalse(commerceStruct.getBoolean(AssetHelper.INHERIT_NAME));
    assertEquals(oldOriginProducts, commerceStruct.getStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME));
    assertEquals(oldProducts, commerceStruct.getStrings(AssetHelper.PRODUCT_LIST_NAME));
    assertEquals(8, updatedStruct.getProperties().size());
  }

  /**
   * Returns a struct with an empty commerce substruct with the given parameters
   * @param inherit The inherit value of the commerce substruct
   * @param originProducts  The product list oroginated from the picture data (XMP products) to be saved in the commerce substruct
   * @param products The current products parameter of the commerce substruct
   */
  private void setupOriginStruct(boolean inherit, List<String> originProducts, List<String> products) {
    StructBuilder originStructBuilder = originStruct.builder();

    if (StructUtil.getSubstruct(originStruct, AssetHelper.COMMERCE_SUBSTRUCT_NAME) != null) {
      originStructBuilder = originStructBuilder.remove(AssetHelper.COMMERCE_SUBSTRUCT_NAME); // step 1 of clear struct
      originStruct = originStructBuilder.build();// step 2 of clear struct
    }

    Struct commerceStruct = structService.createStructBuilder().build();
    StructBuilder commerceStructBuilder = commerceStruct.builder();
    commerceStructBuilder = commerceStructBuilder.declareBoolean(AssetHelper.INHERIT_NAME, inherit);
    commerceStructBuilder = commerceStructBuilder.declareStrings(AssetHelper.ORIGIN_PRODUCT_LIST_NAME, Integer.MAX_VALUE, originProducts);
    commerceStructBuilder = commerceStructBuilder.declareStrings(AssetHelper.PRODUCT_LIST_NAME, Integer.MAX_VALUE, products);
    commerceStruct = commerceStructBuilder.build();

    originStruct = originStructBuilder.declareStruct(AssetHelper.COMMERCE_SUBSTRUCT_NAME, commerceStruct).build();
    content.checkOut();
    content.set("localSettings", originStruct);
    content.checkIn();
  }

  /**
   * Removes the commerce substruct from the origin struct
   */
  private void resetOriginStruct() {
    StructBuilder originStructBuilder = originStruct.builder();

    if (StructUtil.getSubstruct(originStruct, AssetHelper.COMMERCE_SUBSTRUCT_NAME) != null) {
      originStructBuilder = originStructBuilder.remove(AssetHelper.COMMERCE_SUBSTRUCT_NAME); // step 1 of clear struct
      originStruct = originStructBuilder.build();// step 2 of clear struct
    }
    content.checkOut();
    content.set("localSettings", originStruct);
    content.checkIn();
  }

}

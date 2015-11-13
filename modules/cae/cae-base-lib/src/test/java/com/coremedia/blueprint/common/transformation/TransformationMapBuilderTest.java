package com.coremedia.blueprint.common.transformation;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.contentbeans.CMPictureImpl;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.xmlrepo.XmlUapiConfig;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.activation.MimeType;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Test the {@link com.coremedia.blueprint.common.transformation.TransformationMapBuilder}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {TransformationMapBuilderTest.LocalConfig.class, XmlRepoConfiguration.class})
public class TransformationMapBuilderTest {

  public static final String TRANSFORMATION_LARGE_4_X_3 = "large4x3";
  public static final String TRANSFORMATION_LARGE_1_X_1 = "large1x1";
  public static final String TRANSFORMATION_5_X_2 = "5x2";
  public static final String TRANSFORMATION_UNCROPPED = "uncropped";
  TransformationMapBuilder testling;

  CMPictureImpl cmPicture;
  @Mock
  Blob blob;
  @Mock
  MimeType mimeType;
  @Mock
  Struct settingsStruct;
  @Mock
  Struct transformsStruct;
  @Mock
  SettingsService settingsService;
  String mimeTypeString = "image/gif";

  @Inject
  private WebApplicationContext webApplicationContext;

  private Map<String, String> defaults = ImmutableMap.of(
          "a", "2x2",
          TRANSFORMATION_5_X_2, "c;x=0;y=40;w=300;h=120",
          TRANSFORMATION_LARGE_1_X_1, "c;x=50;y=0;w=200;h=200",
          TRANSFORMATION_UNCROPPED, "g;a=1",
          TRANSFORMATION_LARGE_4_X_3, "c;x=17;y=0;w=266;h=200");

  @Before
  public void setUp() {

    MockitoAnnotations.initMocks(this);
    testling = webApplicationContext.getBean("transformationMapBuilder", TransformationMapBuilder.class);

    /**
     * prepare cmPicture
     */
    cmPicture = new CustomPictureImpl(blob, settingsStruct);
    cmPicture.setTransformationMapBuilder(testling);
    cmPicture.setSettingsService(settingsService);
    when(settingsService.setting(eq("transforms"), eq(Struct.class), any(Content.class))).thenReturn(transformsStruct);

    Map<String, Object> transformationStruct = Maps.newHashMap();
    transformationStruct.put("a", "2x2");
    when(transformsStruct.getProperties()).thenReturn(transformationStruct);
    when(blob.getContentType()).thenReturn(mimeType);
    when(mimeType.toString()).thenReturn(mimeTypeString);

    when(blob.getInputStream()).thenReturn(getClass().getResourceAsStream("/com/coremedia/blueprint/common/transformation/coremedia_logo.gif"));
  }

  /**
   * Test that {@link com.coremedia.blueprint.common.transformation.TransformationMapBuilder#build(com.coremedia.blueprint.common.contentbeans.CMMedia, java.util.Map)}
   * merges with transformations present in {@link com.coremedia.blueprint.common.contentbeans.CMPicture#getSettings()}
   */
  @Test
  public void testMerge() {

    Map<String, String> transformedMap = cmPicture.getTransformMap();

    Map<String, String> expectedMap = Maps.newHashMap(defaults);
    assertEquals("Transformations differ: ", expectedMap, transformedMap);
  }

  @Test
  public void testDimensionSettingsHigh() {

    Map<String, String> expectedMap = ImmutableMap.of(
            "a", "2x2",
            TRANSFORMATION_5_X_2, "c;x=0;y=56;w=300;h=120",
            TRANSFORMATION_LARGE_1_X_1, "c;x=80;y=0;w=200;h=200",
            TRANSFORMATION_UNCROPPED, "g;a=1",
            TRANSFORMATION_LARGE_4_X_3, "c;x=27;y=0;w=266;h=200");

    testDimensionSettings(0.8f, 0.7f, expectedMap);
  }

  @Test
  public void testDimensionSettingsLow() {

    Map<String, String> expectedMap = ImmutableMap.of(
            "a", "2x2",
            TRANSFORMATION_5_X_2, "c;x=0;y=32;w=300;h=120",
            TRANSFORMATION_LARGE_1_X_1, "c;x=20;y=0;w=200;h=200",
            TRANSFORMATION_UNCROPPED, "g;a=1",
            TRANSFORMATION_LARGE_4_X_3, "c;x=7;y=0;w=266;h=200");

    testDimensionSettings(0.2f, 0.4f, expectedMap);
  }

  @Test
  public void testDimensionSettingsMax() {

    Map<String, String> expectedMap = ImmutableMap.of(
            "a", "2x2",
            TRANSFORMATION_5_X_2, "c;x=0;y=80;w=300;h=120",
            TRANSFORMATION_LARGE_1_X_1, "c;x=100;y=0;w=200;h=200",
            TRANSFORMATION_UNCROPPED, "g;a=1",
            TRANSFORMATION_LARGE_4_X_3, "c;x=34;y=0;w=266;h=200");

    testDimensionSettings(Transformation.H_ALIGN_RIGHT, Transformation.V_ALIGN_BOTTOM, expectedMap);
  }

  @Test
  public void testDimensionSettingsMin() {

    Map<String, String> expectedMap = ImmutableMap.of(
            "a", "2x2",
            TRANSFORMATION_5_X_2, "c;x=0;y=0;w=300;h=120",
            TRANSFORMATION_LARGE_1_X_1, "c;x=0;y=0;w=200;h=200",
            TRANSFORMATION_UNCROPPED, "g;a=1",
            TRANSFORMATION_LARGE_4_X_3, "c;x=0;y=0;w=266;h=200");

    testDimensionSettings(Transformation.H_ALIGN_LEFT, Transformation.V_ALIGN_TOP, expectedMap);
  }

  /**
   * Test that {@link com.coremedia.blueprint.common.transformation.TransformationMapBuilder#build(com.coremedia.blueprint.common.contentbeans.CMMedia, java.util.Map)}
   * doesn't overwrite transformations present in {@link com.coremedia.blueprint.common.contentbeans.CMPicture#getSettings()}
   */
  @Test
  public void testImageSettingOverwritesTransformationsConfig() {

    //make sure that transformationsStruct returned by CMPictureImpl contains a key "b" that won't be overwritten.
    Map<String, Object> transformationStruct = Maps.newHashMap();
    transformationStruct.put("a", "2x2");
    transformationStruct.put(TRANSFORMATION_LARGE_4_X_3, "4x3");
    when(transformsStruct.getProperties()).thenReturn(transformationStruct);

    Map<String, String> transformedMap = cmPicture.getTransformMap();

    Map<String, String> expectedMap = Maps.newHashMap(defaults);
    //this setting was returned by the cmPicture
    expectedMap.put(TRANSFORMATION_LARGE_4_X_3, "4x3");

    assertEquals("Transformations differ: ", expectedMap, transformedMap);
  }

  /**
   * Test that {@link com.coremedia.blueprint.common.transformation.TransformationMapBuilder#build(com.coremedia.blueprint.common.contentbeans.CMMedia, java.util.Map)}
   * overwrites transformations present in {@link com.coremedia.blueprint.common.contentbeans.CMPicture#getSettings()}
   */
  @Test
  public void testTransformationConfigOverwritesImageSetting() {

    //make sure that transformationsStruct returned by CMPictureImpl contains a key "b" that won't be overwritten.
    Map<String, Object> transformationStruct = Maps.newHashMap();
    transformationStruct.put("a", "2x2");
    transformationStruct.put(TRANSFORMATION_LARGE_4_X_3, "4x3");
    when(transformsStruct.getProperties()).thenReturn(transformationStruct);

    //set configuration for large4x3 to overwrite image settings
    List<Transformation> transformationList = testling.getDefaultTransformations();
    for (Transformation transformation : transformationList) {
      if (TRANSFORMATION_LARGE_4_X_3.equals(transformation.getName())) {
        transformation.setOverwritePictureSettings(true);
      }
    }

    Map<String, String> transformedMap = cmPicture.getTransformMap();

    Map<String, String> expectedMap = Maps.newHashMap(defaults);
    //this setting must have been overwritten by TransformationMapBuilder
    expectedMap.put(TRANSFORMATION_LARGE_4_X_3, "c;x=17;y=0;w=266;h=200");

    assertEquals("Transformations differ: ", expectedMap, transformedMap);
  }

  /**
   * Test that {@link com.coremedia.blueprint.common.transformation.TransformationMapBuilder#build(com.coremedia.blueprint.common.contentbeans.CMMedia, java.util.Map)}
   * handles values for jpegQuality correctly.
   */
  @Test
  public void testJpegQuality() {

    //set configuration for jpeg quality
    List<Transformation> transformationList = testling.getDefaultTransformations();
    for (Transformation transformation : transformationList) {
      if (TRANSFORMATION_LARGE_4_X_3.equals(transformation.getName())) {
        transformation.setJpegQuality(1.0F);
      }
    }

    Map<String, String> transformedMap = cmPicture.getTransformMap();

    Map<String, String> expectedMap = Maps.newHashMap(defaults);
    expectedMap.put(TRANSFORMATION_LARGE_4_X_3, "c;x=17;y=0;w=266;h=200/djq;q=1.0");

    assertEquals("Transformations differ: ", expectedMap, transformedMap);
  }

  //====================================================================================================================

  private void testDimensionSettings(float horizontal, float vertical, Map<String, String> expectedMap) {

    //set horizontal and vertical for all given transformations.
    List<Transformation> transformationList = testling.getDefaultTransformations();
    for (Transformation transformation : transformationList) {
      transformation.setHorizontalAlign(horizontal);
      transformation.setVerticalAlign(vertical);
    }

    Map<String, String> transformedMap = cmPicture.getTransformMap();

    assertEquals("Transformations differ: ", expectedMap, transformedMap);
  }

  /**
   * Local class needed for testing
   * It wouldn't be feasible to use a mock since a call to {@link #getTransformMap()} should go to the real implementation.
   */
  private class CustomPictureImpl extends CMPictureImpl {

    private Blob blob;
    private Struct localSettings;

    public CustomPictureImpl(Blob blob, Struct struct) {
      this.blob = blob;
      this.localSettings = struct;
    }

    @Override
    public Blob getData() {
      return blob;
    }

    @Override
    public Struct getLocalSettings() {
      return localSettings;
    }

    @Override
    public String toString() {
      return "CustomPictureImpl{" +
              "blob=" + blob +
              ", localSettings=" + localSettings +
              '}';
    }
  }

  @Configuration
  @ImportResource(value = {
          "classpath:/framework/spring/mediatransform.xml",
          "classpath:/com/coremedia/cae/uapi-services.xml"
  },
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig implements BeanPostProcessor {
    @Bean
    public com.coremedia.cap.xmlrepo.XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/blueprint/common/transformation/transformationmapbuilder-test-content.xml");
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
      if (bean instanceof TransformationMapBuilder) {
        // Override transformations for testing purpose
        TransformationMapBuilder transformationMapBuilder = (TransformationMapBuilder) bean;
        transformationMapBuilder.setDefaultTransformations(Arrays.asList(
                new TransformationBuilder().name(TRANSFORMATION_LARGE_4_X_3).widthRatio(4).heightRatio(3).build(),
                new TransformationBuilder().name(TRANSFORMATION_LARGE_1_X_1).widthRatio(1).heightRatio(1).build(),
                new TransformationBuilder().name(TRANSFORMATION_5_X_2).widthRatio(5).heightRatio(2).build(),
                new TransformationBuilder().name(TRANSFORMATION_UNCROPPED).widthRatio(-1).heightRatio(-1).build()
        ));
      }
      return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
      return bean;
    }
  }

  private static class TransformationBuilder {
    private String name;
    private int widthRatio;
    private int heightRatio;

    public TransformationBuilder heightRatio(int heightRatio) {
      this.heightRatio = heightRatio;
      return this;
    }

    public TransformationBuilder name(String name) {
      this.name = name;
      return this;
    }

    public TransformationBuilder widthRatio(int widthRatio) {
      this.widthRatio = widthRatio;
      return this;
    }

    public Transformation build() {
      Transformation transformation = new Transformation();
      transformation.setName(name);
      transformation.setWidthRatio(widthRatio);
      transformation.setHeightRatio(heightRatio);
      return transformation;
    }
  }
}

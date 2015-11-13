package com.coremedia.blueprint.common.transformation;

import org.springframework.beans.factory.annotation.Required;

/**
 * Object represents a Transformation to be executed by the {@link com.coremedia.transform.image.ImageOperations Transformation API}
 */
public class Transformation {

  public static final float V_ALIGN_TOP = 0.0f;
  public static final float V_ALIGN_CENTER = 0.5f;
  public static final float V_ALIGN_BOTTOM = 1.0f;

  public static final float H_ALIGN_LEFT = 0.0f;
  public static final float H_ALIGN_CENTER = 0.5f;
  public static final float H_ALIGN_RIGHT = 1.0f;

  private String name;
  private Integer brightness;
  private Double contrast;
  private Double gamma;
  private Integer heightRatio;
  private Integer widthRatio;
  private Float jpegQuality;
  private float horizontalAlign = H_ALIGN_CENTER;
  private float verticalAlign = V_ALIGN_CENTER;
  private boolean overwritePictureSettings = false;

  /**
   * The name of this transformation.
   */
  @Required
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Set brightness value.
   * Translates to parameter "amount" in
   * {@link com.coremedia.transform.image.ImageOperations#brightness(com.coremedia.transform.image.ImageTransformerState, double, double)}
   */
  public void setBrightness(Integer brightness) {
    this.brightness = brightness;
  }

  public Integer getBrightness() {
    return brightness;
  }

  /**
   * Set contrast for
   * Translates to parameter "amount" in
   * {@link com.coremedia.transform.image.ImageOperations#brightness(com.coremedia.transform.image.ImageTransformerState, double, double)}
   */
  public void setContrast(Double contrast) {
    this.contrast = contrast;
  }

  public Double getContrast() {
    return contrast;
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Set the gamma value.
   * {@link com.coremedia.transform.image.ImageOperations#gamma(com.coremedia.transform.image.ImageTransformerState, double)}
   */
  public void setGamma(Double gamma) {
    this.gamma = gamma;
  }

  public Double getGamma() {
    return gamma;
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Set the height ratio to crop the image to.
   * Is translated by {@link TransformationMapBuilder} to parameter "height" of
   * {@link com.coremedia.transform.image.ImageOperations#crop(com.coremedia.transform.image.ImageTransformerState, int, int, int, int)}
   */
  public void setHeightRatio(Integer heightRatio) {
    this.heightRatio = heightRatio;
  }

  public Integer getHeightRatio() {
    return heightRatio;
  }

  /**
   * Set the width ratio to crop the image to.
   * Is translated by {@link TransformationMapBuilder} to parameter "width" of
   * {@link com.coremedia.transform.image.ImageOperations#crop(com.coremedia.transform.image.ImageTransformerState, int, int, int, int)}
   */
  public void setWidthRatio(Integer widthRatio) {
    this.widthRatio = widthRatio;
  }

  public Integer getWidthRatio() {
    return widthRatio;
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Set to true if this setting should overwrite the concrete picture settings.
   * Default: {@link Boolean#FALSE}
   */
  public void setOverwritePictureSettings(boolean overwritePictureSettings) {
    this.overwritePictureSettings = overwritePictureSettings;
  }

  public boolean isOverwritePictureSettings() {
    return overwritePictureSettings;
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Set the jpeg quality in case the transformed image is a JPEG.
   * {@link com.coremedia.transform.image.ImageOperations#jpeg(com.coremedia.transform.image.ImageTransformerState, Float)}
   */
  public void setJpegQuality(Float jpegQuality) {
    this.jpegQuality = jpegQuality;
  }

  public Float getJpegQuality() {
    return jpegQuality;
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Set vertical alignment of the cropped area. The value must be between 0.0 and 1.0 where
   * <ul>
   *   <li>0.0 is aligned to the top ({@link #V_ALIGN_TOP})</li>
   *   <li>0.5 is centered ({@link #V_ALIGN_CENTER})</li>
   *   <li>1.0 is aligned to the bottom ({@link #V_ALIGN_BOTTOM})</li>
   * </ul>
   * Default: {@link #V_ALIGN_CENTER}: {@value #V_ALIGN_CENTER}
   */
  public void setVerticalAlign(float verticalAlign) {
    this.verticalAlign = verticalAlign;
  }

  public float getVerticalAlign() {
    return verticalAlign;
  }

  /**
   * Set horizontal alignment of the cropped area. The value must be between 0.0 and 1.0 where
   * <ul>
   *   <li>0.0 is aligned to the left ({@link #H_ALIGN_LEFT})</li>
   *   <li>0.5 is centered ({@link #H_ALIGN_CENTER})</li>
   *   <li>1.0 is aligned to the right ({@link #H_ALIGN_RIGHT})</li>
   * </ul>
   * Default: {@link #H_ALIGN_CENTER}: {@value #H_ALIGN_CENTER}
   */
  public void setHorizontalAlign(float horizontalAlign) {
    this.horizontalAlign = horizontalAlign;
  }

  public float getHorizontalAlign() {
    return horizontalAlign;
  }

}

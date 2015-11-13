package com.coremedia.blueprint.common.transformation;

import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.blueprint.base.cae.util.ImageDimensionUtil;
import com.coremedia.cap.common.Blob;
import com.coremedia.image.ImageDimensionsExtractor;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * Build transformations from it's configuration and the settings of the concrete {@link CMMedia media object}
 * <br/>
 * This class is used every time an image is requested with a transformation.
 * <br/>
 * {@link com.coremedia.blueprint.common.contentbeans.CMPicture Images} may not contain the necessary struct that describes a transformation
 * <br/>
 * Configured {@link com.coremedia.blueprint.common.transformation.Transformation Transformations} {@link Transformation#isOverwritePictureSettings() may overwrite }
 * {@link com.coremedia.blueprint.common.contentbeans.CMMedia#getTransformMap() transformations} of an image.
 */
public class TransformationMapBuilder {

  private List<Transformation> defaultTransformations;
  private ImageDimensionsExtractor imageDimensionsExtractor;

  public void setDefaultTransformations(List<Transformation> defaultTransformations) {
    this.defaultTransformations = defaultTransformations;
  }

  /**
   * Build map for media / configuration / settings combination.
   *
   * @param toBuildFor media object to generate transformations for
   * @param transformations transformations of media object
   *
   * @return transformed settings map
   */
  public Map<String, String> build(CMMedia toBuildFor, Map<String,String> transformations) {
    ImageDimensions dimensions = getDimensions(toBuildFor);
    for (Transformation transformation : defaultTransformations) {
      String name = transformation.getName();
      if (!transformations.containsKey(name) || transformation.isOverwritePictureSettings()) {
        // if this transformation is already in the configuration struct
        // and it is set NOT to overwrite picture settings, continue to the next one
        String transformMapEntry = buildTransformation(dimensions, transformation);
        transformations.put(name, transformMapEntry);
      }
    }
    return transformations;
  }


  //--------------------------------------------------------------------------------------------------------------------

  @VisibleForTesting
  List<Transformation> getDefaultTransformations() {
    return defaultTransformations;
  }


  //====================================================================================================================

  private String buildTransformation(ImageDimensions dimensions, Transformation transformation) {
    StringBuilder transformMapEntry = new StringBuilder();

    appendBrightnessAndContrast(transformation, transformMapEntry);
    appendGamma(transformation, transformMapEntry);
    appendCoordinates(dimensions, transformation, transformMapEntry);
    appendJpegQuality(transformation, transformMapEntry);

    return transformMapEntry.toString();
  }

  private void appendCoordinates(ImageDimensions dimensions, Transformation transformation, StringBuilder transformMapEntry) {
    int ratioX = transformation.getWidthRatio();
    int ratioY = transformation.getHeightRatio();

    if (ratioX == -1 || ratioY == -1 || !dimensions.isParseable()) {
      // the picture must be not transformed, or its dimensions are unknown.
      // nop operation: gamma adjustment with amount 1.0
      transformMapEntry.append("g;a=1");
      return;
    }

    double ratioImage = (double)dimensions.getWidth() / (double)dimensions.getHeight();
    double ratioPattern = (double)ratioX / (double)ratioY;
    int targetPosX = 0, targetWidth;
    int targetPosY = 0, targetHeight;

    if (ratioImage < ratioPattern) {
      // use width as maximized dimension
      targetWidth = dimensions.getWidth();
      targetHeight = (targetWidth * ratioY) / ratioX;

      targetPosY = Math.round((dimensions.getHeight() - targetHeight) * transformation.getVerticalAlign());
    } else {
      // use height as maximized dimension
      targetHeight = dimensions.getHeight();
      targetWidth = (targetHeight * ratioX) / ratioY;

      targetPosX = Math.round((dimensions.getWidth() - targetWidth) * transformation.getHorizontalAlign());
    }

    /**
     * {@link com.coremedia.transform.image.ImageOperations#crop(com.coremedia.transform.image.ImageTransformerState, int, int, int, int)}
     */
    transformMapEntry.append(format("c;x=%s;y=%s;w=%s;h=%s",targetPosX,targetPosY,targetWidth,targetHeight));
  }

  private void appendJpegQuality(Transformation transformation, StringBuilder transformMapEntry) {
    /**
     * {@link com.coremedia.transform.image.ImageOperations#jpeg(com.coremedia.transform.image.ImageTransformerState, Float)}
     */
    if(transformation.getJpegQuality() != null) {
      transformMapEntry.append(format("/djq;q=%s", transformation.getJpegQuality()));
    }
  }

  private void appendGamma(Transformation transformation, StringBuilder transformMapEntry) {
    /**
     * {@link com.coremedia.transform.image.ImageOperations#gamma(com.coremedia.transform.image.ImageTransformerState, double)}
     */
    if(transformation.getGamma() != null) {
      transformMapEntry.append(format("/g;a=%s",transformation.getGamma()));
    }
  }

  private void appendBrightnessAndContrast(Transformation transformation, StringBuilder transformMapEntry) {
    /**
     * {@link com.coremedia.transform.image.ImageOperations#brightness(com.coremedia.transform.image.ImageTransformerState, double, double)}
     */
    if(transformation.getBrightness() != null || transformation.getContrast() != null) {
      transformMapEntry.append("/b");
      if(transformation.getBrightness() != null) {
        transformMapEntry.append(format(";a=%s",transformation.getBrightness()));
      }
      if(transformation.getContrast() != null) {
        transformMapEntry.append(format(";c=%s",transformation.getContrast()));
      }
    }
  }

  /**
   * Retrieves the dimensions of the media to transform
   * @param media the media
   * @return the dimensions as Pair<Width, Height>
   */
  private ImageDimensions getDimensions(CMMedia media) {
    Blob blob = (Blob) media.getData();

    return new LazyImageDimensions(blob);
  }

  interface ImageDimensions {
    boolean isParseable();
    int getWidth();
    int getHeight();
  }

  class LazyImageDimensions implements ImageDimensions {
    final Blob blob;
    Pair<Integer, Integer> extracted;

    LazyImageDimensions(Blob blob) {
      this.blob = blob;
    }

    Pair<Integer, Integer> getExtracted() {
      if(extracted == null) {
        extracted = ImageDimensionUtil.extractDimensions(imageDimensionsExtractor, blob);
      }
      return extracted;
    }

    @Override
    public boolean isParseable() {
      return getExtracted().getLeft() != null && getExtracted().getRight() != null;
    }

    @Override
    public int getWidth() {
      return getExtracted().getLeft();
    }
    @Override
    public int getHeight() {
      return getExtracted().getRight();
    }
  }

  @Required
  public void setImageDimensionsExtractor(ImageDimensionsExtractor imageDimensionsExtractor) {
    this.imageDimensionsExtractor = imageDimensionsExtractor;
  }

  public ImageDimensionsExtractor getImageDimensionsExtractor() {
    return imageDimensionsExtractor;
  }
}

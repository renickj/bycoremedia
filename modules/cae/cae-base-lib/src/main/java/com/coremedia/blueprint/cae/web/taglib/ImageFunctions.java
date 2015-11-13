package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMImageMap;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.common.Blob;
import com.coremedia.image.ImageDimensionsExtractor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions.Dimension;

/**
 * A adapter for {@link com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions} used in JSP Taglibs.
 * For Freemarker use {@link BlueprintFreemarkerFacade} instead.
 */
public final class ImageFunctions {

  static final ImageDimensionsExtractor IMAGE_DIMENSIONS_EXTRACTOR = new ImageDimensionsExtractor();

  // static class
  private ImageFunctions() {
  }

  /**
   * Provides the URLs for all configured image resolutions for the given image and transformation name. An example:
   * A transformation "landscape_ratio4x3" has the following resolutions configured for a responsive layout: 800x600,
   * 400x300, 200x150, 100x75. This function would then return a map with 4 entries where the keys are the resolutions
   * and the values the respective URLs.
   *
   * @param picture            the picture
   * @param page               the page
   * @param transformationName a transformation name
   * @param settingsService    the SettingsService
   * @param request            the http servlet request
   * @param response           the http servlet response
   * @return a {@link Map} where keys are width/height dimensions and the values are the respective URLs
   * @deprecated Use {@link com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade#responsiveImageLinksData(com.coremedia.blueprint.common.contentbeans.CMPicture, com.coremedia.blueprint.common.contentbeans.Page, java.util.List)}
   */
  public static Map<Dimension, String> responsiveImageLinks(CMPicture picture,
                                                            Page page,
                                                            String transformationName,
                                                            SettingsService settingsService,
                                                            ServletRequest request,
                                                            ServletResponse response) {
    if (page == null) {
      throw new IllegalArgumentException("page must not be null");
    }

    Blob blob = picture.getTransformedData(transformationName);
    return com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions.responsiveImageLinks(blob, page, transformationName, settingsService, request, response);
  }

  /**
   * Return list of area configurations with the 'coords' attribute being transformed according to the image map's
   * picture transformations. If cropping is disabled, an empty list is returned.
   */
  public static List<Map<String, Object>> responsiveImageMapAreas(CMImageMap imageMap,
                                                                  ImageDimensionsExtractor imageDimensionsExtractor,
                                                                  List<String> transformationNames) {
    List<Map<String, Object>> result = Collections.emptyList();
    final CMPicture picture = imageMap.getPicture();

    if (picture != null) {
      // determine which transformations to apply
      final Map<String, String> transformMap = picture.getTransformMap();
      final List<Map<String, Object>> imageMapAreas = imageMap.getImageMapAreas();

      result = com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions.responsiveImageMapAreas(picture.getData(), picture.getDisableCropping(), imageMapAreas, transformMap, imageDimensionsExtractor, transformationNames);
    }

    return result;
  }

  public static List<Map<String, Object>> responsiveImageMapAreasAll(CMImageMap imageMap) {
    return responsiveImageMapAreas(imageMap, Collections.<String>emptyList());
  }

  public static List<Map<String, Object>> responsiveImageMapAreas(CMImageMap imageMap,
                                                                  List<String> transformationNames) {
    return responsiveImageMapAreas(imageMap, IMAGE_DIMENSIONS_EXTRACTOR, transformationNames);
  }

  public static Map<String, Object> responsiveImageMapAreaData(Map<String, Object> coords) {
    return com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions.responsiveImageMapAreaData(coords);
  }

  public static String uncroppedImageLink(CMPicture picture, ServletRequest request, ServletResponse response) {
    if (picture == null) {
      throw new IllegalArgumentException("picture must not be null");
    }

    Blob blob = picture.getData();
    return com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions.uncroppedImageLink(blob, request, response);
  }

}

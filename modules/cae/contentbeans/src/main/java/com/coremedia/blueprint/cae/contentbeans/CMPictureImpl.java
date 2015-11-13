package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.transformation.TransformationMapBuilder;
import com.coremedia.cap.struct.Struct;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.Entry;

/**
 * Generated extension class for immutable beans of document type "CMPicture".
 */
public class CMPictureImpl extends CMPictureBase {
  private static final String TRANSFORMS = "transforms";
  private static final String DISABLE_CROPPING = "disableCropping";

  private TransformationMapBuilder transformationMapBuilder;

  /*
   * Add additional methods here.
   * Add them to the interface {@link com.coremedia.blueprint.common.contentbeans.CMPicture} to make them public.
   */
  @Required
  public void setTransformationMapBuilder(TransformationMapBuilder transformationMapBuilder) {
    this.transformationMapBuilder = transformationMapBuilder;
  }

  /**
   * Overrides the CMTeasable feature for convenience:
   * If this CMPicture document has no delegate teaser picture, it is
   * considered to be its own teaser picture.
   *
   * @return the teasable image or "this" as fallback.
   */
  @Override
  public CMPicture getPicture() {
    CMPicture picture = super.getPicture();
    return picture==null ? this : picture;
  }

  /**
   * Override the method to handle images, which does not have a transformation already.
   * @return a map of transformations, merged from image settings and {@link TransformationMapBuilder} service
   */
  @Override
  public Map<String, String> getTransformMap() {
    // We need a *mutable* map for the transformationMapBuilder
    Map<String, String> transformations = new HashMap<>();
    Struct transforms = getSettingsService().setting(TRANSFORMS, Struct.class, getContent());
    if (transforms != null) {
      Map<String, Object> structMap = transforms.getProperties();
      for (Entry<String, Object> entry : structMap.entrySet()) {
        if (entry.getValue() != null) {
          transformations.put(entry.getKey(), entry.getValue().toString());
        }
      }
    }
    return transformationMapBuilder.build(this, transformations);
  }

  @Override
  public boolean getDisableCropping() {
    return getSettingsService().settingWithDefault(DISABLE_CROPPING, Boolean.class, false, getContent());
  }
}

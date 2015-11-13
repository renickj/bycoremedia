package com.coremedia.blueprint.common.contentbeans;

import java.util.List;

public interface CMImageMap extends CMTeaser {

  /**
   * Returns a {@link java.util.List} of all specified hot zones of the image map.
   * @return  a list of all specified hot zones
   */
  public List<java.util.Map<String, Object>> getImageMapAreas();
}

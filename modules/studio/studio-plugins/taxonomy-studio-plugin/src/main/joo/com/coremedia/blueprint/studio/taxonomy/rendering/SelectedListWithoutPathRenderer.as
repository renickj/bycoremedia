package com.coremedia.blueprint.studio.taxonomy.rendering {

/**
 * The renderer used for the taxonomy filter in the library.
 */
public class SelectedListWithoutPathRenderer extends SelectedListRenderer {

  public function SelectedListWithoutPathRenderer(nodes:Array, componentId:String, scrolling:Boolean) {
    super(nodes, componentId, scrolling);
  }


  /**
   * Disable the path rendering.
   * @return
   */
  override protected function isPathRenderingEnabled():Boolean {
    return false;
  }
}
}
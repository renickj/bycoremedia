package com.coremedia.blueprint.common.layout;

import java.util.List;

/**
 * A PageGrid consists of placements arranged like an HTML table
 */
public interface PageGrid {

  /**
   * Returns the pagegrid's rows
   */
  List<PageGridRow> getRows();

  /**
   * Returns the number of columns of the pagegrid
   */
  int getNumcols();

  /**
   *
   * @return name of the css class of the pagegrid
   */
  String getCssClassName();

  List<?> getMainItems();

  /**
   * Returns the placement which section document has the given name.
   * @param name The name of the placement.
   */
  PageGridPlacement getPlacementForName(String name);
}

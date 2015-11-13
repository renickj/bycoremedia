package com.coremedia.blueprint.common.layout;

import com.coremedia.blueprint.common.navigation.HasViewTypeName;
import com.coremedia.blueprint.common.navigation.Linkable;

import java.util.List;

/**
 * A PageGridPlacement is a part of a whole PageGrid representing for example the sidebar or main column
 */
public interface PageGridPlacement extends HasViewTypeName, Container<Linkable> {
  /**
   * Retrieves the items of this PageGridPlacement
   */
  List<? extends Linkable> getItems();

  /**
   * Returns the logical name of this placement.
   * <p>
   * "main" is magic for the current content.
   */
  String getName();

  /**
   * Return the "virtual" property name of this placement (used for Studio preview integration).
   */
  String getPropertyName();

  /**
   * Returns the absolute position in the row.
   * <p>
   * Count starts with 1.
   */
  int getCol();

  /**
   * Returns the colspan of this placement.
   * <p>
   * The colspan concept is motivated by the HTML table model,
   * the Blueprint templates map this value to CSS styles, though.
   */
  int getColspan();

  /**
   * Returns the relative (percentage) width of this placement.
   */
  int getWidth();

  /**
   * Returns the number of the row of this placement.
   * @return
   */
  int getNumCols();

}


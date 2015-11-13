package com.coremedia.blueprint.common.layout;

import java.util.List;

/**
 * A PageGridRow contains the placements of a row,
 * without row spanning placements from upper rows.
 */
public interface PageGridRow {
  List<PageGridPlacement> getPlacements();
}

package com.coremedia.blueprint.cae.layout;


import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGrid;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyle;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyleGrid;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.layout.PageGridRow;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.objectserver.dataviews.AssumesIdentity;

import java.util.ArrayList;
import java.util.List;

public class PageGridRowImpl implements PageGridRow, AssumesIdentity {
  private ValidationService<Linkable> validationService;
  private ContentBackedPageGridService contentBackedPageGridService;

  private CMNavigation navigation;
  private int row;


  // --- construction -----------------------------------------------

  public PageGridRowImpl(CMNavigation navigation, int row, ContentBackedPageGridService contentBackedPageGridService, ValidationService<Linkable> validationService) {
    this.navigation = navigation;
    this.row = row;
    this.contentBackedPageGridService = contentBackedPageGridService;
    this.validationService = validationService;
  }

  /**
   * Only for dataviews
   */
  @SuppressWarnings("UnusedDeclaration")
  public PageGridRowImpl() {
  }


  // --- PageGridRow ------------------------------------------------

  @Override
  public List<PageGridPlacement> getPlacements() {
    List<PageGridPlacement> placements = new ArrayList<>();
    ContentBackedStyleGrid styleGrid = getContentBackedPageGrid().getStyleGrid();
    List<ContentBackedStyle> styleRow = styleGrid.getRow(row);
    for (int colIndex=0; colIndex<styleRow.size(); ++colIndex) {
      placements.add(new ContentBeanBackedPageGridPlacement(navigation, row, styleRow.size(), colIndex, contentBackedPageGridService, validationService));
    }
    return placements;
  }


  // --- Dataviews --------------------------------------------------

  @Override
  public boolean equals(Object o) { //NOSONAR: cyclomatic complexity 11 is OK for #equals.
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PageGridRowImpl that = (PageGridRowImpl) o;

    if (row != that.row) {
      return false;
    }
    //noinspection RedundantIfStatement
    if (navigation != null ? !navigation.equals(that.navigation) : that.navigation != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = navigation != null ? navigation.hashCode() : 0;
    result = 31 * result + row;
    return result;
  }

  @Override
  public void assumeIdentity(Object o) {
    PageGridRowImpl other = (PageGridRowImpl) o;
    validationService = other.validationService;
    contentBackedPageGridService = other.contentBackedPageGridService;
    navigation = other.navigation;
    row = other.row;
  }


  // --- internal ---------------------------------------------------

  private ContentBackedPageGrid getContentBackedPageGrid() {
    return contentBackedPageGridService.getContentBackedPageGrid(navigation.getContent());
  }
}

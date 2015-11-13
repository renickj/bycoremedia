package com.coremedia.blueprint.cae.layout;

import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGrid;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridPlacement;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.PageGridConstants;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.layout.PageGridRow;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.objectserver.dataviews.AssumesIdentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PageGridImpl implements PageGrid, AssumesIdentity {

  private ValidationService<Linkable> validationService;
  private ContentBackedPageGridService contentBackedPageGridService;
  private CMNavigation navigation;

  // --- construction -----------------------------------------------

  public PageGridImpl(CMNavigation navigation, ContentBackedPageGridService contentBackedPageGridService, ValidationService<Linkable> validationService) {
    this.navigation = navigation;
    this.contentBackedPageGridService = contentBackedPageGridService;
    this.validationService = validationService;
  }

  /**
   * Only for dataviews
   */
  @SuppressWarnings("UnusedDeclaration")
  public PageGridImpl() {
  }

  // --- PageGrid ---------------------------------------------------

  @Override
  public List<PageGridRow> getRows() {
    List<PageGridRow> result = new ArrayList<>();
    int numRows = getContentBackedPageGrid().getStyleGrid().getNumRows();
    for (int row = 0; row < numRows; ++row) {
      result.add(new PageGridRowImpl(navigation, row, contentBackedPageGridService, validationService));
    }
    return result;
  }

  @Override
  public PageGridPlacement getPlacementForName(String name) {
    List<PageGridRow> rows = getRows();
    for (PageGridRow row : rows) {
      List<PageGridPlacement> placements = row.getPlacements();
      for (PageGridPlacement placement : placements) {
        if(placement.getName().equals(name)) {
          return placement;
        }
      }
    }
    return null;
  }

  @Override
  public int getNumcols() {
    return getContentBackedPageGrid().getStyleGrid().getNumColumns();
  }

  @Override
  public String getCssClassName() {
    return getContentBackedPageGrid().getCssClassName();
  }


  // --- features ---------------------------------------------------

  /**
   * Useful to implement FeedSource.
   *
   * @return the items of the "main" placement
   */
  @Override
  public List getMainItems() {
    ContentBackedPageGridPlacement placement = getContentBackedPageGrid().getPlacements().get(PageGridConstants.MAIN_PLACEMENT_NAME);
    return placement == null ? Collections.emptyList() : placement.getItems();
  }


  // --- Dataviews --------------------------------------------------

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PageGridImpl pageGrid = (PageGridImpl) o;

    //noinspection RedundantIfStatement
    if (navigation != null ? !navigation.equals(pageGrid.navigation) : pageGrid.navigation != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return navigation != null ? navigation.hashCode() : 0;
  }

  @Override
  public void assumeIdentity(Object bean) {
    PageGridImpl other = (PageGridImpl) bean;
    validationService = other.validationService;
    contentBackedPageGridService = other.contentBackedPageGridService;
    navigation = other.navigation;
  }


  // --- internal ---------------------------------------------------

  private ContentBackedPageGrid getContentBackedPageGrid() {
    return contentBackedPageGridService.getContentBackedPageGrid(navigation.getContent());
  }
}

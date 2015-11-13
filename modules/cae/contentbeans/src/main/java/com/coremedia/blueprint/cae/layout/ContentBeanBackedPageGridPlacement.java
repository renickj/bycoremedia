package com.coremedia.blueprint.cae.layout;

import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGrid;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridPlacement;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyle;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyleGrid;
import com.coremedia.blueprint.base.pagegrid.PageGridConstants;
import com.coremedia.blueprint.base.pagegrid.PageGridContentKeywords;
import com.coremedia.blueprint.base.pagegrid.TableLayoutData;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.common.util.IsInProductionPredicate;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.AssumesIdentity;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class ContentBeanBackedPageGridPlacement implements PageGridPlacement, AssumesIdentity {
  private static final String CONTENT_PROPERTIES_PROPERTY = "properties";
  private ValidationService<Linkable> validationService;
  private ContentBackedPageGridService contentBackedPageGridService;

  private CMNavigation navigation;
  private int row;
  private int columns;

  /**
   * Simple list logic:
   * Count starts with 0,
   * does not consider col spans and row spans but refers 1:1 to the items of the row itself.
   */
  private int colIndex;


  // --- construction -----------------------------------------------

  public ContentBeanBackedPageGridPlacement(CMNavigation navigation, int row, int columns, int colIndex, ContentBackedPageGridService contentBackedPageGridService, ValidationService<Linkable> validationService) {
    this.navigation = navigation;
    this.row = row;
    this.columns = columns;
    this.colIndex = colIndex;
    this.contentBackedPageGridService = contentBackedPageGridService;
    this.validationService = validationService;
  }

  /**
   * Only for dataviews
   */
  @SuppressWarnings("UnusedDeclaration")
  public ContentBeanBackedPageGridPlacement() {
  }


  // --- PageGridPlacement ------------------------------------------


  public CMNavigation getNavigation() {
    return navigation;
  }

  @Override
  public int getNumCols() {
    return columns;
  }

  @Override
  public String getViewTypeName() {
    Content viewType = getDelegate().getViewtype();
    return viewType ==  null ? null : viewType.getName();
  }

  @Override
  public List<? extends Linkable> getItems() {
    List<? extends Linkable> filteredList = getItemsUnfiltered();
    if (validationService != null) {
      filteredList = validationService.filterList(filteredList);
    }
    return filteredList;
  }

  @Override
  public String getName() {
    Content section = getDelegate().getSection();
    return section == null ? PageGridConstants.MAIN_PLACEMENT_NAME : section.getName();
  }

  @Override
  public String getPropertyName() {
    return String.format("%s.%s-%s",
            CONTENT_PROPERTIES_PROPERTY,
            PageGridContentKeywords.PAGE_GRID_STRUCT_PROPERTY,
            getName());
  }

  @Override
  public int getCol() {
    // Do not confuse with the simple list-related colIndex.
    // getCol takes account of row spans and col spans.
    return getLayout().getCol();
  }

  @Override
  public int getColspan() {
    return getLayout().getColspan();
  }

  @Override
  public int getWidth() {
    return getLayout().getWidth();
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

    ContentBeanBackedPageGridPlacement that = (ContentBeanBackedPageGridPlacement) o;

    if (colIndex != that.colIndex) {
      return false;
    }
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
    int result = (navigation != null ? navigation.hashCode() : 0);
    result = 31 * result + row;
    result = 31 * result + colIndex;
    return result;
  }

  @Override
  public void assumeIdentity(Object bean) {
    ContentBeanBackedPageGridPlacement other = (ContentBeanBackedPageGridPlacement) bean;
    validationService = other.validationService;
    contentBackedPageGridService = other.contentBackedPageGridService;
    navigation = other.navigation;
    row = other.row;
    colIndex = other.colIndex;
  }

  /**
   * Do not use.
   * <p/>
   * Public only for Dataviews.
   * Retrieves the items of this PageGridPlacement
   */
  public List<? extends Linkable> getItemsUnfiltered() {
    List unfiltered = getDelegate().getItems();
    Iterable filteredList = Iterables.filter(unfiltered, new IsInProductionPredicate());
    List contentBeans = createContentBeansFor(Lists.newArrayList(filteredList));
    return Lists.newArrayList(Iterables.filter(contentBeans, Linkable.class));
  }


  // --- internal ---------------------------------------------------

  private List createContentBeansFor(List items) {
    List withContentBeans = new ArrayList();

    for (Object object : items) {
      if (object instanceof Content) {
        withContentBeans.add(getContentBeanFactory().createBeanFor((Content)object));
      }
      else {
        withContentBeans.add(object);
      }
    }

    return withContentBeans;
  }

  private ContentBackedPageGridPlacement getDelegate() {
    String sectionName = getContentBackedStyle().getSection().getName();
    return getContentBackedPageGrid().getPlacements().get(sectionName);
  }

  private TableLayoutData getLayout() {
    ContentBackedStyle style = getContentBackedStyle();
    return style.getLayout();
  }

  private ContentBackedStyle getContentBackedStyle() {
    ContentBackedStyleGrid styleGrid = getContentBackedPageGrid().getStyleGrid();
    List<ContentBackedStyle> styleRow = styleGrid.getRow(row);
    return styleRow.get(colIndex);
  }

  private ContentBackedPageGrid getContentBackedPageGrid() {
    return contentBackedPageGridService.getContentBackedPageGrid(navigation.getContent());
  }

  private ContentBeanFactory getContentBeanFactory() {
    if (navigation != null) {
      return navigation.getContentBeanFactory();
    } else {
      throw new IllegalStateException("cannot determine content bean factory");
    }
  }
}

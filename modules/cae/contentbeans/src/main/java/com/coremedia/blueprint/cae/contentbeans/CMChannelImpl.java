package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper;
import com.coremedia.blueprint.base.links.VanityUrlMapper;
import com.coremedia.blueprint.common.contentbeans.CMCSS;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMCollection;
import com.coremedia.blueprint.common.contentbeans.CMHTML;
import com.coremedia.blueprint.common.contentbeans.CMJavaScript;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridService;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generated extension class for immutable beans of document type "CMChannel".
 */
public class CMChannelImpl extends CMChannelBase {

  private static final SettingsBasedVanityUrlMapper EMPTY_VANITY = new SettingsBasedVanityUrlMapper();

  private PageGridService pageGridService;

  /**
   * If the header is empty, fallback to parent channel.
   */
  @Override
  public List<? extends Linkable> getHeader() {
    List<? extends Linkable> headers = filterItems(getHeaderUnfiltered());
    if (!headers.isEmpty()) {
      return headers;
    }
    CMChannel parent = getParentChannel();
    return parent == null ? Collections.<CMLinkable>emptyList() : parent.getHeader();
  }

  /**
   * public for dataview caching only //todo check alternatives
   */
  public List <? extends Linkable> getHeaderUnfiltered(){
    return super.getHeader();
  }

  /**
   * If the footer is empty, fallback to parent channel.
   */
  @Override
  public List<? extends Linkable> getFooter() {
    List<? extends Linkable> footers = filterItems(getFooterUnfiltered());
    if (!footers.isEmpty()) {
      return footers;
    }
    CMChannel parent = getParentChannel();
    return parent == null ? Collections.<CMLinkable>emptyList() : parent.getFooter();
  }

  /**
   * public for dataview caching only //todo check alternatives
   */
  public List <? extends Linkable> getFooterUnfiltered(){
    return super.getFooter();
  }

  /**
   * Return the channel's CSS.
   * <p>
   * Fallback to the parent channel if the channel has no CSS.
   */
  @Override
  public List<? extends CMCSS> getCss() {
    List<? extends CMCSS> css = super.getCss();
    if (!css.isEmpty()) {
      return css;
    }
    CMChannel parent = getParentChannel();
    return parent==null ? Collections.<CMCSS>emptyList() : parent.getCss();
  }

  /**
   * Return the channel's JavaScript.
   * <p>
   * Fallback to the parent channel if the channel has no JavaScript.
   */
  @Override
  public List<? extends CMJavaScript> getJavaScript() {
    List<? extends CMJavaScript> js = super.getJavaScript();
    if (!js.isEmpty()) {
      return js;
    }
    CMChannel parent = getParentChannel();
    return parent==null ? Collections.<CMJavaScript>emptyList() : parent.getJavaScript();
  }

  // --- internal ---------------------------------------------------

  /**
   * Fetch a parent channel to inherit missing properties from.
   * <p/>
   * If a channel has multiple parents, the chosen parent is somewhat
   * arbitrary, but deterministic: order by {@link #getContentId()}.
   *
   * @return parent channel or <code>null</code> if this is a root channel.
   */
  protected CMChannel getParentChannel() {
    Navigation parent = getParentNavigation();
    while (parent != null && !(parent instanceof CMChannel)) {
      parent = parent.getParentNavigation();
    }
    return (CMChannel) parent;
  }

  @Override
  public VanityUrlMapper getVanityUrlMapper() {
    if (isRoot()) {
      return new SettingsBasedVanityUrlMapper(this, getSettingsService());
    } else {
      // optimization, assume vanity URLs are only managed on the root channel
      return EMPTY_VANITY;
    }
  }

  @Override
  public String getFeedDescription() {
    return getFeedTitle();
  }

  @Override
  public PageGrid getPageGrid() {
    return pageGridService.getContentBackedPageGrid(this);
  }

  /**
   * Ensures that only those items are flattened that
   * should be displayed in the navigation.
   * @param item The item to check the type for.
   * @return True, if the item should be displayed in the navigation.
   */
  private boolean isValidNavigationType(CMLinkable item) {
    ContentType contentType = item.getContent().getType();
    return !contentType.isSubtypeOf(CMMedia.NAME) && !contentType.isSubtypeOf(CMHTML.NAME);
  }

  @Required
  public void setPageGridService(PageGridService pageGridService) {
    this.pageGridService = pageGridService;
  }

  @Override
  public List<? extends CMLinkable> getFeedItems() {
    return getItems();
  }

  @Override
  public List<? extends CMLinkable> getItemsFlattened() {
    List<CMLinkable> result = new ArrayList<>();
    flatten(result, getItems());
    return result;
  }

  @SuppressWarnings("unchecked")
  private List<? extends CMLinkable> getItems() {
    //TODO broaden to implements FeedSource<Object>
    return (List<? extends CMLinkable>) getPageGrid().getMainItems();
  }

  /**
   * Recursive search for items that can be displayed as navigation items.
   * @param result The filtered result list that contains the items to display in the navigation.
   * @param items The items of the current content bean.
   */
  private void flatten(List<CMLinkable> result, List<?> items) {
    for (Object item : items) {
      if (item instanceof CMCollection<?>) {
        //enter child
        final CMCollection<?> cmCollection = (CMCollection<?>) item;
        flatten(result, cmCollection.getItems());
      } else if (item instanceof CMLinkable) {
        final CMLinkable linkable = (CMLinkable) item;
        //filter item for valid types and exclude duplicates
        if (!result.contains(item) && isValidNavigationType(linkable)) {
          result.add(linkable);
        }
      }
    }
  }

  @Override
  public CMPicture getPicture() {
    CMPicture picture = super.getPicture();
    if (picture == null) {
      PageGrid pageGrid = getPageGrid();
      if (pageGrid != null) {
        List<?> mainItems = pageGrid.getMainItems();
        for (Object mainItem : mainItems) {
          if (mainItem instanceof Content) {
            CMTeasable item = (CMTeasable) getContentBeanFactory().createBeanFor((Content) mainItem);
            CMPicture itemPicture = item.getPicture();
            if (itemPicture != null) {
              return itemPicture;
            }
          }
        }
      }
    }
    return picture;
  }
}

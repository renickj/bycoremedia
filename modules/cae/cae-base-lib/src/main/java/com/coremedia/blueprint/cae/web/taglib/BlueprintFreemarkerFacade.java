package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.base.cae.web.taglib.CssClassFor;
import com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions;
import com.coremedia.blueprint.base.cae.web.taglib.SettingsFunction;
import com.coremedia.blueprint.base.cae.web.taglib.UniqueIdGenerator;
import com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNamesFreemarker;
import com.coremedia.blueprint.base.cae.web.taglib.WordAbbreviator;
import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.webflow.BlueprintFlowUrlHandler;
import com.coremedia.blueprint.common.contentbeans.CMCollection;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMImageMap;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.Container;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.blueprint.common.navigation.HasViewTypeName;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.image.ImageDimensionsExtractor;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.util.RequestServices;
import com.coremedia.objectserver.util.undoc.DataRenderer;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.taglib.TemplateHelper;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupUtil;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static com.google.common.collect.ImmutableList.of;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * A Facade for utility functions used by FreeMarker templates.
 */
public class BlueprintFreemarkerFacade {

  private static final Logger LOG = LoggerFactory.getLogger(BlueprintFreemarkerFacade.class);
  private static final String RESPONSIVE_SETTINGS_KEY = "responsiveImageSettings";

  private ContentBeanFactory contentBeanFactory;
  private DataViewFactory dataViewFactory;
  private SettingsService settingsService;
  private ImageDimensionsExtractor imageDimensionsExtractor;
  private WordAbbreviator abbreviator;
  private DataRenderer dataRenderer;

  private final ViewHookEventNamesFreemarker viewHookEventNames = new ViewHookEventNamesFreemarker();

  // --- spring config -------------------------------------------------------------------------------------------------

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setImageDimensionsExtractor(ImageDimensionsExtractor imageDimensionsExtractor) {
    this.imageDimensionsExtractor = imageDimensionsExtractor;
  }

  @Required
  public void setStringAbbreviator(WordAbbreviator abbreviator) {
    this.abbreviator = abbreviator;
  }

  @Required
  public void setDataRenderer(DataRenderer dataRenderer) {
    this.dataRenderer = dataRenderer;
  }


  // --- functionality -------------------------------------------------------------------------------------------------

  public ContentBean createBeanFor(Content content) {
    return dataViewFactory.loadCached(contentBeanFactory.createBeanFor(content), null);
  }

  public List createBeansFor(List<Content> contents) {
    return dataViewFactory.loadAllCached(contentBeanFactory.createBeansFor(contents), null);
  }

  public Object setting(Object self, String key) {
    return setting(self, key, null);
  }

  public Object setting(Object self, String key, Object defaultValue) {
    return SettingsFunction.setting(settingsService, self, key, defaultValue);
  }

  public Navigation findNavigationContext(Object bean) {
    return FindNavigationContext.findNavigationContext(bean, FreemarkerUtils.getCurrentRequest());
  }

  public Boolean isActiveNavigation(Object navigation, List<Object> navigationPathList) {
    return navigationPathList.contains(navigation);
  }

  public String generateId(String prefix) {
    return UniqueIdGenerator.generateId(prefix, FreemarkerUtils.getCurrentRequest());
  }

  public String cssClassFor(Boolean itemHasNext, Integer index, Boolean createCssClassAttribute) {
    return com.coremedia.blueprint.base.cae.web.taglib.CssClassFor.cssClassFor(itemHasNext, index,
            createCssClassAttribute);
  }

  public String cssClassForFirstLast(Boolean itemHasNext, Integer index, Boolean createCssClassAttribute) {
    return com.coremedia.blueprint.base.cae.web.taglib.CssClassFor.cssClassForFirstLast(itemHasNext, index,
            createCssClassAttribute);
  }

  public String cssClassForOddEven(Boolean itemHasNext, Integer index, Boolean createCssClassAttribute) {
    return com.coremedia.blueprint.base.cae.web.taglib.CssClassFor.cssClassForOddEven(itemHasNext, index,
            createCssClassAttribute);
  }

  public String cssClassAppendNavigationActive(String currentCssClass, String appendix, Object navigation, List<Object> navigationPathList) {
    return CssClassFor.cssClassAppendNavigationActive(currentCssClass, appendix, navigation, navigationPathList);
  }

  public String getStackTraceAsString(Exception e) {
    //print stackTrace the Java way here so that we automatically get all causes, messages etc.
    StringWriter stringWriter = new StringWriter(); //NOSONAR
    e.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

  public int parseContentId(String contentId) {
    return IdHelper.parseContentId(contentId);
  }

  public String blobLink(Blob blob, String view) {
    if (blob == null) {
      return "";
    }

    HttpServletRequest request = FreemarkerUtils.getCurrentRequest();
    HttpServletResponse response = FreemarkerUtils.getCurrentResponse();
    LinkFormatter linkFormatter = RequestServices.getLinkFormatter(request);

    return TemplateHelper.escapeAttribute(linkFormatter.formatLink(blob, view, request, response, false));
  }

  /**
   * Returns a String representation of a JSON object with a list of aspect ratios with image links for different sizes.
   * @param picture the image
   * @param page the root page
   * @param aspectRatios list of aspect ratios to use for this image
   * @return Json Object with a list of aspect ratios with image links for different sizes
   * @throws IOException
   */
  public String responsiveImageLinksData(CMPicture picture, Page page, List<String> aspectRatios) throws IOException {

    if (picture == null) {
      throw new IllegalArgumentException("Error creating responsive image links: picture must not be null");
    }
    if (page == null) {
      throw new IllegalArgumentException("Error creating responsive image links: page must not be null");
    }

    // get responsive image settings
    Map<String, Map> responsiveImageSettings = settingsService.settingAsMap(RESPONSIVE_SETTINGS_KEY, String.class, Map.class, page);
    if (isEmpty(responsiveImageSettings)) {
      throw new IllegalArgumentException("Error creating responsive image links: No responsive image settings found");
    }
    List<String> aspectRatiosToUse = aspectRatios;
    // use list of given aspect ratios if set, otherwise use all
    if (isEmpty(aspectRatiosToUse)) {
      aspectRatiosToUse = new ArrayList<>(responsiveImageSettings.keySet());
    }

    HttpServletRequest currentRequest = FreemarkerUtils.getCurrentRequest();
    HttpServletResponse currentResponse = FreemarkerUtils.getCurrentResponse();
    Map<String, Map<Integer, String>> result = new HashMap<>();
    // get aspect ratios
    for (Map.Entry<String, Map> entry : responsiveImageSettings.entrySet()) {
      String aspectRatioName = entry.getKey();

      if (aspectRatiosToUse.contains(aspectRatioName)) {
        @SuppressWarnings("unchecked")
        Map<String, Map> aspectRatioSizes = entry.getValue();
        Blob blob = picture.getTransformedData(aspectRatioName);
        Map<Integer, String> links = ImageFunctions.getImageLinksForAspectRatios(blob, aspectRatioName, aspectRatioSizes, currentRequest, currentResponse);

        if (!isEmpty(links)) {
          result.put(aspectRatioName, links);
        } else {
          LOG.info("No responsive image links found for CMPicture {} with transformationName {}", picture, aspectRatioName);
        }
      }
    }

    if (isEmpty(result)) {
      LOG.warn("No responsive image links found for CMPicture {}", picture);
    }

    return dataRenderer.serializeData(result);
  }

  /**
   * Returns a {@link java.lang.String} URL of the uncropped image
   * @param picture the image for which an URL should be determined
   * @return a {@link java.lang.String} URL of the uncropped image
   */
  public String uncroppedImageLink(CMPicture picture) {
    if (picture == null) {
      throw new IllegalArgumentException("Error creating image link: picture must not be null");
    }

    Blob blob = picture.getData();
    return ImageFunctions.uncroppedImageLink(blob, FreemarkerUtils.getCurrentRequest(),
            FreemarkerUtils.getCurrentResponse());
  }

  public CMContext getPageContext(Page page) throws IOException {
    return page.getContext();
  }

  public String getPlacementPropertyName(PageGridPlacement placement) {
    return placement != null ? placement.getPropertyName() : "";
  }

  /**
   * @param container The container the metadata should be determined for
   * @return The metadata that was determined either as list or as plain object
   */
  public Object getContainerMetadata(Container container) {
    if (container instanceof ContainerWithViewTypeName) {
      return getContainerMetadata(((ContainerWithViewTypeName) container).getBaseContainer());
    }
    if (container instanceof CMCollection) {
      return of(((CMCollection) container).getContent(), "properties.items");
    }
    if (container instanceof PageGridPlacement) {
      return getPlacementPropertyName((PageGridPlacement) container);
    }
    return Collections.emptyList();
  }

  /**
   * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
   * the items the original container had.
   *
   * @param items The items to be put inside the new container
   * @return a new container
   */
  public Container getContainer(final List<Object> items) {
    return new Container() {
      @Override
      public List getItems() {
        return items;
      }
    };
  }

  /**
   * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
   * the items the original container had.
   *
   * @param baseContainer The base container the new container shall be created from
   * @param items The items to be put inside the new container
   * @return a new container based on the given base container
   */
  public Container getContainer(Container baseContainer, List<Object> items) {
    return new ContainerWithViewTypeName<>(baseContainer, items);
  }

  public boolean isWebflowRequest() {
    HttpServletRequest currentRequest = FreemarkerUtils.getCurrentRequest();
    return currentRequest.getRequestURL().toString().contains(UriConstants.Prefixes.PREFIX_DYNAMIC)
            && currentRequest.getParameterMap().containsKey(BlueprintFlowUrlHandler.FLOW_EXECUTION_KEY_PARAMETER);
  }

  /**
   *
   * @param size in bytes
   * @return a human readable size
   */
  public String getDisplaySize(int size) {
    int unit = 1024;
    if (size < unit) {
      return size + " Bytes";
    }
    int exp = (int) (Math.log(size) / Math.log(unit));
    char pre = "KMGTPE".charAt(exp-1);
    String result = String.format("%.1f %sB", size / Math.pow(unit, exp), pre);
    return result.replaceAll(",0 ", " ");
  }

  public List<Map<String, Object>> responsiveImageMapAreas(CMImageMap imageMap, List<String> transformationNames){

    List<Map<String, Object>> result = Collections.emptyList();
    final CMPicture picture = imageMap.getPicture();

    if (picture != null) {
      // determine which transformations to apply
      final Map<String, String> transformMap = picture.getTransformMap();
      final List<Map<String, Object>> imageMapAreas = imageMap.getImageMapAreas();

      result = ImageFunctions.responsiveImageMapAreas(picture.getData(), picture.getDisableCropping(), imageMapAreas, transformMap, imageDimensionsExtractor, transformationNames);
    }

    return result;
  }

  public Map<String, Object> responsiveImageMapAreaData(Map<String, Object> coords) {
    return ImageFunctions.responsiveImageMapAreaData(coords);
  }

  public int getImageTransformationBaseWidth() {
    return ImageFunctions.getImageTransformationBaseWidth();
  }

  public Map<String,List<CMTeasable>> filterRelated(Map<String,List<CMTeasable>> related, List<String> types) {
    return Maps.filterKeys(related, new RelatedByTypePredicate(types));
  }

  public ViewHookEventNamesFreemarker getViewHookEventNames() {
    return viewHookEventNames;
  }

  @Deprecated
  public boolean isEmptyMarkup(Markup markup) {
    return MarkupUtil.isEmptyRichtext(markup, true);
  }

  public boolean isEmptyRichtext(Markup richtext) {
    return MarkupUtil.isEmptyRichtext(richtext, true);
  }

  /**
   * @return given String truncated to given length, based on words.
   */
  public String truncateText(Object text, int maxLength) {

    String toTruncate = "";

    if(text != null) {
      if(text instanceof Markup) {
        toTruncate = MarkupUtil.asPlainText((Markup)text, true);
      } else if (text instanceof String) {
        toTruncate = (String) text;
      } else {
        // should not happen
        LOG.error("Could not abbreviate text since it's type was not supported: {} instead of Markup or String. Input was: {}" + text.getClass().getName(), text);
        throw new UnsupportedOperationException("Cannot abbreviate value " + text + " of Type" + text.getClass().getName());
      }
    }

    return abbreviator.abbreviateString(toTruncate, maxLength);
  }

  /**
   * Calls truncate text with given parameters and closes the last bold tag at the end.
   * This method is kind of stupid: It just adds the bold tag at the end and does not know
   * about where to add it. It's hard to determine where the bold end tag should be added (after which word?
   * truncate text adds three dots...) but it's easy to say that after the truncated text no bold tag should be open.
   *
   * @param text the highlighted text
   * @param maxLength the length the text will be truncated to
   * @return truncated text with closed bold tag
   */
  public String truncateHighlightedText(Object text, int maxLength) {
    String startTag = "<b>";
    String endTag = "</b>";
    return truncateHighlightedText(text, maxLength, startTag, endTag);
  }

  /**
   * More generic version of {@link #truncateHighlightedText(Object, int)}.
   * Instead of closing a bold tag this method gets start and end tag as input and closes the given start tag with the
   * given end tag. The tags must be
   * @param text the highlighted text
   * @param maxLength the length the text will be truncated to
   * @param startTag start tag which should be closed if it's not closed at the end
   * @param endTag end tag - used to close the start tag if it's not closed at the end
   * @return truncated text with closed bold tag
   */
  public String truncateHighlightedText(Object text, int maxLength, String startTag, String endTag) {
    String truncatedText = truncateText(text, maxLength);

    //if the tag is smaller, everything is fine if none of those tags exist -1 < -1 is false, but we shouldn't add an end tag
    boolean lastStartTagHasBeenClosed = truncatedText.lastIndexOf(startTag) <= truncatedText.lastIndexOf(endTag);

    if(lastStartTagHasBeenClosed) {
      return truncatedText;
    }

    return truncatedText + endTag;
  }

  public PageGridPlacement getPlacementByName(String name, Page page) {
    return page.getPageGrid().getPlacementForName(name);
  }

  public static String getLocalizedString(String bundle, String key, String languageTag) {
    Locale locale = Locale.forLanguageTag(languageTag);
    return ResourceBundle.getBundle(bundle, locale).getString(key);
  }

  //====================================================================================================================

  private static class RelatedByTypePredicate implements Predicate<String> {

    private List<String> types;

    public RelatedByTypePredicate(List<String> types) {
      this.types = types;
    }

    @Override
    public boolean apply(String type) {
      return types.contains(type);
    }
  }

  /**
   * Represents custom container having elements and a viewtype name based on the given base container.
   *
   * @param <T> The type if the items
   */
  private class ContainerWithViewTypeName<T> implements Container<T>, HasViewTypeName {

    private Container baseContainer;
    private List<T> items;

    public ContainerWithViewTypeName(Container baseContainer, List<T> items) {
      this.baseContainer = baseContainer;
      this.items = items;
    }

    public Container getBaseContainer() {
      return baseContainer;
    }

    @Override
    public String getViewTypeName() {
      if (baseContainer instanceof HasViewTypeName) {
        return ((HasViewTypeName)baseContainer).getViewTypeName();
      }
      return null;
    }

    @Override
    public List<T> getItems() {
      return items;
    }
  }
}

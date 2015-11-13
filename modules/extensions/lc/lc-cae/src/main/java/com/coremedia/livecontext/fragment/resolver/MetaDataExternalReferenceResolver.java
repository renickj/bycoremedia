package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.resolver.ContentSeoSegmentExternalReferenceResolver.Ids;
import com.coremedia.xml.MarkupUtil;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Resolves the relative or absolute path to identify a content.
 * The meta data information are gathered from the product (if product id is set), from
 * the category and from external channels, if a context lookup is successful for one of
 * the two catalog items.
 * <p/>
 * The result is set into the {@link com.coremedia.livecontext.fragment.FragmentParameters} object which
 * is set into the ModelAndView and read from the Navigation.metaData.ftl template.
 */
public class MetaDataExternalReferenceResolver extends ExternalReferenceResolverBase {
  private static final String PREFIX = "cm-metadata";
  public MetaDataExternalReferenceResolver() {
    super(PREFIX);
  }

  // --- interface --------------------------------------------------
  @Override
  protected boolean include(@Nonnull FragmentParameters fragmentParameters, @Nonnull String referenceInfo) {
    return true;
  }

  @Nullable
  @Override
  protected LinkableAndNavigation resolveExternalRef(@Nonnull FragmentParameters fragmentParameters,
                                                     @Nonnull String referenceInfo,
                                                     @Nonnull Site site) {
    String externalTechId = fragmentParameters.getProductId();
    String categoryId = fragmentParameters.getCategoryId();

    //apply product details as default
    if (!isEmpty(externalTechId) || !isEmpty(categoryId)) {
      applyOriginalMetaData(fragmentParameters);
    } else {
      //apply the meta data from the context and content
      applyContextAndContentMetaData(fragmentParameters);
    }

    //we can use the root context of the site since the navigation might be null and is not used for rendering.
    Content navigationContent = site.getSiteRootDocument();
    return new LinkableAndNavigation(navigationContent, navigationContent);
  }

  // -------------------- Helper --------------------------------


  /**
   * Uses the SEO URL to gather the information which context and content is currently rendered.
   * The combined keywords, description and title is applied to then.
   *
   * @param fragmentParameters the parameters passed by the lc:include.
   */
  private void applyContextAndContentMetaData(FragmentParameters fragmentParameters) {
    String[] metaData = getOriginalMetaData(fragmentParameters);
    if (metaData.length != 4) {
      //no category id, not product id and no seo segment, assuming top category page
      applyOriginalMetaData(fragmentParameters);
      return;
    }

    Ids ids = ContentSeoSegmentExternalReferenceResolver.parseExternalReferenceInfo(metaData[3]);
    // apply context data
    applyMetaDataForContent(fragmentParameters, ids.contentId);
  }

  /**
   * Uses the given content id to apply the content's data as metadata to the parameter instance.
   * @param parameters the parameter object to apply the metadata to.
   * @param id the content id to retrieve the data for.
   */
  private void applyMetaDataForContent(FragmentParameters parameters, int id) {
    String contentId = IdHelper.formatContentId(id);
    Content content = contentRepository.getContent(contentId);
    if (content.getType().isSubtypeOf(CMLinkable.NAME)) {
      String keywords = content.getString(CMLinkable.KEYWORDS);
      parameters.setMetaDataKeywords(keywords);

      String title = content.getString(CMLinkable.TITLE);
      parameters.setMetaDataTitle(title);
      if (content.getType().isSubtypeOf(CMTeasable.NAME)) {
        String teaserText = MarkupUtil.asPlainText(content.getMarkup(CMTeasable.TEASER_TEXT));
        if (!StringUtils.isEmpty(teaserText)) {
          parameters.setMetaDataDescription(teaserText.trim());
        }
      }
    }
  }

  /**
   * The original meta data values are passed as CSV data using the "parameter" attribute of the lc:include tag.
   * The CSV format is '<TITLE>','<DESCRIPTION>','<KEYWORDS>','<SEO_SEGMENT>'.
   *
   * @param parameters the parameters passed by the lc:include.
   */
  private void applyOriginalMetaData(FragmentParameters parameters) {
    String[] metaData = getOriginalMetaData(parameters);
    if (metaData.length >= 3) {
      parameters.setMetaDataTitle(metaData[0]);
      parameters.setMetaDataDescription(metaData[1]);
      parameters.setMetaDataKeywords(metaData[2]);
    }
  }

  /**
   * Returns the array of the original CSV data.
   *
   * @param parameters the parameter
   * @return a string array with 4 elements.
   */
  private String[] getOriginalMetaData(FragmentParameters parameters) {
    String metaDataCSV = parameters.getParameter();
    if (!isEmpty(metaDataCSV)) {
      //remove quotes
      String formatted = metaDataCSV.substring(1, metaDataCSV.length() - 1);
      return formatted.split("','");
    }
    return new String[]{};
  }

  /**
   * Empty check with trim.
   *
   * @param s the string to check
   * @return true if the string is null or the trimmed value is empty.
   */
  private static boolean isEmpty(String s) {
    return s == null || s.length() == 0 || s.trim().length() == 0;
  }
}

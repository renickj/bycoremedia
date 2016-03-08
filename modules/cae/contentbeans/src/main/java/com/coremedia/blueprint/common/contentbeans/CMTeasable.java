package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;
import com.coremedia.xml.Markup;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Each content has an embedded Teaser and a detailText for the page view.
 * <p>
 * If you need different teasers for the document, you can use additional
 * {@link CMTeaser} documents.
 *
 * <p>Represents the document type {@link #NAME CMTeasable}.</p>
 */
public interface CMTeasable extends CMHasContexts {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMTeasable'.
   */
  String NAME = "CMTeasable";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMTeasable} object
   */
  @Override
  CMTeasable getMaster();

  @Override
  Map<Locale, ? extends CMTeasable> getVariantsByLocale();

  @Override
  Collection<? extends CMTeasable> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMTeasable>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMTeasable>> getAspects();

  /**
   * Name of the document property 'teaserTitle'.
   */
  String TEASER_TITLE = "teaserTitle";

  /**
   * Returns the value of the document property {@link #TEASER_TITLE}.
   *
   * @return the value of the document property {@link #TEASER_TITLE}
   */
  String getTeaserTitle();

  /**
   * Name of the document property 'teaserText'.
   */
  String TEASER_TEXT = "teaserText";

  /**
   * Returns the value of the document property {@link #TEASER_TEXT}.
   *
   * @return the value of the document property {@link #TEASER_TEXT}
   */
  Markup getTeaserText();

  /**
   * Name of the document property 'detailText'.
   */
  String DETAIL_TEXT = "detailText";

  /**
   * Returns the value of the document property {@link #DETAIL_TEXT}.
   *
   * @return the value of the document property {@link #DETAIL_TEXT}
   */
  Markup getDetailText();

  /**
   * Name of the document property 'pictures'.
   */
  String PICTURES = "pictures";

  /**
   * Returns the value of the document property {@link #PICTURES}.
   *
   * @return the value of the document property {@link #PICTURES}
   */
  List<? extends CMPicture> getPictures();

  /**
   * Returns the first element of document property {@link #PICTURES}.
   *
   * @return the first element of the document property {@link #PICTURES}
   */
  CMPicture getPicture();

  /**
   * Name of the document property 'thumbnails'.
   */
  String THUMBNAILS = "thumbnails";

  /**
   * Returns the value of the document property {@link #THUMBNAILS}.
   *
   * @return the value of the document property {@link #THUMBNAILS}
   */
  List<? extends CMPicture> getThumbnails();

  /**
   * Returns the first element of document property {@link #THUMBNAILS}.
   *
   * @return the first element of the document property {@link #THUMBNAILS}
   */
  CMPicture getThumbnail();

  /**
   * Returns this. Overridden by standalone teasers.
   *
   * @return a {@link CMLinkable} object
   */
  CMLinkable getTarget();

  /**
   * Name of the document property 'searchable'.
   */
  String NOT_SEARCHABLE = "notSearchable";

  /**
   * Returns the value of the document property {@link #NOT_SEARCHABLE}.
   *
   * @return the value of the document property {@link #NOT_SEARCHABLE}
   */
  boolean isNotSearchable();

  /**
   * Name of the document property 'related'.
   */
  String RELATED = "related";

  /**
   * Returns the value of the document property {@link #RELATED}.
   *
   * @return a list of {@link CMTeasable} objects
   */
  List<? extends CMTeasable> getRelated();

  /**
   * Returns the {@link CMTeasable}s referring to this {@link CMTeasable} in their {@link #RELATED} property.
   *
   * @return a list of {@link CMTeasable} objects
   */
  List<? extends CMTeasable> getRelatedByReferrers();

  /**
   * Returns {@link CMTeasable}s that are related to this item because they have a similar set of taxonomies
   * linked.
   *
   * @return a list of {@link CMTeasable} objects
   */
  List<? extends CMTeasable> getRelatedBySimilarTaxonomies();

  /**
   * Returns a list of related {@link CMTeasable}s which is a merge of {@link CMTeasable#getRelated()},
   * {@link CMTeasable#getRelatedByReferrers()}, {@link CMTeasable#getRelatedBySimilarTaxonomies()}.
   *
   * @return a list of {@link CMTeasable} objects
   */
  List<? extends CMTeasable> getRelatedAll();

  /**
   * Returns a list of related {@link CMTeasable}s which is a merge of {@link CMTeasable#getRelatedByReferrers()},
   * {@link CMTeasable#getRelatedBySimilarTaxonomies()}.
   *
   * @return a list of {@link CMTeasable} objects
   */
  List<? extends CMTeasable> getRelatedImplicitly();

  /**
   * Returns a map where the keys are document type names and the values are Lists of {@link CMTeasable}s dynamically
   * related to this object.
   *
   * @return a Map of String to Lists of CMTeasable
   * @see #getRelatedAll()
   */
  Map<String, List<CMTeasable>> getRelatedAllByType();

  /**
   * Returns a map where the keys are document type names and the values are Lists of {@link CMTeasable}s explicitly
   * related to this object.
   *
   * @return a Map of String to Lists of CMTeasable
   * @see #getRelatedImplicitly()
   */
  Map<String, List<CMTeasable>> getRelatedImplicitlyByType();

  /**
   * Returns the detail text splitted at each paragraph.
   *
   * @return the detail text splitted at each paragraph.the detail text splitted at each paragraph.
   */
  List<Markup> getTextAsParagraphs();
}

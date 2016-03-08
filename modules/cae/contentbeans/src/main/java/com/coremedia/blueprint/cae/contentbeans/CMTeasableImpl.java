package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.blueprint.cae.search.solr.SolrQueryBuilder;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.util.ContentBeanSolrSearchFormatHelper;
import com.coremedia.blueprint.common.util.ParagraphHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.xml.Markup;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.coremedia.xml.MarkupUtil.isEmptyRichtext;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Generated extension class for immutable beans of document type "CMTeasable".
 */
public class CMTeasableImpl extends CMTeasableBase {
  private static final int LIMIT = 20;
  private static final int CACHE_FOR_IN_SECONDS = 300;

  @Override
  public Markup getTeaserText() {
    Markup tt = super.getTeaserText();
    if (isEmptyRichtext(tt, true)) {
      tt = getDetailText();
    }
    return tt;
  }

  /**
   * If the teaserTitle is not set, fallback to the title.
   */
  @Override
  public String getTeaserTitle() {
    String tt = super.getTeaserTitle();
    if (isBlank(tt)) {
      tt = getTitle();
    }
    return tt;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends CMPicture> getPictures() {
    return (List<? extends CMPicture>) filterItems2(getPicturesUnfiltered());
  }

  public List<? extends CMPicture> getPicturesUnfiltered() {
    return super.getPictures();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends CMPicture> getThumbnails() {
    return (List<? extends CMPicture>) filterItems2(getThumbnailsUnfiltered());
  }

  public List<? extends CMPicture> getThumbnailsUnfiltered() {
    return super.getThumbnails();
  }

  /**
   * Filter items using the {@link #getValidationService()}
   *
   * The 2 in the name is required to prevent a compilation error involving type erasure.
   *
   * @param itemsUnfiltered the list of unfiltered items (not necessarily instances of CMLinkable)
   * @return a list of items that have passed validation
   */
  @SuppressWarnings("unchecked")
  protected List<? extends Linkable> filterItems2(List<? extends Linkable> itemsUnfiltered) {
    return getValidationService().filterList(itemsUnfiltered);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends CMTeasable> getRelated() {
    return (List<? extends CMTeasable>) filterItems2(getRelatedUnfiltered());
  }

  public List<? extends CMTeasable> getRelatedUnfiltered(){
    return super.getRelated();
  }

  @Override
  public CMPicture getPicture() {
    List<? extends CMPicture> pictures = getPictures();
    return isNotEmpty(pictures) ? pictures.get(0) : null;
 }

  @Override
  public CMPicture getThumbnail() {
    List<? extends CMPicture> thumbnails = getThumbnails();
    return isNotEmpty(thumbnails) ? thumbnails.get(0) : null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends CMTeasable> getRelatedByReferrers() {
    List<? extends CMTeasable> result = getRelatedByReferrersUnfiltered();
    return (List<? extends CMTeasable>) getValidationService().filterList(result);
  }

  public List<? extends CMTeasable> getRelatedByReferrersUnfiltered(){
    Collection<Content> referrers = getContent().getReferrersWithDescriptorFulfilling(CMTeasable.NAME,
            RELATED, "isInProduction");
    return createBeansFor(new ArrayList<>(referrers), CMTeasable.class);
  }

  @Override
  public List<? extends CMTeasable> getRelatedBySimilarTaxonomies() {
    //noinspection unchecked
    return (List<? extends CMTeasable>) getValidationService().filterList(getRelatedBySimilarTaxonomiesUnfiltered());
  }

  public List<? extends CMTeasable> getRelatedBySimilarTaxonomiesUnfiltered() {
    List<CMTeasable> result = new ArrayList<>();
    List<? extends CMTaxonomy> subjectTaxonomy = getSubjectTaxonomy();
    List<? extends CMTaxonomy> locationTaxonomy = getLocationTaxonomy();
    if (isNotEmpty(subjectTaxonomy) || isNotEmpty(locationTaxonomy)) {
      SearchQueryBean searchQuery = new SearchQueryBean();
      searchQuery.setQuery(SolrQueryBuilder.ANY_FIELD_ANY_VALUE);
      CMContext context = getCurrentContextService().getContext();
      searchQuery.setContext(String.valueOf(context.getRootNavigation().getContentId()));
      // exclude this CMTeasable from results
      Condition excludeThis = Condition.isNot(SearchConstants.FIELDS.ID.toString(), Value.exactly(ContentBeanSolrSearchFormatHelper.getContentBeanId(this)));
      searchQuery.addFilter(excludeThis);
      // set ORed filters for taxonomies
      if (isNotEmpty(subjectTaxonomy)) {
        List<String> subjectTaxonomyIds = ContentBeanSolrSearchFormatHelper.cmObjectsToIds(subjectTaxonomy);
        Condition cond = Condition.is(SearchConstants.FIELDS.SUBJECT_TAXONOMY.toString(), Value.anyOf(subjectTaxonomyIds));
        searchQuery.addFilter(cond);
      }
      if (isNotEmpty(locationTaxonomy)) {
        List<String> locationTaxonomyIds = ContentBeanSolrSearchFormatHelper.cmObjectsToIds(locationTaxonomy);
        Condition cond = Condition.is(SearchConstants.FIELDS.LOCATION_TAXONOMY.toString(), Value.anyOf(locationTaxonomyIds));
        searchQuery.addFilter(cond);
      }
      // set query limit
      searchQuery.setLimit(LIMIT);
      // run the query
      SearchResultBean searchResult = getResultFactory().createSearchResult(searchQuery, CACHE_FOR_IN_SECONDS);
      for (Object aResult : searchResult.getHits()) {
        if (aResult instanceof CMTeasable) {
          result.add((CMTeasable) aResult);
        }
      }
    }
    return result;
  }

  @Override
  public List<? extends CMTeasable> getRelatedAll() {
    Set<CMTeasable> allRelated = new HashSet<>();
    allRelated.addAll(getRelated());
    allRelated.addAll(getRelatedByReferrers());
    allRelated.addAll(getRelatedBySimilarTaxonomies());
    return Lists.newArrayList(allRelated);
  }

  @Override
  public Map<String, List<CMTeasable>> getRelatedAllByType() {
    Map<String, List<CMTeasable>> map = new HashMap<>();
    for (CMTeasable teasable : getRelatedAll()) {
      addToRelatedByType(map, teasable);
    }
    return map;
  }

  @Override
  public List<? extends CMTeasable> getRelatedImplicitly() {
    Set<CMTeasable> allRelated = new HashSet<>();
    allRelated.addAll(getRelatedByReferrers());
    allRelated.addAll(getRelatedBySimilarTaxonomies());
    return Lists.newArrayList(allRelated);
  }

  @Override
  public Map<String, List<CMTeasable>> getRelatedImplicitlyByType() {
    Map<String, List<CMTeasable>> map = new HashMap<>();
    for (CMTeasable teasable : getRelatedImplicitly()) {
      addToRelatedByType(map, teasable);
    }
    return map;
  }

  private static void addToRelatedByType(Map<String, List<CMTeasable>> map, CMTeasable teasable) {
    String teasableType = teasable.getContent().getType().getName();
    List<CMTeasable> relateds = map.get(teasableType);
    if (relateds == null) {
      relateds = new ArrayList<>();
      map.put(teasableType, relateds);
    }
    relateds.add(teasable);
  }

  @Override
  public List<Markup> getTextAsParagraphs() {
    return ParagraphHelper.createParagraphs(getDetailText());
  }
}

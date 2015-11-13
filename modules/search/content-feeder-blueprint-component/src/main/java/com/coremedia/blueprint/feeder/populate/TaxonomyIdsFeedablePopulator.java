package com.coremedia.blueprint.feeder.populate;

import com.coremedia.blueprint.base.tree.ChildrenLinkListContentTreeRelation;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Populates the ids of a taxonomy field, including all ids of the path.
 */
public class TaxonomyIdsFeedablePopulator implements FeedablePopulator<Content> {
  private String propertyName;
  private String solrFieldName;

  private ChildrenLinkListContentTreeRelation taxonomyTreeRelation;

  @Override
  public void populate(MutableFeedable mutableFeedable, Content content) {
    if (mutableFeedable == null || content == null) {
      throw new IllegalArgumentException("mutableFeedable and content must not be null");
    }

    if (content.getType().isSubtypeOf("CMLinkable")) {
      Collection<Content> taxonomies = content.getLinks(propertyName);
      if (taxonomies != null && !taxonomies.isEmpty()) {
        List<String> ids = new ArrayList<>();
        for (Content taxonomy : taxonomies) {
          collectPathIds(ids, taxonomy);
        }
        Collections.reverse(ids);
        String convertedValue = StringUtils.collectionToDelimitedString(ids, ",", "", "");
        mutableFeedable.setStringElement(solrFieldName, convertedValue);
      }
    }
  }

  /**
   * Collects the numeric ids of the taxonomy path.
   * Reverts the result to ensure the correct order.
   *
   * @param ids      The list to store the ids into.
   * @param taxonomy The taxonomy to resolve the path for.
   */
  private void collectPathIds(List<String> ids, Content taxonomy) {
    //add node itself
    ids.add(String.valueOf(IdHelper.parseContentId(taxonomy.getId())));

    //find parents
    List<Content> pathAsContent= new ArrayList<>(taxonomyTreeRelation.pathToRoot(taxonomy));
    for (Content pathElement : pathAsContent) {
      String id = String.valueOf(IdHelper.parseContentId(pathElement.getId()));
      if (ids.contains(id)) { //did we reach a parent that paths is already part of the id list?
        break;
      }
      ids.add(id);
    }
  }

  @Required
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  @Required
  public void setSolrFieldName(String solrFieldName) {
    this.solrFieldName = solrFieldName;
  }

  @Required
  public void setTaxonomyTreeRelation(ChildrenLinkListContentTreeRelation taxonomyTreeRelation) {
    this.taxonomyTreeRelation = taxonomyTreeRelation;
  }
}

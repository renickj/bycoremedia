package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.util.SettingsStructToSearchQueryConverter;
import com.coremedia.cap.content.Content;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Generated extension class for beans of document type "CMQueryList".
 */
public class CMQueryListImpl extends CMQueryListBase {
  private TreeRelation<Content> treeRelation;

  @Required
  public void setTreeRelation(TreeRelation<Content> treeRelation) {
    this.treeRelation = treeRelation;
  }

  public SearchQueryBean getSearchQuery() {
    SettingsStructToSearchQueryConverter converter = new SettingsStructToSearchQueryConverter(
            this,
            getCurrentContextService(),
            treeRelation,
            getSettingsService(),
            getContentBeanFactory());
    return converter.convert();
  }

  @Override
  public List<CMLinkable> getItemsUnfiltered() {
    List<CMLinkable> result = new ArrayList<>();
    List<CMLinkable> tempResult = new ArrayList<>();
    tempResult.addAll(super.getItemsUnfiltered());

    SearchQueryBean searchQuery = getSearchQuery();
    int limit = searchQuery.getLimit();

    if (limit > 0 && limit < tempResult.size()) {
      result.addAll(tempResult.subList(0, tempResult.size() > limit ? limit : tempResult.size()));
    } else {
      result.addAll(tempResult);
      SearchResultBean searchResult = getResultFactory().createSearchResultUncached(searchQuery);
      for (Object t : searchResult.getHits()) {
        if (limit > 0 && result.size() >= limit) {
          break;
        }
        if (t instanceof CMLinkable) {
          final CMLinkable cmLinkable = (CMLinkable) t;
          if (!result.contains(cmLinkable)) {
            result.add(cmLinkable);
          }
        }
      }
    }
    return result;
  }
}

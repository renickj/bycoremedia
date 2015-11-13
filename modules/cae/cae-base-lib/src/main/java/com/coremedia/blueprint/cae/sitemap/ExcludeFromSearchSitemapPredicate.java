package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;
import com.coremedia.common.util.Predicate;
import org.springframework.beans.factory.annotation.Required;

public class ExcludeFromSearchSitemapPredicate implements Predicate {

  private String doctypeName;
  private String notSearchablePropertyName;

  @Override
  public boolean include(Object o) {
    return o instanceof Content && checkIsSearchable((Content) o);
  }

  /**
   * If its not the content type or the searchable flag is not set, return true, false otherwise
   *
   * @param o any content that must be checked
   * @return true if the content is not from given doctype or the searchable flag is not set.
   */
  private boolean checkIsSearchable(Content o) {
    boolean hasSearchableFlag = o.getType().isSubtypeOf(doctypeName);
    if (!hasSearchableFlag) {
      return true;
    }

    //for simpler reading save it to a positive named variable instead of negotiated variable
    //notSearchableProperty < 0 means: Searchable!
    boolean isSearchable = o.getInt(notSearchablePropertyName) < 0;

    if (!isSearchable) {
      return false;
    }

    return true;
  }

  @Required
  public void setDoctypeName(String doctypeName) {
    this.doctypeName = doctypeName;
  }

  @Required
  public void setNotSearchablePropertyName(String notSearchablePropertyName) {
    this.notSearchablePropertyName = notSearchablePropertyName;
  }
}

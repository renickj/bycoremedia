package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.cap.content.Content;

public class TaxonomyValuePathKeyFactory extends TreePathKeyFactory {
  @Override
  public String getPathSegment(Content content) {
    return content.getString(CMTaxonomy.VALUE);
  }
}

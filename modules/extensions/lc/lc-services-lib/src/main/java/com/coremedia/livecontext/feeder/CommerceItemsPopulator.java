package com.coremedia.livecontext.feeder;

import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.TextParameters;
import com.coremedia.cap.feeder.populate.FeedablePopulator;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.contentbeans.CMMarketingSpot;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.objectserver.beans.ContentBean;

import java.util.Collections;
import java.util.List;

public class CommerceItemsPopulator implements FeedablePopulator<Object> {

  @Override
  public void populate(MutableFeedable mutableFeedable, Object source) {
    if (mutableFeedable == null || source == null) {
      throw new IllegalArgumentException("mutableFeedable and source must not be null");
    }

    Content content = null;
    if (source instanceof ContentBean) {
      content = ((ContentBean) source).getContent();
    } else if (source instanceof Content) {
      content = (Content) source;
    }

    if (content != null) {
      if (content.getType().getName().equals(CMProductTeaser.NAME)
              || content.getType().getName().equals(CMMarketingSpot.NAME)
              || content.getType().getName().equals(CMExternalChannel.NAME)) {
        String externalId = content.getString(CMProductTeaser.EXTERNAL_ID);
        if (externalId != null && !externalId.isEmpty()) {
          mutableFeedable.setElement(SearchConstants.FIELDS.COMMERCE_ITEMS.toString(), Collections.singleton(externalId), TextParameters.NONE.asMap());
        }
      } else if (content.getType().isSubtypeOf(CMLinkable.NAME)) {
        List<String> assignedProductPartNumbers = ProductReferenceHelper.getExternalIds(content);
        if (!assignedProductPartNumbers.isEmpty()){
          mutableFeedable.setElement(SearchConstants.FIELDS.COMMERCE_ITEMS.toString(), assignedProductPartNumbers, TextParameters.NONE.asMap());
        }
      }
    }
  }


}

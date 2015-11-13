package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.feeds.FeedFormat;
import com.coremedia.xml.MarkupUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Generated extension class for immutable beans of document type "CMCollection".
 */
public class CMCollectionImpl<T> extends CMCollectionBase<T> {
  @Override
  public List<T> getItemsFlattened() {
    return getItems();
  }

  @Override
  public FeedFormat getFeedFormat() {
    // determine the target feed format
    FeedFormat configuredFeedFormat = FeedFormat.Rss_2_0; // RSS is the default format
    String formatSetting = getSettingsService().setting("site.rss.format", String.class, this);
    if (FeedFormat.Atom_1_0.toString().equals(formatSetting)) {
      configuredFeedFormat = FeedFormat.Atom_1_0;
    }
    return configuredFeedFormat;
  }

  @Override
  public List<T> getFeedItems() {
    return getItems();
  }

  @Override
  public String getFeedTitle() {
    return StringUtils.isNotBlank(getTeaserTitle()) ? getTeaserTitle() : StringUtils.EMPTY;
  }

  @Override
  public String getFeedDescription() {
    String description = null;
    if(getTeaserText() != null) {
      description = MarkupUtil.asPlainText(getTeaserText());
    }
    else if(getDetailText() != null) {
      description = MarkupUtil.asPlainText(getDetailText());
    }

    return description;
  }
}

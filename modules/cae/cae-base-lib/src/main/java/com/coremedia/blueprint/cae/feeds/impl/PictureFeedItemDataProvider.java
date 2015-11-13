package com.coremedia.blueprint.cae.feeds.impl;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.xml.MarkupUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;


public class PictureFeedItemDataProvider extends TeasableFeedItemDataProvider {
  @Override
  public boolean isSupported(Object item) {
    return (super.isSupported(item) && CMPicture.class.isAssignableFrom(item.getClass()));
  }

  @Override
  public String getTitle(HttpServletRequest request, HttpServletResponse response, CMTeasable teasable) {
    return getMediaTitle(teasable);
  }

  @Override
  protected String getText(CMTeasable teasable) {
    String textPlain = MarkupUtil.asPlainText(teasable.getTeaserText());
    if (textPlain==null || textPlain.length()==0) {
      textPlain = ((CMPicture) teasable).getAlt();
    }
    return textPlain;
  }

  @Override
  protected List<CMTeasable> getRelatedMediaContents(CMTeasable teasable) {
    return Collections.singletonList(teasable);
  }
}
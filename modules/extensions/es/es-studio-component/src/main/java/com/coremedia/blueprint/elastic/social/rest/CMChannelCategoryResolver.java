package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.elastic.social.rest.api.CategoryKeyAndDisplay;

import javax.annotation.Nonnull;
import javax.inject.Named;

@Named
public class CMChannelCategoryResolver implements CategoryResolver {

  // copied from content beans definitions
  public static final String CMCHANNEL_SEGMENT = "segment";
  public static final String CMCHANNEL_TITLE = "title";
  public static final String CMCHANNEL_DOCTYPE = "CMChannel";

  @Override
  public CategoryKeyAndDisplay resolve(@Nonnull Content content) {
    return handlesType(content.getType()) ?
            new CategoryKeyAndDisplay(content.getString(CMCHANNEL_SEGMENT), content.getString(CMCHANNEL_TITLE)) : null;
  }

  private boolean handlesType(ContentType contentType) {
    return contentType.isSubtypeOf(CMCHANNEL_DOCTYPE);
  }
}

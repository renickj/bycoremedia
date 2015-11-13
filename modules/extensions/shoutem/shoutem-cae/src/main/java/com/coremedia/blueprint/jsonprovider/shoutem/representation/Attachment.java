package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.blueprint.common.contentbeans.CMVisual;
import com.coremedia.cap.common.IdHelper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Represents a CMVisual.
 */
public class Attachment {
  // unique identifier of the attachment (unique within the post)
  private int id;
  // type of attachment, ex: 'image', 'video'
  private String type;
  // url to the attachment
  private String src;
  // optional
  private int width;
  // optional
  private int height;
  // optional. url to (video) thumbnail
  private String thumbnail_url; // NOSONAR
  private CMMedia media;

  public Attachment(String type, CMMedia media, String url, String thumbnailUrl) {
    this(type, media, url, thumbnailUrl, 0, 0);
  }

  public Attachment(String type, CMMedia media, String url, String thumbnailUrl, int width, int height) {
    this.media = media;
    this.id = IdHelper.parseContentId(media.getContent().getId());
    this.width = width;
    this.height = height;
    this.type = type;
    this.thumbnail_url = thumbnailUrl;
    this.src = url;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Integer getId() {
    return id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getType() {
    return type;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getSrc() {
    return src;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public int getWidth() {
    if (media.getContent().getType().isSubtypeOf(CMVisual.NAME)) {
      Integer widthInContent = ((CMVisual) media).getWidth();
      if (widthInContent != null && widthInContent > 0) {
        width = widthInContent;
      }
    }
    return width;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public int getHeight() {
    if (media.getContent().getType().isSubtypeOf(CMVisual.NAME)) {
      Integer heightInContent = ((CMVisual) media).getHeight();
      if (heightInContent != null && heightInContent > 0) {
        height = heightInContent;
      }
    }
    return height;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getThumbnail_url() {// NOSONAR
    return thumbnail_url;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getBuy_link() {// NOSONAR
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Attachment attachment = (Attachment) o;

    if (attachment.getId().equals(getId())) {
      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return media != null ? media.hashCode() : 0;
  }
}

package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.jsonprovider.shoutem.BBCodeParser;
import com.coremedia.blueprint.jsonprovider.shoutem.ShoutemApiCredentials;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.models.ModelException;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Represents the comment of a post/an article.
 */
public class PostComment {
  private static final Logger LOG = LoggerFactory.getLogger(PostComment.class);
  private String comment_id;
  // optional or id of the parent comment
  private String parent_comment_id;
  private Date published_at;
  private String author;
  // unique identifier of the post author (present only if author is not anonymous)
  private String author_id;
  // optional url image (avatar)
  private String author_image_url;
  private String message;
  // string - wheter this post can be commented. possible values: "yes", "no","denied"
  private String likeable;
  // optional - only set if current comment ca be "liked"
  private int likes_count;
  // boolean - whether this comment can be deleted by the current user. typically true for user's own comments
  private boolean deletable;
  //indicates if the comment was approved or no
  private boolean approved;
  // for future use
  private String subject;

  public PostComment(ShoutemApiCredentials credentials, com.coremedia.elastic.social.api.comments.Comment comment, String likeable) {
    this.comment_id = comment.getId();
    this.published_at = comment.getCreationDate();
    String authorName = comment.getAuthorName();
    String authorId = "";
    Blob image = null;
    boolean isDeletable = false;
    String imageId = null;
    CommunityUser communityUser = comment.getAuthor();
    if (communityUser != null) {
      try {
        authorId = communityUser.getId();
        isDeletable = authorId.equals(credentials.getUser().getId());
        boolean anonymous = communityUser.isAnonymous();
        if (!anonymous) {
          authorName = communityUser.getName();
          image = communityUser.getImage();
        }
      } catch (ModelException e) {
        LOG.warn("Could not resolve reference from comment {} to author: {}", comment.getId(), e.getMessage());
      }
      if (image != null) {
          imageId = image.getId();
      }
    }
    this.author = StringUtils.isBlank(authorName) ? "anonymous" : authorName;
    this.author_id = authorId;
    if(imageId != null) {
      this.author_image_url = credentials.getHost() + "elastic/image/" + imageId + "/50/50";
    }
    this.message = BBCodeParser.parse(comment.getText());
    this.likeable = likeable;
    this.likes_count = 0;
    this.deletable = isDeletable;
    this.approved = comment.isApproved();
  }

  public String getComment_id() {// NOSONAR
    return comment_id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getParent_comment_id() {// NOSONAR
    return parent_comment_id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Date getPublished_at() {// NOSONAR
    return published_at;// NOSONAR
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getAuthor() {
    return author;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getAuthor_id() {// NOSONAR
    return author_id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getAuthor_image_url() {// NOSONAR
    return author_image_url;
  }

  public String getMessage() {
    return message;
  }

  public String getLikeable() {
    return likeable;
  }

  public int getLikes_count() {// NOSONAR
    return likes_count;
  }

  public boolean isDeletable() {
    return deletable;
  }

  public boolean isApproved() {
    return approved;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getSubject() {
    return subject;
  }
}

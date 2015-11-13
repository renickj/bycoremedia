package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.xml.MarkupUtil;
import com.coremedia.blueprint.jsonprovider.shoutem.ShoutemApi;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Represents a post, which is an article or product in our case.
 */
public class Post {
  private static final int DEFAULT_LENGTH = 100;
  private int post_id; // NOSONAR
  private List<Integer> category_ids = new ArrayList<>();// NOSONAR // array - optional. category ids post belongs to
  private Date published_at;// NOSONAR // date of publishing
  private String author; // NOSONAR // string containing author name
  private int author_id;// NOSONAR // unique identifier of the post author (present only if author is not anonymous)
  private String author_image_url;// NOSONAR // optional url image (avatar)
  private String title;
  // short summary of a post
  private String summary;
  // post content
  private String body;
  private Attachments attachments = new Attachments();
  // string - whether this post can be commented. possible values: "yes", "no", "denied". "denied" means that only logged in users can comment
  private String commentable;
  // string - whether this post can be commented. possible values: "yes", "no"
  private String likeable;
  private int comments_count;// NOSONAR // a number of comments. optional, only set if post can be commented
  private List<PostComment> comments = new ArrayList<>(); // NOSONAR// sample of the last 5 comments (in the same format as returned with posts/comments).
  private int likes_count;// NOSONAR // a number of likes. optional, only set if post can be "liked"
  // boolean that indicates if logged in user liked the post. If user is not logged in, this field should be omitted
  private boolean liked;
  // sample of up to 5 users who liked the post. If logged user is the one who liked the post, he should be the first one in this list. link, // optional link to the blog post web page
  private List<User> likes = new ArrayList<>();
  private boolean anonymous_contact_data_allowed;// NOSONAR // indicates if comments can contains arbitrary contact data

  public Post(CMTeasable content, List<Filter> filters, List<User> likes, List<PostComment> comments,
              String commentable, boolean liked, String likeable) {
    this.post_id = IdHelper.parseContentId(content.getContent().getId());
    if (content.getContent().getModificationDate() != null) {
      this.published_at = content.getContent().getModificationDate().getTime();
    }
    if (content.getContent().getCreator() != null && !content.getContent().getCreator().isDestroyed()) {
      this.author = content.getContent().getCreator().getName();
      this.author_id = IdHelper.parseUserId(content.getContent().getCreator().getId());
    }
    this.title = content.getTitle();
    if (this.title == null) {
      this.title = content.getTeaserTitle();
    }
    if (content.getTeaserText() != null) {
      String plainText = MarkupUtil.asPlainText(content.getTeaserText());
      this.summary = abbreviateString(plainText);
    }
    if (content.getDetailText() != null) {
      Markup teaserText = content.getDetailText().transform(filters);
      this.body = teaserText.asXml();
    }

    this.likeable = likeable;
    this.commentable = commentable;
    this.liked = liked;
    this.likes_count = likes.size();
    this.likes = likes;
    if (likes.size() > ShoutemApi.DEFAULT_SAMPLE_COUNT) { //NOSONAR
      this.likes = likes.subList(0, ShoutemApi.DEFAULT_SAMPLE_COUNT);//NOSONAR
    }
    this.comments_count = comments.size();
    this.comments = comments;

    //the last 5 comments as sample, so it can differ from the comments count in json
    if (comments.size() > ShoutemApi.DEFAULT_SAMPLE_COUNT) {//NOSONAR
      this.comments = comments.subList(0, ShoutemApi.DEFAULT_SAMPLE_COUNT);//NOSONAR
    }

    //recursive search for all parent categories
    List<? extends CMContext> contexts = content.getContexts();
    for (CMContext context : contexts) {
      resolveCategories(context.getContent());
    }
  }

  /**
   * Recursive search for all parent channels.
   *
   * @param channel
   */
  private void resolveCategories(Content channel) {
    category_ids.add(IdHelper.parseContentId(channel.getId()));
    Collection<Content> referrers = channel.getReferrersWithType(CMChannel.NAME);
    for (Content referrer : referrers) {
      resolveCategories(referrer);
    }
  }

  private String abbreviateString(String value) {
    return StringUtils.abbreviate(value.trim(), DEFAULT_LENGTH);
  }

  public int getPost_id() {// NOSONAR
    return post_id;
  }

  public List<Integer> getCategory_ids() {// NOSONAR
    return category_ids;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Date getPublished_at() {// NOSONAR
    return published_at; // NOSONAR
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getAuthor() {
    return author;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public int getAuthor_id() {// NOSONAR
    return author_id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getAuthor_image_url() {// NOSONAR
    return author_image_url;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getTitle() {
    return title;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getSummary() {
    return summary;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getBody() {
    return body;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getImage_url() { // NOSONAR
    return null;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Attachments getAttachments() {
    return attachments;
  }

  public String getCommentable() {
    return commentable;
  }

  public String getLikeable() {
    return likeable;
  }

  public int getComments_count() {// NOSONAR
    return comments_count;
  }

  public List<PostComment> getComments() {
    return comments;
  }

  public int getLikes_count() {// NOSONAR
    return likes_count;
  }

  public boolean isLiked() {
    return liked;
  }

  public List<User> getLikes() {
    return likes;
  }

  public boolean isAnonymous_contact_data_allowed() {// NOSONAR
    return anonymous_contact_data_allowed;
  }

}

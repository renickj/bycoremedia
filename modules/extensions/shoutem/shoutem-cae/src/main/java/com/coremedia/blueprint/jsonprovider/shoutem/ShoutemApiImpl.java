package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.TransformedBlobHandler;
import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.blueprint.cae.search.solr.SolrSearchQueryBuilder;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMAudio;
import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.contentbeans.CMVideo;
import com.coremedia.blueprint.common.contentbeans.CMVisual;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.base.navigation.context.ContentRootNavigationsBySegmentCacheKey;
import com.coremedia.blueprint.common.util.ContentBeanSolrSearchFormatHelper;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Attachments;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Categories;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PageableRepresentation;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Post;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PostComment;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.ServiceInfo;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Session;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.User;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.api.SortOrder;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.ratings.Like;
import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.links.HtmlUriFilter;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.xml.DefaultNamespaceFilter;
import com.coremedia.xml.Filter;
import com.coremedia.xml.NamespaceMapper;
import com.coremedia.xml.Xlink;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;
import static com.coremedia.elastic.social.api.ContributionType.REGISTERED;
import static com.coremedia.elastic.social.api.ContributionType.ANONYMOUS;

/**
 * Implements all methods of the Shoutem API.
 */
public class ShoutemApiImpl implements ShoutemApi {
  private static final Logger LOG = LoggerFactory.getLogger(ShoutemApiImpl.class);

  private static final String RICHTEXT_NAMESPACE = "http://www.coremedia.com/2003/richtext-1.0";
  private static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

  private static final String ELASTIC_SOCIAL_SETTINGS = "elasticSocial";
  private static final String ELASTIC_SOCIAL_SETTING_POST_MODERATION = "POST_MODERATION";

  private SearchResultFactory resultFactory;
  private CapConnection capConnection;
  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;
  private LinkFormatter linkFormatter;
  private Map<String, Object> elasticSettings;

  @Inject
  private LikeService likeService;
  @Inject
  private CommentService commentService;
  @Inject
  private SettingsService settingsService;

  @Required
  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }

  @Required
  public void setResultFactory(SearchResultFactory resultFactory) {
    this.resultFactory = resultFactory;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public void setElasticSocialSettings(Map<String,Object> settingsMap) {
    this.elasticSettings = settingsMap;
  }

  @Override
  public ServiceInfo getServiceInfo() {
    return new ServiceInfo();
  }

  @Override
  public Session getUserAuthenticate(ShoutemApiCredentials credentials, String login, String password) {
    return credentials.authenticate(login, password);
  }

  @Override
  public boolean getUserLogout(ShoutemApiCredentials credentials) {
    //there is no real logout possible
    return true;
  }

  @Override
  public Categories getPostsCategories(ShoutemApiCredentials credentials) {
    Categories categories = new Categories();
    //apply site navigation document as path filer
    CMNavigation document = getSiteNavigation(credentials.getSite());
    addCategory(categories, document);
    final List<? extends Linkable> firstLevel = document.getVisibleChildren();
    for (Linkable first : firstLevel) {
      if (first instanceof Navigation) {
        Navigation firstNav = (Navigation)first;
        addCategory(categories, firstNav);
        List<? extends Linkable> secondLevel = firstNav.getVisibleChildren();
        for (Linkable second : secondLevel) {
          if (second instanceof Navigation) {
            addCategory(categories, (Navigation)second);
          }
        }
      }
    }

    return categories;
  }

  /**
   * Adds a navigation item to the list of categories.
   * @param categories The categories representation instance.
   * @param nav The navigation to add.
   */
  private void addCategory(Categories categories, Navigation nav) {
    CMNavigation navigation = (CMNavigation) nav;
    String title = navigation.getTitle();
    if(StringUtils.isEmpty(title)) {
      title = navigation.getContent().getName();
    }
    categories.addCategory(IdHelper.parseContentId(navigation.getContent().getId()), title);
  }

  @Override
  public PageableRepresentation getPostsFind(ShoutemApiCredentials credentials,
                                             HttpServletRequest request, HttpServletResponse response,
                                             Integer category,
                                             String term, Integer offset, Integer limit) {
    PageableRepresentation rep = new PageableRepresentation();
    SearchQueryBean search = new SearchQueryBean();
    List<String> types = new ArrayList<>();
    types.add(CMArticle.NAME);
    search.setQuery(SolrSearchQueryBuilder.ANY_FIELD_ANY_VALUE);
    search.setLimit(limit);
    search.setSortFields(Arrays.asList(SearchConstants.FIELDS.MODIFICATION_DATE.toString()));
    search.addFilter(Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(types)));

    //apply search term to the query
    if (term != null && term.length() > DEFAULT_MIN_SEARCH_TERM_LENGTH) {
      search.addFilter(Condition.is(SearchConstants.FIELDS.TEASER_TEXT, Value.anyOf(Arrays.asList(term))));
    }
    //apply category/channel filter to the query
    if (category != null && category > 0) {
      CMNavigation document = (CMNavigation)findContentBean(String.valueOf(category));
      List<String> convertedNavigation = ContentBeanSolrSearchFormatHelper.cmNavigationsToId(Arrays.asList(document));
      search.addFilter(Condition.is(SearchConstants.FIELDS.NAVIGATION_PATHS, Value.anyOf(convertedNavigation)));
    }

    //finally fire search
    SearchResultBean result = resultFactory.createSearchResultUncached(search);
    List<?> hits = result.getHits();
    for (Object hit : hits) {
      if (hit instanceof CMTeasable) {
        Post post = createPost(credentials, request, response, (CMTeasable) hit);
        rep.addItem(post);
      }
    }
    rep.filter(credentials.getSessionId(), METHOD_GET_POSTS_FIND, offset, limit);
    return rep;
  }


  @Override
  public Post getPostsGet(ShoutemApiCredentials credentials, HttpServletRequest request, HttpServletResponse response, String postId) {
    CMTeasable content = (CMTeasable)findContentBean(postId);
    if (content != null) {
      return createPost(credentials, request, response, content);
    }
    return null;
  }

  @Override
  public PageableRepresentation getPostsLikes(ShoutemApiCredentials credentials, String postId, Integer offset, Integer limit) {
    PageableRepresentation likes = new PageableRepresentation();
    CMObject content = findContentBean(postId);
    if (content != null) {
      final CommunityUser user = credentials.getUser();
      List<Like> postLikes = likeService.getLikesForTarget(content, 100000);//NOSONAR
      Collections.sort(postLikes, new LikesComparator(user));
      for (Like like : postLikes) {
        likes.addItem(new User(like));
      }

      likes.filter(credentials.getSessionId(), METHOD_GET_POSTS_LIKES, PARAM_POST_ID, String.valueOf(postId), offset, limit);
    }
    return likes;
  }

  @Override
  public boolean postPostsLikesNew(ShoutemApiCredentials credentials, String postId) {
    CMObject content = findContentBean(postId);
    if (content != null) {
      CommunityUser user = credentials.getUser();
      likeService.updateLike(user, content, null, true);
      return true;
    }
    return false;
  }

  @Override
  public boolean postPostsLikesDelete(ShoutemApiCredentials credentials, String postId) {
    CMObject content = findContentBean(postId);
    if (content != null) {
      CommunityUser user = credentials.getUser();
      likeService.updateLike(user, content, null, false);
      return true;
    }
    return false;
  }


  @Override
  public PageableRepresentation getPostsComments(ShoutemApiCredentials credentials, String postId, Integer offset, Integer limit) {
    PageableRepresentation comments = new PageableRepresentation();
    CMObject content = findContentBean(postId);
    if (content != null) {
      List<Comment> elasticComments = commentService.getComments(content, null, SortOrder.DESCENDING, 100000);//NOSONAR
      for (Comment comment : elasticComments) {
        PostComment pComment = createComment(credentials, comment);
        if (pComment != null) {
          comments.addItem(pComment);
        }
      }

      comments.filter(credentials.getSessionId(), METHOD_GET_POSTS_COMMENTS, offset, limit);
    }
    return comments;
  }

  @Override
  public PostComment postPostsCommentsNew(ShoutemApiCredentials credentials, String postId, String parentCommentId, String authorId, String authorNickName,
                                          String authorEmail, String authorUrl, String message, String subject) { // NOSONAR
    if ((credentials.isAnonymous() && (authorNickName == null || authorEmail == null)) || StringUtils.isEmpty(message)) {
      Response r = Response.status(401).entity("MANDATORY PARAMETERS FOR ANONYMOUS COMMENT NOT SET").build();//NOSONAR
      throw new WebApplicationException(r);
    }

    CommunityUser user = credentials.getUser();
    user.setName(authorNickName);
    user.setEmail(authorEmail);
    CMObject content = findContentBean(postId);
    if (content != null) {
      Comment replyTo = commentService.getComment(parentCommentId);
      Comment newComment = commentService.createComment(user, message,
              content, Collections.<String>emptyList(), replyTo);
      if (newComment != null) {
        newComment.save();
        return new PostComment(credentials, newComment, ShoutemApi.VALUE_NO);
      }
    }
    return null;
  }

  @Override
  public PostComment postPostsCommentsUpdate(ShoutemApiCredentials credentials, String commentId, String message) {
    CommunityUser user = credentials.getUser();
    Comment communityComment = commentService.getComment(commentId);
    if (communityComment != null) {
      communityComment.setText(message);
      communityComment.setAuthor(user);
      communityComment.save();
      return new PostComment(credentials, communityComment, ShoutemApi.VALUE_NO);
    }
    return null;
  }

  @Override
  public boolean postPostsCommentsDelete(ShoutemApiCredentials credentials, String commentId) {
    Comment communityComment = commentService.getComment(commentId);
    if (communityComment != null) {
      communityComment.remove();
      return true;
    }
    return false;
  }

  //---------- Helper --------------

  /**
   * Returns the filters that are applied when richtext is formatted to HTML
   */
  private List<Filter> getXMLFilters() {
    return Arrays.asList(
            //new UriFormatter(linkFormatter, idProvider, request, response),    //NOSONAR // reformat uris
            new HtmlUriFilter(),                                       //NOSONAR // replace xlink with xhtml equivalent
            new NamespaceMapper(RICHTEXT_NAMESPACE, XHTML_NAMESPACE),  //NOSONAR // rewrite all richtext to xhtml namespace
            new NamespaceMapper(Xlink.NAMESPACE_URI, null),            //NOSONAR // kill all xlink references
            new DefaultNamespaceFilter(XHTML_NAMESPACE),
            new ShoutemFilter());                //NOSONAR // tell the serializer that xhtml prefix is ""
  }

  /**
   * Creates a new post representation, including the media items as attachments.
   */
  private Post createPost(ShoutemApiCredentials credentials, HttpServletRequest request, HttpServletResponse response, CMTeasable content) {
    if (content != null) {
      List<Like> likes = likeService.getLikesForTarget(content, 100000);//NOSONAR

      //try to resolve if the current user already likes the given article/post.
      boolean liked = false;
      CommunityUser user = credentials.getUser();
      for (Like like : likes) {
        if (like.getAuthor().getId().equals(user.getId())) {
          liked = true;
          break;
        }
      }

      //resolve likeable and commentable status and create post representation
      String likeable = isLikeable(credentials);
      String commentable = isCommentable(credentials);
      Post post = new Post(content, getXMLFilters(), convertLikes(likes), getComments(credentials, content),
              commentable, liked, likeable);

      //add media items.
      List<? extends CMTeasable> related = content.getRelated();
      for (CMTeasable relatedItem : related) {
        if (relatedItem.getContent().getType().isSubtypeOf(CMMedia.NAME)) {
          addMediaItem(request, response, post.getAttachments(), relatedItem);
        }
      }
      if (content.getPicture() != null) {
        addMediaItem(request, response, post.getAttachments(), content.getPicture());
      }
      for (CMPicture picture : content.getPictures()) {
        addMediaItem(request, response, post.getAttachments(), picture);
      }

      return post;
    }
    return null;
  }

  /**
   * Checks the type of the teasable and adds it to the attachment representation
   * if the given teasable is a media item.
   *
   * @param attachments The attachment representation of a post.
   * @param item        The item to check the media attributes for.
   */
  private void addMediaItem(HttpServletRequest request, HttpServletResponse response, Attachments attachments, CMTeasable item) {
    ContentType type = item.getContent().getType();
    if (type.isSubtypeOf(CMPicture.NAME)) {
      CMPicture image = (CMPicture) item;
      Blob blob = image.getTransformedData(DEFAULT_IMAGE_RATIO);
      String url = resolveBlobUrl(request, response, blob, image, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
      String thumbnailUrl = resolveBlobUrl(request, response, blob, image, DEFAULT_THUMBNAIL_WIDTH, DEFAULT_THUMBNAIL_HEIGHT);
      attachments.addImage(image, url, thumbnailUrl, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
    } else if (type.isSubtypeOf(CMVideo.NAME)) {
      CMVideo video = (CMVideo) item;
      String url = resolveBlobUrl(request, response, video.getData(), video, 0, 0);
      String thumbnailUrl = resolveBlobUrl(request, response, video.getData(), video, 0, 0);
      attachments.addVideo(video, url, thumbnailUrl, video.getWidth(), video.getHeight());
    } else if (type.isSubtypeOf(CMAudio.NAME)) {
      CMAudio audio = (CMAudio) item;
      String url = resolveBlobUrl(request, response, audio.getData(), audio, 0, 0);
      attachments.addAudio((CMMedia) item, url, null);
    }
  }

  /**
   * Helper for finding a content bean.
   *
   * @param id The numeric content id too lookup.
   * @return The content bean or null.
   */
  private CMObject findContentBean(String id) {
    String capId = IdHelper.formatContentId(id);
    Content teasable = capConnection.getContentRepository().getContent(capId);
    if (teasable != null) {
      return contentBeanFactory.createBeanFor(teasable, CMObject.class);
    }

    Response r = Response.status(403).entity("NO CONTENT BEAN FOUND FOR'" + id + "'").build();//NOSONAR
    throw new WebApplicationException(r);
  }


  /**
   * Converts the given list of elastic objects to representations.
   *
   * @param likes Converts elastic likes to the shoutem format that is represented by a user.
   * @return The list of users that clicked like.
   */
  private List<User> convertLikes(List<Like> likes) {
    List<User> users = new ArrayList<>();
    for (Like like : likes) {
      users.add(new User(like));
    }
    return users;
  }

  /**
   * Returns the comments for the given teasable.
   *
   * @return A list of comment representations for the given content.
   */
  private List<PostComment> getComments(ShoutemApiCredentials credentials, CMObject content) {
    List<Comment> elasticComments = commentService.getComments(content, null, SortOrder.DESCENDING, 100000);//NOSONAR
    List<PostComment> comments = new ArrayList<>();
    for (Comment comment : elasticComments) {
      PostComment pComment = createComment(credentials, comment);
      if (pComment != null) {
        comments.add(pComment);
      }
    }
    return comments;
  }


  /**
   * Resolves the CAE link for the media data blob of the given CMMedia.
   */
  private String resolveBlobUrl(HttpServletRequest request, HttpServletResponse response, Blob blob, CMMedia mediaItem, int width, int height) {
    Map<String, String> params = new HashMap<>();
    request.setAttribute(ABSOLUTE_URI_KEY, true);
    if (blob != null) {
      try {
        if (width > 0 && height > 0) {
          LOG.debug("Applying image properties of " + TransformedBlobHandler.class);
          saveAndAdd(params, TransformedBlobHandler.WIDTH_SEGMENT, String.valueOf(width), request);
          saveAndAdd(params, TransformedBlobHandler.HEIGHT_SEGMENT, String.valueOf(height), request);
          request.setAttribute(ViewUtils.PARAMETERS, params);
        }

        return linkFormatter.formatLink(blob, null, request, response, false);
      } finally {
        for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
          Map.Entry e = (Map.Entry) stringStringEntry;
          request.removeAttribute((String) e.getKey());
        }
      }
    }
    if (mediaItem instanceof CMVisual) {
      return ((CMVisual) mediaItem).getDataUrl();
    }
    return null;
  }

  private void saveAndAdd(Map<String, String> map, String key, String value, HttpServletRequest request) {
    if (StringUtils.isNotEmpty(value)) {
      request.setAttribute(key, value);
      map.put(key, value);
    }
  }

  /**
   * Creates a comment if the comment has a state that can be published.
   *
   * @param credentials The API credentials
   * @param comment     The comment to create the representation for.
   * @return The comment wrapper or null if the comment should not be published or processed.
   */
  private PostComment createComment(ShoutemApiCredentials credentials, Comment comment) {
    // todo
    if (comment.isApproved() || isPostModeration(credentials)) {
      return new PostComment(credentials, comment, ShoutemApi.VALUE_NO);
    }
    return null;
  }

  /**
   * Returns whether the post is commentable or not.
   *
   * @return "yes" if the user can comment the post.
   */
  private String isCommentable(ShoutemApiCredentials credentials) {
    Object setting = getElasticSetting(credentials.getSite(), "commentType");
    if (isEnabled(credentials, setting)) {
      return ShoutemApi.VALUE_YES;
    }
    return ShoutemApi.VALUE_NO;
  }

  /**
   * Returns whether the post is likeable or not.
   *
   * @return "yes" if the user can like the post.
   */
  private String isLikeable(ShoutemApiCredentials credentials) {
    Object setting = getElasticSetting(credentials.getSite(), "likeType");
    if (isEnabled(credentials, setting)) {
      return ShoutemApi.VALUE_YES;
    }
    return ShoutemApi.VALUE_NO;
  }

  private boolean isEnabled(ShoutemApiCredentials credentials, Object setting) {
    return setting != null && (ANONYMOUS.toString().equals(setting) || (REGISTERED.toString().equals(setting) && !credentials.getUser().isAnonymous()));
  }

  /**
   * Checks if Elastic Social uses pre-moderation.
   *
   * @return True if elastic social is configured with pre-moderation.
   */
  private boolean isPostModeration(ShoutemApiCredentials credentials) {
    Object setting = getElasticSetting(credentials.getSite(), "userModerationType");
    return setting != null && String.valueOf(setting).equalsIgnoreCase(ELASTIC_SOCIAL_SETTING_POST_MODERATION);
  }

  /**
   * Returns the root navigation content bean for the given site.
   * @param site The site to find the navigation for.
   */
  private CMNavigation getSiteNavigation(String site) {
    ContentRootNavigationsBySegmentCacheKey cacheKey = new ContentRootNavigationsBySegmentCacheKey(sitesService);
    Map<String, Content> rootNavigations = capConnection.getCache().get(cacheKey);
    Content navigation = rootNavigations.get(site);
    if (navigation == null) {
      Response r = Response.status(501).entity("SITE SEGMENT '" + site + "' NOT FOUND IN NAVIGATION CACHE").build(); //NOSONAR
      throw new WebApplicationException(r);
    }
    return contentBeanFactory.createBeanFor(navigation, CMNavigation.class);
  }


  /**
   * Returns the root channel for the given site.
   */
  public Object getElasticSetting(String site, String settingKey) {
    if(elasticSettings == null) {
      elasticSettings = settingsService.settingAsMap(ELASTIC_SOCIAL_SETTINGS, String.class, Object.class, getSiteNavigation(site));
    }
    return elasticSettings.get(settingKey);
  }
}

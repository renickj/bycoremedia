package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.jsonprovider.shoutem.representation.Categories;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PageableRepresentation;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Post;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PostComment;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.ServiceInfo;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Defines all method names of the Shoutem API.
 */
public interface ShoutemApi {
  /**
   * Defaults
   */
  int DEFAULT_SAMPLE_COUNT = 5;
  //see shoutem spec
  int DEFAULT_LIMIT = 100;
  int DEFAULT_MIN_SEARCH_TERM_LENGTH = 2;
  String DEFAULT_IMAGE_RATIO = "landscape_ratio4x3";
  int DEFAULT_IMAGE_WIDTH = 400;
  int DEFAULT_IMAGE_HEIGHT = 300;
  int DEFAULT_THUMBNAIL_WIDTH = 100;
  int DEFAULT_THUMBNAIL_HEIGHT = 75;

  /**
   * Properties value constants
   */
  String VALUE_NO = "no";
  String VALUE_YES = "yes";

  /**
   * The method params
   */
  String PARAM_METHOD = "method";
  // - Optional - Category to filter.
  String PARAM_CATEGORY_ID = "category_id";
  // - Optional - Search term for full text search across posts
  String PARAM_TERM = "term";
  // Optional - has to be set if posts in that category require authenticated user
  String PARAM_SESSION_ID = "session_id";
  // Offset of the first record. Optional, defaults to 0.
  String PARAM_OFFSET = "offset";
  // Number of results per page. Optional, defaults to 100.
  String PARAM_LIMIT = "limit";
  String PARAM_POST_ID = "post_id";
  String PARAM_COMMENT_ID = "comment_id";
  // - (optional)
  String PARAM_PARENT_COMMENT_ID = "parent_comment_id";
  //- (optional)
  String PARAM_AUTHOR_ID = "author_id";
  //- author name. required when comment is anonymous (session_id is not set)
  String PARAM_AUTHOR_NICKNAME = "author_nickname";
  // - author email. required only when comment is anonymous (session_id is not set)
  String PARAM_AUTHOR_EMAIL = "author_email";
  // - author's website url. it is optional but usually provided when comment is anonymous (session_id is not set)
  String PARAM_AUTHOR_URL = "author_url";
  //- Comment
  String PARAM_MESSAGE = "message";
  // - (optional) Comment's subject (Drupal has it, for example)
  String PARAM_SUBJECT = "subject";
  //Username (or email) used to sign in
  String PARAM_USERNAME = "username";
  String PARAM_PASSWORD = "password"; // NOSONAR false positive: Credentials should not be hard-coded


  /**
   * The list of API methods below.
   */
  String METHOD_GET_SERVICE_INFO = "service/info";

  String METHOD_POST_USERS_AUTHENTICATE = "users/authenticate";//NOSONAR
  String METHOD_POST_USERS_LOGOUT = "users/logout"; //NOSONAR

  String METHOD_GET_POSTS_CATEGORY = "posts/categories";
  String METHOD_GET_POSTS_FIND = "posts/find";
  String METHOD_GET_POSTS_GET = "posts/get";

  String METHOD_GET_POSTS_LIKES = "posts/likes";
  String METHOD_POST_POSTS_LIKES_NEW = "posts/likes/new";
  String METHOD_POST_POSTS_LIKES_DELETE = "posts/likes/delete";

  String METHOD_GET_POSTS_COMMENTS = "posts/comments";
  String METHOD_POST_POSTS_COMMENTS_NEW = "posts/comments/new";
  String METHOD_POST_POSTS_COMMENTS_UPDATE = "posts/comments/update";
  String METHOD_POST_POSTS_COMMENTS_DELETE = "posts/comments/delete";

  /**
   * Returns version and name of this service.
   * Example: GET http://localhost:40080/blueprint/media/shoutemapi?method=service/info
   *
   * @return Service info values.
   */
  ServiceInfo getServiceInfo();

  /**
   * Authenticates a user
   *
   * @param login    The username or email of the user.
   * @param password The password of the user.
   * @return The session with the user id.
   */
  Session getUserAuthenticate(ShoutemApiCredentials credentials, String login, String password);

  /**
   * Invalidates the user session.
   *
   * @return True, if the use is logged out successfully.
   * @param credentials
   */
  boolean getUserLogout(ShoutemApiCredentials credentials);

  /**
   * Returns list of all post categories.
   * We use taxonomies as categories here.
   * Example: GET http://localhost:40080/blueprint/media/shoutemapi?method=posts/categories
   *
   * @return The list of categories/taxonomies.
   * @param credentials
   */
  Categories getPostsCategories(ShoutemApiCredentials credentials);

  /**
   * Lookup for all posts matching the given search criteria.
   * See IShoutem interface for parameter details.
   * Example: GET http://localhost:40080/blueprint/media/shoutemapi?method=posts/find&category_id=2&session_id=9303242390582903&limit=10&offset=50
   *
   * @return A pageable representation of filtered posts.
   */
  PageableRepresentation getPostsFind(ShoutemApiCredentials credentials,
                                      HttpServletRequest request, HttpServletResponse response,
                                      Integer category,
                                      String term, Integer offset, Integer limit);

  /**
   * Lookup for a single post.
   * See IShoutem interface for parameter details.
   * Example GET http://localhost:40080/blueprint/media/shoutemapi?method=posts/get&post_id=12428&session_id=9303242390582903
   *
   * @return The post representation or null if post was not found.
   */
  Post getPostsGet(ShoutemApiCredentials credentials, HttpServletRequest request, HttpServletResponse response, String postId);

  /**
   * Returns the user data of the likes of the post with the given id.
   * Be aware that paging should be supported here.
   * See IShoutem interface for parameter details.
   * GET http://localhost:40080/blueprint/media/shoutemapi?method=posts/likes&post_id=33
   *
   * @return A representation including paging links.
   */
  PageableRepresentation getPostsLikes(ShoutemApiCredentials credentials, String postId, Integer offset, Integer limit);

  /**
   * Applies a like of the current user to the post with the given id.
   * See IShoutem interface for parameter details.
   * Example: POST http://localhost:40080/blueprint/media/shoutemapi
   * data:
   * method = "posts/likes/new"
   * post_id: 33
   * session_id = "9349u823895z"
   *
   * @return True, if the operation was successful.
   */
  boolean postPostsLikesNew(ShoutemApiCredentials credentials, String postId);

  /**
   * Deletes a like of the current user of the post with the given id.
   * See IShoutem interface for parameter details.
   *
   * @return True, if the operation was successful.
   */
  boolean postPostsLikesDelete(ShoutemApiCredentials credentials, String postId);

  /**
   * Returns the comments of a post with the given id.
   * Be aware that paging should be supported here.
   * See IShoutem interface for parameter details.
   * Example: GET http://localhost:40080/blueprint/media/shoutemapi?method=posts/comments&post_id=33
   *
   * @return A representation including paging links.
   */
  PageableRepresentation getPostsComments(ShoutemApiCredentials credentials, String postId, Integer offset, Integer limit);

  /**
   * Creates a new comment of the post with the given id.
   * See IShoutem interface for parameter details.
   *
   * @return The created comment.
   */
  PostComment postPostsCommentsNew(ShoutemApiCredentials credentials, String postId, String parentCommentId, String authorId, String authorNickName, // NOSONAR
                                   String authorEmail, String authorUrl, String message, String subject);// NOSONAR

  /**
   * Updates the comment with the given id.
   * See IShoutem interface for parameter details.
   *
   * @return The updated comment.
   */
  PostComment postPostsCommentsUpdate(ShoutemApiCredentials credentials, String commentId, String message);

  /**
   * Deletes the comment with the given id.
   * See IShoutem interface for parameter details.
   */
  boolean postPostsCommentsDelete(ShoutemApiCredentials credentials, String commentId);
}

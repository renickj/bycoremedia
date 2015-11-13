package com.coremedia.blueprint.elastic.social.cae.tags;

import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.action.AuthenticationHandler;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.ratings.Like;
import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.ratings.Rating;
import com.coremedia.elastic.social.api.ratings.RatingService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

import static com.coremedia.blueprint.elastic.social.cae.user.UserContext.getUser;
import static com.coremedia.elastic.core.api.users.UserService.USERS_COLLECTION;
import static com.coremedia.elastic.social.api.comments.CommentService.COMMENTS_COLLECTION;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript;

public final class ElasticSocialFunctions {

  private ElasticSocialFunctions() {
  }

  private static final LoadingCache<Class<?>, ?> CACHE = CacheBuilder.newBuilder()
          .build(
                  new CacheLoader<Class, Object>() {
                    @Override
                    public Object load(@Nonnull Class clazz) throws Exception {
                      final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                      final WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
                      return webApplicationContext.getBean(clazz);
                    }
                  });

  private static <T> T getBeanOfType(@Nonnull Class<T> type) {
    try {
      return type.cast(CACHE.get(type));
    } catch (ExecutionException e) {
      throw new IllegalStateException(format("unable to resolve bean of type '%s'", type), e);
    }
  }

  public static @Nonnull Boolean isAnonymousUser() {
    return null == getUser();
  }

  public static @Nonnull Boolean isAnonymous(@Nonnull CommunityUser user) {
    return user.isAnonymous();
  }

  public static @Nonnull Boolean isActivated(@Nonnull CommunityUser user) {
    return user.isActivated() || user.isActivatedAndRequiresModeration();
  }

  public static Boolean isNotAuthor(@Nonnull CommunityUser user) {
    return !user.equals(getUser());
  }

  public static double getAverageRating(@Nonnull CMTeasable target) {
    final ContentWithSite contentWithSite = getContentWithSite(target);
    return getBeanOfType(RatingService.class).getAverageRating(contentWithSite);
  }

  public static Integer getRatingForCurrentUser(@Nonnull CMTeasable target) {
    CommunityUser user = getCurrentOrAnonymousUser();
    final Rating ratingForUser = getBeanOfType(RatingService.class).getRatingForUser(user, getContentWithSite(target));
    return ratingForUser != null ? ratingForUser.getValue() : 0;
  }

  public static Boolean hasLikeForCurrentUser(CMTeasable target) {
    CommunityUser user = getCurrentOrAnonymousUser();
    Like like = getBeanOfType(LikeService.class).getLikeForUser(user, getContentWithSite(target));
    return like != null;
  }

  public static Boolean hasComplaintForCurrentUser(String id, String collection) {
    CommunityUser user = getCurrentOrAnonymousUser();
    if (collection.equals(COMMENTS_COLLECTION)) {
      final CommentService commentService = getBeanOfType(CommentService.class);
      final Comment comment = commentService.getComment(id);
      return commentService.hasComplaintForUser(user, comment);
    } else if (collection.equals(USERS_COLLECTION)) {
      final CommunityUserService communityUserService = getBeanOfType(CommunityUserService.class);
      final CommunityUser communityUser = communityUserService.getUserById(id);
      return communityUserService.hasComplaintForUser(user, communityUser);
    }
    return false;
  }

  @Nonnull
  public static CommunityUser getCurrentOrAnonymousUser() {
    return getBeanOfType(ElasticSocialUserHelper.class).getCurrentOrAnonymousUser();
  }

  public static long getNumberOfRatings(CMTeasable target) {
    return getBeanOfType(RatingService.class).getNumberOfRatings(getContentWithSite(target));
  }

  public static long getNumberOfComments(CMTeasable target) {
    return getBeanOfType(CommentService.class).getNumberOfComments(getContentWithSite(target));
  }

  public static long getNumberOfLikes(@Nonnull CMTeasable target) {
    return getBeanOfType(LikeService.class).getNumberOfLikes(getContentWithSite(target));
  }

  public static String escapeJavaScript(String text) {
    return escapeEcmaScript(text);
  }

  public static Boolean isValid(CMTeasable target, HttpServletRequest request) {

    ContextHelper contextHelper = (ContextHelper) request.getAttribute(ContextHelper.NAME_CONTEXTHELPER);
    if (contextHelper == null) {
      throw new IllegalStateException("No ContextHelper available");
    }
    CMNavigation context = contextHelper.contextFor(target);

    return context != null;
  }

  public static Boolean isLoginAction(Object bean) {
    return bean instanceof CMAction && AuthenticationHandler.LOGIN_ACTION_ID.equals(((CMAction) bean).getId());
  }

  public static ContentWithSite getContentWithSite(CMTeasable target) {
    final SitesService sitesService = getBeanOfType(SitesService.class);
    final Content content = target.getContent();
    final Site site = sitesService.getContentSiteAspect(content).getSite();
    return new ContentWithSite(content, site);
  }

  public static ElasticSocialConfiguration getElasticSocialConfiguration(Page page) {
    ElasticSocialPlugin elasticSocialPlugin = getBeanOfType(ElasticSocialPlugin.class);
    return elasticSocialPlugin.getElasticSocialConfiguration(page);
  }
}

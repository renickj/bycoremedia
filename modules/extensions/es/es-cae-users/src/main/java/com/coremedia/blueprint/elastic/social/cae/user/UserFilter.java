package com.coremedia.blueprint.elastic.social.cae.user;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class UserFilter implements Filter {

  @Inject
  private CommunityUserService communityUserService;
  @Inject
  private ElasticSocialPlugin elasticSocialPlugin;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;

      CommunityUser communityUser = getLoggedInUser();
      if (communityUser != null) {
        final boolean mayLogin = communityUser.isActivated() || communityUser.isActivatedAndRequiresModeration() || communityUser.isIgnored();
        if (mayLogin && isFeedbackEnabled(request)) {
          UserContext.setUser(communityUser);
        } else {
          SecurityContextHolder.clearContext();
          httpServletRequest.logout();
        }
      }
    }
    try {
      chain.doFilter(request, response);
    } finally {
      UserContext.clear();
    }
  }

  private boolean isFeedbackEnabled(ServletRequest request) {
    Site siteFromRequest = SiteHelper.getSiteFromRequest(request);
    if(null != siteFromRequest) {
      ElasticSocialConfiguration feedbackSettings = elasticSocialPlugin.getElasticSocialConfiguration(siteFromRequest);
      return feedbackSettings.isFeedbackEnabled();
    }
    return true;
  }

  @Override
  public void destroy() {
  }

  public CommunityUser getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (authentication == null || authentication.getName().equals("anonymousUser")) ? null : getUser(authentication.getPrincipal());
  }

  public CommunityUser getUser(Object principal) {
    CommunityUser result = null;
    if (principal instanceof String) {
      result = communityUserService.getUserByName((String) principal);
      if (null == result) {
        result = communityUserService.getUserByEmail((String) principal);
      }
    } else if (principal instanceof UserPrincipal) {
      UserPrincipal userPrincipal = (UserPrincipal) principal;
      result = communityUserService.getUserById(userPrincipal.getUserId());
    }
    return result;
  }
}

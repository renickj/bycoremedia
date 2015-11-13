package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.web.links.LinkFormatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public class CustomConnectController extends ConnectController {
  @Inject
  private LinkFormatter linkFormatter;

  @Inject
  private CommunityUserService communityUserService;

  public CustomConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
    super(connectionFactoryLocator, connectionRepository);
  }

  @Override
  public RedirectView oauth1Callback(@PathVariable String providerId, NativeWebRequest request) {
    super.oauth1Callback(providerId, request);

    return createRedirectView(request);
  }

  @Override
  public RedirectView oauth2Callback(@PathVariable String providerId, NativeWebRequest request) {
    super.oauth2Callback(providerId, request);

    return createRedirectView(request);
  }

  private RedirectView createRedirectView(NativeWebRequest request) {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request.getNativeRequest();
    HttpServletResponse httpServletResponse = (HttpServletResponse) request.getNativeResponse();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CommunityUser user = communityUserService.getUserById(((Principal) authentication.getPrincipal()).getName());
    return new RedirectView(linkFormatter.formatLink(user, null, httpServletRequest, httpServletResponse, false));
  }
}

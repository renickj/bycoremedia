package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PageableRepresentation;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.Post;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.PostComment;
import com.coremedia.blueprint.jsonprovider.shoutem.representation.ServiceInfo;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.elastic.core.api.SortOrder;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.LinkScheme;
import com.coremedia.transform.TransformedBeanBlob;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.elastic.core.test.Injection.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoutemApiImplTest {
  private ShoutemApiImpl shoutemApi;

  @Mock
  private CommentService commentService;

  @Mock
  private LikeService likeService;

  @Mock
  private SearchResultFactory resultFactory;

  @Mock
  private CommunityUserService userService;

  @Mock
  private CommunityUser anonymousCommunityUser;

  @Mock
  private CommunityUser user;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private Authentication auth;

  @Mock
  private SearchResultBean srb;

  @Mock
  private Comment comment;

  private ShoutemApiCredentials credentials;
  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
          .withDataViewFactory()
          .withIdProvider()
          .withLinkFormatter()
          .withCache()
          .withSites()
          .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
          .build();

  @Before
  public void setUp() {
    when(userService.getUserByName(anyString())).thenReturn(anonymousCommunityUser);
    when(userService.createAnonymousUser()).thenReturn(user);

    credentials = new ShoutemApiCredentials();
    credentials.setAuthenticationManager(authenticationManager);
    credentials.setSite("media");
    credentials.setUserService(userService);

    shoutemApi = new ShoutemApiImpl();
    shoutemApi.setResultFactory(resultFactory);
    SearchResultBean result = new SearchResultBean();
    result.setHits(Arrays.asList(ContentTestCaseHelper.getContentBean(infrastructure, 4)));
    when(resultFactory.createSearchResultUncached(any(SearchQueryBean.class))).thenReturn(result);
    when(likeService.getLikesForTarget(any(), anyInt())).thenReturn(Collections.EMPTY_LIST);
    inject(shoutemApi, likeService);

    when(commentService.getComments(any(), any(CommunityUser.class), any(SortOrder.class), anyInt())).thenReturn(Collections.EMPTY_LIST);
    inject(shoutemApi, commentService);

    when(auth.getPrincipal()).thenReturn("user-1");
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
    credentials.authenticate("login", "pass");


    LinkFormatter linkFormatter = new LinkFormatter();
    linkFormatter.setSchemes(Arrays.asList(new GeneralPurposeLinkScheme()));

    when(resultFactory.createSearchResultUncached(Mockito.any(SearchQueryBean.class))).thenReturn(srb);
    List results = Collections.emptyList();
    when(srb.getHits()).thenReturn(results);

    shoutemApi.setCapConnection(infrastructure.getContentRepository().getConnection());
    shoutemApi.setContentBeanFactory(infrastructure.getContentBeanFactory());
    shoutemApi.setLinkFormatter(linkFormatter);
    shoutemApi.setResultFactory(resultFactory);

    Map<String, Object> elasticSettings = new HashMap<>();
    elasticSettings.put("POST_MODERATION", true);
    elasticSettings.put("likeType", "REGISTERED");
    elasticSettings.put("commentingEnabled", true);
    shoutemApi.setElasticSocialSettings(elasticSettings);
  }

  @Test
  public void testGetServiceInfo() {
    assertEquals(1, shoutemApi.getServiceInfo().getApi_version());
    assertEquals("coremedia", shoutemApi.getServiceInfo().getServer_type());
  }

  @Test
  public void testPostsGet() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    Post post = shoutemApi.getPostsGet(credentials, request, response, "4");
    assertNotNull(post);
  }

  @Test
  public void testPostComments() {
    PageableRepresentation result = shoutemApi.getPostsComments(credentials, "4", 0, 0);
    assertNotNull(result);
  }

  @Test
  public void testPostLikes() {
    PageableRepresentation result = shoutemApi.getPostsLikes(credentials, "4", 0, 0);
    assertNotNull(result);
  }

  @Test
  public void testPostFind() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    PageableRepresentation result = shoutemApi.getPostsFind(credentials, request, response, null, "title", 0, 0);
    assertNotNull(result);
  }

  @Test
  public void testServiceInfo() {
    ServiceInfo result = shoutemApi.getServiceInfo();
    assertNotNull(result);
  }

  @Test
  public void testComments() {
    String commentId = "4";
    when(commentService.createComment(any(CommunityUser.class), anyString(), anyObject(), anyCollectionOf(String.class), any(Comment.class))).thenReturn(comment);
    when(commentService.getComment(commentId)).thenReturn(comment);
    when(comment.getId()).thenReturn(commentId);
    PostComment comment = shoutemApi.postPostsCommentsNew(credentials, commentId, null, "4711", "nick", "mail", "url", "hello world", "subject");
    Assert.assertNotNull(comment);
    comment = shoutemApi.postPostsCommentsUpdate(credentials, comment.getComment_id(), "hello updated world");
    Assert.assertNotNull(comment);
    Assert.assertTrue(shoutemApi.postPostsCommentsDelete(credentials, comment.getComment_id()));
  }


  /**
   * Link scheme for tests. This link scheme renders links for all content beans with the pattern
   * http://www.coremedia.com/<content type>/<content id>
   */
  class GeneralPurposeLinkScheme implements LinkScheme {

    @Override
    public String formatLink(Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) throws URISyntaxException {
      ContentBean contentBean = null;
      if (bean instanceof ContentBean) {
        contentBean = (ContentBean) bean;

      } else if (bean instanceof TransformedBeanBlob) {
        Object blobBean = ((TransformedBeanBlob) bean).getBean();
        if (blobBean instanceof ContentBean) {
          contentBean = (ContentBean) blobBean;
        }
      }

      if (contentBean == null) {
        return null;
      }

      //noinspection StringBufferReplaceableByString
      StringBuilder stringBuilder = new StringBuilder("http://www.coremedia.com/");
      stringBuilder.append(contentBean.getContent().getType().getName())
              .append("/")
              .append(IdHelper.parseContentId(contentBean.getContent().getId()));

      return stringBuilder.toString();
    }
  }
}

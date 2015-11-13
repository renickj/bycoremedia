package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMPlaceholder;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.controller.ContributionMessageKeys;
import com.coremedia.blueprint.elastic.social.cae.controller.HandlerInfo;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;
import com.coremedia.blueprint.elastic.social.cae.guid.GuidFilter;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.common.ContributionTargetHelper;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.elastic.social.api.ContributionType.ANONYMOUS;
import static com.coremedia.elastic.social.api.ContributionType.DISABLED;
import static com.coremedia.elastic.social.api.ContributionType.REGISTERED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductReviewsResultHandlerTest {

  private String contextId = "5678";
  private String targetId = "1234";
  private String text = "test test test test test test test";
  private String title = "title";
  private int rating = 5;

  @InjectMocks
  private ProductReviewsResultHandler handler;

  @Mock
  private ElasticSocialService elasticSocialService;

  @Mock
  private ElasticSocialPlugin elasticSocialPlugin;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private SettingsService settingsService;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private Content content;

  @Mock
  private Content navigationContent;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private Navigation navigation;

  @Mock
  private CommunityUser user;

  @Mock
  private Review review;

  @Mock
  private CMContext context;

  @Mock
  private ContextHelper contextHelper;

  @Mock
  private Site site;

  @Mock
  private CMPlaceholder page;

  @Mock
  private UriTemplate uriTemplate;

  @Mock
  private HttpServletRequest request;

  @Mock
  private ReviewsResult reviewsResult;

  @Mock
  private ProductReviewsResult productReviewsResult;

  @Mock
  private Product product;

  @Mock
  private CatalogService catalogService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private ContributionTargetHelper contributionTargetHelper;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private CMNavigation cmNavigation;

  @Mock
  private Enumeration<String> headerNames;

  @Mock
  private CommerceConnection commerceConnection;

  @Before
  public void setup() {
    handler.setElasticSocialUserHelper(new ElasticSocialUserHelper(communityUserService));
    handler.setContextHelper(contextHelper);
    handler.setElasticSocialPlugin(elasticSocialPlugin);
    handler.setContributionTargetHelper(contributionTargetHelper);
    handler.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);

    when(request.getHeaderNames()).thenReturn(headerNames);
    when(headerNames.hasMoreElements()).thenReturn(false);

    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;productId=1234";
    FragmentContext fragmentContext = new FragmentContext();
    FragmentParameters fragmentParameters = FragmentParametersFactory.create(url);
    fragmentContext.setParameters(fragmentParameters);
    fragmentContext.setFragmentRequest(true);
    when(request.getAttribute("CM_FRAGMENT_CONTEXT")).thenReturn(fragmentContext);

    GuidFilter.setCurrentGuid("1234+5678");
    when(communityUserService.getUserById("1234")).thenReturn(user);
    String contextPath = "perfectchef";
    when(navigationSegmentsUriHelper.parsePath(Arrays.asList(contextPath))).thenReturn(navigation);
    when(navigationSegmentsUriHelper.parsePath(contextPath)).thenReturn(navigation);

    when(navigation.getContext()).thenReturn(context);
    when(contentRepository.getContent(IdHelper.formatContentId(contextId))).thenReturn(navigationContent);
    when(contentBeanFactory.createBeanFor(navigationContent)).thenReturn(cmNavigation);
    when(cmNavigation.getContext()).thenReturn(context);
    when(context.getContent()).thenReturn(content);

    String siteId = "123";
    when(site.getId()).thenReturn(siteId);
    when(elasticSocialPlugin.getElasticSocialConfiguration(anyVararg())).thenReturn(elasticSocialConfiguration);
    when(elasticSocialConfiguration.isFeedbackEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.getReviewType()).thenReturn(ANONYMOUS);
    when(elasticSocialConfiguration.isReviewingEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isWritingReviewsEnabled()).thenReturn(true);
    when(elasticSocialConfiguration.isAnonymousReviewingEnabled()).thenReturn(true);

    when(catalogService.findProductById(anyString())).thenReturn(product);
    when(storeContext.getSiteId()).thenReturn(siteId);
    when(storeContextProvider.createContext(site)).thenReturn(storeContext);

    when(request.getAttribute(SiteHelper.SITE_KEY)).thenReturn(site);

    Commerce.setCurrentConnection(commerceConnection);
    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);
    when(commerceConnection.getStoreContext()).thenReturn(storeContext);
    when(commerceConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(commerceConnection.getIdProvider()).thenReturn(new BaseCommerceIdProvider("vendor"));
    when(commerceConnection.getCatalogService()).thenReturn(catalogService);
    when(catalogService.withStoreContext(storeContext)).thenReturn(catalogService);
  }

  @Test
  public void getReviews() {
    String view = "default";
    List<Review> reviews = new ArrayList<>();
    reviews.add(review);
    when(elasticSocialService.getReviews(product, user)).thenReturn(reviews);
    ModelAndView result = handler.getReviews(contextId, targetId, view, request);

    assertEquals(view, result.getViewName());
    assertEquals(cmNavigation, result.getModelMap().get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION));

    ProductReviewsResult productReviewsResult = getModel(result, ProductReviewsResult.class);
    Product target = (Product) productReviewsResult.getTarget();
    assertEquals(product, target);

    List<Review> reviewsResult = productReviewsResult.getReviews();

    assertEquals(reviews, reviewsResult);
    // check if configuration is what configured in setup
    assertTrue(productReviewsResult.isEnabled());
    assertTrue(productReviewsResult.isWritingContributionsEnabled());
    assertTrue(productReviewsResult.isAnonymousContributingEnabled());
    verify(elasticSocialService).getReviews(any(Product.class), any(CommunityUser.class));

    // make sure reviews are loaded lazily when getReviews is called
    productReviewsResult.getReviews();
    verify(elasticSocialService).getReviews(target, user);
  }

  @Test
  public void createReview() {
    when(elasticSocialService.createReview(eq(user), any(Product.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class))).thenReturn(review);
    when(elasticSocialConfiguration.getReviewModerationType()).thenReturn(ModerationType.POST_MODERATION);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertTrue(resultModel.isSuccess());
    verify(elasticSocialService).createReview(eq(user), any(Product.class), eq(text),  eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Product.class), any(Navigation.class)); // page
  }

  @Test
  public void createReviewRatingNull() {
    when(elasticSocialService.createReview(eq(user), any(Product.class), eq(text), eq(title), eq(rating), eq(ModerationType.POST_MODERATION), anyListOf(Blob.class), any(Navigation.class))).thenReturn(review);
    when(elasticSocialConfiguration.getReviewModerationType()).thenReturn(ModerationType.POST_MODERATION);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, null, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertFalse(resultModel.isSuccess());
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(Product.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService, never()).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Page.class)); // page
  }

  @Test
  public void createReviewDisabled() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(DISABLED);
    when(elasticSocialConfiguration.isWritingReviewsEnabled()).thenReturn(false);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertFalse(resultModel.isSuccess());
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(Product.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService, never()).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Page.class)); // page
  }

  @Test
  public void createReviewAnonymousDisabled() {
    when(elasticSocialConfiguration.getReviewType()).thenReturn(REGISTERED);
    when(elasticSocialConfiguration.isAnonymousReviewingEnabled()).thenReturn(false);
    when(user.isAnonymous()).thenReturn(true);
    ModelAndView modelAndView = handler.createReview(contextId, targetId, text, title, rating, request);

    HandlerInfo resultModel = getModel(modelAndView, HandlerInfo.class);
    assertTrue(resultModel.getErrors().isEmpty());
    assertEquals(1, resultModel.getMessages().size());
    assertFalse(resultModel.isSuccess());
    verify(elasticSocialService, never()).createReview(any(CommunityUser.class), any(Product.class), anyString(), anyString(), anyInt(), any(ModerationType.class), anyListOf(Blob.class), any(Navigation.class));
    verify(settingsService, never()).settingWithDefault(eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), eq(String.class), eq(ContributionMessageKeys.REVIEW_FORM_SUCCESS), any(Page.class)); // page
  }

  @Test
  public void getReviewsPlaceholder() {
    StringBuffer requestUrl = new StringBuffer("requestUrl/12345/en-US/params;productId=1234");
    when(request.getRequestURL()).thenReturn(requestUrl);
    when(page.getContent()).thenReturn(content);
    ProductReviewsResult reviewsResult = handler.getReviews(page, request);

    assertNotNull(reviewsResult);
    assertEquals(product, reviewsResult.getTarget());
  }

  @Test
  public void buildFragmentLink() throws URISyntaxException {
    Map<String, Object> linkParameters = new HashMap<>();
    List<String> pathList = new ArrayList<>();
    String path = "path/" + contextId;
    pathList.add(path);
    when(productReviewsResult.getTarget()).thenReturn(product);
    when(contextHelper.currentSiteContext()).thenReturn(cmNavigation);
    when(cmNavigation.getContext()).thenReturn(context);
    when(context.getContentId()).thenReturn(Integer.parseInt(contextId));
    when(navigationSegmentsUriHelper.getPathList(cmNavigation)).thenReturn(pathList);
    URI uri = new URI(path);
    when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);
    ProductReviewsResult productReviewsResult = new ProductReviewsResult(product);
    UriComponents uriComponents = handler.buildFragmentLink(productReviewsResult, uriTemplate, linkParameters, request);

    assertNotNull(uriComponents);
    assertEquals(path, uriComponents.getPath());
  }

  @Test
  public void buildReviewInfoLink() throws URISyntaxException {
    List<String> pathList = new ArrayList<>();
    String path = "path/" + contextId;
    pathList.add(path);
    when(productReviewsResult.getTarget()).thenReturn(product);
    when(contextHelper.currentSiteContext()).thenReturn(cmNavigation);
    when(cmNavigation.getContext()).thenReturn(context);
    when(context.getContentId()).thenReturn(Integer.parseInt(contextId));
    when(navigationSegmentsUriHelper.getPathList(cmNavigation)).thenReturn(pathList);
    URI uri = new URI(path);
    when(uriTemplate.expand(any(String.class), any(Integer.class), any())).thenReturn(uri);

    ProductReviewsResult productReviewsResult = new ProductReviewsResult(product);
    UriComponents uriComponents = handler.buildInfoLink(productReviewsResult, uriTemplate, request);

    assertNotNull(uriComponents);
    assertEquals(path, uriComponents.getPath());
  }

  private <T> T getModel(ModelAndView modelAndView, Class<T> type) {
    return getModel(modelAndView, "self", type);
  }

  private <T> T getModel(ModelAndView modelAndView, String key, Class<T> type) {
    Map<String, Object> modelMap = modelAndView.getModel();
    Object model = modelMap.get(key);
    // assertTrue(model instanceof type);
    return (T) model; // NO_SONAR
  }
}

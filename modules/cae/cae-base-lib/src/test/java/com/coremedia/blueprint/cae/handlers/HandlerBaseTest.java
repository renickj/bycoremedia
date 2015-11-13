package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.HttpError;
import com.coremedia.objectserver.web.UriTemplatePathMatcher;
import com.coremedia.objectserver.web.binding.PathVariableSegmentStringListConverter;
import com.coremedia.objectserver.web.links.AnnotationLinkScheme;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.activation.MimeTypeParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Basic functionality for testing {@link HandlerBase resources}.
 * It is assumed that all requests use  "UTF-8"
 * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding() character encoding}, either by deploying
 * {@link org.springframework.web.filter.CharacterEncodingFilter} or configuring the servlet container
 * appropriately.
 */
public abstract class HandlerBaseTest {

  public static final String CONTEXT_PATH = "/context";
  public static final String SERVLET_PATH = "/servlet";

  private static final String SOME_VIEW = "someView";
  private static final Map<String, String> VIEW_PARAMETER = ImmutableMap.of("view", SOME_VIEW);
  private static final String PERMITTED_PARAM = "key";
  private static Map<String, Object> PARAMS = ImmutableMap.<String, Object>of(
          PERMITTED_PARAM, "value",
          "forbidden", "secret");


  private LinkFormatter linkFormatter = new LinkFormatter();

  private ApplicationContext applicationContext = mock(ApplicationContext.class);
  private MimeTypeService mimeTypeService = mock(MimeTypeService.class);
  private UrlPathFormattingHelper urlPathFormattingHelper = mock(UrlPathFormattingHelper.class);
  private ContextHelper contextHelper = mock(ContextHelper.class);
  private SitesService sitesService = mock(SitesService.class);

  private FormattingConversionService conversionService = new DefaultFormattingConversionService();
  private ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
  private DefaultAnnotationHandlerMapping handlerMapping = new DefaultAnnotationHandlerMapping();
  private AnnotationMethodHandlerAdapter handlerAdapter = new AnnotationMethodHandlerAdapter();
  private IdContentBeanConverter idContentBeanConverter;
  private IdActionDocConverter idActionDocConverter;

  @Before
  public void setUp() throws Exception {
  }

  public static Set<String> getPermittedParameter() {
    return Collections.singleton(PERMITTED_PARAM);
  }

  protected SitesService getSitesService() {
    return sitesService;
  }

  protected void registerHandler(Object handler) throws Exception {
    // setup controller in application context
    final Class<?> type = handler.getClass();
    String beanName = type.getName();
    when(applicationContext.getBeanNamesForType(any(Class.class))).thenReturn(new String[]{beanName});
    doReturn(type).when(applicationContext).getType(beanName);
    when(applicationContext.isSingleton(beanName)).thenReturn(true);
    when(applicationContext.getBean(beanName)).thenReturn(handler);

    RequestMapping requestMapping = type.getAnnotation(RequestMapping.class);
    Link linkMapping = type.getAnnotation(Link.class);

    doReturn(requestMapping).when(applicationContext).findAnnotationOnBean(beanName, RequestMapping.class);
    doReturn(linkMapping).when(applicationContext).findAnnotationOnBean(beanName, Link.class);

    UriTemplatePathMatcher pathMatcher = new UriTemplatePathMatcher();

    // setup converters
    idContentBeanConverter = mock(IdContentBeanConverter.class);
    idActionDocConverter = mock(IdActionDocConverter.class);
    conversionService.addConverter(idContentBeanConverter);
    conversionService.addConverter(new PathVariableSegmentStringListConverter());
    conversionService.addConverter(idActionDocConverter);
    webBindingInitializer.setConversionService(conversionService);
    handlerAdapter.setWebBindingInitializer(webBindingInitializer);

    // initialize handler adapter
    handlerAdapter.setPathMatcher(pathMatcher);

    // initialize handler mapping
    handlerMapping.setApplicationContext(applicationContext);
    handlerMapping.setPathMatcher(pathMatcher);
    handlerMapping.initApplicationContext();

    // setup link scheme
    when(applicationContext.getBeansWithAnnotation(Link.class)).thenReturn(ImmutableMap.of(
            beanName, handler
    ));
    AnnotationLinkScheme linkScheme = new AnnotationLinkScheme();
    linkScheme.setApplicationContext(applicationContext);
    linkFormatter.addScheme(linkScheme);
  }

  protected MockHttpServletRequest newRequest(String path) {
    return newRequest(path, Collections.<String, String>emptyMap());
  }

  protected MockHttpServletRequest newRequest(String path, Map<String, String> parameters) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", CONTEXT_PATH + SERVLET_PATH + path);
    request.setContextPath(CONTEXT_PATH);
    request.setServletPath(SERVLET_PATH);
    request.setParameters(parameters);
    request.setCharacterEncoding("UTF-8");
    return request;
  }

  protected ModelAndView handleRequest(String path) throws Exception {
    return handleRequest(newRequest(path));
  }

  protected ModelAndView handleRequest(String path, Map<String, String> parameters) throws Exception {
    return handleRequest(newRequest(path, parameters));
  }

  protected ModelAndView handleRequest(HttpServletRequest request) throws Exception {
    HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(request);
    assertNotNull("no mapping found for " + request.getRequestURI(), handlerExecutionChain);

    Object handler = handlerExecutionChain.getHandler();

    assertTrue("handlerAdapter does not support handler", handlerAdapter.supports(handler));
    return handlerAdapter.handle(request, new MockHttpServletResponse(), handler);
  }

  protected ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  protected IdContentBeanConverter getIdContentBeanConverter() {
    return idContentBeanConverter;
  }

  protected IdActionDocConverter getIdActionDocConverter() {
    return idActionDocConverter;
  }

  protected MimeTypeService getMimeTypeService() {
    return mimeTypeService;
  }

  protected UrlPathFormattingHelper getUrlPathFormattingHelper() {
    return urlPathFormattingHelper;
  }

  protected ContextHelper getContextHelper() {
    return contextHelper;
  }

  protected String formatLink(Object bean, String viewName, boolean forRedirect) {
    return formatLink(bean, viewName, forRedirect, Collections.<String, Object>emptyMap());
  }

  protected String formatLink(Object bean, String viewName, boolean forRedirect, Map<String, Object> parameters) {
    MockHttpServletRequest request = newRequest("/");
    request.setAttribute(ViewUtils.PARAMETERS, newHashMap(parameters));
    return linkFormatter.formatLink(bean, viewName, request, new MockHttpServletResponse(), forRedirect);
  }

  protected void setContextFor(CMLinkable target, CMNavigation navigation) {
    when(contextHelper.contextFor(eq(target))).thenReturn(navigation);
  }

  protected void registerMimeTypeWithExtensions(String mimeType, String extension) throws MimeTypeParseException {
    when(mimeTypeService.getExtensionForMimeType(mimeType)).thenReturn(extension);
    when(mimeTypeService.getMimeTypeForExtension(extension)).thenReturn(mimeType);
  }

  /**
   * Helper method to test handling of a request to a URL path, with or without a view parameter.
   * View parameter will be passed as an optional query parameter.
   */
  protected void assertModelWithPageAndView(String path, CMNavigation navigation, CMLinkable content) throws Exception {
    assertModelWithPageBean(handleRequest(path), navigation, content);
    assertModelWithPageBean(handleRequest(path, VIEW_PARAMETER), navigation, content, SOME_VIEW);
  }

  /*
  * common assertions after controller parses link into model for controllers that generate pages
  */
  protected void assertModelWithPageBean(ModelAndView modelAndView, CMNavigation navigation, CMLinkable content) {
    assertModelWithPageBean(modelAndView, navigation, content, ViewUtils.DEFAULT_VIEW);
  }

  /*
   * common assertions after controller parses link into model for controllers that generate pages
   */
  protected void assertModelWithPageBean(ModelAndView modelAndView, CMNavigation navigation, CMLinkable content, String viewName) {

    assertNotNull("Model was not resolved correctly", modelAndView);

    Object self = modelAndView.getModel().get("self");
    Page page = (Page) self;
    assertEquals("Model does not match expected content", content, page.getContent());
    assertEquals("Model does not match expected navigation context", navigation, page.getNavigation());
    assertEquals("view name", viewName, modelAndView.getViewName());
  }

  /*
   * common assertions after controller parses link into model for controllers that do not generate pages
   */
  protected void assertModel(ModelAndView modelAndView, Object bean) {
    assertNotNull("Model was not resolved correctly", modelAndView);
    assertEquals("Model does not match mocked content", bean, modelAndView.getModel().get("self"));
  }

  protected void assertNotFound(String message, ModelAndView modelAndView) {
    assertHttpError(message, HttpServletResponse.SC_NOT_FOUND, modelAndView);
  }

  protected void assertBadRequest(String message, ModelAndView modelAndView) {
    assertHttpError(message, HttpServletResponse.SC_BAD_REQUEST, modelAndView);
  }

  protected void assertHttpError(String message, int expectedErrorCode, ModelAndView modelAndView) {
    assertEquals(String.format("Unexpected root model (%s):", message), HttpError.class, HandlerHelper.getRootModel(modelAndView).getClass());
    assertEquals(String.format("Unexpected error code (%s):", message), expectedErrorCode, ((HttpError) HandlerHelper.getRootModel(modelAndView)).getErrorCode());
  }

  protected Page createPage(CMNavigation navigation, CMLinkable content) {
    return new PageImpl(navigation, content, true, sitesService, null);
  }

  protected CMChannel mockChannel(CMNavigation parent, String segment, NavigationSegmentsUriHelper navigationHelperMock) {
    CMChannel navigation = mock(CMChannel.class, segment);
    when(navigation.getSegment()).thenReturn(segment);

    CMNavigation root = parent != null ? parent.getRootNavigation() : navigation;
    when(navigation.getRootNavigation()).thenReturn(root);

    List<Linkable> path = newArrayList();
    if (parent != null) {
      path.addAll(parent.getNavigationPathList());
    }
    path.add(navigation);

    //when().thenReturn() can't be used with methods that return type wildcards
    doReturn(path).when(navigation).getNavigationPathList();

    List<String> segments = newArrayList();
    for (Linkable nav : path) {
      segments.add(nav.getSegment());
    }
    when(navigationHelperMock.parsePath(eq(segments))).thenReturn(navigation);
    when(navigationHelperMock.getPathList(navigation)).thenReturn(segments);

    // context for any navigation object is the navigation itself
    setContextFor(navigation, navigation);

    return navigation;
  }

  /**
   * Helper method to test URL generation in four different variations: called for a {@link com.coremedia.blueprint.common.contentbeans.CMLinkable} or
   * called for a {@link com.coremedia.blueprint.common.contentbeans.Page} for a linkable, each with and without
   * <ul>
   * <li>permitted link parameters</li>
   * <li>view parameter</li>
   * </ul>
   * Permitted link parameters and the optional view parameter should be added to the resulting URL as query
   * parameters.
   */
  protected void assertUrl(String expectedPath, CMNavigation navigation, CMLinkable content) throws URISyntaxException {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(expectedPath);
    String expectedUri = uriBuilder.build().toUri().toString();

    // build URLs from linkable or page, without view name or parameters
    assertEquals(expectedUri, formatLink(content, null, false));
    assertEquals(expectedUri, formatLink(createPage(navigation, content), null, false));

    // build URLs from linkable or page, plus view name and parameter
    expectedUri = uriBuilder
            .queryParam("view", SOME_VIEW)
            .queryParam(PERMITTED_PARAM, PARAMS.get(PERMITTED_PARAM))
            .build().toUri().toString();

    assertEquals(expectedUri, formatLink(content, SOME_VIEW, false, PARAMS));
    assertEquals(expectedUri, formatLink(createPage(navigation, content), SOME_VIEW, false, PARAMS));
  }

  public static interface IdContentBeanConverter extends Converter<String, ContentBean> {
  }

  public static interface IdActionDocConverter extends Converter<String, CMAction> {
  }
}

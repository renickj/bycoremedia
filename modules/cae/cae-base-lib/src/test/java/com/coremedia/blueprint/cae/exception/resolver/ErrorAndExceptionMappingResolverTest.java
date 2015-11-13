package com.coremedia.blueprint.cae.exception.resolver;

import com.coremedia.blueprint.cae.exception.InvalidContentException;
import com.coremedia.blueprint.cae.exception.handler.CyclicDependencyExceptionHandler;
import com.coremedia.blueprint.cae.exception.handler.ErrorAndExceptionHandler;
import com.coremedia.blueprint.cae.exception.handler.InvalidContentExceptionHandler;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.objectserver.web.HandlerHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ErrorAndExceptionMappingResolverTest {
  public static final String NOT_VALID_CONTENT = "notValidContent";
  private ErrorAndExceptionMappingResolver errorAndExceptionMappingResolver;

  @Before
  public void setUp() throws Exception {
    errorAndExceptionMappingResolver = new ErrorAndExceptionMappingResolver();
    Properties properties = new Properties();
    properties.setProperty("com.coremedia.blueprint.cae.exception.InvalidContentException", NOT_VALID_CONTENT);
    properties.setProperty("com.coremedia.cache.EvaluationException", "asError");
    errorAndExceptionMappingResolver.setExceptionMappings(properties);
    errorAndExceptionMappingResolver.setDefaultErrorView("asError");
    List<ErrorAndExceptionHandler> errorAndExceptionHandlers = new ArrayList<>();
    errorAndExceptionHandlers.add(new CyclicDependencyExceptionHandler());
    errorAndExceptionHandlers.add(new InvalidContentExceptionHandler());

    errorAndExceptionMappingResolver.setExceptionHandler(errorAndExceptionHandlers);
  }

  @Test
  public void testDetermineViewName() throws Exception {
    Page page = Mockito.mock(Page.class);
    ModelAndView modelAndView = errorAndExceptionMappingResolver.resolveException(new MockHttpServletRequest(), new MockHttpServletResponse(), null, new InvalidContentException("invalid", page));
    Assert.assertNotNull(modelAndView);
    Assert.assertEquals(NOT_VALID_CONTENT, modelAndView.getViewName());
    Assert.assertEquals(page, HandlerHelper.getRootModel(modelAndView));
  }
}

package com.coremedia.livecontext.preview;

import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class WcCookieFilterTest {
  private WcCookieFilter cookieFilter;

  @Before
  public void setUp() throws Exception {
    cookieFilter = new WcCookieFilter();
  }

  @Test
  public void doFilter() throws IOException, ServletException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    Cookie cookie = new Cookie("WC_ToBeFiltered", "doesNotMatter");
    request.addHeader("Cookie", "WC_USERACTIVITY_-1002=-1002%2C10302%2Cbla");
    request.setAttribute(LiveContextPageHandlerBase.HAS_PREVIEW_TOKEN, true);
    request.setCookies(cookie);
    ServletResponse response = mock(ServletResponse.class);
    MyFilterChain filterChain = new MyFilterChain();

    cookieFilter.doFilter(request, response, filterChain);
    cookieFilter.destroy();
  }

  private class MyFilterChain implements FilterChain {
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
      assertEquals(0, ((HttpServletRequest) servletRequest).getCookies().length);
      assertEquals("NOT_APPLICABLE_HERE_USERACTIVITY_-1002=-1002%2C10302%2Cbla", ((HttpServletRequest) servletRequest).getHeader("Cookie"));
    }
  }
}

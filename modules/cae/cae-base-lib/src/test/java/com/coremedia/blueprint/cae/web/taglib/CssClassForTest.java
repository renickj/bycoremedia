package com.coremedia.blueprint.cae.web.taglib;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.jsp.jstl.core.LoopTagStatus;

public class CssClassForTest {
  private LoopTagStatus status;

  @Before
  public void setUp() throws Exception {
    status = Mockito.mock(LoopTagStatus.class);
    Mockito.when(status.isFirst()).thenReturn(true);
    Mockito.when(status.isLast()).thenReturn(true);
    Mockito.when(status.getIndex()).thenReturn(1);

  }

  @Test
  public void testCssClassFor() throws Exception {
    //jsp
    Assert.assertEquals("first last odd", CssClassFor.cssClassFor(status, false));
    Assert.assertEquals(" class=\"first last odd\"", CssClassFor.cssClassFor(status, true));
  }

  @Test
  public void testCssClassForFirstLast() throws Exception {
    //jsp
    Assert.assertEquals("first last", CssClassFor.cssClassForFirstLast(status, false));
    Assert.assertEquals(" class=\"first last\"", CssClassFor.cssClassForFirstLast(status, true));
  }

  @Test
  public void testCssClassForOddEven() throws Exception {
    //jsp
    Assert.assertEquals("odd", CssClassFor.cssClassForOddEven(status, false));
    Assert.assertEquals(" class=\"odd\"", CssClassFor.cssClassForOddEven(status, true));
  }
}

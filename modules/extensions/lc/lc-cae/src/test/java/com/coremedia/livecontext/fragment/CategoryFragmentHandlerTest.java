package com.coremedia.livecontext.fragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryFragmentHandlerTest extends FragmentHandlerTestBase<CategoryFragmentHandler> {

  @Test(expected = IllegalStateException.class)
  public void handleCategoryViewFragmentNoLiveContextNavigationFound() {
    when(getResolveContextStrategy().resolveContext(getSite(), CATEGORY_ID)).thenReturn(null);
    getTestling().createModelAndView(getFragmentParameters4Product(), request);
  }

  @Test
  public void handleCategoryViewFragment() {
    ModelAndView result = getTestling().createModelAndView(getFragmentParameters4Product(), request);
    assertDefaultPage(result);
    verifyDefault();
  }

  @Override
  protected CategoryFragmentHandler createTestling() {
    return new CategoryFragmentHandler();
/*
    when(fragmentPageHandler.createFragmentModelAndViewForPlacementAndView(navigation, PLACEMENT, VIEW, rootChannelBean)).thenReturn(modelAndView);
    when(fragmentPageHandler.createFragmentModelAndView(navigation, VIEW, rootChannelBean)).thenReturn(modelAndView);
    when(fragmentPageHandler.createModelAndViewForPlacementAndView(rootChannelBean, PLACEMENT, VIEW)).thenReturn(modelAndView);
*/
  }

  @Before
  public void defaultSetup() {
    super.defaultSetup();
    getTestling().setContextStrategy(resolveContextStrategy);
  }
}

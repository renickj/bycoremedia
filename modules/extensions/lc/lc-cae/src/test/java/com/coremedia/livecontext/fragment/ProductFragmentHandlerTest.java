package com.coremedia.livecontext.fragment;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductFragmentHandlerTest extends FragmentHandlerTestBase<ProductFragmentHandler> {

  @Mock
  private ProductFragmentContextStrategy contextStrategy;

  @Mock
  protected CommerceBeanFactory commerceBeanFactory;

  @Test(expected = IllegalStateException.class)
  public void handleProductViewFragmentNoSitesFound() {
    FragmentParameters params = getFragmentParameters4Product();
    when(request.getAttribute(SITE_ATTRIBUTE_NAME)).thenReturn(null);
    getTestling().createModelAndView(params, request);
  }

  @Test(expected = IllegalStateException.class)
  public void handleProductViewFragmentNoLiveContextNavigationFound() {
    when(getResolveContextStrategy().resolveContext(getSite(), EXTERNAL_TECH_ID)).thenReturn(null);
    getTestling().createModelAndView(getFragmentParameters4Product(), request);
  }

  @Test
  public void handleProductViewFragment() {
    ModelAndView result = getTestling().createModelAndView(getFragmentParameters4Product(), request);
    assertDefaultPage(result);
    verifyDefault();
  }

  @Test
  public void handleProductViewFragmentWithCategory() {
    FragmentParameters params = getFragmentParameters4Product();
    params.setCategoryId("categoryId");
    ModelAndView result = getTestling().createModelAndView(params, request);
    assertDefaultPage(result);
    verifyDefault();
  }

  @Test(expected = IllegalStateException.class)
  public void handleProductPlacementFragmentNoLiveContextNavigationFound() {
    when(getResolveContextStrategy().resolveContext(getSite(), EXTERNAL_TECH_ID)).thenReturn(null);
    getTestling().createModelAndView(getFragmentParameters4Product(), request);
  }

  @Test
  public void handleProductPlacementFragmentFound() {
    FragmentParameters fragmentParameters4Product = getFragmentParameters4Product();
    fragmentParameters4Product.setPlacement(PLACEMENT);

    ModelAndView result = getTestling().createModelAndView(fragmentParameters4Product, request);
    assertDefaultPlacement(result);
    verifyDefault();
  }

  @Override
  protected ProductFragmentHandler createTestling() {
    return new ProductFragmentHandler();
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
    getTestling().setCommerceBeanFactory(commerceBeanFactory);
  }
}

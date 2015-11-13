package com.coremedia.livecontext.handler.preview.product;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdProvider;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PreviewHandlerTestForCommerce {
  private static final String KNOWN_SITE_ID = "4711";
  private final String productReferenceId = "ibm:///catalog/product/123";
  private final String categoryReferenceId = "ibm:///catalog/category/456";

  @InjectMocks
  private PreviewHandler previewHandler = new PreviewHandler();

  @Mock
  private IdProvider idProvider;

  @Mock
  private SitesService sitesService;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  @Mock
  private Product product;

  @Mock
  private Category category;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private StoreContext storeContext;

  @Mock
  private Site site;

  @Before
  public void setup() {
    Commerce.setCurrentConnection(commerceConnection);

    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    when(commerceConnection.getStoreContext()).thenReturn(storeContext);
    when(storeContext.getSiteId()).thenReturn(KNOWN_SITE_ID);
    when(sitesService.getSite(KNOWN_SITE_ID)).thenReturn(site);
  }

  @Test
  public void handleProductPreview() throws IOException {
    when(idProvider.parseId(productReferenceId)).thenReturn(product);
    //doNothing().when(response).sendRedirect("");
    ModelAndView modelAndView = previewHandler.handleId(productReferenceId, "", KNOWN_SITE_ID, "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof Product);
    assertTrue(modelAndView.getViewName().contains("redirect:"));
  }

  @Test
  public void handleCategoryPreview() throws IOException {
    when(idProvider.parseId(categoryReferenceId)).thenReturn(product);
    //doNothing().when(response).sendRedirect("");
    ModelAndView modelAndView = previewHandler.handleId(categoryReferenceId, "", KNOWN_SITE_ID, "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof Product);
    assertTrue(modelAndView.getViewName().contains("redirect:"));
  }

  @Test
  public void handleNoSite() throws IOException {
    when(idProvider.parseId(categoryReferenceId)).thenReturn(product);
    //doNothing().when(response).sendRedirect("");
    ModelAndView modelAndView = previewHandler.handleId(categoryReferenceId, "", "", "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof HttpError);
  }

  @Test
  public void handleNoBean() throws IOException {
    when(idProvider.parseId("unknown_id")).thenReturn(null);
    //doNothing().when(response).sendRedirect("");
    ModelAndView modelAndView = previewHandler.handleId("unknown_id", "", KNOWN_SITE_ID, "", request);
    assertTrue(modelAndView.getModel().get("self") instanceof HttpError);
  }
}


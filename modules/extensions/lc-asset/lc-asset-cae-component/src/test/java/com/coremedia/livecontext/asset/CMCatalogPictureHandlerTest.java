package com.coremedia.livecontext.asset;


import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMCatalogPictureHandlerTest {
  @InjectMocks
  private CMCatalogPictureHandler testling = new CMCatalogPictureHandler();

  @Mock
  private LiveContextSiteResolver siteResolver;
  @Mock
  private Site site;
  @Mock
  private ContentBeanFactory contentBeanFactory;
  @Mock
  private AssetManagementLicenseInspector licenseInspector;
  @Mock
  private SettingsService settingsService;
  @Mock
  private AssetService assetService;
  @Mock
  private CMPicture picture;
  @Mock
  private Content pictureContent;
  @Mock
  private Blob blob;


  private BaseCommerceConnection commerceConnection;

  @Before
  public void setUp() throws Exception {
    when(licenseInspector.isFeatureActive()).thenReturn(true);
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    commerceConnection.setAssetService(assetService);

    Map<String, String> pictureFormats = new HashMap<>();
    pictureFormats.put("thumbnail", "portrait_ratio20x31/200/310");
    pictureFormats.put("full", "portrait_ratio20x31/646/1000");
    testling.setPictureFormats(pictureFormats);

    when(contentBeanFactory.createBeanFor(pictureContent, CMPicture.class)).thenReturn(picture);
    when(picture.getContent()).thenReturn(pictureContent);
    when(picture.getTransformedData(anyString())).thenReturn(blob);
  }

  @Test
  public void testHandleRequestWithLicenseMissing() throws Exception {
    when(licenseInspector.isFeatureActive()).thenReturn(false);
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);

    testling.handleRequestWidthHeight(
            "10201", "en_US", "full", "PC_SUMMER_DRESS", mock(HttpServletRequest.class)
    );

    verify(assetService).getDefaultPicture(site);
  }

  @Test
  public void testHandleRequestWithSiteNull() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(null);

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", "PC_SUMMER_DRESS", mock(HttpServletRequest.class)
    );
    assert404(result);
  }

  @Test
  public void testHandleRequestWithPictureFormatsEmpty() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);
    testling.setPictureFormats(Collections.<String, String>emptyMap());

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", "PC_SUMMER_DRESS", mock(HttpServletRequest.class)
    );
    assert404(result);
  }

  @Test
  public void testHandleRequestNoPictureFound() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);
    when(assetService.findPictures(anyString())).thenReturn(Collections.EMPTY_LIST);

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", "PC_SUMMER_DRESS", mock(HttpServletRequest.class)
    );
    assert404(result);
  }

  @Test
  public void testHandleRequestSuccess() throws Exception {
    when(siteResolver.findSiteFor(anyString(), any(Locale.class))).thenReturn(site);
    List<Content> cmPictures = new ArrayList<>();
    cmPictures.add(pictureContent);
    when(assetService.findPictures("PC_SUMMER_DRESS")).thenReturn(cmPictures);

    ModelAndView result = testling.handleRequestWidthHeight(
            "10201", "en_US", "full", "PC_SUMMER_DRESS", mock(HttpServletRequest.class)
    );
    assert202(result);
  }

  private void assert404(ModelAndView result) {
    assertNotNull(result.getModel().get("self"));
    assertTrue(result.getModel().get("self") instanceof HttpError);
    HttpError error = (HttpError) result.getModel().get("self");
    assertEquals(404, error.getErrorCode());
  }

  private void assert202(ModelAndView result) {
    assertNotNull(result.getModel().get("self"));
    assertTrue(result.getModel().get("self") instanceof Blob);
    assertTrue(result.getViewName().equals("redirect:DEFAULT"));
  }
}

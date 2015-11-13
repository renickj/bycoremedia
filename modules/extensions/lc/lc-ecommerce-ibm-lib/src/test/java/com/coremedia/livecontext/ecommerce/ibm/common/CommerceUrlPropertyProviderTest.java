package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.google.common.base.Function;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceUrlPropertyProviderTest extends AbstractServiceTest {

  private static final String SEO_SEGMENT = "seo";
  private static final String SEARCH_TERM_WITH_UMLAUTS = "eté-küche";

  private static final String URL_TEMPLATE = "/SearchDisplay?storeId={storeId}&storeName={storeName}&seoSegment={seoSegment}&searchTerm={searchTerm}&language={language}&catalogId={catalogId}&langId={langId}&pageSize=12";
  private static final String DEFAULT_STOREFRONT = "//shop-preview-production-helios.blueprint-box.vagrant/webapp/wcs/stores/servlet";
  private static final String PREVIEW_STOREFRONT = "//shop-preview-helios.blueprint-box.vagrant/webapp/wcs/preview/servlet";
  private static final String SHOPPING_FLOW = "/Logon?logonId=manni&logonPassword=geheim" +
          "&URL=ContractSetInSession?URL={redirectUrl}&reLogonURL=LogonForm&storeId={storeId}" +
          "&catalogId={catalogId}&langId={langId}";
  private static final Function<String, String> URL_ENCODE_FUNCTION = new Function<String, String>() {
    @Nullable
    @Override
    public String apply(String input) {
      try {
        return URLEncoder.encode(input, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
  };

  @Mock
  private CommerceConnection connection;

  @Mock
  private CatalogServiceImpl catalogService;

  private CommerceUrlPropertyProvider testling;

  @Before
  public void setup(){
    super.setup();
    testling = new CommerceUrlPropertyProvider();
    testling.setDefaultStoreFrontUrl(DEFAULT_STOREFRONT);
    testling.setPreviewStoreFrontUrl(PREVIEW_STOREFRONT);
    testling.setUrlPattern(URL_TEMPLATE);
    testling.setShoppingFlowUrlForContractPreview(SHOPPING_FLOW);

    Commerce.setCurrentConnection(connection);
    when(connection.getCatalogService()).thenReturn(catalogService);
    when(catalogService.getLanguageId(any(Locale.class))).thenReturn("-1");
  }

  @Test
  public void testUrlFormatting() throws UnsupportedEncodingException {
    Map<String, Object> params = new HashMap<>();
    params.put(CommerceUrlPropertyProvider.STORE_CONTEXT, testConfig.getStoreContext());
    params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, URL_TEMPLATE);
    params.put(CommerceUrlPropertyProvider.SEO_SEGMENT, SEO_SEGMENT);
    params.put(CommerceUrlPropertyProvider.SEARCH_TERM, SEARCH_TERM_WITH_UMLAUTS);

    UriComponents url = (UriComponents) testling.provideValue(params);
    String formattedUrl = url.toString();
    assertNotNull(formattedUrl);
    assertThat("URL Tokens got replaced and umlauts are correctly added to the URL.",
            formattedUrl,
            allOf(not(containsString("{")),
                    not(containsString("}")),
                    containsString(SEARCH_TERM_WITH_UMLAUTS)
            ));
  }

  @Test
  public void testUrlPreviewLive() {
    Map<String, Object> params = new HashMap<>();
    params.put(CommerceUrlPropertyProvider.IS_STUDIO_PREVIEW, true);
    params.put(CommerceUrlPropertyProvider.STORE_CONTEXT, testConfig.getStoreContext());

    UriComponents url = (UriComponents) testling.provideValue(params);
    assertTrue(url.toString().startsWith(PREVIEW_STOREFRONT));

    params.put(CommerceUrlPropertyProvider.IS_STUDIO_PREVIEW, false);
    url = (UriComponents) testling.provideValue(params);
    assertTrue(url.toString().startsWith(DEFAULT_STOREFRONT));

    params.remove(CommerceUrlPropertyProvider.IS_STUDIO_PREVIEW);
    url = (UriComponents) testling.provideValue(params);
    assertTrue(url.toString().startsWith(DEFAULT_STOREFRONT));
  }

  @Test
  public void testUrlNullParam() throws UnsupportedEncodingException {
    assertEquals(UriComponentsBuilder.fromUriString(DEFAULT_STOREFRONT + URL_TEMPLATE).build().toString(),
            testling.provideValue(null).toString());
  }

  @Test
  public void testUrlTemplateIsEmpty(){
    Map<String, Object> params = new HashMap<>();
    params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, "");
    assertEquals("", testling.provideValue(params));

    params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, null);
    assertEquals(UriComponentsBuilder.fromUriString(DEFAULT_STOREFRONT + URL_TEMPLATE).build().toString(),
            testling.provideValue(params).toString());
  }

  @Test
  public void testShoppingFlowUrl(){
    Map<String, Object> params = new HashMap<>();
    params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, "{language}/{storeName}/{seoSegment}");
    StoreContext storeContext = testConfig.getStoreContext();
    storeContext.setContractIdsForPreview(new String[]{"4711", "0815"});
    params.put(CommerceUrlPropertyProvider.STORE_CONTEXT, storeContext);
    params.put(CommerceUrlPropertyProvider.SEO_SEGMENT, SEO_SEGMENT);
    params.put(CommerceUrlPropertyProvider.IS_STUDIO_PREVIEW, true);

    String providedUrl = testling.provideValue(params).toString();
    assertTrue(providedUrl.contains("en/auroraesite/seo"));
    assertTrue(providedUrl.startsWith(PREVIEW_STOREFRONT + "/Logon?"));
    assertTrue(providedUrl.contains("contractId=4711"));
    assertTrue(providedUrl.contains("contractId=0815"));
  }

  @Test
  public void testUrlWithRemainingTokens() {
    Map<String, Object> params = new HashMap<>();
    params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, "{language}/{storeName}/{seoSegment}");
    params.put(CommerceUrlPropertyProvider.SEO_SEGMENT, "simsalabim");
    String providedUrl = testling.provideValue(params).toString();
    assertThat("Remaining tokens in the URL are kept.",
            providedUrl,
            allOf(
                    Matchers.stringContainsInOrder(
                            asList("{language}", "{storeName}", "simsalabim")),
                    not(containsString("seoSegment")))
    );
  }

}

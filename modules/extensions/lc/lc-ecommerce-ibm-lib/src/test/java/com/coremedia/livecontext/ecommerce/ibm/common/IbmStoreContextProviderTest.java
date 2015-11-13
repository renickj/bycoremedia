package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.impl.SitesServiceImpl;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IbmStoreContextProviderTest {

  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withSites()
          .withBeans("classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml")
          .withContentRepository("/content/testcontent.xml")
          .build();

  StoreContextProviderImpl testling;

  @Before
  public void setup() {
    testling = infrastructure.getBean("storeContextProvider", StoreContextProviderImpl.class);
  }

  @Test
  public void testFindContextBySiteNameAvailable() {
    StoreContext context = testling.findContextBySiteName("Helios");
    assertNotNull(context);

    assertEquals("PerfectChefESite", StoreContextHelper.getStoreName(context));
    assertEquals("10202", StoreContextHelper.getStoreId(context));
    assertEquals("10051", StoreContextHelper.getCatalogId(context));
    assertEquals(new Locale("en"), StoreContextHelper.getLocale(context));
    assertEquals(Currency.getInstance("USD"), StoreContextHelper.getCurrency(context));
  }

  @Test
  public void testFindContextBySite() {
    Site currentSite = getSite("Helios");
    StoreContext context = testling.findContextBySite(currentSite);

    assertNotNull(context);
    assertEquals("PerfectChefESite", StoreContextHelper.getStoreName(context));
    assertEquals("10202", StoreContextHelper.getStoreId(context));
    assertEquals("10051", StoreContextHelper.getCatalogId(context));
    assertEquals(new Locale("en"), StoreContextHelper.getLocale(context));
    assertEquals(Currency.getInstance("USD"), StoreContextHelper.getCurrency(context));
  }

  @Test
  public void testFindContextBySiteAlternatively() {
    Site currentSite = getSite("Alternative");
    StoreContext context = testling.findContextBySite(currentSite);
    assertNotNull(context);
    assertEquals("springsite", StoreContextHelper.getStoreName(context));
    assertEquals("12345", StoreContextHelper.getStoreId(context));
    assertEquals("67890", StoreContextHelper.getCatalogId(context));
    assertEquals(new Locale("de"), StoreContextHelper.getLocale(context));
    assertEquals(Currency.getInstance("EUR"), StoreContextHelper.getCurrency(context));
    assertEquals("spring.only.setting", context.getReplacements().get("spring.only.setting"));
  }

  @Test
  public void testFindContextBySiteNoStoreConfig() throws Exception {
    Site currentSite = getSite("Media");
    assertNotNull("Expected site Media in test content was not found.", currentSite);
    StoreContext context = testling.findContextBySite(currentSite);
    assertNull(context);
  }

  @Test
  public void testFindContextBySiteWrongSite() {
    StoreContext context = testling.findContextBySiteName("not available");
    assertNull(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testFindContextBySiteIncompleteStoreConfig() throws Exception {
    Site currentSite = getSite("Helios-incomplete");
    assertNotNull("Expected site Helios-incomplete in test content was not found.", currentSite);
    StoreContext contextBySite = testling.findContextBySite(currentSite);// should throw an InvalidContextException
    assertNotNull(contextBySite);
    StoreContextHelper.validateContext(contextBySite);
  }

  @Test
  public void testParseReplacementsFromStruct(){
    Site currentSite = getSite("Helios");
    StoreContext context = testling.findContextBySite(currentSite);
    Map<String, String> replacements = context.getReplacements();
    assertEquals("shop-ref.ecommerce.coremedia.com", replacements.get("livecontext.ibm.wcs.host"));
    assertEquals("shop-helios.blueprint-box.vagrant", replacements.get("livecontext.apache.wcs.host"));
    assertEquals("spring.only.setting", replacements.get("spring.only.setting"));
  }

  private Site getSite(String siteName) {
    SitesServiceImpl siteService = infrastructure.getBean("sitesService", SitesServiceImpl.class);
    Site currentSite = null;
    for (Site site : siteService.getSites()) {
      if (site.getName().equals(siteName)) {
        currentSite = site;
        break;
      }
    }
    return currentSite;
  }

}

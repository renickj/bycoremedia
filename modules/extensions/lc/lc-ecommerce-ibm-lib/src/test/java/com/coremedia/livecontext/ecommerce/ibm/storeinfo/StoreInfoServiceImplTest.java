package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoServiceImpl}
 */
public class StoreInfoServiceImplTest extends AbstractServiceTest {

  private static final String BEAN_NAME_STORE_INFO_SERVICE = "storeInfoService";

  private StoreInfoServiceImpl testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_STORE_INFO_SERVICE, StoreInfoServiceImpl.class);
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetStoreId() {
    String storeId = testling.getStoreId("Aurora");
    assertEquals("10001", storeId);
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testDefaultCatalogId() {
    String catalogId = testling.getDefaultCatalogId("Aurora");
    assertNotNull(catalogId);
  }

  @Betamax(tape = "sis_testGetStoreInfos", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCatalogId() {
    String catalogId = testling.getCatalogId("Aurora", "Aurora");
    assertNotNull(catalogId);
  }

}

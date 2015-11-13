package com.coremedia.livecontext.ecommerce.ibm.p13n;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.MarketingImage;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingText;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for {@link com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotServiceImpl}
 */
public class MarketingSpotServiceImplTest extends AbstractServiceTest {

    private static final String MARKETING_SPOT_EXTERNAL_ID1 = "ApparelRow1_Content";
    private static final String MARKETING_SPOT_EXTERNAL_ID2 = "BoysRow4_CatEntries";
    private static final String MARKETING_SPOT_EXTERNAL_ID3 = "PC_Homepage_Offer";
    private static final String BEAN_NAME_MARKETING_SPOT_SERVICE = "marketingSpotService";

    MarketingSpotServiceImpl testling;

    @Before
    public void setup() {
        super.setup();
        testling = infrastructure.getBean(BEAN_NAME_MARKETING_SPOT_SERVICE, MarketingSpotServiceImpl.class);
        testling.getMarketingSpotWrapperService().clearLanguageMapping();
    }


    @Betamax(tape = "csi_testFindMarketingSpots", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpots() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(null);
        UserContextHelper.setCurrentContext(userContext);
        List<MarketingSpot> marketingSpots = testling.findMarketingSpots();
        assertTrue(marketingSpots.size() > 100);
        MarketingSpot spot = marketingSpots.get(0);
        assertNotNull(spot.getName());
        assertNotNull(spot.getId());
        assertNotNull(spot.getExternalId());
    }


    @Betamax(tape = "csi_testFindMarketingSpotByExternalId1", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpotByExternalId1() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(null);
        UserContextHelper.setCurrentContext(userContext);
        MarketingSpotImpl spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1);
        assertNotNull(spot);
        List<CommerceObject> entities = spot.getEntities();
        assertNotNull(entities);
        assertEquals("entities should have size of 1", entities.size(), 1);
        CommerceObject item = entities.get(0);
        assertTrue("MarketingContent expected", item instanceof MarketingImage || item instanceof MarketingText);
    }

    @Betamax(tape = "csi_testFindMarketingSpotByExternalId2", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpotByExternalId2() throws Exception {
      StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
      UserContext userContext = userContextProvider.createContext(null);
      UserContextHelper.setCurrentContext(userContext);
      MarketingSpotImpl spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID2);
      assertNotNull(spot);
      List<CommerceObject> entities = spot.getEntities();
      assertNotNull(entities);
      assertTrue("entities should have size > 0", entities.size() > 0);
      int count = 0;
      for (CommerceObject item : entities) {
        count++;
        assertTrue("Product expected", item instanceof Product);
        Product product = (Product) item;
        if (product.getName().equals("Gusso Green Khaki Shirt")) {
          assertTrue("Found the expected product", true);
          break;
        }
        if (count == entities.size()) {
          assertTrue("Can not find the expected product", false);
          break;
        }
      }
    }

    @Betamax(tape = "csi_testFindMarketingSpotByExternalId3", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpotByExternalId3() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(null);
        UserContextHelper.setCurrentContext(userContext);
        MarketingSpotImpl spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3);
        assertNotNull(spot);
        List entities = spot.getEntities();
        assertNotNull(entities);
        assertTrue("entities should have size > 0", entities.size() > 0);
        assertTrue("Product expected", entities.get(0) instanceof MarketingText);
        MarketingText first = (MarketingText) entities.get(0);
        assertTrue("the other text should be there", !first.getText().isEmpty() && !first.getText().contains("Men's"));
    }

    @Betamax(tape = "csi_testFindMarketingSpotPersonalizedOld", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpotPersonalizedOld() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(USER2_NAME);
        UserContextHelper.setCurrentContext(userContext);
        MarketingSpotImpl spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3);
        assertNotNull(spot);
        List entities = spot.getEntities();
        assertNotNull(entities);
        assertTrue("entities should have size > 0", entities.size() > 0);
        assertTrue("MarketingText expected", entities.get(0) instanceof MarketingText);
        MarketingText first = (MarketingText) entities.get(0);
        assertTrue("the men's text should be there", first.getText().contains("Men's"));
    }

    @Test
    public void testFindMarketingSpotPersonalizedTestContext() throws Exception {
        if (!"*".equals(System.getProperties().get("betamax.ignoreHosts"))) return;
        StoreContext storeContext = testConfig.getStoreContext();
        storeContext.setUserSegments("8000000000000000604,8000000000000000601");
        StoreContextHelper.setCurrentContext(storeContext);
        MarketingSpotImpl spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID3);
        assertNotNull(spot);
        List entities = spot.getEntities();
        assertNotNull(entities);
        assertTrue("entities should have size > 0", entities.size() > 0);
        assertTrue("MarketingText expected", entities.get(0) instanceof MarketingText);
        MarketingText first = (MarketingText) entities.get(0);
        assertTrue("the men's text should be there", first.getText().contains("Men's"));
    }

    @Betamax(tape = "csi_testFindMarketingSpotByExternalTechId", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpotByExternalTechId() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(null);
        UserContextHelper.setCurrentContext(userContext);
        MarketingSpotImpl spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1);
        assertNotNull(spot);
        spot = (MarketingSpotImpl) testling.findMarketingSpotByExternalTechId(spot.getExternalTechId());
        assertNotNull(spot);
        assertNotNull(spot.getDescription());
        assertNotNull(spot.getExternalId());
        assertNotNull(spot.getExternalTechId());
        assertNotNull(spot.getName());
        assertNotNull(spot.getReference());
    }

    @Betamax(tape = "csi_testFindMarketingSpotByExternalIdForStudio", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testFindMarketingSpotByExternalIdForStudio() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(null);
        UserContextHelper.setCurrentContext(userContext);
        testling.getMarketingSpotWrapperService().setUseServiceCallsForStudio(true);
        try {
            MarketingSpotImpl test = (MarketingSpotImpl) testling.findMarketingSpotByExternalId(MARKETING_SPOT_EXTERNAL_ID1);
            assertNotNull(test);
            assertNotNull(test.getDescription());
            assertNotNull(test.getExternalId());
            assertNotNull(test.getExternalTechId());
            assertNotNull(test.getName());
            assertNotNull(test.getReference());
        }finally {
            testling.getMarketingSpotWrapperService().setUseServiceCallsForStudio(false);
        }
    }


    @Betamax(tape = "csi_testSearchMarketingSpots", match = {MatchRule.path, MatchRule.query})
    @Test
    public void testSearchMarketingSpots() throws Exception {
        StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
        UserContext userContext = userContextProvider.createContext(null);
        UserContextHelper.setCurrentContext(userContext);
        SearchResult<MarketingSpot> marketingSpots = testling.searchMarketingSpots("Shirts", null);
        assertFalse(marketingSpots.getSearchResult().isEmpty());
    }

}
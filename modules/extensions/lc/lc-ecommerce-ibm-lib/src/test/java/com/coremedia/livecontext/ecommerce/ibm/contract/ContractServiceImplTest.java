package com.coremedia.livecontext.ecommerce.ibm.contract;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ContractServiceImplTest extends AbstractServiceTest {
  private static final String BEAN_NAME_CONTRACT_SERVICE = "contractService";
  ContractServiceImpl testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_CONTRACT_SERVICE, ContractServiceImpl.class);
  }

  @Betamax(tape = "contract_testFindContractIdsForUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractIdsForUser() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    UserContext userContext = userContextProvider.createContext(USER2_NAME);
    UserContextHelper.setCurrentContext(userContext);
    Collection<Contract> contractIdsForUser = testling.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contractIdsForUser);
    if (StoreContextHelper.getWcsVersion(testConfig.getB2BStoreContext()) > StoreContextHelper.WCS_VERSION_7_7) {
      assertTrue("number of eligible contracts should be more than zero", contractIdsForUser.size() > 0);
    } else {
      assertTrue("number of eligible contracts should be zero", contractIdsForUser.size() == 0);
    }
  }

  @Betamax(tape = "contract_testFindContractIdsForUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractIdsForPreviewUser() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    UserContext userContext = userContextProvider.createContext(PREVIEW_USER_NAME);
    UserContextHelper.setCurrentContext(userContext);
    Collection<Contract> contracts = testling.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);
    if (StoreContextHelper.getWcsVersion(testConfig.getB2BStoreContext()) > StoreContextHelper.WCS_VERSION_7_7) {
      assertEquals(2, contracts.size());
      Iterator iterator = contracts.iterator();
      while (iterator.hasNext()) {
        Contract contract = (Contract) iterator.next();
        assertTrue("contrat id has wrong format: " +  contract.getId(), contract.getId().startsWith("ibm:///catalog/contract/4000"));
      }
    }
  }

  @Test
  public void testFindContractIdsForServiceUserWithNoServiceUser() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    Collection<Contract> contracts = testling.findContractIdsForServiceUser(StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);
    assertTrue(contracts.size() == 0);
  }

  @Betamax(tape = "contract_testFindContractById", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindContractById() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    UserContext userContext = userContextProvider.createContext(PREVIEW_USER_NAME);
    UserContextHelper.setCurrentContext(userContext);
    Collection<Contract> contracts = testling.findContractIdsForUser(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
    assertNotNull(contracts);
    if (StoreContextHelper.getWcsVersion(testConfig.getB2BStoreContext()) > StoreContextHelper.WCS_VERSION_7_7) {
      Iterator iterator = contracts.iterator();
      while (iterator.hasNext()) {
        Contract contract = (Contract) iterator.next();
        Contract contractById = testling.findContractById(contract.getExternalId());
        assertNotNull(contractById);
        assertEquals(contract.getExternalId(), contractById.getExternalId());
      }
    }
  }

  @Betamax(tape = "contract_testInvalidContract", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testInvalidContract() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getB2BStoreContext());
    Contract testcontract = testling.findContractById("xxxx");
    assertNull(testcontract);
  }
}

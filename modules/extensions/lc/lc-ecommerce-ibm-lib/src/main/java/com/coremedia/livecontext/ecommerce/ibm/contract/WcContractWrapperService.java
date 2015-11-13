package com.coremedia.livecontext.ecommerce.ibm.contract;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;


public class WcContractWrapperService extends AbstractWcWrapperService {

  private static final Logger LOG = LoggerFactory.getLogger(WcContractWrapperService.class);

  private static final WcRestConnector.WcRestServiceMethod<HashMap, Void>
          FIND_CONTRACT_BY_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/contract/{id}?profileName=IBM_Contract_Usage", true, true, HashMap.class);

  private static final WcRestConnector.WcRestServiceMethod<Map, Void>
          FIND_CONTRACTS_FOR_USER = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/contract?q=eligible", false, true, false, true, Void.class, Map.class);


  /**
   * Finds the contracts the current user is eligible to.
   *
   * @param storeContext the store context
   * @return the list of contracts for the current user
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  public Map<String, Object> findContractsForUser(final UserContext userContext, final StoreContext storeContext) throws CommerceException {
    try {
      if (StoreContextHelper.getWcsVersion(storeContext) < StoreContextHelper.WCS_VERSION_7_8) {
        return Collections.emptyMap();
      }
      //noinspection unchecked
      Map<String, Object> contractMap = getRestConnector().callService(
              FIND_CONTRACTS_FOR_USER, asList(getStoreId(storeContext)),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext),
                      UserContextHelper.getForUserName(userContext), null), null, storeContext, userContext);

      return contractMap;

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a contract by a given external id.
   *
   * @param externalId   the external id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the contract map which contains the JSON response data retrieved from the commerce server or null if no contract was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> findContractByTechId(final String externalId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      if (StoreContextHelper.getWcsVersion(storeContext) < StoreContextHelper.WCS_VERSION_7_8) {
        return Collections.emptyMap();
      }
      Map<String, Object> data = getRestConnector().callService(
              FIND_CONTRACT_BY_ID, asList(getStoreId(storeContext), externalId),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext)),
              null, storeContext, userContext);
      if (data != null) {
        List<Map<String, Object>> resultList = DataMapHelper.getValueForPath(data, "resultList", List.class);
        if (resultList != null && resultList.size() > 0) {
          return resultList.get(0);
        }
      }
      return null;

    } catch (CommerceException e) {
      //ibm returns 403 or 500 instead of 404 for a unknown contract id
      LOG.warn("CommerceException", e);
      return null;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }
}


package com.coremedia.livecontext.ecommerce.ibm.contract;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContractServiceImpl implements ContractService {
  private static final Logger LOG = LoggerFactory.getLogger(ContractServiceImpl.class);

  private WcContractWrapperService contractWrapperService;
  private CommerceCache commerceCache;
  private CommerceBeanFactory commerceBeanFactory;
  private String contractPreviewServiceUserName;

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public Contract findContractById(@Nonnull String id) throws CommerceException {
    Map<String, Object> contract = (Map<String, Object>) commerceCache.get(
            new ContractCacheKey(CommerceIdHelper.formatContractId(id),
                    StoreContextHelper.getCurrentContext(), UserContextHelper.getCurrentContext(),
                    contractWrapperService, commerceCache));
    return createContractBeanFor(contract);
  }

  @Override
  public Collection<Contract> findContractIdsForUser(UserContext userContext, StoreContext storeContext) throws CommerceException {
    if (storeContext == null)
      storeContext = StoreContextHelper.getCurrentContext();

    Map<String, Object> contractMap = (Map<String, Object>) commerceCache.get(new ContractsByUserCacheKey(userContext,
            storeContext,
            contractWrapperService,
            commerceCache));

    Map contracts = DataMapHelper.getValueForKey(contractMap, "contracts", Map.class);
    return createContractBeansFor(contracts);
  }

  @Override
  public Collection<Contract> findContractIdsForServiceUser(StoreContext storeContext) throws CommerceException {
    if (contractPreviewServiceUserName != null){
      return findContractIdsForUser(UserContextHelper.createContext(contractPreviewServiceUserName, null), storeContext);
    } else {
      LOG.warn("No service user for contract preview configured for ContractService");
    }
    return Collections.EMPTY_LIST;
  }

  protected Contract createContractBeanFor(Map<String, Object> contractMap) {
    if (contractMap != null && !contractMap.isEmpty()) {
      String id = CommerceIdHelper.formatContractId(String.valueOf(contractMap.get("referenceNumber")));
      if (CommerceIdHelper.isContractId(id)) {
        Contract contract = (Contract) commerceBeanFactory.createBeanFor(id, StoreContextHelper.getCurrentContext());
        ((AbstractIbmCommerceBean) contract).setDelegate(contractMap);
        return contract;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  protected List<Contract> createContractBeansFor(Map<String, Object> contractsMap) {
    if (contractsMap == null || contractsMap.isEmpty()) {
      return Collections.emptyList();
    }
    List<Contract> result = new ArrayList<>(contractsMap.size());
    Iterator entries = contractsMap.entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry thisEntry = (Map.Entry) entries.next();
      Object key = thisEntry.getKey();

      String id = CommerceIdHelper.formatContractId(String.valueOf(key));
      if (CommerceIdHelper.isContractId(id)) {
        CommerceBean contractBean = commerceBeanFactory.createBeanFor(id, StoreContextHelper.getCurrentContext());
        result.add((Contract) contractBean);
      }
    }
    return Collections.unmodifiableList(result);
  }

  @Required
  public void setContractWrapperService(WcContractWrapperService contractWrapperService) {
    this.contractWrapperService = contractWrapperService;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  public void setContractPreviewServiceUserName(String contractPreviewServiceUserName) {
    this.contractPreviewServiceUserName = contractPreviewServiceUserName;
  }
}

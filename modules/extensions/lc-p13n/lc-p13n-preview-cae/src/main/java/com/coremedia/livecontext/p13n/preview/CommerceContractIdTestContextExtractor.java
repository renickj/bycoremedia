package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.preview.TestContextExtractor;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extracts commerce contractIds from cmUserProfile and enriches the the StoreContext.
 * The p13n ContextCollection is not enriched since there is no CoreMedia p13n feature based on contracts implemented yet.
 * The contractIds of test personas are extracted for contract based perview of b2b shop pages in studio.
 */
public class CommerceContractIdTestContextExtractor implements TestContextExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceContractIdTestContextExtractor.class);

  private ContentBeanFactory contentBeanFactory;

  static final String PROPERTIES_PREFIX = "properties";
  static String COMMERCE_CONTEXT = "commerce";
  static String USER_CONTRACT_PROPERTY = "usercontracts";

  private static String CONTRACT_PROPERTY_PATH = PROPERTIES_PREFIX + "." + COMMERCE_CONTEXT + "." + USER_CONTRACT_PROPERTY;

  @Override
  public void extractTestContextsFromContent(Content content, ContextCollection contextCollection) {
    if (content == null) {
      LOG.debug("supplied content is null; cannot extract any contexts");
      return;
    }

    ContentBean cmUserProfileBean = contentBeanFactory.createBeanFor(content);
    if (!(cmUserProfileBean instanceof CMUserProfile)) {
      LOG.debug("cannot extract context from contentbean of type {}", cmUserProfileBean.getClass().toString());
      return;
    }

    Map<String, Object> profileExtensions = ((CMUserProfile) cmUserProfileBean).getProfileExtensions();
    Object contractIds = getProperty(profileExtensions, CONTRACT_PROPERTY_PATH);

    if (contractIds != null && contractIds instanceof List) {
      List contractList = (List) contractIds;
      if (!contractList.isEmpty()) {
        addContractIdsForPreviewToStoreContext(contractList);
      }
    }
  }

  private void addContractIdsForPreviewToStoreContext(List<String> contractList) {
    StoreContext currentContext = getStoreContextProvider().getCurrentContext();
    if (currentContext != null) {
      List<String> contractIds = new ArrayList<>();
      for (String contract : contractList) {
        String contractId = getCommerceIdProvider().parseExternalIdFromId(contract);
        if (contractId != null) {
          contractIds.add(contractId);
        }
      }
      currentContext.setContractIdsForPreview(contractIds.toArray((new String[0])));
    }
  }

  private Object getProperty(Map<String, Object> profileExtensions, String propertyPath) {
    try {
      return PropertyUtils.getNestedProperty(profileExtensions, propertyPath);
    } catch (Exception e) { // NOSONAR
      // it is ok
    }
    return null;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  public CommerceIdProvider getCommerceIdProvider() {
    return Commerce.getCurrentConnection().getIdProvider();
  }
}

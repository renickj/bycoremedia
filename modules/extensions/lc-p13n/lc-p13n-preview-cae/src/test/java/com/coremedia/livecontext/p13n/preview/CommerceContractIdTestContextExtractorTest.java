package com.coremedia.livecontext.p13n.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.blueprint.personalization.contentbeans.CMUserProfile;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.util.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceContractIdTestContextExtractorTest {

  private CommerceContractIdTestContextExtractor testling;

  @Mock
  private Content content;

  @Mock
  private CMUserProfile cmUserProfile;

  @Mock
  private Map<String, Object> profileExtensions, properties, commerce;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private StoreContextProvider storeContextProvider;

  private StoreContext storeContext;

  @Mock
  CommerceConnection commerceConnection;

  @Before
  public void setUp() throws Exception {
    testling = new CommerceContractIdTestContextExtractor();
    testling.setContentBeanFactory(contentBeanFactory);
    storeContext = StoreContextBuilder.create().build();

    Commerce.setCurrentConnection(commerceConnection);

    when(commerceConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(commerceConnection.getIdProvider()).thenReturn(new BaseCommerceIdProvider("vendor"));
  }

  @Test
  public void testExtractTestContextsFromContent() {
    String userContractsStr = "ibm:///catalog/contract/contract1,ibm:///catalog/contract/contract2";
    String[] userContractIds = new String[]{"contract1", "contract2"};
    List<String> contracts = StringUtil.tokenizeToList(userContractsStr, ",");
    when(contentBeanFactory.createBeanFor(content)).thenReturn(cmUserProfile);
    when(cmUserProfile.getProfileExtensions()).thenReturn(profileExtensions);
    when(profileExtensions.get(CommerceContractIdTestContextExtractor.PROPERTIES_PREFIX)).thenReturn(properties);
    when(properties.get(CommerceContractIdTestContextExtractor.COMMERCE_CONTEXT)).thenReturn(commerce);
    when(commerce.get(CommerceContractIdTestContextExtractor.USER_CONTRACT_PROPERTY)).thenReturn(contracts);
    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);

    testling.extractTestContextsFromContent(content, null);

    //assert the user segments in the store context
    assertArrayEquals(userContractIds, storeContext.getContractIdsForPreview());
  }
}

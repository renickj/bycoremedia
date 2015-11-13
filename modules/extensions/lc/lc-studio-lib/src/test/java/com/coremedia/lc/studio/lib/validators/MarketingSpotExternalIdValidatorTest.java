package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MarketingSpotExternalIdValidatorTest {
  private MarketingSpotExternalIdValidator testling;

  @Mock
  private Issues issues;

  @Mock
  private Content marketingSpot;

  @Mock
  private ContentType marketingSpotType;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private StoreContext defaultStoreContext;

  @Mock
  private CommerceBeanFactory commerceBeanFactory;

  private static final String PROPERTY_NAME = "externalId";
  private static final String EXTERNAL_ID = "ibm:///catalog/marketingspot/BathAccessoriesRow2_Content";

  @Before
  public void setupDefault() {
    PowerMockito.mockStatic(Commerce.class);
/*    when(Commerce.getCurrentConnection()).thenReturn(commerceConnection);
    when(commerceConnection.getCommerceBeanFactory()).thenReturn(commerceBeanFactory);
    when(commerceConnection.getWorkspaceService()).thenReturn(workspaceService);
    when(commerceConnection.getIdProvider()).thenReturn(new CommerceIdProvider());
    */
    testling = new MarketingSpotExternalIdValidator();
    testling.setPropertyName(PROPERTY_NAME);
   /* testling.setStoreContextProvider(storeContextProvider);
    testling.setCommerceBeanFactory(commerceBeanFactory);
*/
    when(marketingSpot.getId()).thenReturn(EXTERNAL_ID);
    when(marketingSpot.isInProduction()).thenReturn(true);
    when(marketingSpot.getString(PROPERTY_NAME)).thenReturn(EXTERNAL_ID);
    when(marketingSpot.getType()).thenReturn(marketingSpotType);
    when(marketingSpotType.isSubtypeOf("CMMarketingSpot")).thenReturn(true);
    when(storeContextProvider.findContextByContent(marketingSpot)).thenReturn(defaultStoreContext);
  }

  @Test
  public void testEmptyPropertyValue() throws Exception {
    testling.emptyPropertyValue(issues);
    verify(issues, times(1)).addIssue(eq(ERROR), eq(PROPERTY_NAME), anyString());
  }

  @Test
  public void testInvalidStoreContext() throws Exception {
    testling.invalidStoreContext(issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME), anyString());
  }

  @Test
  public void testStoreContextNotFound() throws Exception {
    testling.storeContextNotFound(issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME), anyString());
  }

  @Test
  public void testInvalidExternalId() throws Exception {
    testling.invalidExternalId(issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME), anyString());
  }

  @Test
  public void validateUniquenessBlankExternalId() {
    when(marketingSpot.getString(PROPERTY_NAME)).thenReturn("   ");

    testling.validate(marketingSpot, issues);

    verify(issues, times(1)).addIssue(any(Severity.class), anyString(), anyString());
  }
}

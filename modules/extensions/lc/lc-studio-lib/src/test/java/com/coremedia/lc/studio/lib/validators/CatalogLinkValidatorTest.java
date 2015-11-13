package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SpringCommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.validation.Severity;
import com.coremedia.rest.validation.impl.IssuesImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CatalogLinkValidatorTest extends AbstractCatalogLinkValidatorTest {
  private static final String PROPERTY_NAME = "externalId";
  private static final String VALID_ID = "ibm:///catalog/product/GDA035_3508";

  private CatalogLinkValidator testling;

  @Before
  public void setup() throws Exception {
    super.init();
    when(content.getString(PROPERTY_NAME)).thenReturn(VALID_ID);

    testling = new ProductTeaserExternalIdValidator();
    testling.setConnection(capConnection);
    testling.setPropertyName(PROPERTY_NAME);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
  }

  @Test
  public void testValid() {
    CommerceBean commerceBean = mock(CommerceBean.class);
    when(commerceConnection.getCommerceBeanFactory().loadBeanFor(anyString(), any(StoreContext.class))).thenReturn(commerceBean);
    assertTrue(validate(content).getByProperty().isEmpty());
  }

  @Test
  public void testEmpty() {
    when(content.getString(PROPERTY_NAME)).thenReturn(null);
    assertTrue(validate(content).hasIssueAtSeverity(Severity.ERROR));
  }

  @Test
  public void testInvalidId() {
    when(content.getString(PROPERTY_NAME)).thenReturn("ibm:///this/id/is/not/valid");

    assertNotNull(validate(content).getByProperty().get(PROPERTY_NAME));
  }

  @Test
  public void testNotFound() {
    when(content.getString(PROPERTY_NAME)).thenReturn("ibm:///catalog/product/not_in_catalog");

    assertNotNull(validate(content).getByProperty().get(PROPERTY_NAME));
  }

  @Test
  public void testInvalidContext() {
    commerceConnection.getStoreContext().put("storeId", null);
    assertNotNull(validate(content).getByProperty().get(PROPERTY_NAME));
  }

  @Test
  public void testContextNotFound() throws Exception {
    commerceConnection.setStoreContext(null);
    assertNotNull(validate(content).getByProperty().get(PROPERTY_NAME));
  }

  @Test
  public void testBrokenCatalogConnection() {
    SpringCommerceBeanFactory commerceBeanFactory = mock(SpringCommerceBeanFactory.class);
    when(commerceBeanFactory.loadBeanFor(anyString(), any(StoreContext.class))).thenThrow(CommerceException.class);
    assertNotNull(validate(content).getByProperty().get(PROPERTY_NAME));
  }

  protected IssuesImpl<Content> validate(Content content) {
    IssuesImpl result = new IssuesImpl<>(content, Collections.<String>emptySet());
    testling.validate(content, result);
    return result;
  }
}

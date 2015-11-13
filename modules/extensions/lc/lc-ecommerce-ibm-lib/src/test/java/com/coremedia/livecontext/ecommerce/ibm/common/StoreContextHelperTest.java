package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StoreContextHelperTest {

  private static final String STORE_CONFIG_ID = TestConfig.STORE_CONFIG_ID;
  private static final String STORE_NAME = TestConfig.STORE_NAME;
  private static final String STORE_ID = TestConfig.STORE_ID;
  private static final String CATALOG_ID = TestConfig.CATALOG_ID;
  private static final String LOCALE = TestConfig.LOCALE;
  private static final String CURRENCY = TestConfig.CURRENCY;
  private static final String WORKSPACE = TestConfig.WORKSPACE_ID;

  @Test
  public void testCreateContext() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    context.setWorkspaceId(WORKSPACE);
    assertNotNull(context);
  }

  @Test
  public void testCreateContextWithMissingValues() {
    // Attention: it should work without an InvalidContext exception
    // the idea is the exception will be thrown only on access time
    StoreContext context = StoreContextHelper.createContext(null, null, null, null, null, null);
    assertNotNull(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidLocale() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, "xx1234XX", CURRENCY);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidCurrency() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, "XX");
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidStoreId() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, "   ", STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidStoreName() {
    StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, "    ", CATALOG_ID, LOCALE, CURRENCY);
  }

  @Test(expected = InvalidContextException.class)
  public void testCreateContextWithInvalidWorkspace() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWorkspaceId(context, "    ");
  }

  @Test(expected = InvalidContextException.class)
  public void testValidateContext() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, null, CURRENCY);
    StoreContextHelper.validateContext(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingLocale() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, null, CURRENCY);
    StoreContextHelper.getLocale(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingCurrency() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, null);
    StoreContextHelper.getCurrency(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingStoreId() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, null, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.getStoreId(context);
  }

  @Test(expected = InvalidContextException.class)
  public void testAccessContextWithMissingStoreName() {
    StoreContext context = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, null, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.getStoreName(context);
  }

}

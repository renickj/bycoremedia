package com.coremedia.livecontext.validation;

import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CMExternalChannelValidatorTest {

  private CMExternalChannelValidator testling;

  @Before
  public void setup() {
    testling = new CMExternalChannelValidator();
  }

  @Test
  public void validNonCatalogCategory() {
    CMExternalChannel channel = mock(CMExternalChannel.class);
    assertTrue(testling.validate(channel));
  }

  @Test
  public void validCatalogCategory() {
    CMExternalChannel channel = mock(CMExternalChannel.class);
    when(channel.isCatalogPage()).thenReturn(true);
    when(channel.getExternalId()).thenReturn("externalId");
    // partial mock
    testling = spy(testling);
    doReturn(mock(Category.class)).when(testling).getCategory(anyString());

    assertTrue(testling.validate(channel));
  }

  @Test
  public void invalidCatalogCategory() {
    CMExternalChannel channel = mock(CMExternalChannel.class);
    when(channel.isCatalogPage()).thenReturn(true);
    when(channel.getExternalId()).thenReturn("externalId");
    // category does not exist
    assertFalse(testling.validate(channel));
  }

  @Test
  public void emptyExternalId() {
    CMExternalChannel channel = mock(CMExternalChannel.class);
    when(channel.isCatalogPage()).thenReturn(true);
    // external id is empty
    assertFalse(testling.validate(channel));
  }

}
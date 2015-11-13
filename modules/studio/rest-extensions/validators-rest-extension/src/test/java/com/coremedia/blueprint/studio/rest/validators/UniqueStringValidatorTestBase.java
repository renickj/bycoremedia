package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.descriptors.BlobPropertyDescriptor;
import com.coremedia.cap.common.descriptors.StringPropertyDescriptor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.rest.validation.Issues;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public abstract class UniqueStringValidatorTestBase {

  static final String CONTENT_TYPE = "Type";
  static final String PROPERTY = "property";

  @Mock
  private CapConnection connection;

  @Mock
  ContentRepository contentRepository;

  @Mock
  Issues issues;

  final SetMultimap<String, Content> map = HashMultimap.create();

  @Before
  public void setUp() {
    when(connection.getContentRepository()).thenReturn(contentRepository);
    when(contentRepository.getConnection()).thenReturn(connection);

    ContentType contentType = mock(ContentType.class);
    when(contentType.getName()).thenReturn(CONTENT_TYPE);
    StringPropertyDescriptor descriptor = mock(StringPropertyDescriptor.class);
    when(contentRepository.getContentType(CONTENT_TYPE)).thenReturn(contentType);
    when(contentType.getDescriptor(PROPERTY)).thenReturn(descriptor);
  }

  @Test(expected = IllegalStateException.class)
  public void testNonExistingContentType() throws Exception {
    newValidator("FooType", PROPERTY);
  }

  @Test(expected = IllegalStateException.class)
  public void testNonExistingProperty() throws Exception {
    newValidator(CONTENT_TYPE, "fooProperty");
  }

  @Test(expected = IllegalStateException.class)
  public void testNoStringProperty() throws Exception {
    ContentType type = contentRepository.getContentType(CONTENT_TYPE);
    assertNotNull(type);

    BlobPropertyDescriptor blobDescriptor = mock(BlobPropertyDescriptor.class);
    when(type.getDescriptor("fooProperty")).thenReturn(blobDescriptor);
    newValidator(CONTENT_TYPE, "fooProperty");
  }

  @Test
  public void testValidateNullValueIsValid() throws Exception {
    Content content = mock(Content.class);
    newValidator().validate(content, issues);
    verifyZeroInteractions(issues);
  }

  @Test
  public void testValidateEmptyValueIsValid() throws Exception {
    Content content = mock(Content.class);
    when(content.getString(PROPERTY)).thenReturn("");
    newValidator().validate(content, issues);
    verifyZeroInteractions(issues);
  }

  @Test
  public void testValidateValid() throws Exception {
    Content content = mock(Content.class);
    String value = "value";
    when(content.getString(PROPERTY)).thenReturn(value);
    map.put(value, content);
    newValidator().validate(content, issues);
    verifyZeroInteractions(issues);
  }

  // ----------------------------------------------------------------------

  UniqueStringValidator newValidator() throws Exception {
    return newValidator(CONTENT_TYPE, PROPERTY);
  }

  abstract UniqueStringValidator newValidator(String contentType, String property) throws Exception;

}
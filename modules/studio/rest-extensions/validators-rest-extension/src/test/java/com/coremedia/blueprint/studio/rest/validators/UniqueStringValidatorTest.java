package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.common.util.Function;
import com.coremedia.rest.validation.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UniqueStringValidatorTest extends UniqueStringValidatorTestBase {

  @Test
  public void testValidateMultipleContentsWithSameValue() throws Exception {
    Content content1 = mock(Content.class);
    Content content2 = mock(Content.class);
    String value = "value";
    when(content1.getString(PROPERTY)).thenReturn(value);
    when(content2.getString(PROPERTY)).thenReturn(value);
    map.put(value, content1);
    map.put(value, content2);
    newValidator().validate(content1, issues);

    verify(issues).addIssue(Severity.ERROR, PROPERTY, UniqueStringValidator.class.getSimpleName(), content2);
  }

  @Test
  public void testValidateMultipleContentsWithSameValueDifferentErrorCode() throws Exception {
    Content content1 = mock(Content.class);
    Content content2 = mock(Content.class);
    String value = "value";
    when(content1.getString(PROPERTY)).thenReturn(value);
    when(content2.getString(PROPERTY)).thenReturn(value);
    map.put(value, content1);
    map.put(value, content2);
    UniqueStringValidator validator = newValidator();
    validator.setCode("code");
    validator.validate(content1, issues);

    verify(issues).addIssue(Severity.ERROR, PROPERTY, "code", content2);
  }

  // ----------------------------------------------------------------------

  UniqueStringValidator newValidator(String contentType, String property) throws Exception {
    UniqueStringValidator validator = new UniqueStringValidator(contentRepository, contentType, property,
            new Function<String, Set<Content>>() {
              @Override
              public Set<Content> apply(String input) {
                return map.get(input);
              }
            });
    validator.afterPropertiesSet();
    return validator;
  }


}
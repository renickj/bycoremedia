package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.coremedia.rest.validation.impl.IssuesImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TeaserLinkTargetValidatorTest {

  @Test
  public void testValidateDefaultSettings() throws Exception {
    TeaserLinkTargetValidator validator = new TeaserLinkTargetValidator();
    List<String> properties = new ArrayList<>();

    Content blankContent = mock(Content.class);
    Issues issues = new IssuesImpl<>(blankContent, properties);

    validator.validate(blankContent, issues);
    assertTrue(issues.hasIssueAtSeverityOrWorse(Severity.WARN));
  }

  @Test
  public void testValidateDisabledCallToActionNoLinkTarget() throws Exception {
    TeaserLinkTargetValidator validator = new TeaserLinkTargetValidator();
    List<String> properties = new ArrayList<>();

    Content content = mock(Content.class);
    Struct settingsStruct = mock(Struct.class);
    CapType type = mockType();

    Issues issues = new IssuesImpl<>(content, properties);

    when(settingsStruct.getType()).thenReturn(type);
    when(settingsStruct.getBoolean(anyString())).thenReturn(true);
    when(content.getStruct(anyString())).thenReturn(settingsStruct);

    validator.validate(content, issues);
    assertFalse(issues.hasIssueAtSeverityOrWorse(Severity.WARN));
  }

  private CapType mockType() {
    CapType result = mock(CapType.class);
    CapPropertyDescriptor mockDescriptor = mock(CapPropertyDescriptor.class);
    when(result.getDescriptor(anyString())).thenReturn(mockDescriptor);
    return result;
  }

  @Test
  public void testValidateEnabledCallToActionNoLinkTarget() throws Exception {
    TeaserLinkTargetValidator validator = new TeaserLinkTargetValidator();
    List<String> properties = new ArrayList<>();

    Content content = mock(Content.class);
    Struct settingsStruct = mock(Struct.class);
    CapType type = mockType();

    Issues issues = new IssuesImpl<>(content, properties);

    when(settingsStruct.getType()).thenReturn(type);
    when(settingsStruct.getBoolean(anyString())).thenReturn(false);
    when(content.getStruct(anyString())).thenReturn(settingsStruct);

    validator.validate(content, issues);
    assertTrue(issues.hasIssueAtSeverityOrWorse(Severity.WARN));
  }

  @Test
  public void testValidateEnabledCallToActionWithLinkTarget() throws Exception {
    TeaserLinkTargetValidator validator = new TeaserLinkTargetValidator();
    List<String> properties = new ArrayList<>();

    Content content = mock(Content.class);
    Struct settingsStruct = mock(Struct.class);
    CapType type = mockType();

    Issues issues = new IssuesImpl<>(content, properties);

    when(settingsStruct.getType()).thenReturn(type);
    when(settingsStruct.getBoolean(anyString())).thenReturn(false);
    when(content.getStruct(anyString())).thenReturn(settingsStruct);
    when(content.getLink(anyString())).thenReturn(content);

    validator.validate(content, issues);
    assertFalse(issues.hasIssueAtSeverityOrWorse(Severity.WARN));
  }

}
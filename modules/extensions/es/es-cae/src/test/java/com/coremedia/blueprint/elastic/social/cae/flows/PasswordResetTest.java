package com.coremedia.blueprint.elastic.social.cae.flows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PasswordResetTest {

  @Mock
  private RequestContext requestContext;

  @Mock
  private ExternalContext externalContext;

  @Mock
  private HttpServletRequest request;
  
  @Mock
  private DefaultMessageContext messageContext;

  @Mock
  private LocalizationContext localizationContext;
  
  @Before
  public void setup() {
    when(requestContext.getMessageContext()).thenReturn(messageContext);
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getNativeRequest()).thenReturn(request);
    when(request.getAttribute(javax.servlet.jsp.jstl.core.Config.FMT_LOCALIZATION_CONTEXT)).thenReturn(localizationContext);
  }
  
  @Test
  public void testEmailAddress() {
    PasswordReset passwordReset = new PasswordReset();
    assertNull(passwordReset.getEmailAddress());
    passwordReset.setEmailAddress("horst@coremedia.com");
    assertEquals("horst@coremedia.com", passwordReset.getEmailAddress());
  }

  @Test
  public void testPassword() {
    PasswordReset passwordReset = new PasswordReset();
    assertNull(passwordReset.getPassword());
    passwordReset.setPassword("secret");
    assertEquals("secret", passwordReset.getPassword());
  }

  @Test
  public void testConfirmPassword() {
    PasswordReset passwordReset = new PasswordReset();
    assertNull(passwordReset.getConfirmPassword());
    passwordReset.setConfirmPassword("secret");
    assertEquals("secret", passwordReset.getConfirmPassword());
  }

  @Test
  public void testPasswordPolicy() {
    PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
    PasswordReset passwordReset = new PasswordReset();
    assertNull(passwordReset.getPasswordPolicy());
    passwordReset.setPasswordPolicy(passwordPolicy);
    assertSame(passwordPolicy, passwordReset.getPasswordPolicy());
  }

  @Test
  public void testValidatePasswordResetSuccess() {
    PasswordReset passwordReset = new PasswordReset();
    passwordReset.setEmailAddress("horst@coremedia.com");

    passwordReset.validate(requestContext);

    verify(messageContext, never()).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testValidatePasswordResetFailure() {
    PasswordReset passwordReset = new PasswordReset();

    passwordReset.validate(requestContext);

    verify(messageContext).addMessage(any(MessageResolver.class));
  }

  @Test
  public void testValidateResetFormSuccess() {
    PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
    when(passwordPolicy.verify("secret")).thenReturn(true);
    PasswordReset passwordReset = new PasswordReset();
    passwordReset.setPassword("secret");
    passwordReset.setConfirmPassword("secret");
    passwordReset.setPasswordPolicy(passwordPolicy);

    passwordReset.validateResetForm(requestContext);

    verify(messageContext, never()).addMessage(any(MessageResolver.class));
    verify(passwordPolicy).verify("secret");
  }

  @Test
  public void testValidateResetFormFailure() {
    PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
    when(passwordPolicy.verify("")).thenReturn(false);
    PasswordReset passwordReset = new PasswordReset();
    passwordReset.setPasswordPolicy(passwordPolicy);
    passwordReset.setPassword("");
    passwordReset.setConfirmPassword("xxx");

    passwordReset.validateResetForm(requestContext);

    verify(messageContext, times(3)).addMessage(any(MessageResolver.class));
  }
}

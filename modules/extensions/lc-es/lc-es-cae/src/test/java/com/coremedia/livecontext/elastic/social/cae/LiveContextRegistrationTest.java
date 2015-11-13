package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.elastic.social.cae.flows.PasswordPolicy;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.binding.message.DefaultMessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestContextHolder.class})
public class LiveContextRegistrationTest {
  @Mock
  RequestContext requestContext;

  @Mock
  private ValidationContext validationContext;

  @Mock
  private ExternalContext externalContext;

  @Mock
  private HttpServletRequest request;

  @Mock
  private DefaultMessageContext messageContext;

  @Mock
  private Page page;

  @InjectMocks
  private LiveContextRegistration testling;

  @Before
  @SuppressWarnings("unchecked")
  public void setup() {
    mockStatic(RequestContextHolder.class);
    when(RequestContextHolder.getRequestContext()).thenReturn(requestContext);
    when(validationContext.getMessageContext()).thenReturn(messageContext);
    when(requestContext.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getNativeRequest()).thenReturn(request);
    when(RequestAttributeConstants.getPage(request)).thenReturn(page);

    // make WebApplicationContextyUtils happy
    ServletContext servletContext = mock(ServletContext.class);
    WebApplicationContext webApplicationContext = mock(WebApplicationContext.class);
    ElasticSocialPlugin elasticSocialPlugin = mock(ElasticSocialPlugin.class);

    when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(webApplicationContext);
    when(elasticSocialPlugin.getElasticSocialConfiguration(anyVararg())).thenReturn(mock(ElasticSocialConfiguration.class));
    when(webApplicationContext.getBean(ElasticSocialPlugin.class)).thenReturn(elasticSocialPlugin);

    when(request.getServletContext()).thenReturn(servletContext);
    when(RequestAttributeConstants.getPage(request)).thenReturn(page);


    testling = new LiveContextRegistration();
    PasswordPolicy passwordPolicy = mock(PasswordPolicy.class);
    when(passwordPolicy.verify("secret")).thenReturn(true);
    testling.setUsername("horst");
    testling.setGivenname("horstl");
    testling.setSurname("Schneider");
    testling.setEmailAddress("horst@coremedia.com");
    testling.setPassword("secret");
    testling.setConfirmPassword("secret");
    testling.setPasswordPolicy(passwordPolicy);
    testling.setAcceptTermsOfUse(true);
  }

  @Test
  public void acceptTooYoung() {
    assertFalse(testling.isAcceptTooYoungPolicy());

    testling.setAcceptTooYoungPolicy(true);
    assertTrue(testling.isAcceptTooYoungPolicy());
  }

  @Test
  public void birthdate() {
    assertNull(testling.getBirthdate());

    Date now = new Date();
    testling.setBirthdate(now);
    assertEquals(now, testling.getBirthdate());
  }

  @Test
  public void isYoungetThan() {
    LocalDate now = new LocalDate();
    LocalDate birthdate = new LocalDate(now.getYear()-10, 5, 5);

    testling.setBirthdate(birthdate.toDateTimeAtStartOfDay().toDate());
    assertTrue(testling.isYoungerThan(12));
  }

  @Test
  public void validateBirthdateFailed() {
    testling.validateEnterUserDetails(validationContext);
    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));

    testling.validateEnterUserDetails(validationContext);
    verify(messageContext, times(2)).addMessage(any(MessageResolver.class));
  }

  @Test
  public void validateBirthdateSuccess() {
    LocalDate birthdate = new LocalDate(2010, 1, 1);
    testling.setBirthdate(birthdate.toDateTimeAtStartOfDay().toDate());
    testling.validateEnterUserDetails(validationContext);
    verify(messageContext, never()).addMessage(any(MessageResolver.class));
  }

  @Test
  public void validateAcceptToYoungPolicyFailed() {
    testling.validateAcceptTooYoungPolicy(validationContext);
    verify(messageContext, times(1)).addMessage(any(MessageResolver.class));
  }

  @Test
  public void validateAcceptToYoungPolicySuccess() {
    testling.setAcceptTooYoungPolicy(true);
    testling.validateAcceptTooYoungPolicy(validationContext);
    verify(messageContext, never()).addMessage(any(MessageResolver.class));
  }
}
